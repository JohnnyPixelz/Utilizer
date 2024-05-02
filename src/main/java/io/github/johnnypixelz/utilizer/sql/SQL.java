package io.github.johnnypixelz.utilizer.sql;

public class SQL {

    public static SQLClient newClient(DatabaseCredentials databaseCredentials) {
        return new SQLClient(databaseCredentials);
    }

    public static SQLPoller newPoller(DatabaseCredentials databaseCredentials, String table) {
        return new SQLPoller(databaseCredentials, table);
    }

    public static SQLPoller newPoller(DatabaseCredentials databaseCredentials, String table, int pollIntervalInTicks) {
        return new SQLPoller(databaseCredentials, table, pollIntervalInTicks);
    }

}
