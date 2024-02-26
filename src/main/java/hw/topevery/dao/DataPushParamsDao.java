package hw.topevery.dao;

import hw.topevery.framework.db.base.BaseEntityDao;
import hw.topevery.pojo.po.DgDataPushParams;

import java.util.List;

/**
 * 数据推送参数 Dao
 *
 * @author LeoLedger
 * @date 2023-12-23
 */
public interface DataPushParamsDao extends BaseEntityDao<DgDataPushParams, Integer> {
    List<DgDataPushParams> findAll();
}
