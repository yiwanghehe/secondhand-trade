package com.yw.secondhandtrade.auth.service.impl;

import com.yw.secondhandtrade.auth.mapper.UserMapper;
import com.yw.secondhandtrade.auth.service.UserService;
import com.yw.secondhandtrade.common.constant.RoleConstant;
import com.yw.secondhandtrade.pojo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 获取原始的OAuth2User
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 从OAuth2User中获取GitHub用户信息
        Map<String, Object> attributes = oauth2User.getAttributes();
        String githubUsername = (String) attributes.get("login"); // GitHub的唯一用户名
        String nickname = (String) attributes.get("name"); // GitHub上的显示名称
        String avatarUrl = (String) attributes.get("avatar_url");

        // 处理本地用户（查询或自动注册）
        User user = userService.processOAuthUser(githubUsername, nickname, avatarUrl);

        // 获取完整的用户信息（包括角色等）
        User fullUser = userMapper.getByUsername(user.getUsername());

        // 创建与UserDetailsService返回格式一致的attributes
        Map<String, Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("username", fullUser.getUsername());
        customAttributes.put("password", "{noop}" + fullUser.getPassword()); // 与UserDetailsService保持一致的格式
        customAttributes.put("authorities", Collections.singletonList(
                new SimpleGrantedAuthority(fullUser.getRole().equals(RoleConstant.ADMIN) ? "ROLE_ADMIN" : "ROLE_USER")));

        log.info("为GitHub用户 {} 创建了与UserDetails一致格式的OAuth2User", fullUser.getUsername());

        // 返回一个 DefaultOAuth2User 实例
        // 第一个参数是权限集合
        // 第二个参数是用户属性 Map
        // 第三个参数是 Principal 的 name 属性所使用的 key (这里我们使用 'login'，即GitHub的用户名)
        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority(fullUser.getRole().equals(RoleConstant.ADMIN) ? "ROLE_ADMIN" : "ROLE_USER")),
                customAttributes,
                "login"
        );
    }
}
