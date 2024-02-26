package hw.topevery.dao;

import hw.topevery.config.Database;
import hw.topevery.framework.db.CommandType;
import hw.topevery.framework.db.DaoCacheService;
import hw.topevery.framework.db.DbExecute;
import hw.topevery.framework.db.base.BaseEntityDaoImpl;
import hw.topevery.framework.entity.DbCsp;
import hw.topevery.framework.entity.KeyValue;
import hw.topevery.pojo.po.DgDataPushParams;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.List;

/**
 * 数据推送参数 Dao实现类
 *
 * @author LeoLedger
 * @date 2023-12-23
 */
@Repository
public class DataPushParamsDaoImpl extends BaseEntityDaoImpl<DgDataPushParams, Integer> implements DataPushParamsDao, DaoCacheService<DgDataPushParams> {

    @Override
    public DbExecute getDbExecute() {
        return Database.dbDatabase;
    }

    @Override
    public <P extends DbCsp> List<DgDataPushParams> getSearch(P p) {
        return null;
    }

    @Override
    public void setDeleteField(String s, DgDataPushParams dgDataPushParams) {
    }

    @Override
    public void updateBefore(String userId, DgDataPushParams val) {
    }

    @Override
    public void insertBefore(String userId, DgDataPushParams val) {
    }

    @Override
    public String getKey() {
        return DataPushParamsDaoImpl.class.getName();
    }

    @Override
    public KeyValue<CommandType> getSql() {
        return new KeyValue<>(MessageFormat.format("SELECT * FROM {0}t_dg_data_push_params;", this.getDbPrefix()), CommandType.Text);
    }

    @Override
    public int expire() {
        return DaoCacheService.super.expire();
    }

    @Override
    protected DaoCacheService<DgDataPushParams> getCacheService() {
        return this;
    }

    @Override
    public List<DgDataPushParams> findAll() {
        return this.getCacheData();
    }
}
