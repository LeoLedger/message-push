package hw.topevery.pojo.dto;

import com.alibaba.fastjson.annotation.JSONField;
import hw.topevery.enums.DataTypeEnum;
import hw.topevery.enums.SourceTypeEnum;
import hw.topevery.enums.TargetTypeEnum;
import hw.topevery.parser.DataTypeSerializer;
import hw.topevery.parser.SourceTypeSerializer;
import hw.topevery.parser.TargetTypeSerializer;
import lombok.Data;

import java.util.Map;

/**
* 数据推送 DTO
*
 * @author LeoLedger
* @date 2023-12-23
*/
@Data
public class DgDataPushRecordDTO<K, V> {

    /** 数据目标类型 */
    @JSONField(serializeUsing = TargetTypeSerializer.class, deserializeUsing = TargetTypeSerializer.class)
    private TargetTypeEnum targetType;

    /** 数据源类型 */
    @JSONField(serializeUsing = SourceTypeSerializer.class, deserializeUsing = SourceTypeSerializer.class)
    private SourceTypeEnum sourceType;

    /** 数据类型 */
    @JSONField(serializeUsing = DataTypeSerializer.class, deserializeUsing = DataTypeSerializer.class)
    private DataTypeEnum dataType;

    /** 数据（key:唯一便是，value:JSON数据） */
    private Map<K, V> data;

}
