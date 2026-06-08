package managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import exceptions.InvalidValueFieldException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import structs.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final Logger logger = LoggerFactory.getLogger(DBManager.class);
    private static volatile DBManager instance;
    private HikariDataSource dataSource;
    private static final String DB_URL = "jdbc:postgresql://localhost:";
    private static final String DB_SHEM = "studs";
    private static final String DB_USER = "s502610";
    private static final HikariConfig config = new HikariConfig();

    private DBManager() {
    }

    public void setDbPassword(String dbPassword) {
        config.setPassword(dbPassword);
    }

    public void init() {
        SshTunnelManager.getInstance().ensureTunnel();
        String dbUrl = DB_URL + SshTunnelManager.getInstance().getLocalPort() + "/" + DB_SHEM;
        config.setJdbcUrl(dbUrl);
        config.setUsername(DB_USER);

        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(5000);

        this.dataSource = new HikariDataSource(config);
        createTablesIfNotExist();
    }

    public static DBManager getInstance() {
        DBManager localInstance = instance;
        if (localInstance == null) {
            synchronized (DBManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DBManager();
                }
            }
        }
        return localInstance;
    }

    /**
     * Предоставляет соединение из пула HikariCP.
     * Используется внутри AuthService для работы с таблицей пользователей.
     */
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    /**
     * Проверяет и создаёт схему таблиц, если сервер запускается на чистой базе данных.
     */
    private void createTablesIfNotExist() {
        String createUserTable = "CREATE TABLE IF NOT EXISTS app_user (id SERIAL PRIMARY KEY, login VARCHAR(100) UNIQUE NOT NULL, password_hash VARCHAR(128) NOT NULL);";
        String createSequence = "CREATE SEQUENCE IF NOT EXISTS music_band_id_seq START WITH 1 INCREMENT BY 1;";
        String createCollectionTable =
                "CREATE TABLE IF NOT EXISTS music_band (" +
                        "    id INT PRIMARY KEY DEFAULT nextval('music_band_id_seq'), name TEXT NOT NULL CHECK (name <> '')," +
                        "    coord_x INT NOT NULL CHECK (coord_x <= 617), coord_y DOUBLE PRECISION NOT NULL CHECK (coord_y > -370)," +
                        "    creation_date TIMESTAMP NOT NULL, number_of_participants INT NOT NULL CHECK (number_of_participants > 0)," +
                        "    genre VARCHAR(50), studio_address TEXT NOT NULL, owner_id INT NOT NULL," +
                        "    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES app_user(id) ON DELETE CASCADE" +
                        ");";
        String createHistoryTable =
                "CREATE TABLE IF NOT EXISTS user_history (" +
                        "    id SERIAL PRIMARY KEY," +
                        "    owner_id INT NOT NULL," +
                        "    command_name VARCHAR(100) NOT NULL," +
                        "    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "    CONSTRAINT fk_history_owner FOREIGN KEY (owner_id) REFERENCES app_user(id) ON DELETE CASCADE" +
                        ");";

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.addBatch(createUserTable);
            stmt.addBatch(createSequence);
            stmt.addBatch(createCollectionTable);
            stmt.addBatch(createHistoryTable);
            stmt.executeBatch();
            logger.info("Проверка и развертывание структуры таблиц в СУБД успешно завершены.");
        } catch (SQLException e) {
            throw new RuntimeException("Критическая ошибка при создании таблиц", e);
        }
    }

    /**
     * Выгружает всю коллекцию из БД при старте сервера.
     */
    public List<MusicBand> loadAllBands() {
        SshTunnelManager.getInstance().ensureTunnel();
        List<MusicBand> list = new ArrayList<>();
        String sql = "SELECT id, name, coord_x, coord_y, creation_date, number_of_participants, genre, studio_address, owner_id FROM music_band;";

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                try {
                    Coordinates coords = Coordinates.builder()
                            .x(rs.getInt("coord_x"))
                            .y(rs.getDouble("coord_y"))
                            .build();

                    Studio studio = Studio.builder()
                            .address(rs.getString("studio_address"))
                            .build();

                    String genreStr = rs.getString("genre");
                    MusicGenre genre = genreStr != null ? MusicGenre.valueOf(genreStr) : null;

                    MusicBand band = MusicBand.builder()
                            .id(rs.getInt("id"))
                            .name(rs.getString("name"))
                            .coordinates(coords)
                            .creationDate(new java.util.Date(rs.getTimestamp("creation_date").getTime()))
                            .numberOfParticipants(rs.getInt("number_of_participants"))
                            .genre(genre)
                            .studio(studio)
                            .ownerId(rs.getInt("owner_id"))
                            .build();

                    list.add(band);
                } catch (InvalidValueFieldException e) {
                    logger.error("Запись в БД нарушает правила валидации модели: {}", e.getMessage());
                }
            }
        } catch (SQLException e) {
            logger.error("Исключение при выгрузке данных из СУБД: {}", e.getMessage());
        }
        return list;
    }

    /**
     * Добавление группы в СУБД. Возвращает собранный объект с ID и датой из базы.
     */
    public MusicBand insertBand(MusicBand clientBand, int ownerId) {
        SshTunnelManager.getInstance().ensureTunnel();
        String sql = "INSERT INTO music_band (name, coord_x, coord_y, creation_date, number_of_participants, genre, studio_address, owner_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, creation_date;";
        java.util.Date now = new java.util.Date();

        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clientBand.getName());
            pstmt.setInt(2, clientBand.getCoordinates().getX());
            pstmt.setDouble(3, clientBand.getCoordinates().getY());
            pstmt.setTimestamp(4, new Timestamp(now.getTime()));
            pstmt.setInt(5, clientBand.getNumberOfParticipants());
            if (clientBand.getGenre() != null) pstmt.setString(6, clientBand.getGenre().name()); else pstmt.setNull(6, Types.VARCHAR);
            pstmt.setString(7, clientBand.getStudio().getAddress());
            pstmt.setInt(8, ownerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return MusicBand.builder()
                            .id(rs.getInt("id"))
                            .name(clientBand.getName())
                            .coordinates(clientBand.getCoordinates())
                            .creationDate(new java.util.Date(rs.getTimestamp("creation_date").getTime()))
                            .numberOfParticipants(clientBand.getNumberOfParticipants())
                            .genre(clientBand.getGenre())
                            .studio(clientBand.getStudio())
                            .ownerId(ownerId)
                            .build();
                }
            }
        } catch (SQLException | InvalidValueFieldException e) {
            logger.error("Не удалось выполнить INSERT операцию: {}", e.getMessage());
        }
        return null;
    }

    public boolean updateBandInDb(int id, MusicBand band, int ownerId) {
        SshTunnelManager.getInstance().ensureTunnel();
        String sql = "UPDATE music_band SET name = ?, coord_x = ?, coord_y = ?, " +
                "number_of_participants = ?, genre = ?, studio_address = ? " +
                "WHERE id = ? AND owner_id = ?;";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, band.getName());
            pstmt.setInt(2, band.getCoordinates().getX());
            pstmt.setDouble(3, band.getCoordinates().getY());
            pstmt.setInt(4, band.getNumberOfParticipants());
            if (band.getGenre() != null) pstmt.setString(5, band.getGenre().name()); else pstmt.setNull(5, Types.VARCHAR);
            pstmt.setString(6, band.getStudio().getAddress());
            pstmt.setInt(7, id);
            pstmt.setInt(8, ownerId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при выполнении UPDATE для id {}: {}", id, e.getMessage());
            return false;
        }
    }

    /**
     * Удаление группы
     */
    public boolean deleteBand(int id, int ownerId) {
        SshTunnelManager.getInstance().ensureTunnel();
        String sql = "DELETE FROM music_band WHERE id = ? AND owner_id = ?;";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, ownerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении записи id {}: {}", id, e.getMessage());
            return false;
        }
    }

    /**
     * Корректно закрывает пул соединений СУБД при выключении сервера.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Пул соединений HikariCP закрыт.");
        }
    }
}