package com.github.ddth.recipes.qnd.checkpoint;

import com.github.ddth.dao.BoId;
import com.github.ddth.dao.jdbc.IJdbcHelper;
import com.github.ddth.dao.jdbc.impl.DdthJdbcHelper;
import com.github.ddth.recipes.checkpoint.jdbc.JdbcCheckpointDao;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class QndJdbcCheckpointDao {
    static Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "test", "test");
    }

    static DataSource createDataSource() throws SQLException {
        return new SingleConnectionDataSource(createConnection(), true);
    }

    public static void main(String[] args) throws Exception {
        DataSource ds = createDataSource();
        IJdbcHelper jdbcHelper = new DdthJdbcHelper().setDataSource(ds).init();
        JdbcCheckpointDao dao = new JdbcCheckpointDao();
        dao.setTableName("dr_checkpoint").setJdbcHelper(jdbcHelper).init();

        System.out.println(dao.getCheckpoint("id"));
    }
}
