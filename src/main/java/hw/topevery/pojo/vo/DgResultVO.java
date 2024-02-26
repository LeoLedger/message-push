package hw.topevery.pojo.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
* 数据推送 DTO
*
 * @author LeoLedger
* @date 2023-12-23
*/
@Data
public class DgResultVO<T> {

    private Integer existsNum = 0;
    private List<T> existsList = new ArrayList<>();
    
    private Integer insertNum = 0;
    private List<T> insertList = new ArrayList<>();

    private Integer updateNum = 0;
    private List<T> updateList = new ArrayList<>();

    private Integer failNum = 0;
    private List<T> failList = new ArrayList<>();

}
