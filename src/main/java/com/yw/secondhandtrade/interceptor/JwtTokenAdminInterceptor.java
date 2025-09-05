package com.yw.secondhandtrade.interceptor;

import com.yw.secondhandtrade.common.constant.JwtClaimsConstant;
import com.yw.secondhandtrade.common.constant.RoleConstant;
import com.yw.secondhandtrade.common.context.BaseContext;
import com.yw.secondhandtrade.common.properties.JwtProperties;
import com.yw.secondhandtrade.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

//    @Autowired
//    private JwtProperties jwtProperties;
//
//    /**
//     * 校验jwt
//     */
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (!(handler instanceof HandlerMethod)) {
//            return true;
//        }
//
//        String token = request.getHeader(jwtProperties.getAdminTokenName());
//
//        try {
//            log.info("jwt校验:{}", token);
//            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
//
//            // 校验角色权限
//            Integer role = (Integer) claims.get(JwtClaimsConstant.USER_ROLE);
//            if (role == null || !role.equals(RoleConstant.ADMIN)) {
//                // 角色不匹配，无权限
//                log.warn("用户角色权限不足, role: {}", role);
//                response.setStatus(403); // 403 Forbidden
//                return false;
//            }
//
//            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
//            log.info("当前管理员id：{}", userId);
//
//            BaseContext.setId(userId);
//            return true;
//        } catch (Exception ex) {
//            log.error("JWT校验失败: ", ex);
//            response.setStatus(401);
//            return false;
//        }
//    }
}

