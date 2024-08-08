package com.yumi.QrCodeLogin.respository;

import com.yumi.QrCodeLogin.respository.dos.UserDo;
import org.springframework.stereotype.Repository;

@Repository
public class UserInfoRepository {

    public UserDo getUser() {
        UserDo userDo = new UserDo();
        userDo.setUsername("yumi");
        userDo.setPassword("********");
        return userDo;
    }
}
