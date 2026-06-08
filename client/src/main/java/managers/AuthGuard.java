package managers;

public class AuthGuard {
    /**
     * Проверяет, может ли пользователь выполнить команду в текущем состоянии сессии.
     * 
     * @param commandName имя команды
     * @return true, если команда разрешена, false - если доступ заблокирован
     */
    public static boolean canExecute(String commandName) {
        if (SessionManager.isAuthorized()) {
            if (commandName.equals("login") || commandName.equals("register")) {
                System.out.println("Вы уже авторизованы. Пожалуйста, используйте команду 'logout', чтобы выйти из текущего аккаунта.");
                return false;
            }
        } else {
            if (!commandName.equals("login") && !commandName.equals("register") && !commandName.equals("exit") && !commandName.equals("execute_script")) {
                System.out.println("Вы не авторизованы.");
                System.out.println("Используйте команду 'login' для входа.");
                System.out.println("Используйте команду 'register' для создания аккаунта.");
                return false;
            }
        }
        return true;
    }
}
