package hw.topevery.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.topevery.sdk.produce.ProduceMessageUtil;
import hw.topevery.enums.DataTypeEnum;
import hw.topevery.enums.PushStatusEnum;
import hw.topevery.enums.SourceTypeEnum;
import hw.topevery.enums.TargetTypeEnum;
import hw.topevery.exception.ParamsException;
import hw.topevery.framework.SystemConst;
import hw.topevery.framework.util.LogUtil;
import hw.topevery.logic.DataPushParamsLogic;
import hw.topevery.logic.DataPushRecordLogic;
import hw.topevery.pojo.dto.DgDataPushRecordDTO;
import hw.topevery.pojo.po.DgDataPushParams;
import hw.topevery.pojo.po.DgDataPushRecord;
import hw.topevery.pojo.vo.DgResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LeoLedger
 * @description: 生产者基础服务接口
 * @date 2023-12-23 16:19
 */
@Component
@EnableScheduling
public class PushServiceImpl implements PushService {
    @Autowired
    private ProduceMessageUtil produceMessageUtil;
    @Autowired
    private DataPushRecordLogic dataPushRecordLogic;
    @Autowired
    private DataPushParamsLogic dataPushParamsLogic;

    private List<DgDataPushParams> pushParams;

    public List<DgDataPushParams> getPushParams() {
        if (null == pushParams) {
            pushParams = dataPushParamsLogic.findAll();
        }
        return pushParams;
    }

    public DgDataPushParams getParam(Integer id) {
        List<DgDataPushParams> params = this.getPushParams();
        for (DgDataPushParams obj: params) {
            if (obj.getId().equals(id)) {
                return obj;
            }
        }
        return null;
    }

    public DgDataPushParams getParam(TargetTypeEnum targetType, SourceTypeEnum sourceType, DataTypeEnum dataType) {
        List<DgDataPushParams> params = this.getPushParams();
        for (DgDataPushParams obj: params) {
            if (obj.getTargetType().equals(targetType.getValue())
                    && obj.getSourceType().equals(sourceType.getValue())
                    && obj.getDataType().equals(dataType.getValue())) {
                return obj;
            }
        }
        return null;
    }

    private boolean send(DgDataPushRecord entity)  {
        boolean success = false;
        DgDataPushParams params = this.getParam(entity.getParamsId());
        if (null == params || params.getDisable()) {
            entity.setCanceled(Boolean.TRUE);
        } else {
            JSONObject jsonObject = JSONObject.parseObject(entity.getData());
            jsonObject.put("sysId", params.getSystemId());
            success = produceMessageUtil.sendMessage(params.getSystemCode(), params.getTopic(), jsonObject.toJSONString());
        }
        return success;
    }

    @Override
    public DgResultVO<Long> save(DgDataPushRecordDTO<Long, String> dto) {
        DgDataPushParams param = this.getParam(dto.getTargetType(), dto.getSourceType(), dto.getDataType());
        if (null == param) {
            throw new ParamsException();
        }
        DgResultVO<Long> result = new DgResultVO<>();
        Map<Long, String> data = dto.getData();
        if (null == data || data.isEmpty()) {
            return result;
        }
        data.forEach((k, v) -> {
            try {
                DgDataPushRecord entity = dataPushRecordLogic.getEntity(k, param.getId());
                if (null != entity && StrUtil.equals(v, entity.getData())) {
                    result.setExistsNum(result.getExistsNum() + 1);
                    result.getExistsList().add(k);
                    return;
                }
                Long id = this.save(k, v, param, entity);
                if (null == id) {
                    result.setInsertNum(result.getInsertNum() + 1);
                    result.getInsertList().add(k);
                } else {
                    result.setUpdateNum(result.getUpdateNum() + 1);
                    result.getUpdateList().add(k);
                }
            } catch (Exception e) {
                result.setFailNum(result.getFailNum() + 1);
                result.getFailList().add(k);
                LogUtil.error(String.format("\"%s\":\"%s\"", k, v), e);
            }
        });
        return result;
    }

    private Long save(Long dataId, String data, DgDataPushParams param, DgDataPushRecord entity) {
        if (null == entity) {
            entity = new DgDataPushRecord();
        }
        entity.setDataId(dataId);
        entity.setCreateTime(LocalDateTime.now());
        entity.setParamsId(param.getId());
        entity.setPushTime(null);
        entity.setPushStatus(PushStatusEnum.WAITING.getValue());
        entity.setFailCount(0);
        entity.setData(data);
        entity.setCanceled(Boolean.FALSE);
        entity.setDbStatus(Boolean.FALSE);
        if (null == entity.getId()) {
            dataPushRecordLogic.insert(SystemConst.UUID_EMPTY_STRING, entity);
        } else {
            dataPushRecordLogic.updateData(entity);
        }
        return entity.getId();
    }

    /**
     * 执行待推送任务
     *
     */
    @Scheduled(cron = "${hw.data-push-config.waitCron:0/30 * * * * ? }")
    public void executeWaitPush() {
        List<DgDataPushRecord> list = dataPushRecordLogic.findListByWait();
        dataPushRecordLogic.updatePushStatus(list.stream().map(DgDataPushRecord::getId).collect(Collectors.toList()), PushStatusEnum.PUSHING);
        list.forEach(item -> {
            boolean success = false;
            try {
                success = send(item);
            } catch (Exception e) {
                LogUtil.error(item.getData(), e);
            } finally {
                item.setPushTime(LocalDateTime.now());
                if (success) {
                    item.setPushStatus(PushStatusEnum.FINISHED.getValue());
                } else {
                    item.setPushStatus(PushStatusEnum.FAILED.getValue());
                    item.setFailCount(null == item.getFailCount() ? 1 : item.getFailCount() + 1);
                }
                dataPushRecordLogic.update(null, item);
            }
        });
    }

    /**
     * 执行补发推送任务
     *
     */
    @Scheduled(cron = "${hw.data-push-config.reissueCron:0/30 * * * * ? }")
    public void executeWaitReissue() {
        List<DgDataPushRecord> list = dataPushRecordLogic.findListByReissue();
        dataPushRecordLogic.updatePushStatus(list.stream().map(DgDataPushRecord::getId).collect(Collectors.toList()), PushStatusEnum.PUSHING);
        list.forEach(item -> {
            boolean success = false;
            try {
                success = send(item);
            } catch (Exception e) {
                LogUtil.error(item.getData(), e);
            } finally {
                item.setPushTime(LocalDateTime.now());
                if (success) {
                    item.setPushStatus(PushStatusEnum.FINISHED.getValue());
                } else {
                    item.setPushStatus(PushStatusEnum.FAILED.getValue());
                    item.setFailCount(null == item.getFailCount() ? 1 : item.getFailCount() + 1);
                }
                dataPushRecordLogic.update(null, item);
            }
        });
    }

    /**
     * 取消过期数据
     *
     */
    @Scheduled(cron = "${hw.data-push-config.cancelExpiredCron:0 30 2 * * ? }")
    public void cancelExpired() {
        dataPushRecordLogic.cancelExpired();
    }

    /**
     * 删除过期数据
     *
     */
    @Scheduled(cron = "${hw.data-push-config.deleteExpiredCron:0 30 3 * * ? }")
    public void deleteExpired() {
        dataPushRecordLogic.deleteExpired();
    }

    /**
     * 清理无用数据，，默认每个月凌晨零点20
     *
     */
    @Scheduled(cron = "${hw.data-push-config.cleanDataCron:0 20 0 1 * ? }")
    public void cleanData() {
        dataPushRecordLogic.cleanData();
    }
}
