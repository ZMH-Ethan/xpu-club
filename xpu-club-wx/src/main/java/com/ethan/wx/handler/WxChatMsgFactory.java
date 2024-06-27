package com.ethan.wx.handler;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 微信消息工厂类
 */
@Component
public class WxChatMsgFactory implements InitializingBean {

    @Resource
    private List<WxChatMsgHandler> wxChatMsgHandlerList; // 微信消息处理器列表

    private Map<WxChatMsgTypeEnum, WxChatMsgHandler> handlerMap = new HashMap<>(); // 微信消息类型与微信消息处理器的映射

    // 根据消息类型获取对应的微信消息处理器
    public WxChatMsgHandler getHandlerByMsgType(String msgType) {
        WxChatMsgTypeEnum msgTypeEnum = WxChatMsgTypeEnum.getByMsgType(msgType);
        return handlerMap.get(msgTypeEnum);
    }

    // 注入微信消息处理器
    @Override
    public void afterPropertiesSet() throws Exception {
        // 遍历微信消息处理器列表，将微信消息类型与微信消息处理器的映射关系存入handlerMap
        for (WxChatMsgHandler wxChatMsgHandler : wxChatMsgHandlerList) {
            handlerMap.put(wxChatMsgHandler.getMsgType(), wxChatMsgHandler);
        }
    }

}
