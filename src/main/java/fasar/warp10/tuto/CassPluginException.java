package fasar.warp10.tuto;

public class CassPluginException extends RuntimeException {
    public CassPluginException() {
    }

    public CassPluginException(String message) {
        super(message);
    }

    public CassPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public CassPluginException(Throwable cause) {
        super(cause);
    }

    public CassPluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
