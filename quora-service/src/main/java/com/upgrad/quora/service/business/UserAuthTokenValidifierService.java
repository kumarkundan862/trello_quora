package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class UserAuthTokenValidifierService {
    @Autowired
    UserDao userDao;
    boolean userSignOutStatus(String authorizationToken) {
        UserAuthEntity userAuthTokenEntity = userDao.getUserByAccessToken(authorizationToken);
        ZonedDateTime loggedOutStatus = userAuthTokenEntity.getLogoutAt();
        ZonedDateTime loggedInStatus = userAuthTokenEntity.getLoginAt();
        if (loggedOutStatus != null && loggedOutStatus.isAfter(loggedInStatus)) {
            return true;
        } else return false;
    }
}

