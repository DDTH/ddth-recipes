package com.github.ddth.recipes.checkpoint.jdbc;

import java.util.Date;

import com.github.ddth.dao.jdbc.annotations.AnnotatedGenericRowMapper;
import com.github.ddth.dao.jdbc.annotations.ColumnAttribute;
import com.github.ddth.dao.jdbc.annotations.PrimaryKeyColumns;
import com.github.ddth.dao.jdbc.annotations.UpdateColumns;
import com.github.ddth.recipes.checkpoint.CheckpointBo;

/**
 * Map db-row to {@link CheckpointBo}.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
//map to CheckpointBo.setId(String)
@ColumnAttribute(column = CheckpointRowMapper.COL_ID, attr = "id", attrClass = String.class)

//map to CheckpointBo.setTimestamp(Date)
@ColumnAttribute(column = CheckpointRowMapper.COL_TIMESTAMP, attr = "timestamp", attrClass = Date.class)

//map to CheckpointBo.setData(String)
@ColumnAttribute(column = CheckpointRowMapper.COL_DATA, attr = "data", attrClass = String.class)

@PrimaryKeyColumns({ CheckpointRowMapper.COL_ID })
@UpdateColumns({ CheckpointRowMapper.COL_TIMESTAMP, CheckpointRowMapper.COL_DATA })
public class CheckpointRowMapper extends AnnotatedGenericRowMapper<CheckpointBo> {
    /**
     * Name of database table to store checkpoint id.
     */
    public final static String COL_ID = "cp_id";

    /**
     * Name of database table to store checkpoint timestamp.
     */
    public final static String COL_TIMESTAMP = "cp_timestamp";

    /**
     * Name of database table to store checkpoint data.
     */
    public final static String COL_DATA = "cp_data";

    public final static CheckpointRowMapper INSTANCE = new CheckpointRowMapper();
}
