package hw.topevery.service;

import hw.topevery.pojo.dto.DgDataPushRecordDTO;
import hw.topevery.pojo.vo.DgResultVO;

public interface PushService {

    DgResultVO<Long> save(DgDataPushRecordDTO<Long, String> dto);

    /**
     * 执行待推送任务
     *
     */
     void executeWaitPush();

    /**
     * 执行补发推送任务
     *
     */
    void executeWaitReissue();

    /**
     * 取消过期数据
     *
     */
    void cancelExpired();

    /**
     * 删除过期数据
     *
     */
    void deleteExpired();

    /**
     * 清理无用数据，默认每个月凌晨零点20
     *
     */
    void cleanData();
}
