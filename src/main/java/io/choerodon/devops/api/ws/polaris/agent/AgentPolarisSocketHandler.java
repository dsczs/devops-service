package io.choerodon.devops.api.ws.polaris.agent;

import static io.choerodon.devops.infra.constant.DevOpsWebSocketConstants.AGENT_POLARIS;
import static io.choerodon.devops.infra.constant.DevOpsWebSocketConstants.KEY;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import io.choerodon.devops.api.vo.polaris.PolarisResponsePayloadVO;
import io.choerodon.devops.api.ws.AbstractSocketHandler;
import io.choerodon.devops.api.ws.WebSocketTool;
import io.choerodon.devops.app.service.PolarisScanningService;
import io.choerodon.devops.infra.util.JsonHelper;
import io.choerodon.devops.infra.util.TypeUtil;

/**
 * @author zmf
 * @since 20-5-9
 */
@Component
public class AgentPolarisSocketHandler extends AbstractSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentPolarisSocketHandler.class);

    @Autowired
    private PolarisScanningService polarisScanningService;

    @Override
    public String processor() {
        return AGENT_POLARIS;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        LOGGER.info("Polaris: received message from agent...");

        // 获取集群id
        Long clusterId = WebSocketTool.getClusterId(TypeUtil.objToString(session.getAttributes().get(KEY)));
        LOGGER.info("Polaris: the cluster id is {}", clusterId);


        polarisScanningService.handleAgentPolarisMessage(JsonHelper.unmarshalByJackson(message.getPayload(), PolarisResponsePayloadVO.class));

        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            LOGGER.warn("Exception occurred when close webSocketSession for cluster with id {} and for type polaris. The ex is: {}", e);
        }
    }
}
