package com.github.ddth.recipes.checkpoint;

import com.github.ddth.dao.BaseDataJsonFieldBo;

import java.util.Date;

/**
 * Represent a checkpoint data.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class CheckpointBo extends BaseDataJsonFieldBo {

    /**
     * Helper method to create a new checkpoint instance.
     *
     * @param id
     * @return
     */
    public static CheckpointBo newInstance(String id) {
        return newInstance(id, null);
    }

    /**
     * Helper method to create a new checkpoint instance.
     *
     * @param id
     * @param timestamp
     * @return
     */
    public static CheckpointBo newInstance(String id, Date timestamp) {
        return newInstance(id, timestamp, null);
    }

    /**
     * Helper method to create a new checkpoint instance.
     *
     * @param id
     * @param timestamp
     * @param data
     * @return
     */
    public static CheckpointBo newInstance(String id, Date timestamp, String data) {
        CheckpointBo bo = new CheckpointBo();
        bo.setId(id).setTimestamp(timestamp).setData(data);
        return bo;
    }

    private static String FIELD_ID = "id";
    private static String FIELD_TIMESTAMP = "t";

    /**
     * Getter for #id field.
     *
     * @return
     */
    public String getId() {
        return getAttribute(FIELD_ID, String.class);
    }

    /**
     * Setter for #id field.
     *
     * @param id
     * @return
     */
    public CheckpointBo setId(String id) {
        setAttribute(FIELD_ID, id != null ? id.trim().toUpperCase() : null);
        return this;
    }

    /**
     * Getter for #timestamp field.
     *
     * @return
     */
    public Date getTimestamp() {
        return getAttribute(FIELD_TIMESTAMP, Date.class);
    }

    /**
     * Setter for #timestamp field.
     *
     * @param timestamp
     * @return
     */
    public CheckpointBo setTimestamp(Date timestamp) {
        setAttribute(FIELD_TIMESTAMP, timestamp);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckpointBo clone() {
        return (CheckpointBo) super.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckpointBo setData(String value) {
        super.setData(value != null ? value.trim() : "{}");
        return this;
    }
}
