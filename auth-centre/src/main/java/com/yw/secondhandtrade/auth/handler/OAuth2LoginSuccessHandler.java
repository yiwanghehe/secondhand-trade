package com.yw.secondhandtrade.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationSuccessHandler delegate =
            new SavedRequestAwareAuthenticationSuccessHandler();
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("【OAuth2 登录】认证成功，正在重定向到最初请求的URL...");
        delegate.onAuthenticationSuccess(request, response, authentication); // 重定向到原始url
//        response.sendRedirect("http://localhost:8080/doc.html");
    }
}
