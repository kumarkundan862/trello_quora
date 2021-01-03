package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    private UserBusinessService userBusinessService;

    /*
        This endpoint is used to register a new user in the Quora Application.
        On successful registration, the information is stored in the database and JSON response
        is created with appropriate message and HTTP status.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("1234abc");
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        final UserEntity createdUserEntity = userBusinessService.signUp(userEntity);
        SignupUserResponse userResponse =
                new SignupUserResponse().id(createdUserEntity.getUuid())
                        .status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }


    /*
        This endpoint is used for user authentication. The user authenticates in the application
        and after successful authentication, access token is given to a user.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signin",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, UserNotFoundException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArr = decodedText.split(":");
        UserAuthEntity userAuthToken = userBusinessService.authenticate(decodedArr[0],decodedArr[1]);
        UserEntity user = userAuthToken.getUser();
        SigninResponse response = new SigninResponse().id(UUID.fromString(user.getUuid()).toString()).message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token",userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(response,headers,HttpStatus.OK);
    }


    /*
        This endpoint is used to sign out from the Quora Application. The user cannot access
        any other endpoint once he is signed out of the application.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        UserAuthEntity userAuthToken = userBusinessService.getUserByAuthToken(authorization,true);
        if(userAuthToken == null) {
            throw new SignOutRestrictedException("SGR-001","User is not signed in");
        }
        UserEntity user = userAuthToken.getUser();

        SignoutResponse response = new SignoutResponse().id(UUID.fromString(user.getUuid()).toString()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(response,HttpStatus.OK);
    }

}
