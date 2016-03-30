package mp.classmodifier.exception;

public class InstanceModificationException extends Exception {
    private static final long serialVersionUID = -2131880982516633069L;

    public InstanceModificationException(String message) {
        super(message);
    }

    public InstanceModificationException(Throwable cause) {
        super(cause);
    }

    public InstanceModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
