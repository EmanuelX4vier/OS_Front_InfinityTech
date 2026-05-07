package os.infinitytech.os_front_infinitytech.config;

public class LoginResult {

    private final boolean success;
    private final String message;
    private final int statusCode;

    public LoginResult(boolean success, String message, int statusCode) {
        this.success = success;
        this.message = message;
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
