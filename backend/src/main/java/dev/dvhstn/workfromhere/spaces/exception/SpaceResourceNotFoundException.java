package dev.dvhstn.workfromhere.spaces.exception;

public class SpaceResourceNotFoundException extends RuntimeException {

    public SpaceResourceNotFoundException(Long id, String message) {
        super(message);
    }

    public SpaceResourceNotFoundException(String message) {
        super(message);
    }
}
