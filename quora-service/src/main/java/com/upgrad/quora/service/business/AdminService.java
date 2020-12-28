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
public class AdminService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorizationToken);
        if(userAuthEntity != null){

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime loggedOutTime = userAuthEntity.getLogoutAt();
            final long difference = now.compareTo(loggedOutTime);

            if(difference < 0){
                final String role = userAuthEntity.getUser().getRole();

                if(role.equals("admin")){
                    UserEntity userEntity =  userDao.getUserByUuid(userUuid);

                    if(userEntity != null){
                        userDao.deleteUserUuid(userEntity,userEntity.getUuid());
                        return userEntity;
                    }
                    throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
                }
                throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
            }
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }
        throw new AuthorizationFailedException("USR-001", "User has not signed in");
    }
}