package com.yumi.QrCodeLogin.respository;

import com.yumi.QrCodeLogin.respository.dos.TokenDo;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TokenKvRepository {
    private final ConcurrentHashMap<String, TokenDo> map = new ConcurrentHashMap<>();

    public void save(String token, TokenDo tokenDo) {
        map.put(token, tokenDo);
    }

    public TokenDo get(String token) {
        return Optional.of(map.get(token)).orElseThrow(() -> new IllegalArgumentException("token不存在"));
    }

    public void remove(String token) {
        map.remove(token);
    }
}
