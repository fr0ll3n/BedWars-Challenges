/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package dev.bwchallenges.storage;

import dev.bwchallenges.Challenge;
import dev.bwchallenges.ChallengesPlugin;
import dev.bwchallenges.PlayerProfile;
import dev.bwchallenges.storage.Storage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.bukkit.plugin.Plugin;

public final class MySqlStorage
implements Storage {
    private final ChallengesPlugin plugin;
    private final String url;
    private final Properties props;
    private final int poolSize;
    private final String playersTable;
    private final String statsTable;
    private final BlockingQueue<Connection> pool;
    private volatile boolean closed;

    public MySqlStorage(ChallengesPlugin challengesPlugin) throws Exception {
        this.plugin = challengesPlugin;
        String string = challengesPlugin.getConfig().getString("database.host", "localhost");
        int n = challengesPlugin.getConfig().getInt("database.port", 3306);
        String string2 = challengesPlugin.getConfig().getString("database.name", "bedwars");
        String string3 = challengesPlugin.getConfig().getString("database.username", "root");
        String string4 = challengesPlugin.getConfig().getString("database.password", "password");
        boolean bl = challengesPlugin.getConfig().getBoolean("database.ssl", false);
        String string5 = challengesPlugin.getConfig().getString("database.params", "characterEncoding=utf8&useUnicode=true&allowPublicKeyRetrieval=true");
        String string6 = challengesPlugin.getConfig().getString("database.table-prefix", "bwchallenges_");
        this.playersTable = string6 + "players";
        this.statsTable = string6 + "stats";
        this.poolSize = Math.max(2, Math.min(12, challengesPlugin.getConfig().getInt("database.pool-size", 4)));
        this.pool = new ArrayBlockingQueue<Connection>(this.poolSize);
        this.url = "jdbc:mysql://" + string + ":" + n + "/" + string2 + "?useSSL=" + bl + "&" + string5;
        this.props = new Properties();
        this.props.setProperty("user", string3);
        this.props.setProperty("password", string4);
        this.props.setProperty("autoReconnect", "true");
        this.loadDriver();
        this.init();
    }

    private void loadDriver() throws Exception {
        ClassNotFoundException classNotFoundException = null;
        String[] stringArray = new String[]{"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver", "org.mariadb.jdbc.Driver"};
        for (String string : stringArray) {
            try {
                Class.forName(string);
                return;
            }
            catch (ClassNotFoundException classNotFoundException2) {
                classNotFoundException = classNotFoundException2;
            }
        }
        throw new IllegalStateException("No MySQL JDBC driver found. Paper 1.17+ includes one, or add mysql-connector to the server.", classNotFoundException);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void init() throws SQLException {
        for (int i = 0; i < this.poolSize; ++i) {
            this.pool.add(this.newConnection());
        }
        Connection connection = this.take();
        try (Statement statement = connection.createStatement();){
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.playersTable + " (uuid VARCHAR(36) NOT NULL PRIMARY KEY,name VARCHAR(16) NULL,active VARCHAR(64) NULL,completed TEXT NOT NULL,unlocked TEXT NOT NULL,updated_at BIGINT NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.statsTable + " (uuid VARCHAR(36) NOT NULL,challenge VARCHAR(64) NOT NULL,completions INT NOT NULL DEFAULT 0,fails INT NOT NULL DEFAULT 0,last_ms BIGINT NOT NULL DEFAULT 0,PRIMARY KEY (uuid, challenge)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            try {
                statement.executeUpdate("ALTER TABLE " + this.playersTable + " ADD COLUMN wins INT NOT NULL DEFAULT 0");
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
            try {
                statement.executeUpdate("ALTER TABLE " + this.playersTable + " ADD COLUMN claimed TEXT NULL");
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
            try {
                statement.executeUpdate("ALTER TABLE " + this.playersTable + " ADD COLUMN permissions TEXT NULL");
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
        finally {
            this.give(connection);
        }
    }

    private Connection newConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(this.url, this.props);
        connection.setAutoCommit(true);
        return connection;
    }

    private Connection take() throws SQLException {
        if (this.closed) {
            throw new SQLException("MySQL pool closed");
        }
        try {
            Connection connection = this.pool.poll(3L, TimeUnit.SECONDS);
            if (connection == null) {
                return this.newConnection();
            }
            if (connection.isClosed() || !this.valid(connection)) {
                try {
                    connection.close();
                }
                catch (SQLException sQLException) {
                    // empty catch block
                }
                return this.newConnection();
            }
            return connection;
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted waiting for MySQL connection", interruptedException);
        }
    }

    private boolean valid(Connection connection) {
        try {
            return connection.isValid(2);
        }
        catch (Throwable throwable) {
            return true;
        }
    }

    private void give(Connection connection) {
        if (connection == null) {
            return;
        }
        if (this.closed || !this.pool.offer(connection)) {
            try {
                connection.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PlayerProfile load(UUID uUID) {
        PlayerProfile playerProfile;
        block27: {
            playerProfile = new PlayerProfile(uUID);
            Connection connection = null;
            try {
                connection = this.take();
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT active, completed, unlocked, name, wins, claimed, permissions FROM " + this.playersTable + " WHERE uuid=?");){
                    preparedStatement.setString(1, uUID.toString());
                    try (ResultSet resultSet = preparedStatement.executeQuery();){
                        if (resultSet.next()) {
                            Challenge challenge = Challenge.byId(resultSet.getString("active"));
                            Set<Challenge> set = MySqlStorage.parseList(resultSet.getString("completed"));
                            Set<Challenge> set2 = MySqlStorage.parseList(resultSet.getString("unlocked"));
                            playerProfile.setName(resultSet.getString("name"));
                            int n = 0;
                            try {
                                n = resultSet.getInt("wins");
                            }
                            catch (SQLException sQLException) {
                                // empty catch block
                            }
                            Set<Challenge> set3 = EnumSet.noneOf(Challenge.class);
                            LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
                            try {
                                set3 = MySqlStorage.parseList(resultSet.getString("claimed"));
                            }
                            catch (SQLException sQLException) {
                                // empty catch block
                            }
                            try {
                                linkedHashSet = MySqlStorage.parseStrings(resultSet.getString("permissions"));
                            }
                            catch (SQLException sQLException) {
                                // empty catch block
                            }
                            playerProfile.applyLoaded(challenge, set, set2, n, set3, linkedHashSet);
                            break block27;
                        }
                        playerProfile.applyLoaded(null, EnumSet.noneOf(Challenge.class), EnumSet.noneOf(Challenge.class));
                    }
                }
            }
            catch (SQLException sQLException) {
                this.plugin.logSafe("MySQL load failed for " + String.valueOf(uUID), sQLException);
                playerProfile.applyLoaded(null, EnumSet.noneOf(Challenge.class), EnumSet.noneOf(Challenge.class));
            }
            finally {
                this.give(connection);
            }
        }
        return playerProfile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void save(PlayerProfile playerProfile) {
        Connection connection = null;
        try {
            connection = this.take();
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + this.playersTable + " (uuid, name, active, completed, unlocked, updated_at, wins, claimed, permissions) VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=VALUES(name), active=VALUES(active), completed=VALUES(completed), unlocked=VALUES(unlocked), updated_at=VALUES(updated_at), wins=VALUES(wins), claimed=VALUES(claimed), permissions=VALUES(permissions)");){
                preparedStatement.setString(1, playerProfile.uuid().toString());
                preparedStatement.setString(2, playerProfile.name());
                preparedStatement.setString(3, playerProfile.active() == null ? null : playerProfile.active().id());
                preparedStatement.setString(4, MySqlStorage.join(playerProfile.completed()));
                preparedStatement.setString(5, MySqlStorage.join(playerProfile.unlocked()));
                preparedStatement.setLong(6, System.currentTimeMillis());
                preparedStatement.setInt(7, playerProfile.wins());
                preparedStatement.setString(8, MySqlStorage.join(playerProfile.claimed()));
                preparedStatement.setString(9, MySqlStorage.joinStrings(playerProfile.grantedPermissions()));
                preparedStatement.executeUpdate();
            }
            playerProfile.markClean();
        }
        catch (SQLException sQLException) {
            this.plugin.logSafe("MySQL save failed for " + String.valueOf(playerProfile.uuid()), sQLException);
        }
        finally {
            this.give(connection);
        }
    }

    @Override
    public void saveAsync(final PlayerProfile playerProfile) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, new Runnable(){

            @Override
            public void run() {
                MySqlStorage.this.save(playerProfile);
            }
        });
    }

    public void recordResult(final UUID uUID, final Challenge challenge, final boolean bl) {
        if (challenge == null) {
            return;
        }
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                Connection connection = null;
                try {
                    connection = MySqlStorage.this.take();
                    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + MySqlStorage.this.statsTable + " (uuid, challenge, completions, fails, last_ms) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE completions=completions+?, fails=fails+?, last_ms=VALUES(last_ms)");){
                        preparedStatement.setString(1, uUID.toString());
                        preparedStatement.setString(2, challenge.id());
                        preparedStatement.setInt(3, bl ? 1 : 0);
                        preparedStatement.setInt(4, bl ? 0 : 1);
                        preparedStatement.setLong(5, System.currentTimeMillis());
                        preparedStatement.setInt(6, bl ? 1 : 0);
                        preparedStatement.setInt(7, bl ? 0 : 1);
                        preparedStatement.executeUpdate();
                    }
                }
                catch (SQLException sQLException) {
                    MySqlStorage.this.plugin.logSafe("MySQL stats write failed", sQLException);
                }
                finally {
                    MySqlStorage.this.give(connection);
                }
            }
        });
    }

    @Override
    public void close() {
        Connection connection;
        this.closed = true;
        while ((connection = (Connection)this.pool.poll()) != null) {
            try {
                connection.close();
            }
            catch (SQLException sQLException) {}
        }
    }

    @Override
    public String kind() {
        return "MySQL";
    }

    private static String join(Set<Challenge> set) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Challenge challenge : set) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(challenge.id());
        }
        return stringBuilder.toString();
    }

    private static String joinStrings(Set<String> set) {
        StringBuilder stringBuilder = new StringBuilder();
        if (set == null) {
            return "";
        }
        for (String string : set) {
            if (string == null || string.trim().isEmpty()) continue;
            if (stringBuilder.length() > 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(string.trim());
        }
        return stringBuilder.toString();
    }

    private static Set<String> parseStrings(String string) {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>();
        if (string == null || string.isEmpty()) {
            return linkedHashSet;
        }
        for (String string2 : string.split(",")) {
            if (string2 == null || string2.trim().isEmpty()) continue;
            linkedHashSet.add(string2.trim());
        }
        return linkedHashSet;
    }

    private static Set<Challenge> parseList(String string) {
        String[] stringArray;
        EnumSet<Challenge> enumSet = EnumSet.noneOf(Challenge.class);
        if (string == null || string.isEmpty()) {
            return enumSet;
        }
        for (String string2 : stringArray = string.split(",")) {
            Challenge challenge = Challenge.byId(string2.trim());
            if (challenge == null) continue;
            enumSet.add(challenge);
        }
        return enumSet;
    }
}

