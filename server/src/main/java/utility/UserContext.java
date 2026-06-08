package utility;

public final class UserContext {
    private static final ThreadLocal<Integer> currentUserId = new ThreadLocal<>();

    private UserContext() {}

    public static void setUserId(int userId) {
        currentUserId.set(userId);
    }

    public static Integer getUserId() {
        return currentUserId.get();
    }

    public static void clear() {
        currentUserId.remove();
    }
}