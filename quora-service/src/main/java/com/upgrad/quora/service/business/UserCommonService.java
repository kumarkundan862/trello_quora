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

    //Required services are autowired to enable access to methods defined in respective UserCommon services
    @Autowired
    private UserDao userDao;

    /*
        This service is used to get the details of any user in the Quora Application. This
        service can be accessed by any user in the application. It takes userId of the user
        whose information is to be retrieved.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException,
            UserNotFoundException {

        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        return userEntity;
    }

}