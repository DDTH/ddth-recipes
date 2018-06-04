package com.github.ddth.recipes.checkpoint.jdbc;

import com.github.ddth.dao.jdbc.annotations.AnnotatedGenericRowMapper;
import com.github.ddth.dao.jdbc.annotations.ColumnAttribute;
import com.github.ddth.dao.jdbc.annotations.PrimaryKeyColumns;
import com.github.ddth.dao.jdbc.annotations.UpdateColumns;
import com.github.ddth.recipes.checkpoint.CheckpointBo;

import java.util.Date;

/**
 * Map db-row to {@link CheckpointBo}.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
@PrimaryKeyColumns(columns = { "cp_id" })
@UpdateColumns(columns = { "cp_timestamp", "cp_data" })
@ColumnAttribute(column = "cp_id", attr = "id", attrClass = String.class)
@ColumnAttribute(column = "cp_timestamp", attr = "timestamp", attrClass = Date.class)
@ColumnAttribute(column = "cp_data", attr = "data", attrClass = String.class)
public class CheckpointRowMapper extends AnnotatedGenericRowMapper<CheckpointBo> {
    public final static CheckpointRowMapper INSTANCE = new CheckpointRowMapper();
}
