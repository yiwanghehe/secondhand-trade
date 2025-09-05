package com.yw.secondhandtrade.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yw.secondhandtrade.common.constant.JwtClaimsConstant;
import com.yw.secondhandtrade.common.properties.JwtProperties;
import com.yw.secondhandtrade.common.result.Result;
import com.yw.secondhandtrade.common.utils.JwtUtil;
import com.yw.secondhandtrade.pojo.entity.User;
import com.yw.secondhandtrade.pojo.vo.UserLoginVO;
import com.yw.secondhandtrade.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();

        // 从OAuth2User中获取GitHub用户信息
        Map<String, Object> attributes = oauth2User.getAttributes();
        String githubUsername = (String) attributes.get("login"); // GitHub的唯一用户名
        String nickname = (String) attributes.get("name"); // GitHub上的显示名称
        String avatarUrl = (String) attributes.get("avatar_url");

        // 处理本地用户（查询或自动注册）
        User user = userService.processOAuthUser(githubUsername, nickname, avatarUrl);

        // 生成JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        claims.put(JwtClaimsConstant.USER_ROLE, user.getRole());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );

        log.info("GitHub登录成功, 为用户 {} 生成JWT", user.getUsername());

//        // 将JWT返回给前端 (重定向或直接返回JSON)
//        // 这里我们选择直接返回JSON，方便前后端分离的应用
//        response.setContentType("application/json;charset=UTF-8");
//        Map<String, Object> result = new HashMap<>();
//        result.put("code", 200);
//        result.put("msg", "Login Success");
//        Map<String, Object> data = new HashMap<>();
//        data.put("token", token);
//        data.put("id", user.getId());
//        data.put("username", user.getUsername());
//        data.put("nickname", user.getNickname());
//        data.put("role", user.getRole());
//        result.put("data", data);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .token(token)
                .build();

        response.getWriter().write(new ObjectMapper().writeValueAsString(Result.success(userLoginVO)));
    }
}
