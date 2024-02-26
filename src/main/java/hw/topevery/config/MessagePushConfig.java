package hw.topevery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hw.message-push-config")
public class MessagePushConfig {
    /** 待推送数据cron表达式 **/
    public static String WAIT_CRON;
    /** 每次推送的数据量 **/
    public static Integer WAIT_LIMIT;
    /** 待补发数据cron表达式 **/
    public static String REISSUE_CRON;
    /** 每次补发的数据量 **/
    public static Integer REISSUE_LIMIT;
    /** 补发最大次数 **/
    public static Integer REISSUE_COUNT;
    /** 数据推送有效期（天） **/
    public static Long CANCEL_EXPIRATION_DATE;
    /** 数据保留有效期（天） **/
    public static Long DELETE_EXPIRATION_DATE;
    @Value("${waitCron:20 0/5 * * * ? }")
    public void setWaitCron(String waitCron) {
        MessagePushConfig.WAIT_CRON = waitCron;
    }

    @Value("${waitLimit:50}")
    public void setWaitLimit(Integer waitLimit) {
        MessagePushConfig.WAIT_LIMIT = waitLimit;
    }

    @Value("${reissueCron:40 0/5 * * * ? }")
    public void setReissueCron(String reissueCron) {
        MessagePushConfig.REISSUE_CRON = reissueCron;
    }

    @Value("${reissueLimit:50}")
    public void setReissueLimit(Integer reissueLimit) {
        MessagePushConfig.REISSUE_LIMIT = reissueLimit;
    }

    @Value("${reissueCount:5}")
    public void setReissueCount(Integer reissueCount) {
        MessagePushConfig.REISSUE_COUNT = reissueCount;
    }

    @Value("${cancelExpirationDate:3}")
    public void setCancelExpirationDate(Long cancelExpirationDate) {
        MessagePushConfig.CANCEL_EXPIRATION_DATE = cancelExpirationDate;
    }

    @Value("${deleteExpirationDate:5}")
    public void setDeleteExpirationDate(Long deleteExpirationDate) {
        MessagePushConfig.DELETE_EXPIRATION_DATE = deleteExpirationDate;
    }

}
