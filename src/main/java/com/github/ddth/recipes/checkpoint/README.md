# ddth-recipes: Checkpoint

Checkpoint recipe added since version `v0.1.0`.

Checkpoint recipe's built-in implementation uses `ddth-dao-jdbc` to store/retrieve data to/from DB table.
Add `ddth-dao-jdbc` to Maven dependency list like this:

```xml
<dependency>
	<groupId>com.github.ddth</groupId>
	<artifactId>ddth-dao-jdbc</artifactId>
	<version>${ddth-dao-version}</version>
	<type>pom</type>
</dependency>
```

## Design & Use-case

Assuming you are writing a routine to process log records stored in a db table, ordered by unique log-id.
Usually, your routine processes log records faster than they are written to the table.
Hence, when you have processed the last record, you would sleep for a while, wake up and continue from what you left off.
You may need to store last-processed log-id somewhere so that when the routine wakes up, it knows how/where to continue its task.

In this use-case, checkpoint recipe would be handy. A checkpoint record has 3 attributes:

- `id`: (`String`) checkpoint's unique id.
- `timestamp`: (`java.util.Date`) checkpoint's last save timestamp.
- `data`: (`json-string`) checkpoint's data.

For the use-case above, `checkpoint-id` is the routine's name/id,
`checkpoint-timestamp` marks routine's last-run-timestamp and
last-processed log-id can be stored in `checkpoint-data` field like this:

```json
{
    "last_log_record": 12345
}
```

## Usage

**Create an instance of `ICheckpointDao`**: as of `v0.1.0` there is one built-in implementation that can be used out-of-the-box `JdbcCheckpointDao`.

Sample code to create `JdbcCheckpointDao`:

```java
//firstly: a javax.sql.DataSource is needed.
DataSource ds = createDataSource(...);

//secondly: create a IJdbcHelper
IJdbcHelper jdbcHelper = new DdthJdbcHelper().setDataSource(ds).init();

//finally: create JdbcCheckpointDao instance
JdbcCheckpointDao dao = new JdbcCheckpointDao();
dao.setTableName("dr_checkpoint").setJdbcHelper(jdbcHelper);
//setting row mapper is optional. If not set, CheckpointRowMapper.INSTANCE is used
//setRowMapper(CheckpointRowMapper.INSTANCE);
dao.init();

//from here, dao can be used to read/write checkpoints
CheckpointBo cp = dao.getCheckpoint(id);
DaoResult result = dao.setCheckpoint(id, new Date(), data);

//CheckpointUtils can be helpful
CheckpointUtils.setCheckpointAttr(dao, id, "name", "Thanh Nguyen");
String name = CheckpointUtils.getCheckpointAttr(dao, id, "name", String.class).orElse(null);
```

**DB schema**: `JdbcCheckpointDao` stores checkpoints in a db table.
Sample of table schema for MySQL is in file [checkpoint.mysql.sql](../../../../../../../../dbschema/checkpoint/checkpoint.mysql.sql).

**ICheckpointDao's APIs**

- `CheckpointBo getCheckpoint(String)`: get an existing checkpoint by id.
- `DaoResult setCheckpoint(CheckpointBo)`: save a checkpoint.
- `DaoResult setCheckpoint(String, Date, String)`: save a checkpoint.
