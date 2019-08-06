package com.github.ddth.recipes.checkpoint;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.github.ddth.dao.utils.DaoResult;

/**
 * Checkpoint helper class.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.1
 */
public class CheckpointUtils {
    /**
     * Fetch checkpoint, return create a new instance if not found.
     *
     * @param dao
     * @param id
     * @param defaultTimestamp
     * @return
     */
    private static CheckpointBo getCheckpoint(ICheckpointDao dao, String id, Date defaultTimestamp) {
        CheckpointBo checkpoint = dao.getCheckpoint(id);
        return checkpoint != null ? checkpoint : CheckpointBo.newInstance(id, defaultTimestamp);
    }

    /**
     * Save a checkpoint.
     *
     * @param dao
     * @param checkpoint
     * @return
     */
    private static DaoResult saveCheckpoint(ICheckpointDao dao, CheckpointBo checkpoint) {
        return dao.setCheckpoint(checkpoint);
    }

    /**
     * Set & Save a checkpoint attribute.
     *
     * @param dao
     * @param checkpointId
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static DaoResult setCheckpointAttr(ICheckpointDao dao, String checkpointId, String fieldName,
            Object fieldValue) {
        CheckpointBo cp = getCheckpoint(dao, checkpointId, new Date());
        cp.setTimestamp(new Date()).setDataAttr(fieldName, fieldValue);
        return saveCheckpoint(dao, cp);
    }

    /**
     * Set & Save checkpoint attributes.
     *
     * @param dao
     * @param checkpointId
     * @param fieldNamesAndValues
     * @return
     */
    public static DaoResult setCheckpointAttrs(ICheckpointDao dao, String checkpointId,
            Map<String, Object> fieldNamesAndValues) {
        CheckpointBo cp = getCheckpoint(dao, checkpointId, new Date());
        cp.setTimestamp(new Date());
        for (Map.Entry<String, Object> entry : fieldNamesAndValues.entrySet()) {
            cp.setDataAttr(entry.getKey(), entry.getValue());
        }
        return saveCheckpoint(dao, cp);
    }

    /**
     * Get a checkpoint attribute.
     *
     * @param dao
     * @param checkpointId
     * @param fieldName
     * @param clazz
     * @return
     */
    public static <T> Optional<T> getCheckpointAttr(ICheckpointDao dao, String checkpointId, String fieldName,
            Class<T> clazz) {
        return getCheckpoint(dao, checkpointId, new Date()).getDataAttrOptional(fieldName, clazz);
    }

    /**
     * Check checkpoint's timestamp.
     *
     * @param dao
     * @param checkpointId
     * @param defaultValue
     * @return
     */
    public static Date getCheckpointTimestamp(ICheckpointDao dao, String checkpointId, Date defaultValue) {
        return getCheckpoint(dao, checkpointId, defaultValue).getTimestamp();
    }

    /**
     * Update checkpoint's timestamp.
     *
     * @param dao
     * @param checkpointId
     * @param timestamp
     * @return
     * @deprecated since v1.0.0, use {@link #setCheckpointTimestamp(ICheckpointDao, String, Date)}
     */
    public static DaoResult updateCheckpointTimestamp(ICheckpointDao dao, String checkpointId, Date timestamp) {
        CheckpointBo cp = getCheckpoint(dao, checkpointId, timestamp);
        cp.setTimestamp(timestamp);
        return saveCheckpoint(dao, cp);
    }

    /**
     * Set checkpoint's timestamp.
     *
     * @param dao
     * @param checkpointId
     * @param timestamp
     * @return
     * @since 1.0.0
     */
    public static DaoResult setCheckpointTimestamp(ICheckpointDao dao, String checkpointId, Date timestamp) {
        CheckpointBo cp = getCheckpoint(dao, checkpointId, timestamp);
        cp.setTimestamp(timestamp);
        return saveCheckpoint(dao, cp);
    }
}
