package com.github.ddth.recipes.checkpoint.jdbc;

import java.util.Date;

import com.github.ddth.dao.BoId;
import com.github.ddth.dao.jdbc.GenericBoJdbcDao;
import com.github.ddth.dao.utils.DaoResult;
import com.github.ddth.recipes.checkpoint.CheckpointBo;
import com.github.ddth.recipes.checkpoint.ICheckpointDao;

/**
 * JDBC implementation of {@link ICheckpointDao}.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class JdbcCheckpointDao extends GenericBoJdbcDao<CheckpointBo> implements ICheckpointDao {
    /**
     * {@inheritDoc}
     */
    @Override
    public JdbcCheckpointDao init() {
        if (getRowMapper() == null) {
            setRowMapper(CheckpointRowMapper.INSTANCE);
        }
        super.init();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckpointBo getCheckpoint(String id) {
        return get(new BoId(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DaoResult setCheckpoint(String id, Date timestamp, String data) {
        return setCheckpoint(CheckpointBo.newInstance(id, timestamp, data));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DaoResult setCheckpoint(CheckpointBo cp) {
        return updateOrCreate(cp);
    }
}
