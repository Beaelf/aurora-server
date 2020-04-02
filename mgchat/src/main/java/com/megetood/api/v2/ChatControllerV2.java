package com.megetood.api.v2;

import com.megetood.pojo.ChatMsg;
import com.megetood.service.v1.ChatService;
import com.megetood.utils.JSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Date 2020/3/26 21:14
 */
@RestController("chatControllerV2")
@RequestMapping("/chat/")
public class ChatControllerV2 {

    @Autowired
    private ChatService chatService;

    /**
     * 用户手机端接收未读消息列表
     * @param acceptUserId
     * @return
     */
    @GetMapping("{userId}/get/unread/msgs")
    public List<ChatMsg> getUnReadMsgList(@PathVariable("userId") @NotNull String acceptUserId){

        // 查询
        List<ChatMsg> unReadMsgList = chatService.getUnReadMsgList(acceptUserId);

        return unReadMsgList;
    }
}
