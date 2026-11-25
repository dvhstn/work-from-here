#!/usr/bin/env python3
"""Load space photos into the local image CDN (local development only).

For each manifest entry it downloads the image, checks the bytes really are an
image, uploads it to the S3 bucket under its key and records the image metadata
on the space row.

Usage:
  python3 -I backend/local/ingest_images.py MANIFEST.json DOWNLOAD_DIR

Manifest: a JSON array of objects with
  spaceId, key, downloadUrl, alt, credit, license, sourceUrl

Environment (defaults match backend/local/docker-compose.cdn.yaml):
  CDN_NETWORK   docker network of the CDN stack   (wfh-local-cdn_default)
  S3_ENDPOINT   S3 endpoint inside that network   (http://s3:8333)
  S3_BUCKET     bucket name                       (wfh-images)
  S3_ACCESS_KEY / S3_SECRET_KEY                   (wfh-local / wfh-local-secret)
  PG_CONTAINER  postgres container name           (wfh-local-pg)
  PG_USER / PG_DB                                 (wfh / workfromhere)
"""
import json
import os
import subprocess
import sys
import time
import urllib.error
import urllib.request

USER_AGENT = "work-from-here-local-dev/0.1 (local development seed loader)"
MAX_BYTES = 15 * 1024 * 1024

SIGNATURES = {
    b"\xff\xd8\xff": "image/jpeg",
    b"\x89PNG\r\n\x1a\n": "image/png",
    b"RIFF": "image/webp",  # confirmed by the WEBP marker below
}


def env(name, default):
    return os.environ.get(name, default)


def sniff(data):
    for magic, content_type in SIGNATURES.items():
        if data.startswith(magic):
            if content_type == "image/webp" and data[8:12] != b"WEBP":
                continue
            return content_type
    return None


def sql_literal(value):
    if value is None:
        return "NULL"
    text = " ".join(str(value).split())  # collapse stray whitespace from source metadata
    return "'" + text.replace("'", "''") + "'"


MAX_RETRY_DELAY = 30


def fetch(url, attempts=6):
    """GET with capped backoff on HTTP 429/5xx (Wikimedia rate-limits fast clients)."""
    request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    for attempt in range(1, attempts + 1):
        try:
            with urllib.request.urlopen(request, timeout=60) as response:
                return response.read(MAX_BYTES + 1)
        except urllib.error.HTTPError as error:
            retryable = error.code == 429 or error.code >= 500
            if not retryable or attempt == attempts:
                raise
            retry_after = error.headers.get("Retry-After", "")
            delay = int(retry_after) if retry_after.isdigit() else 2 ** attempt
            delay = min(delay, MAX_RETRY_DELAY)
            print(f"      HTTP {error.code}, retrying in {delay}s", file=sys.stderr, flush=True)
            time.sleep(delay)


def read_image(path):
    """Return (content_type, size) if path holds a valid image, else None."""
    if not os.path.isfile(path):
        return None
    with open(path, "rb") as handle:
        data = handle.read()
    content_type = sniff(data)
    return (content_type, len(data)) if content_type else None


def download(url, dest):
    data = fetch(url)
    if len(data) > MAX_BYTES:
        raise ValueError("image larger than 15 MB")
    content_type = sniff(data)
    if content_type is None:
        raise ValueError("downloaded bytes are not a JPEG/PNG/WebP image")
    os.makedirs(os.path.dirname(dest), exist_ok=True)
    with open(dest, "wb") as handle:
        handle.write(data)
    return content_type, len(data)


def upload_all(staging_dir):
    """Upload every staged file in one container run; object keys mirror the staging paths."""
    subprocess.run(
        [
            "docker", "run", "--rm", "--network", env("CDN_NETWORK", "wfh-local-cdn_default"),
            "-v", f"{staging_dir}:/in:ro",
            "-e", f"AWS_ACCESS_KEY_ID={env('S3_ACCESS_KEY', 'wfh-local')}",
            "-e", f"AWS_SECRET_ACCESS_KEY={env('S3_SECRET_KEY', 'wfh-local-secret')}",
            "-e", "AWS_DEFAULT_REGION=us-east-1",
            "amazon/aws-cli", "--endpoint-url", env("S3_ENDPOINT", "http://s3:8333"),
            "s3", "cp", "/in", f"s3://{env('S3_BUCKET', 'wfh-images')}/", "--recursive", "--only-show-errors",
        ],
        check=True,
    )


def run_sql(statements):
    subprocess.run(
        ["docker", "exec", "-i", env("PG_CONTAINER", "wfh-local-pg"), "psql", "-q", "-v", "ON_ERROR_STOP=1",
         "-U", env("PG_USER", "wfh"), "-d", env("PG_DB", "workfromhere")],
        input="BEGIN;\n" + "\n".join(statements) + "\nCOMMIT;\n",
        text=True,
        check=True,
    )


def main():
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    manifest_path, download_dir = sys.argv[1], os.path.abspath(sys.argv[2])
    staging_dir = os.path.join(download_dir, "staging")
    os.makedirs(staging_dir, exist_ok=True)
    with open(manifest_path, encoding="utf-8") as handle:
        entries = json.load(handle)

    # 1) Download (resumable: already-staged valid images are reused).
    staged, failures = [], []
    for entry in entries:
        space_id, key = int(entry["spaceId"]), entry["key"]
        if key.startswith("/") or ".." in key.split("/"):
            failures.append((space_id, key, "unsafe key"))
            continue
        dest = os.path.join(staging_dir, key)
        existing = read_image(dest)
        if existing:
            print(f"have  space {space_id}: {key}", flush=True)
            staged.append(entry)
            continue
        try:
            content_type, size = download(entry["downloadUrl"], dest)
        except Exception as error:  # report and keep going
            failures.append((space_id, key, str(error)))
            print(f"FAIL  space {space_id}: {error}", file=sys.stderr, flush=True)
            continue
        staged.append(entry)
        print(f"ok    space {space_id}: {key} ({content_type}, {size // 1024} KB)", flush=True)
        time.sleep(1.0)  # be polite to the image hosts

    # 2) Upload everything in one go, 3) record metadata for what was uploaded.
    if staged:
        upload_all(staging_dir)
        run_sql([
            "UPDATE spaces SET "
            f"image_key = {sql_literal(e['key'])}, image_alt = {sql_literal(e.get('alt'))}, "
            f"image_credit = {sql_literal(e.get('credit'))}, image_license = {sql_literal(e.get('license'))}, "
            f"image_source_url = {sql_literal(e.get('sourceUrl'))} WHERE id = {int(e['spaceId'])};"
            for e in staged
        ])
    print(f"\n{len(staged)} loaded, {len(failures)} failed", flush=True)
    for space_id, key, reason in failures:
        print(f"  failed space {space_id} ({key}): {reason}")
    sys.exit(1 if failures else 0)


if __name__ == "__main__":
    main()
