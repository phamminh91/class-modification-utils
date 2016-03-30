package mp.classmodifier.exception;

public class ClassModificationException extends Exception {
    private static final long serialVersionUID = 2442636491222624137L;

    public ClassModificationException(Throwable cause) {
        super(cause);
    }

    public ClassModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
