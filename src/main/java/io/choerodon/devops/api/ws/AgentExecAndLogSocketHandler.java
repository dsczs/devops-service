package io.choerodon.devops.api.ws;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import io.choerodon.core.exception.CommonException;

/**
 * Created by Sheep on 2019/8/19.
 */
@Component
public class AgentExecAndLogSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentExecAndLogSocketHandler.class);

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) serverHttpRequest;
        HttpServletRequest request = servletRequest.getServletRequest();

        //校验ws连接参数是否正确
        String group = WebSocketTool.getGroup(request);
        if (WebSocketTool.isEmptyOrTrimmedEmpty(group)) {
            throw new CommonException("group is unexpectedly null");
        }

        return true;
    }

    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        String group = WebSocketTool.getGroup(webSocketSession);
        String processor = WebSocketTool.getProcessor(webSocketSession);
        LOGGER.info("Connection established from agent. The group is {} and the processor is {}", group, processor);
    }
}
