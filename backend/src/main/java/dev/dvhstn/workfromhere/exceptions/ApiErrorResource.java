package dev.dvhstn.workfromhere.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApiErrorResource {
    private int status;
    private String error;
    private String message;
    private String path;
    private Instant timestamp;
}
