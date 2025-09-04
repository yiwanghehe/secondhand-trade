package com.yw.secondhandtrade.config; // 包名可以自定义

import com.yw.secondhandtrade.common.constant.JwtClaimsConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.properties.JwtProperties;
import com.yw.secondhandtrade.common.utils.JwtUtil;
import com.yw.secondhandtrade.websocket.WebSocketServer;
import io.jsonwebtoken.Claims;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * WebSocket握手阶段的自定义配置器
 * 用于在建立连接前获取并传递当前登录的用户ID
 */
@Component
@Slf4j
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    public static final String LOGGED_IN_USER_ID = "LOGGED_IN_USER_ID";

    public static JwtProperties jwtProperties;

    @Autowired
    public void setJwtProperties(JwtProperties jwtProperties){
        GetHttpSessionConfigurator.jwtProperties = jwtProperties;
    }

    /**
     * 在握手阶段修改配置
     * @param sec ServerEndpointConfig
     * @param request HandshakeRequest
     * @param response HandshakeResponse
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        log.info("WebSocket 正在进行握手...");

        if (jwtProperties == null) {
            log.error("JwtProperties 未注入，无法验证 WebSocket Token！");
            super.modifyHandshake(sec, request, response);
            return;
        }

        // 从握手请求的HTTP头中获取token
        Map<String, List<String>> headers = request.getHeaders();
        String token = null;
        List<String> tokenHeader = headers.get(jwtProperties.getUserTokenName().toLowerCase());
        if (tokenHeader != null && !tokenHeader.isEmpty()) {
            token = tokenHeader.get(0);
        }

        if (StringUtils.hasText(token)) {
            log.info("WebSocket 握手请求中发现Token: {}", token);
            try {
                // 解析Token
                Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
                Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
                log.info("Token 解析成功，获取到用户ID: {}", userId);

                // 将验证通过的用户ID存入 userProperties，供 @OnOpen 方法使用
                sec.getUserProperties().put(LOGGED_IN_USER_ID, userId);

            } catch (Exception e) {
                log.warn("WebSocket 握手失败，Token 无效或已过期: {}", e.getMessage());
            }
        } else {
            log.warn("WebSocket 握手失败，请求头中未找到Token。");
        }

        super.modifyHandshake(sec, request, response);
    }
}
