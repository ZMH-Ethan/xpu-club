package com.ethan.wx.controller;

import com.ethan.wx.handler.WxChatMsgFactory;
import com.ethan.wx.handler.WxChatMsgHandler;
import com.ethan.wx.utils.MessageUtil;
import com.ethan.wx.utils.SHA1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 微信回调接口
 */
@RestController
@Slf4j
public class CallBackController {

    private static final String token = "adwidhaidwoaid";

    @Resource
    private WxChatMsgFactory wxChatMsgFactory;
    @RequestMapping("/test")
    public String test() {
        return "hello world";
    }

    /**
     * 回调消息校验
     */
    @GetMapping("callback")
    public String callback(@RequestParam("signature") String signature,
                           @RequestParam("timestamp") String timestamp,
                           @RequestParam("nonce") String nonce,
                           @RequestParam("echostr") String echostr) {
        log.info("get验签请求参数：signature:{}，timestamp:{}，nonce:{}，echostr:{}",
                signature, timestamp, nonce, echostr);
        String shaStr = SHA1.getSHA1(token, timestamp, nonce, "");
        if (signature.equals(shaStr)) {
            return echostr;
        }
        return "unknown";
    }

    @PostMapping(value = "callback", produces = "application/xml;charset=UTF-8") // 接收微信消息(xml格式)
    public String callback(
            @RequestBody String requestBody,
            @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam(value = "msg_signature", required = false) String msgSignature) {
        log.info("接收到微信消息：requestBody：{}", requestBody);
        // 验证消息的真实性
        Map<String, String> messageMap = MessageUtil.parseXml(requestBody); // 解析xml
        String msgType = messageMap.get("MsgType"); // 消息类型
        String event = messageMap.get("Event") == null ? "" : messageMap.get("Event"); // 事件类型
        log.info("msgType:{},event:{}", msgType, event); // 打印消息类型和事件类型

        StringBuilder sb = new StringBuilder();
        sb.append(msgType);
        if (!StringUtils.isEmpty(event)) {
            sb.append(".");
            sb.append(event);
        }
        String msgTypeKey = sb.toString();
        WxChatMsgHandler wxChatMsgHandler = wxChatMsgFactory.getHandlerByMsgType(msgTypeKey);
        if (Objects.isNull(wxChatMsgHandler)) {
            return "unknown";
        }
        String replyContent = wxChatMsgHandler.dealMsg(messageMap);
        log.info("replyContent:{}", replyContent);
        return replyContent;
    }
}
