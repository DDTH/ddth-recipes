package com.github.ddth.recipes.qnd.checkpoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.github.ddth.dao.jdbc.IJdbcHelper;
import com.github.ddth.dao.jdbc.impl.DdthJdbcHelper;
import com.github.ddth.recipes.checkpoint.CheckpointBo;
import com.github.ddth.recipes.checkpoint.jdbc.JdbcCheckpointDao;

public class QndJdbcCheckpointDao {
    static Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "test", "test");
    }

    static DataSource createDataSource() throws SQLException {
        return new SingleConnectionDataSource(createConnection(), true);
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        DataSource ds = createDataSource();
        IJdbcHelper jdbcHelper = new DdthJdbcHelper().setDataSource(ds).init();
        JdbcCheckpointDao dao = new JdbcCheckpointDao();
        dao.setTableName("dr_checkpoint").setJdbcHelper(jdbcHelper).init();

        CheckpointBo cp = dao.getCheckpoint("id");
        System.out.println("Checkpoint[id]: " + cp);
        if (cp == null) {
            cp = CheckpointBo.newInstance("id");
        }
        System.out.println("Save checkpoint[id]: " + dao.setCheckpoint(cp));
    }
}
