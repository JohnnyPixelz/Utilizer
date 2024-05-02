package io.github.johnnypixelz.utilizer.sql;

import io.github.johnnypixelz.utilizer.Scheduler;
import io.github.johnnypixelz.utilizer.event.StatefulEventEmitter;
import org.bukkit.scheduler.BukkitTask;
import org.jooq.*;
import org.jooq.impl.SQLDataType;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.jooq.impl.DSL.*;

public class OldSQLMessenger {
    private static final Field<Long> ID = field("id", SQLDataType.BIGINT.identity(true));
    private static final Field<Long> TIME = field("time", SQLDataType.BIGINT.notNull());
    private static final Field<String> MESSAGE = field("message", SQLDataType.VARCHAR.notNull());
    private static final Field<String> MESSENGER_ID = field("messengerId", SQLDataType.VARCHAR.notNull());

    public static OldSQLMessenger setup(DatabaseCredentials credentials, String table) {
        return setup(credentials, table, 20);
    }

    public static OldSQLMessenger setup(DatabaseCredentials credentials, String table, long tickInterval) {
        final String dataSource = credentials.getDataSource();

        SQLDialect dialect;

        try {
            dialect = SQLDialect.valueOf(dataSource.toUpperCase());
        } catch (IllegalArgumentException ex) {
            dialect = SQLDialect.MARIADB;
        }

        return setup(credentials, table, dialect, tickInterval);
    }

    public static OldSQLMessenger setup(DatabaseCredentials credentials, String table, SQLDialect dialect) {
        return setup(credentials, table, dialect, 20);
    }

    public static OldSQLMessenger setup(DatabaseCredentials credentials, String table, SQLDialect dialect, long tickInterval) {
        OldSQLMessenger messenger = new OldSQLMessenger(credentials, table, dialect);

        messenger.sql.execute(dslContext -> {
            dslContext.createTableIfNotExists(table(table))
                    .columns(ID, TIME, MESSAGE, MESSENGER_ID)
                    .primaryKey(ID)
                    .execute();
        });

        final Optional<Long> latestId = messenger.sql.query(dslContext -> {
            final Result<Record1<Long>> fetch = dslContext.select(max(ID))
                    .from(table)
                    .fetch();

            return fetch.stream()
                    .filter(record -> record.value1() != null)
                    .mapToLong(Record1::value1)
                    .max()
                    .orElse(-1L);
        });

        latestId.ifPresent(id -> messenger.lastId = id);

        messenger.pollTask = Scheduler.asyncTimer(messenger::poll, tickInterval);
        messenger.cleanOldMessagesTask = Scheduler.asyncTimer(messenger::cleanOldMessages, 20 * 30);

        return messenger;
    }

    private final OldSQL sql;
    private final String table;
    private final StatefulEventEmitter<String> eventEmitter;
    private final UUID messengerId;

    private long lastId = -1;
    private BukkitTask pollTask;
    private BukkitTask cleanOldMessagesTask;

    private OldSQLMessenger(DatabaseCredentials credentials, String table, SQLDialect dialect) {
        this.sql = OldSQL.connect(credentials, dialect);
        this.table = table;
        this.eventEmitter = new StatefulEventEmitter<>();
        this.messengerId = UUID.randomUUID();
    }

    public StatefulEventEmitter<String> getEventEmitter() {
        return eventEmitter;
    }

    public void sendMessage(String message) {
        sql.execute(dslContext -> {
            dslContext.insertInto(table(table))
                    .columns(TIME, MESSAGE, MESSENGER_ID)
                    .values(Instant.now().toEpochMilli(), message, messengerId.toString())
                    .execute();
        });
    }

    private void poll() {
        sql.query(dslContext ->
                        dslContext.select(ID, MESSAGE, MESSENGER_ID)
                                .from(table)
                                .where(ID.gt(inline(lastId)))
                                .and(
                                        val(Instant.now().toEpochMilli())
                                                .minus(TIME)
                                                .lt(inline(60000L))
                                )
                                .fetch()
                )
                .ifPresent(record3s -> {
                    for (Record3<Long, String, String> record : record3s) {
                        this.lastId = Math.max(this.lastId, record.value1());
                        if (record.value3().equals(messengerId.toString())) continue;
                        eventEmitter.emit(record.value2());
                    }
                });
    }

    private void cleanOldMessages() {
        sql.execute(dslContext -> {
            dslContext.deleteFrom(table(table))
                    .where(
                            val(Instant.now().toEpochMilli())
                                    .minus(TIME)
                                    .gt(inline(60000L))
                    )
                    .execute();
        });
    }

    public void close() {
        if (sql.getHikari().isClosed()) return;

        pollTask.cancel();
        cleanOldMessagesTask.cancel();
        sql.close();
    }

}
