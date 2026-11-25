# Local development data and image CDN

Everything here is for running the app locally. None of it is used in production.

| File | What it is |
|---|---|
| `seed-spaces.sql` | 48 real places to work from in Belfast and Dublin, each with its source URL. Wi-Fi values are placeholders (`ask-staff` or NULL), never real passwords. |
| `docker-compose.cdn.yaml` | A simulated image CDN: SeaweedFS as an S3-compatible origin (bucket `wfh-images`, :9000), with imgproxy in front for resizing, AVIF/WebP and cache headers (:8090). |
| `s3-config.json` | SeaweedFS identities: anonymous read, plus a local admin key. |
| `space-images.json` | Photo manifest: one Wikimedia Commons photo per seeded space, with download URL, alt text, credit, licence and source page. |
| `ingest_images.py` | Downloads each photo in a manifest, checks the file really is an image, uploads it to the bucket by key, and writes the `image_*` columns. |

## Run it

```sh
# 1. Database + API (any Postgres works; DB_* point the app at it)
DB_NAME=... DB_USERNAME=... DB_PASSWORD=... DB_URL=jdbc:postgresql://localhost:5432/... mvn spring-boot:run

# 2. Seed spaces (after Flyway has run)
docker exec -i <postgres-container> psql -U <user> -d <db> < backend/local/seed-spaces.sql

# 3. Image CDN
docker compose -f backend/local/docker-compose.cdn.yaml up -d

# 4. Photos (manifest: JSON array of {spaceId, key, downloadUrl, alt, credit, license, sourceUrl})
python3 -I backend/local/ingest_images.py backend/local/space-images.json /tmp/space-images
```

Image keys look like `spaces/{id}/{slug}.jpg`. The API returns only the key. The frontend builds imgproxy URLs from `VITE_CDN_BASE_URL`.

Photo licences are CC0, CC BY and CC BY-SA from Wikimedia Commons. The app shows each photo's credit and licence on the detail page, which those licences require.
