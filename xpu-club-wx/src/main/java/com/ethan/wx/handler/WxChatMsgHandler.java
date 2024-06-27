package com.ethan.wx.handler;

import java.util.Map;

/**
 * 微信消息处理器接口
 */
public interface WxChatMsgHandler {

    WxChatMsgTypeEnum getMsgType(); // 获取消息类型

    String dealMsg(Map<String, String> messageMap); // 根据消息类型处理微信消息

}
