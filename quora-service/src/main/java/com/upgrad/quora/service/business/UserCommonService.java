package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserCommonService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException,
            UserNotFoundException {

        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime loggedOutTime = userAuthEntity.getLogoutAt();
        final long difference = now.compareTo(loggedOutTime);

        if (difference < 0) {
            return userEntity;
        }
        throw new AuthorizationFailedException("ATHR-002", "User is signed out");
    }

}