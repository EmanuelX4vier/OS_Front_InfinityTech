package os.infinitytech.os_front_infinitytech.auth;

public class AuthStorage {

    private static AuthStorage instance;

    private String accessToken;

    private AuthStorage() {}

    public static AuthStorage getInstance() {

        if (instance == null) {
            instance = new AuthStorage();
        }

        return instance;
    }

    public void saveAccessToken(String token) {
        this.accessToken = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void clear() {
        accessToken = null;
    }

    public boolean isAuthenticated() {
        return accessToken != null && !accessToken.isBlank();
    }
}