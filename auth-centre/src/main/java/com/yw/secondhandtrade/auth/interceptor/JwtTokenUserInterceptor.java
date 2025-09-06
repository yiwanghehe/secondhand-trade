package com.yw.secondhandtrade.auth.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

//    @Autowired
//    private JwtProperties jwtProperties;
//
//    /**
//     * 校验jwt
//     *
//     * @param request
//     * @param response
//     * @param handler
//     * @return
//     * @throws Exception
//     */
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // 判断当前拦截到的是Controller的方法还是其他资源
//        if (!(handler instanceof HandlerMethod)) {
//            //当前拦截到的不是动态方法，直接放行
//            return true;
//        }
//
//        // 从请求头中获取令牌
//        // 先尝试获得用户token, 管理员访问用户端也需要将自己的token放入请求头
//        String token = request.getHeader(jwtProperties.getUserTokenName());
//        // token为空直接拒绝
//        if(token == null || token.isEmpty()){
//            response.setStatus(401);
//            return false;
//        }
//
//        // 校验令牌
//        try{
//            log.info("jwt校验: {}", token);
//            // 先尝试使用用户令牌进行解析
//            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
//            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
//            log.info("当前用户id: {}", userId);
//
//            BaseContext.setId(userId);
//
//            return true;
//        } catch (Exception ex) {
//            log.warn("作为用户令牌解析失败: {}. 尝试作为管理员令牌解析...", ex.getMessage());
//            // 如果作为用户令牌解析失败，再尝试作为管理员令牌进行解析
//            try {
//                Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
//                Integer role = (Integer) claims.get(JwtClaimsConstant.USER_ROLE);
//
//                // 必须是管理员角色
//                if (role != null && role.equals(RoleConstant.ADMIN)) {
//                    Long adminId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
//                    log.info("验证为管理员令牌，管理员ID: {}", adminId);
//                    BaseContext.setId(adminId);
//                    // 验证通过，放行
//                    return true;
//                } else {
//                    // 令牌有效但角色不是管理员，拒绝访问
//                    log.warn("令牌虽为管理员令牌，但角色权限不足，拒绝访问");
//                    response.setStatus(403);
//                    return false;
//                }
//
//            } catch (Exception adminEx) {
//                // 两种方式都解析失败，说明令牌无效或已过期
//                log.error("JWT令牌最终验证失败", adminEx);
//                // 不通过，响应401状态码
//                response.setStatus(401);
//                return false;
//            }
//
//        }
//
//    }
}

