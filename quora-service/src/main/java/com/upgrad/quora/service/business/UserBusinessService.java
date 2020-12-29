package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signUp(UserEntity userEntity) throws SignUpRestrictedException {
        UserEntity userEntity1 = userDao.getUserByUserName(userEntity.getUserName());
        if (userEntity1 != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this " +
                    "Username has already been taken");
        }

        UserEntity userEntity2 = userDao.getUserByEmail(userEntity.getEmail());
        if (userEntity2 != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been " +
                    "registered, try with any other emailId");
        }

        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException, UserNotFoundException {
        final UserEntity userEntity = userDao.getUserByUserName(username);
        if(userEntity == null)
            throw new UserNotFoundException("ATH-001","This username does not exist");
        String encryptedPassword = PasswordCryptographyProvider.encrypt(password,userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword()))
        {
            UserAuthEntity userAuthTokenEntity = new UserAuthEntity();
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthTokenEntity.setUser(userEntity);
            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);
            userAuthTokenEntity.setLogoutAt(expiresAt);
            userAuthTokenEntity.setUuid(UUID.randomUUID().toString());

            UserAuthEntity createdUserAuthToken = userDao.createAuthToken(userAuthTokenEntity);
            userDao.updateUser(userEntity);
            return createdUserAuthToken;

        }
        else
        {
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity getUserByAuthToken(String authorization,boolean isLogOut) {

        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorization);
        if(userAuthEntity != null && isLogOut)
        {
            userAuthEntity.setLogoutAt(ZonedDateTime.now());
            userDao.updateUserAuthEntity(userAuthEntity);
        }
        return userAuthEntity;
    }
}
