package com.github.ddth.recipes.test.checkpoint;

import com.github.ddth.dao.jdbc.IJdbcHelper;
import com.github.ddth.dao.jdbc.impl.DdthJdbcHelper;
import com.github.ddth.recipes.checkpoint.ICheckpointDao;
import com.github.ddth.recipes.checkpoint.jdbc.JdbcCheckpointDao;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

public class TestFunctionalJdbcCheckpointDao extends BaseFunctionalCheckpointTest {
    static class MyCheckpointDao extends JdbcCheckpointDao {
        public MyCheckpointDao init() {
            super.init();
            // cleanup data
            execute("DELETE FROM " + getTableName());
            return this;
        }
    }

    @SuppressWarnings("resource")
    @Override
    protected ICheckpointDao initCheckpointDao() throws Exception {
        if (System.getProperty("enableTestsMySql") == null && System.getProperty("enableTestsMySQL") == null
                && System.getProperty("enableTestsMYSQL") == null) {
            return null;
        }
        String jdbcUrl = System.getProperty("jdbc.url", "jdbc:mysql://localhost:3306/test");
        String jdbcUser = System.getProperty("jdbc.user", "test");
        String jdbcPassword = System.getProperty("jdbc.password", "test");
        String tableName = System.getProperty("jdbc.table", "dr_checkpoint");

        Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
        DataSource ds = new SingleConnectionDataSource(conn, true);
        IJdbcHelper jdbcHelper = new DdthJdbcHelper().setDataSource(ds).init();
        JdbcCheckpointDao dao = new MyCheckpointDao();
        dao.setTableName(tableName).setJdbcHelper(jdbcHelper).init();
        return dao;
    }
}
