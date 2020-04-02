package com.megetood.mapper;

import com.megetood.pojo.ChatMsg;
import com.megetood.utils.MyMapper;

import java.util.List;

public interface ChatMsgMapperCustom extends MyMapper<ChatMsg> {
    public void batchUpdateMsgSigned( List<String> msgIdList);
}