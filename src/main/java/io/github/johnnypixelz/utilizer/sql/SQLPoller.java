package io.github.johnnypixelz.utilizer.sql;

import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import io.github.johnnypixelz.utilizer.tasks.Tasks;
import org.bukkit.scheduler.BukkitTask;

import java.io.Closeable;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class SQLPoller implements Closeable {
    private final SQLClient sqlClient;
    private final String table;
    private final StatefulEventEmitter<String> eventEmitter;
    private final UUID pollerId;

    private long lastId = -1;
    private BukkitTask pollTask;
    private BukkitTask cleanOldMessagesTask;

    public SQLPoller(DatabaseCredentials credentials, String table) {
        this(credentials, table, 20);
    }

    public SQLPoller(DatabaseCredentials credentials, String table, int pollIntervalInTicks) {
        this.sqlClient = new SQLClient(credentials);
        this.table = table;
        this.eventEmitter = new StatefulEventEmitter<>();
        this.pollerId = UUID.randomUUID();

        initialize(pollIntervalInTicks);
    }

    public void initialize() {
        initialize(20);
    }

    public void initialize(int pollIntervalInTicks) {
        sqlClient.execute("""
                CREATE TABLE IF NOT EXISTS %s (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    time BIGINT NOT NULL,
                    message VARCHAR(255) NOT NULL,
                    poller_id VARCHAR(255) NOT NULL
                )
                """.formatted(table));

        final Optional<Long> latestId = sqlClient.executeQuery("""
                SELECT COALESCE(MAX(ID), -1) AS latest_id
                FROM %s
                """.formatted(table), preparedStatement -> {
        }, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getLong("latest_id");
            } else {
                return -1L;
            }
        });

        this.lastId = latestId.orElse(-1L);

        this.pollTask = Tasks.async().timer(this::poll, pollIntervalInTicks);
        this.cleanOldMessagesTask = Tasks.async().timer(this::cleanOldMessages, 20 * 30);
    }

    public StatefulEventEmitter<String> getEventEmitter() {
        return eventEmitter;
    }

    public void sendMessage(String message) {
        sqlClient.execute("""
                INSERT INTO %s (time, message, poller_id)
                VALUES (?, ?, ?)
                """.formatted(table), preparedStatement -> {
            preparedStatement.setLong(1, Instant.now().toEpochMilli());
            preparedStatement.setString(2, message);
            preparedStatement.setString(3, pollerId.toString());
        });
    }

    private void poll() {
        sqlClient.executeQuery("""
                SELECT id, message
                    FROM %s
                    WHERE id > ?
                    AND (UNIX_TIMESTAMP(NOW()) * 1000) - time < 60000
                    AND poller_id != '?'
                """.formatted(table), preparedStatement -> {
            preparedStatement.setLong(1, lastId);
            preparedStatement.setString(2, pollerId.toString());
        }, resultSet -> {
            long id = resultSet.getLong("id");
            String message = resultSet.getString("message");

            this.lastId = Math.max(this.lastId, id);
            eventEmitter.emit(message);
            return null;
        });
    }

    private void cleanOldMessages() {
        sqlClient.execute("""
                DELETE FROM %s
                WHERE (UNIX_TIMESTAMP(NOW()) * 1000) - time > 60000
                """.formatted(table));
    }

    @Override
    public void close() {
        pollTask.cancel();
        cleanOldMessagesTask.cancel();
        sqlClient.close();
    }

}
