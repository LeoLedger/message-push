package hw.topevery.logic;

import hw.topevery.config.MessagePushConfig;
import hw.topevery.dao.DataPushRecordDao;
import hw.topevery.enums.PushStatusEnum;
import hw.topevery.framework.db.base.BaseEntityDao;
import hw.topevery.framework.db.base.BaseEntityLogic;
import hw.topevery.pojo.po.DgDataPushRecord;
import hw.topevery.pojo.query.DataPushRecordQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据推送记录 Logic
 *
 * @author LeoLedger
 * @date 2023-12-23
 */
@Service
public class DataPushRecordLogic implements BaseEntityLogic<DgDataPushRecord, Long> {

    @Autowired
    private DataPushRecordDao dataPushRecordDao;

    @Override
    public BaseEntityDao<DgDataPushRecord, Long> getDao() {
        return dataPushRecordDao;
    }

    public List<DgDataPushRecord> findList(DataPushRecordQuery param) {
        return dataPushRecordDao.findList(param);
    }

    /**
     * 获取待推送数据
     */
    public List<DgDataPushRecord> findListByWait() {
        DataPushRecordQuery param = new DataPushRecordQuery();
        LocalDateTime now = LocalDateTime.now();
//        param.setStartTime(now.minusDays(MessagePushConfig.CANCEL_EXPIRATION_DATE));
        param.setStartTime(now.minusDays(100));
        param.setEndTime(now);
        param.setLimit(MessagePushConfig.WAIT_LIMIT);
        param.setCanceled(Boolean.FALSE);
        param.setPushStatus(PushStatusEnum.WAITING);
        param.setMaxFailCount(0);
        return dataPushRecordDao.findList(param);
    }

    /**
     * 获取待补发数据
     */
    public List<DgDataPushRecord> findListByReissue() {
        DataPushRecordQuery param = new DataPushRecordQuery();
        LocalDateTime now = LocalDateTime.now();
        param.setStartTime(now.minusDays(MessagePushConfig.CANCEL_EXPIRATION_DATE));
        param.setEndTime(now);
        param.setLimit(MessagePushConfig.WAIT_LIMIT);
        param.setCanceled(Boolean.FALSE);
        param.setPushStatus(PushStatusEnum.FAILED);
        param.setMaxFailCount(MessagePushConfig.REISSUE_COUNT);
        return dataPushRecordDao.findList(param);
    }

    /**
     * 取消过期数据
     */
    public void cancelExpired() {
        DataPushRecordQuery param = new DataPushRecordQuery();
        param.setEndTime(LocalDateTime.now().minusDays(MessagePushConfig.CANCEL_EXPIRATION_DATE));
        dataPushRecordDao.cancel(param);
    }

    /**
     * 逻辑删除过期数据
     */
    public void deleteExpired() {
        DataPushRecordQuery param = new DataPushRecordQuery();
        param.setEndTime(LocalDateTime.now().minusDays(MessagePushConfig.DELETE_EXPIRATION_DATE));
        dataPushRecordDao.delete(param);
    }

    /**
     * 更新数据
     */
    public void updateData(DgDataPushRecord entity) {
        dataPushRecordDao.updateData(entity);
    }

    /**
     * 更新推送状态
     */
    public void updatePushStatus(Long id, PushStatusEnum pushStatus) {
        dataPushRecordDao.updatePushStatus(id, pushStatus);
    }

    /**
     * 更新推送状态
     */
    public void updatePushStatus(List<Long> ids, PushStatusEnum pushStatus) {
        dataPushRecordDao.updatePushStatus(ids, pushStatus);
    }

    public DgDataPushRecord getEntity(Long dataId, Integer paramsId) {
        return dataPushRecordDao.getEntity(dataId, paramsId);
    }

    /**
     * 物理删除无用数据
     */
    public void cleanData() {
        dataPushRecordDao.cleanData();
    }
}
