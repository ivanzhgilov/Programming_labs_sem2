package managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.PasswordEncryptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static volatile AuthService instance;
    private final ConcurrentHashMap<String, String> userCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> userIdMap = new ConcurrentHashMap<>();

    private AuthService() {
        loadUsersFromDb();
    }

    public static AuthService getInstance() {
        AuthService localInstance = instance;
        if (localInstance == null) {
            synchronized (AuthService.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new AuthService();
                }
            }
        }
        return localInstance;
    }

    private void loadUsersFromDb() {
        String sql = "SELECT id, login, password_hash FROM app_user;";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                userCache.put(rs.getString("login"), rs.getString("password_hash"));
                userIdMap.put(rs.getString("login"), rs.getInt("id"));
            }
            logger.info("Загружено аккаунтов: {}", userCache.size());
        } catch (SQLException e) {
            logger.error("Ошибка при инициализации AuthService (загрузка пользователей): {}", e.getMessage());
        }
    }

    /**
     * Проверка подлинности пользователя (используется в UdpRequestProcessor)
     */
    public boolean authenticate(String login, String password) {
        if (login == null || password == null) return false;

        String cachedHash = userCache.get(login);
        if (cachedHash == null) return false;

        return cachedHash.equals(PasswordEncryptor.hashPassword(password));
    }

    /**
     * Регистрация нового пользователя в СУБД с занесением в кэш.
     */
    public boolean register(String login, String password) {
        if (login == null || password == null) return false;
        if (userCache.containsKey(login)) return false;

        SshTunnelManager.getInstance().ensureTunnel();
        String sql = "INSERT INTO app_user (login, password_hash) VALUES (?, ?) RETURNING id;";
        String hash = PasswordEncryptor.hashPassword(password);

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setString(2, hash);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    userCache.put(login, hash);
                    userIdMap.put(login, id);
                    logger.info("Зарегистрирован новый пользователь: {}, id: {}", login, id);
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при записи нового пользователя в БД: {}", e.getMessage());
        }
        return false;
    }

    public Integer getValidatedUserId(String login, String password) {
        if (login == null || password == null) return null;

        String cachedHash = userCache.get(login);
        if (cachedHash == null) return null;

        String incomingHash = PasswordEncryptor.hashPassword(password);
        if (cachedHash.equals(incomingHash)) {
            return userIdMap.get(login);
        }

        return null;
    }
}