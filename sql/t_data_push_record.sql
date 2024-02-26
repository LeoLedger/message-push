-- 数据推送记录表
CREATE TABLE t_dg_data_push_record
(
    c_id                     bigint AUTO_INCREMENT COMMENT '主键ID'  PRIMARY KEY,
    c_data_id                bigint                                 NULL COMMENT '数据ID',
    c_create_time            timestamp                              NULL COMMENT '创建时间',
    c_params_id              int                                    NOT NULL COMMENT '参数ID',
    c_push_time              timestamp                              NULL COMMENT '推送时间',
    c_push_status            smallint                               NULL COMMENT '推送状态（0-待推送，1-已推送，2-推送中，-1-推送失败）',
    c_fail_count             int                                    NULL COMMENT '失败次数',
    c_data                   json                                   NULL COMMENT 'JSON数据',
    c_canceled               boolean                                NULL COMMENT '取消推送',
    c_db_status              boolean DEFAULT FALSE                  NULL COMMENT '状态（false-正常，true-删除）'
) COMMENT '数据推送记录表';

CREATE INDEX idx_dg_data_push_record_create_time
    ON t_dg_data_push_record (c_create_time, c_push_status);
CREATE INDEX idx_data_push_record_target_source_data
    ON t_dg_data_push_record (c_data_id, c_params_id);

-- 数据推送参数表
CREATE TABLE t_dg_data_push_params
(
    c_id                     int AUTO_INCREMENT COMMENT '主键ID'     PRIMARY KEY,
    c_topic                  varchar(255)                           NULL COMMENT 'topic值',
    c_system_id              varchar(255)                           NULL COMMENT '系统ID',
    c_system_code            varchar(255)                           NULL COMMENT '系统code',
    c_message_code           varchar(255)                           NULL COMMENT '消息code',
    c_url                    varchar(255)                           NULL COMMENT '推送地址',
    c_debug                  boolean DEFAULT FALSE                  NULL COMMENT '调试',
    c_target_type            smallint                               NOT NULL COMMENT '数据目标类型（1-数据治理平台）',
    c_source_type            int                                    NOT NULL COMMENT '数据源类型',
    c_data_type              int                                    NOT NULL COMMENT '数据类型',
    c_disable                boolean DEFAULT FALSE                  NULL COMMENT '禁用'
) COMMENT '数据推送参数表';