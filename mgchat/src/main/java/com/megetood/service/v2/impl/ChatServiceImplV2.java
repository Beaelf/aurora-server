package com.megetood.service.v2.impl;

import com.megetood.enums.MsgSignFlagEnum;
import com.megetood.mapper.ChatMsgMapper;
import com.megetood.mapper.ChatMsgMapperCustom;
import com.megetood.netty.entity.ChatMsg;
import com.megetood.service.v2.ChatServiceV2;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @Date 2020/3/26 12:42
 */
@Service
public class ChatServiceImplV2 implements ChatServiceV2 {

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Autowired
    private ChatMsgMapperCustom chatMsgMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveMsg(ChatMsg chatmsg) {
        com.megetood.pojo.ChatMsg msgDB = new com.megetood.pojo.ChatMsg();
        String msgId = sid.nextShort();
        msgDB.setSignFlag(MsgSignFlagEnum.unsign.type)
                .setSendUserId(chatmsg.getSenderId())
                .setMsg(chatmsg.getMsg())
                .setId(msgId)
                .setCreateTime(new Date())
                .setAcceptUserId(chatmsg.getReceiverId());

        chatMsgMapper.insert(msgDB);
        return msgId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        chatMsgMapperCustom.batchUpdateMsgSigned(msgIdList);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<com.megetood.pojo.ChatMsg> getUnReadMsgList(String acceptUserId) {
        Example example = new Example(com.megetood.pojo.ChatMsg.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("signFlag", 0);
        criteria.andEqualTo("acceptUserId", acceptUserId);

        List<com.megetood.pojo.ChatMsg> chatMsgList = chatMsgMapper.selectByExample(example);
        return chatMsgList;
    }
}
