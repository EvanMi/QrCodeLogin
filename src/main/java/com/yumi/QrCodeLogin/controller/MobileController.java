package com.yumi.QrCodeLogin.controller;

import com.alibaba.fastjson2.JSON;
import com.yumi.QrCodeLogin.respository.CodeStateRepository;
import com.yumi.QrCodeLogin.controller.vo.CodeInfoVo;
import com.yumi.QrCodeLogin.controller.vo.LoginInfoVo;
import com.yumi.QrCodeLogin.respository.TokenKvRepository;
import com.yumi.QrCodeLogin.respository.UserInfoRepository;
import com.yumi.QrCodeLogin.respository.dos.LoginType;
import com.yumi.QrCodeLogin.respository.dos.TokenDo;
import com.yumi.QrCodeLogin.respository.dos.UserDo;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.UUID;

@RequestMapping("/mobile")
@Controller
public class MobileController {

    @Resource
    private UserInfoRepository userInfoRepository;
    @Resource
    private TokenKvRepository tokenKvRepository;
    @Resource
    private CodeStateRepository codeStateRepository;

    @GetMapping("/login")
    @ResponseBody
    public LoginInfoVo login() {
        UserDo user = userInfoRepository.getUser();
        TokenDo tokenDo = new TokenDo();
        tokenDo.setDeadline(System.currentTimeMillis() + 7 * 86400 * 1000);
        tokenDo.setLoginType(LoginType.MOBILE);
        tokenDo.setUserName(user.getUsername());
        String token = UUID.randomUUID().toString();
        LoginInfoVo mobileLoginInfoVo = new LoginInfoVo();
        mobileLoginInfoVo.setToken(token);
        mobileLoginInfoVo.setUsername(user.getUsername());
        tokenKvRepository.save(token, tokenDo);
        return mobileLoginInfoVo;
    }
    @GetMapping("/page")
    public String page() {
        return "mobilePage";
    }
    @GetMapping("/scan")
    @ResponseBody
    public CodeInfoVo scan(@RequestParam("code") String code,
                           @RequestParam("username") String username,
                           @RequestParam("token") String token) throws IOException {
        TokenDo tokenDo = tokenKvRepository.get(token);
        if (tokenDo.getDeadline() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("token 失效");
        }
        if (!username.equals(tokenDo.getUserName()) || !LoginType.MOBILE.equals(tokenDo.getLoginType())) {
            throw new IllegalArgumentException("token 信息有误");
        }
        code = codeStateRepository.getCode(code);
        Session session = codeStateRepository.getSession(code);
        if (null == session || !session.isOpen()) {
            throw new IllegalArgumentException("code 已过期");
        }
        String tempCode = UUID.randomUUID().toString();
        codeStateRepository.addCode(tempCode, code);
        LoginInfoVo loginInfoVo = new LoginInfoVo();
        loginInfoVo.setUsername(username);
        session.getBasicRemote().sendText(JSON.toJSONString(loginInfoVo));
        CodeInfoVo codeInfoVo = new CodeInfoVo();
        codeInfoVo.setCode(tempCode);
        return codeInfoVo;
    }
    @GetMapping("/confirm")
    @ResponseBody
    public ResponseEntity<?> confirm(@RequestParam("token") String token,
                              @RequestParam("username") String username,
                              @RequestParam("code") String code) throws IOException {
        TokenDo tokenDo = tokenKvRepository.get(token);
        if (tokenDo.getDeadline() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("token 失效");
        }
        if (!username.equals(tokenDo.getUserName()) || !LoginType.MOBILE.equals(tokenDo.getLoginType())) {
            throw new IllegalArgumentException("token 信息有误");
        }
        String targetCode = codeStateRepository.getCode(code);
        if (null == targetCode) {
            throw new IllegalArgumentException("code 已过期");
        }
        Session session = codeStateRepository.getSession(targetCode);
        if (null == session || !session.isOpen()) {
            throw new IllegalArgumentException("code 已过期");
        }
        String newToken = UUID.randomUUID().toString();
        TokenDo newTokenDo = new TokenDo();
        newTokenDo.setUserName(tokenDo.getUserName());
        newTokenDo.setDeadline(System.currentTimeMillis() + 7 * 86400 * 1000);
        newTokenDo.setLoginType(LoginType.PC);
        tokenKvRepository.save(newToken, newTokenDo);
        LoginInfoVo loginInfoVo = new LoginInfoVo();
        loginInfoVo.setUsername(username);
        loginInfoVo.setToken(newToken);
        session.getBasicRemote().sendText(JSON.toJSONString(loginInfoVo));
        return ResponseEntity.ok().build();
    }
}
