package com.ethan.wx.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 处理用户关注事件
 */
@Component
@Slf4j
public class SubscribeMsgHandler implements WxChatMsgHandler {

    // 获取消息类型
    @Override
    public WxChatMsgTypeEnum getMsgType() {
        return WxChatMsgTypeEnum.SUBSCRIBE;
    }

    // 处理用户关注事件
    @Override
    public String dealMsg(Map<String, String> messageMap) {
        log.info("触发用户关注事件！");
        String fromUserName = messageMap.get("FromUserName");
        String toUserName = messageMap.get("ToUserName");
        String subscribeContent = "感谢您的关注，这里是鲜橙大club！";
        String content = "<xml>\n" +
                "  <ToUserName><![CDATA[" + fromUserName + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + toUserName + "]]></FromUserName>\n" +
                "  <CreateTime>12345678</CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA[" + subscribeContent + "]]></Content>\n" +
                "</xml>";
        return content;
    }
}
