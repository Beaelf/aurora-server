package com.megetood.service.v2;

import com.megetood.netty.entity.ChatMsg;

import java.util.List;

public interface ChatServiceV2 {
    /**
     * 保存聊天消息到数据库
     * @param chatmsg
     * @return
     */
    public String saveMsg(ChatMsg chatmsg);

    /**
     * 批量签收消息
     * @param msgIdList
     */
    public void updateMsgSigned(List<String> msgIdList);

    /**
     * 获取未签收消息列表
     * @param acceptUserId
     * @return
     */
    public List<com.megetood.pojo.ChatMsg> getUnReadMsgList(String acceptUserId);
}
