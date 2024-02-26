package hw.topevery.controller;

import hw.topevery.framework.web.JsonResult;
import hw.topevery.pojo.dto.DgDataPushRecordDTO;
import hw.topevery.pojo.vo.DgResultVO;
import hw.topevery.service.PushService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
public class MessageController {

    @Autowired
    private PushService pushService;
    @ApiOperation("推送消息接收")
    @PostMapping("/receive")
    public JsonResult<DgResultVO<Long>> receive(@RequestBody DgDataPushRecordDTO<Long, String> body) {
        return JsonResult.ok(pushService.save(body));
    }


}
