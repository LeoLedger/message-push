package hw.topevery.dao;

import hw.topevery.config.Database;
import hw.topevery.enums.PushStatusEnum;
import hw.topevery.framework.db.CommandType;
import hw.topevery.framework.db.DbCommand;
import hw.topevery.framework.db.DbExecute;
import hw.topevery.framework.db.base.BaseEntityDaoImpl;
import hw.topevery.framework.db.entity.SqlQueryMate;
import hw.topevery.framework.db.entity.SqlUpdateMate;
import hw.topevery.framework.db.enums.ScriptConditionEnum;
import hw.topevery.framework.entity.DbCsp;
import hw.topevery.pojo.po.DgDataPushRecord;
import hw.topevery.pojo.query.DataPushRecordQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 数据推送记录 Dao实现类
 *
 * @author LeoLedger
 * @date 2023-12-23
 */
@Repository
public class DataPushRecordDaoImpl extends BaseEntityDaoImpl<DgDataPushRecord, Long> implements DataPushRecordDao {

    @Override
    public DbExecute getDbExecute() {
        return Database.dbDatabase;
    }

    @Override
    public <P extends DbCsp> List<DgDataPushRecord> getSearch(P p) {
        return null;
    }

    @Override
    public void setDeleteField(String s, DgDataPushRecord dgDataPushRecord) {
        dgDataPushRecord.setDbStatus(Boolean.TRUE);
    }

    @Override
    public void updateBefore(String userId, DgDataPushRecord val) {
        super.updateBefore(userId, val);
    }

    @Override
    public void insertBefore(String userId, DgDataPushRecord val) {
        val.setDbStatus(Boolean.FALSE);
        val.setCreateTime(LocalDateTime.now());
        super.insertBefore(userId, val);
    }

    public List<DgDataPushRecord> findList(DataPushRecordQuery param) {
        String table = "t_dg_data_push_record t";
        String column = "t.*";
        SqlQueryMate queryMate = new SqlQueryMate(table).setColumns(column);
        queryMate.setOrderBy("t.c_create_time DESC LIMIT " + (null == param.getLimit() ? 100 : param.getLimit()));
        queryMate
                .where(true,"t.c_db_status", ScriptConditionEnum.Eq, Boolean.FALSE)
                .whereForValDbNullCondition("t.c_canceled", ScriptConditionEnum.Eq, param.getCanceled())
                .whereForValDbNullCondition("t.c_create_time", ScriptConditionEnum.GreaterThanEq, param.getStartTime())
                .whereForValDbNullCondition("t.c_create_time", ScriptConditionEnum.LessThan, param.getEndTime())
                .whereForValDbNullCondition("t.c_fail_count", ScriptConditionEnum.LessThanEq, param.getMaxFailCount())
                .whereForValDbNullCondition("t.c_params_id", ScriptConditionEnum.Eq, param.getParamsId());
        if (null != param.getPushStatus()) {
            queryMate.where(true, "t.c_push_status", ScriptConditionEnum.Eq, param.getPushStatus().getValue());
        }
        if (null != param.getPushStatusArr() && param.getPushStatusArr().length > 0) {
            queryMate.where(true,"t.c_push_status", ScriptConditionEnum.In, Arrays.stream(param.getPushStatusArr()).map(PushStatusEnum::getValue).collect(Collectors.toList()));
        }
        List<DgDataPushRecord> result = new ArrayList<>();
        run(queryMate.getSql(), CommandType.Text, dbCmd -> {
            queryMate.getParameters().forEach(dbCmd::addParameter);
            dbCmd.executeToList(result, DgDataPushRecord.class);
        });
        return result;
    }

    @Override
    public void cancel(DataPushRecordQuery param) {
        String table = "t_dg_data_push_record t";
        SqlUpdateMate updateMate = new SqlUpdateMate(table);
        updateMate.setColumnValue(true, "t.c_canceled", Boolean.TRUE);
        updateMate
                .where(true,"t.c_db_status", ScriptConditionEnum.Eq, Boolean.FALSE)
                .whereForValDbNullCondition("t.c_canceled", ScriptConditionEnum.Eq, Boolean.FALSE)
                .whereForValDbNullCondition("t.c_push_status", ScriptConditionEnum.NotEq, PushStatusEnum.WAITING.getValue())
                .whereForValDbNullCondition("t.c_push_status", ScriptConditionEnum.NotEq, PushStatusEnum.PUSHING.getValue())
                .whereForValDbNullCondition("t.c_create_time", ScriptConditionEnum.GreaterThanEq, param.getStartTime())
                .whereForValDbNullCondition("t.c_create_time", ScriptConditionEnum.LessThan, param.getEndTime());

        run(updateMate.getSql(), CommandType.Text, dbCmd -> {
            updateMate.getParameters().forEach(dbCmd::addParameter);
            dbCmd.executeNonQuery();
        });

    }

    @Override
    public void delete(DataPushRecordQuery param) {
        String table = "t_dg_data_push_record t";
        SqlUpdateMate updateMate = new SqlUpdateMate(table);
        updateMate.setColumnValue(true, "t.c_db_status", Boolean.TRUE);
        updateMate
                .whereForValDbNullCondition("t.c_db_status", ScriptConditionEnum.Eq, Boolean.FALSE)
                .whereForValDbNullCondition("t.c_canceled", ScriptConditionEnum.Eq, Boolean.TRUE)
                .whereForValDbNullCondition("t.c_push_status", ScriptConditionEnum.NotEq, PushStatusEnum.WAITING.getValue())
                .whereForValDbNullCondition("t.c_push_status", ScriptConditionEnum.NotEq, PushStatusEnum.PUSHING.getValue())
                .whereForValDbNullCondition("t.c_create_time", ScriptConditionEnum.LessThan, param.getEndTime());

        run(updateMate.getSql(), CommandType.Text, dbCmd -> {
            updateMate.getParameters().forEach(dbCmd::addParameter);
            dbCmd.executeNonQuery();
        });

    }

    @Override
    public void updateData(DgDataPushRecord entity) {
        String table = "t_dg_data_push_record t";
        SqlUpdateMate updateMate = new SqlUpdateMate(table);
        updateMate.setColumnValue(true, "t.c_data", entity.getData());
        updateMate.setColumnValue(true, "t.c_create_time", LocalDateTime.now());
        updateMate.setColumnValue(true, "t.c_push_time", null);
        updateMate.setColumnValue(true, "t.c_push_status", PushStatusEnum.WAITING.getValue());
        updateMate.setColumnValue(true, "t.c_fail_count", 0);
        updateMate.setColumnValue(true, "t.c_canceled", Boolean.FALSE);
        updateMate.setColumnValue(true, "t.c_db_status", Boolean.FALSE);
        updateMate
                .where(true,"t.c_id", ScriptConditionEnum.Eq, entity.getId());

        run(updateMate.getSql(), CommandType.Text, dbCmd -> {
            updateMate.getParameters().forEach(dbCmd::addParameter);
            dbCmd.executeNonQuery();
        });
    }

    @Override
    public void updatePushStatus(Long id, PushStatusEnum pushStatus) {
        String table = "t_dg_data_push_record t";
        SqlUpdateMate updateMate = new SqlUpdateMate(table);
        updateMate.setColumnValue(true, "t.c_push_status", pushStatus.getValue());
        updateMate
                .whereForValDbNullCondition("t.c_id", ScriptConditionEnum.Eq, id);

        run(updateMate.getSql(), CommandType.Text, dbCmd -> {
            updateMate.getParameters().forEach(dbCmd::addParameter);
            dbCmd.executeNonQuery();
        });
    }

    @Override
    public void updatePushStatus(List<Long> ids, PushStatusEnum pushStatus) {
        if (null == ids || ids.isEmpty()) {
            return;
        }
        String table = "t_dg_data_push_record t";
        SqlUpdateMate updateMate = new SqlUpdateMate(table);
        updateMate.setColumnValue(true, "t.c_push_status", pushStatus.getValue());
        updateMate
                .where(true, "t.c_id", ScriptConditionEnum.In, ids);

        run(updateMate.getSql(), CommandType.Text, dbCmd -> {
            updateMate.getParameters().forEach(dbCmd::addParameter);
            dbCmd.executeNonQuery();
        });
    }

    @Override
    public DgDataPushRecord getEntity(Long dataId, Integer paramsId) {
        String table = "t_dg_data_push_record t";
        String column = "t.*";
        SqlQueryMate queryMate = new SqlQueryMate(table).setColumns(column);
        queryMate
                .where(true,"t.c_data_id", ScriptConditionEnum.Eq, dataId)
                .where(true,"t.c_params_id", ScriptConditionEnum.Eq, paramsId);
        AtomicReference<DgDataPushRecord> result = new AtomicReference<>();
        run(queryMate.getSql(), CommandType.Text, dbCmd -> {
            queryMate.getParameters().forEach(dbCmd::addParameter);
            result.set(dbCmd.executeToEntity(DgDataPushRecord.class));
        });
        return result.get();
    }

    @Override
    public void cleanData() {
        String sql = " DELETE FROM t_dg_data_push_record WHERE c_db_status = true AND c_create_time < DATE_SUB(NOW(), INTERVAL 1 MONTH)";
        run(sql, CommandType.Text, DbCommand::executeNonQuery);
    }
}
