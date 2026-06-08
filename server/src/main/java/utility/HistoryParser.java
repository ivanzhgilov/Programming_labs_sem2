package utility;

import managers.DBManager;
import managers.SshTunnelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Синхронизирует историю команд пользователя напрямую с базой данных PostgreSQL.
 * Данные гарантированно сохраняются при перезагрузке сервера.
 */
public final class HistoryParser {
    private static final Logger logger = LoggerFactory.getLogger(HistoryParser.class);

    private HistoryParser() {
    }

    /**
     * Записывает вызванную команду пользователя в СУБД.
     */
    public static void addCommandForUser(int ownerId, String commandName) {
        SshTunnelManager.getInstance().ensureTunnel();
        String sql = "INSERT INTO user_history (owner_id, command_name) VALUES (?, ?);";

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            pstmt.setString(2, commandName);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Ошибка при сохранении команды '{}' в историю СУБД для пользователя {}: {}",
                    commandName, ownerId, e.getMessage());
        }
    }

    public static List<String> getCommandsListForUser(int ownerId) {
        SshTunnelManager.getInstance().ensureTunnel();
        List<String> history = new ArrayList<>();

        String sql = "SELECT command_name FROM user_history WHERE owner_id = ? ORDER BY id DESC LIMIT 8;";

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    history.add(rs.getString("command_name"));
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении истории команд из СУБД для пользователя {}: {}", ownerId, e.getMessage());
        }

        java.util.Collections.reverse(history);
        return history;
    }
}