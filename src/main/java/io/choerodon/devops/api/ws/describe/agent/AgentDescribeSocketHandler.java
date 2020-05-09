package io.choerodon.devops.api.ws.describe.agent;

import static io.choerodon.devops.infra.constant.DevOpsWebSocketConstants.AGENT_DESCRIBE;

import java.io.IOException;

import org.hzero.websocket.helper.KeySocketSendHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import io.choerodon.devops.api.ws.AbstractSocketHandler;
import io.choerodon.devops.api.ws.WebSocketTool;

/**
 * @author zmf
 * @since 20-5-9
 */
@Component
public class AgentDescribeSocketHandler extends AbstractSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentDescribeSocketHandler.class);

    @Autowired
    private KeySocketSendHelper keySocketSendHelper;

    @Override
    public String processor() {
        return AGENT_DESCRIBE;
    }


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String registerKey = WebSocketTool.getGroup(session);
        String rawKey = WebSocketTool.getRawKey(registerKey);
        String frontSessionGroup = WebSocketTool.buildFrontGroup(rawKey);

        keySocketSendHelper.sendByGroup(frontSessionGroup, "Describe", message.getPayload());
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            LOGGER.error("Agent describe: close session failed!", e);
        }
    }
}
