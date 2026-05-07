package os.infinitytech.os_front_infinitytech.config;


import os.infinitytech.os_front_infinitytech.service.AuthService;

public class Session {

    private static AuthService authService;

    public static void setAuthService(AuthService service) {
        authService = service;
    }

    public static AuthService getAuthService() {
        return authService;
    }

    public static boolean isLogged() {
        return authService != null && authService.isLogged();
    }

    public static void logout() {
        if (authService != null) {
            authService.logout();
        }
        authService = null;
    }
}
