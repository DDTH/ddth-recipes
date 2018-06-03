package com.github.ddth.recipes.checkpoint;

import com.github.ddth.dao.IGenericBoDao;
import com.github.ddth.dao.utils.DaoResult;

import java.util.Date;

/**
 * API to access checkpoints.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface ICheckpointDao extends IGenericBoDao<CheckpointBo> {

    /**
     * Get an existing checkpoint by id.
     *
     * @param id
     * @return
     */
    CheckpointBo getCheckpoint(String id);

    /**
     * Save a checkpoint.
     *
     * @param cp
     * @return
     */
    DaoResult setCheckpoint(CheckpointBo cp);

    /**
     * Save a checkpoint.
     *
     * @param id
     * @param timestamp
     * @param data
     * @return
     */
    DaoResult setCheckpoint(String id, Date timestamp, String data);

}
