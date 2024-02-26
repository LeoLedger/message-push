package hw.topevery.dao;

import hw.topevery.enums.PushStatusEnum;
import hw.topevery.framework.db.base.BaseEntityDao;
import hw.topevery.pojo.po.DgDataPushRecord;
import hw.topevery.pojo.query.DataPushRecordQuery;

import java.util.List;

/**
 * 数据推送记录 Dao
 *
 * @author LeoLedger
 * @date 2023-12-23
 */
public interface DataPushRecordDao extends BaseEntityDao<DgDataPushRecord, Long> {

    List<DgDataPushRecord> findList(DataPushRecordQuery param);
    void cancel(DataPushRecordQuery param);
    void delete(DataPushRecordQuery param);
    void updateData(DgDataPushRecord entity);
    DgDataPushRecord getEntity(Long dataId, Integer paramsId);
    void cleanData();
    void updatePushStatus(Long id, PushStatusEnum pushStatus);
    void updatePushStatus(List<Long> ids, PushStatusEnum pushStatus);
}
