package dev.dvhstn.workfromhere.exceptions;

import dev.dvhstn.workfromhere.spaces.exception.SpaceTypeNotFoundException;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceAlreadyExistsException;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Maps exceptions to {@link ApiErrorResource} responses.
 *
 * <p>Spring MVC's own exceptions (malformed JSON, unsupported method or media type, unknown routes,
 * missing parameters, ...) are handled by {@link ResponseEntityExceptionHandler}, which picks the
 * correct status; {@link #handleExceptionInternal} then replaces its ProblemDetail body with our
 * {@link ApiErrorResource} shape.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // --- Application exceptions ---

    @ExceptionHandler(SpaceResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResource> handleSpaceResourceNotFound(SpaceResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(SpaceTypeNotFoundException.class)
    public ResponseEntity<ApiErrorResource> handleSpaceTypeNotFound(SpaceTypeNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(SpaceResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResource> handleSpaceResourceAlreadyExists(SpaceResourceAlreadyExistsException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // --- Request / persistence exceptions not covered by ResponseEntityExceptionHandler ---

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResource> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, invalidValueMessage(ex.getName()), request);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiErrorResource> handleInvalidSortProperty(PropertyReferenceException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Invalid sort property '" + ex.getPropertyName() + "'", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResource> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Request conflicts with existing data", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResource> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI()));
    }

    // --- Spring MVC exceptions: customised messages ---

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::fieldErrorMessage)
                .collect(Collectors.joining("; "));

        return handleExceptionInternal(ex, buildBody(status, message, pathOf(request)), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, buildBody(status, "Malformed or unreadable request body", pathOf(request)),
                headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, buildBody(status, "Resource not found", pathOf(request)),
                headers, status, request);
    }

    /**
     * Every response produced by {@link ResponseEntityExceptionHandler} passes through here. Any
     * body that is not already an {@link ApiErrorResource} (normally a ProblemDetail) is replaced,
     * keeping the status and headers (e.g. {@code Allow} on 405) chosen by Spring.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ApiErrorResource error = body instanceof ApiErrorResource apiError
                ? apiError
                : buildBody(statusCode, messageFrom(ex, body, statusCode), pathOf(request));

        if (statusCode.is5xxServerError()) {
            log.error("Request to {} failed: {}", error.getPath(), ex.getMessage(), ex);
        } else {
            log.warn("Request to {} rejected with {}: {}", error.getPath(), statusCode.value(), error.getMessage());
        }

        return super.handleExceptionInternal(ex, error, headers, statusCode, request);
    }

    // --- Helpers ---

    private ResponseEntity<ApiErrorResource> build(HttpStatus status, String message, HttpServletRequest request) {
        log.warn("Request to {} rejected with {}: {}", request.getRequestURI(), status.value(), message);
        return ResponseEntity.status(status).body(buildBody(status, message, request.getRequestURI()));
    }

    private static ApiErrorResource buildBody(HttpStatusCode status, String message, String path) {
        return ApiErrorResource.builder()
                .status(status.value())
                .path(path)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    private static String fieldErrorMessage(FieldError error) {
        // Binding failures (e.g. "yes" for a Boolean) carry a default message containing Java type
        // names, so replace them with a client-friendly one.
        return error.isBindingFailure() ? invalidValueMessage(error.getField()) : error.getDefaultMessage();
    }

    private static String invalidValueMessage(String name) {
        return "Invalid value for '" + name + "'";
    }

    private static String messageFrom(Exception ex, Object body, HttpStatusCode statusCode) {
        if (body instanceof ProblemDetail problemDetail && problemDetail.getDetail() != null) {
            return problemDetail.getDetail();
        }
        // ResponseEntityExceptionHandler passes a null body for ErrorResponse exceptions and lets
        // the superclass fill it in, so read the detail from the exception itself.
        if (ex instanceof ErrorResponse errorResponse && errorResponse.getBody().getDetail() != null) {
            return errorResponse.getBody().getDetail();
        }
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        return status != null ? status.getReasonPhrase() : "Request failed";
    }

    private static String pathOf(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return request.getDescription(false).replaceFirst("^uri=", "");
    }
}
