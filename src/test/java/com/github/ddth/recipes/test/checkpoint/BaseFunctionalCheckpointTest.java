package com.github.ddth.recipes.test.checkpoint;

import com.github.ddth.commons.utils.ReflectionUtils;
import com.github.ddth.dao.utils.DaoResult;
import com.github.ddth.recipes.checkpoint.CheckpointBo;
import com.github.ddth.recipes.checkpoint.ICheckpointDao;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.lang.reflect.Method;
import java.util.Date;

public abstract class BaseFunctionalCheckpointTest extends TestCase {

    protected ICheckpointDao checkpointDao;

    protected abstract ICheckpointDao initCheckpointDao() throws Exception;

    protected void destroyCheckpointDao(ICheckpointDao checkpointDao) throws Exception {
        Method m = ReflectionUtils.getMethod("destroy", checkpointDao.getClass());
        if (m != null) {
            m.invoke(checkpointDao);
        }
    }

    @Before
    public void setUp() throws Exception {
        checkpointDao = initCheckpointDao();
    }

    @After
    public void tearDown() throws Exception {
        if (checkpointDao != null) {
            destroyCheckpointDao(checkpointDao);
        }
    }

    @org.junit.Test
    public void testGetNotExists() throws Exception {
        if (checkpointDao == null) {
            return;
        }
        CheckpointBo cp = checkpointDao.getCheckpoint("not-found");
        Assert.assertNull(cp);
    }

    @org.junit.Test
    public void testSetAndGet1() throws Exception {
        if (checkpointDao == null) {
            return;
        }

        CheckpointBo cpOrg = CheckpointBo.newInstance("id", new Date(), "{}");
        DaoResult daoResult = checkpointDao.setCheckpoint(cpOrg);
        Assert.assertEquals(daoResult.getStatus(), DaoResult.DaoOperationStatus.SUCCESSFUL);

        CheckpointBo cpGet = checkpointDao.getCheckpoint("id");
        Assert.assertNotNull(cpGet);

        Assert.assertEquals(cpOrg, cpGet);
    }

    @org.junit.Test
    public void testSetAndGet2() throws Exception {
        if (checkpointDao == null) {
            return;
        }

        String id = "id";
        Date timestamp = new Date();
        String data = "{}";
        DaoResult daoResult = checkpointDao.setCheckpoint(id, timestamp, data);
        Assert.assertEquals(daoResult.getStatus(), DaoResult.DaoOperationStatus.SUCCESSFUL);

        CheckpointBo cp = checkpointDao.getCheckpoint("id");
        Assert.assertNotNull(cp);

        Assert.assertEquals(id, cp.getId());
        Assert.assertEquals(timestamp, cp.getTimestamp());
        Assert.assertEquals(data, cp.getData());
    }

    @org.junit.Test
    public void testSetUpdateAndGet1() throws Exception {
        if (checkpointDao == null) {
            return;
        }

        CheckpointBo cpOrg = CheckpointBo.newInstance("id", new Date(), "{}");
        {
            DaoResult daoResult = checkpointDao.setCheckpoint(cpOrg);
            Assert.assertEquals(daoResult.getStatus(), DaoResult.DaoOperationStatus.SUCCESSFUL);
        }

        cpOrg.setTimestamp(new Date()).setDataAttr("progress", 0.75);
        {
            DaoResult daoResult = checkpointDao.setCheckpoint(cpOrg);
            Assert.assertEquals(daoResult.getStatus(), DaoResult.DaoOperationStatus.SUCCESSFUL);
        }

        CheckpointBo cpGet = checkpointDao.getCheckpoint("id");
        Assert.assertNotNull(cpGet);

        Assert.assertEquals(cpOrg, cpGet);
    }

    @org.junit.Test
    public void testSetUpdateAndGet2() throws Exception {
        if (checkpointDao == null) {
            return;
        }

        CheckpointBo cpOrg = CheckpointBo.newInstance("id", new Date(), "{}");
        {
            DaoResult daoResult = checkpointDao.setCheckpoint(cpOrg);
            Assert.assertEquals(daoResult.getStatus(), DaoResult.DaoOperationStatus.SUCCESSFUL);
        }

        String id = "id";
        Date timestamp = new Date();
        String data = "{\"ok\":false}";
        {
            DaoResult daoResult = checkpointDao.setCheckpoint(id, timestamp, data);
            Assert.assertEquals(daoResult.getStatus(), DaoResult.DaoOperationStatus.SUCCESSFUL);
        }

        CheckpointBo cpGet = checkpointDao.getCheckpoint("id");
        Assert.assertNotNull(cpGet);

        Assert.assertEquals(id, cpGet.getId());
        Assert.assertEquals(timestamp, cpGet.getTimestamp());
        Assert.assertEquals(data, cpGet.getData());
    }
}
