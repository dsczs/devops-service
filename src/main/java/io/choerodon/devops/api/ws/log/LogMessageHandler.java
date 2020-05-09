package io.choerodon.devops.api.ws.log;


import static io.choerodon.devops.infra.constant.DevOpsWebSocketConstants.FRONT_LOG;

import java.nio.ByteBuffer;

import org.hzero.websocket.helper.KeySocketSendHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import io.choerodon.devops.api.ws.WebSocketTool;

/**
 * Created by Sheep on 2019/7/26.
 */
@Component
public class LogMessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogMessageHandler.class);
    private static final String AGENT_LOG = "AgentLog";

    @Autowired
    @Lazy
    private KeySocketSendHelper keySocketSendHelper;

    public void handle(WebSocketSession webSocketSession, BinaryMessage message) {
        // Group格式： log:${rawKey}  形如 log:ab124ac
        String sessionGroup = WebSocketTool.getGroup(webSocketSession);

        // 获取rawKey， 用于拼接转发的目的地group
        String rawKey = WebSocketTool.getRawKey(sessionGroup);


        ByteBuffer buffer = message.getPayload();
        byte[] bytesArray = new byte[buffer.remaining()];
        buffer.get(bytesArray, 0, bytesArray.length);

        String processor = WebSocketTool.getProcessor(webSocketSession);

        if (FRONT_LOG.equals(processor)) {
            LOGGER.info("Received message from front. The processor is {} and the byte array length is {}", processor, bytesArray.length);
            sessionGroup = WebSocketTool.buildAgentGroup(rawKey);
        } else {
            LOGGER.info("Received message from agent. The processor is {} and the byte array length is {}", processor, bytesArray.length);
            sessionGroup = WebSocketTool.buildFrontGroup(rawKey);
        }

        keySocketSendHelper.sendByGroup(sessionGroup, AGENT_LOG, bytesArray);
    }

}
