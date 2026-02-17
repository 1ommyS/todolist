package com.indistudia.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public class TxManager {
    private DbConfig dbConfig;

    public TxManager(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public <T> T inTx(Function<Connection, T> func) {
        try (var con = DbManager.openConnection(dbConfig)) {
            boolean oldAuto = con.getAutoCommit();

            con.setAutoCommit(false);

            try {
                T result = func.apply(con);
                con.commit();
                return result;
            } catch (Exception ex) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    e.addSuppressed(ex);
                }
            } finally {
                con.setAutoCommit(oldAuto);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
