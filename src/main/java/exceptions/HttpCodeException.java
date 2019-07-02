package exceptions;

/**
 * Исключение с http-кодом.
 * Может быть транслировано в ответ клиенту на основе кода и сообщения.
 */
public final class HttpCodeException extends RuntimeException {

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
