package org.lanjianghao.security.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanjianghao.security.custom.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserAuthorityCache {

    private final static String KEY_PREFIX = "USER_";

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private String getRedisKey(String username) {
        return KEY_PREFIX + username;
    }

    public void set(CustomUser user) {
        try {
            String key = getRedisKey(user.getSysUser().getUsername());
            List<String> authStrings = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            String value = objectMapper.writeValueAsString(authStrings);
            redisTemplate.opsForValue().set(key, value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<SimpleGrantedAuthority> getByUsername(String username) {
        String key = getRedisKey(username);
        String value = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            List<String> authStrings = objectMapper.readValue(value, new TypeReference<List<String>>() {});
            return authStrings.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
