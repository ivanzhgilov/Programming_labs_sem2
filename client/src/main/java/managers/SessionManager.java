package managers;

public class SessionManager {
    private static String currentLogin = null;
    private static String currentPassword = null;

    public static String getCurrentLogin() {
        return currentLogin;
    }

    public static String getCurrentPassword() {
        return currentPassword;
    }

    public static void setSession(String login, String password) {
        currentLogin = login;
        currentPassword = password;
    }

    public static void clearSession() {
        currentLogin = null;
        currentPassword = null;
    }

    public static boolean isAuthorized() {
        return currentLogin != null && currentPassword != null;
    }
}
