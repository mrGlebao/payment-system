package exceptions;

public class HttpCodeException extends RuntimeException {

    private final int httpCode;

    public HttpCodeException(int httpCode,
                             String message) {
        super(message);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
