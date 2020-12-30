<<<<<<< HEAD
package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final Question question = new Question();
        //question.setUuid(UUID.randomUUID().toString());
        question.setContent(questionEditRequest.getContent());

        Question editedQuestion = questionService.editQuestion(questionUuid, question, authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(UUID.fromString(editedQuestion.getUuid()).toString()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
}
=======


package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(path = "/question/create", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String auth, QuestionRequest questionRequest) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userBusinessService.getUserByAuthToken(auth,false);
        if(userAuthEntity == null)
        {
            throw new AuthorizationFailedException("ATH-001","User has not signed in");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if(userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATH-002","User is signed out.Sign in first to post a question");
        }
        Question newQuestion = new Question();
        newQuestion.setUser(userAuthEntity.getUser());
        newQuestion.setUuid(UUID.randomUUID().toString());
        newQuestion.setDate(now);
        newQuestion.setContent(questionRequest.getContent());
        Question createdQuestion = questionService.createQuestion(newQuestion);
        return new ResponseEntity<QuestionResponse>(new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED"), HttpStatus.OK);
    }

    @RequestMapping(path = "/question/all", method = RequestMethod.GET, produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestions(
            @RequestHeader("authorization") final String auth) throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userBusinessService.getUserByAuthToken(auth,false);
        if(userAuthEntity == null)
        {
            throw new AuthorizationFailedException("ATH-001","User has not signed in");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if(userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATH-002","User is signed out.Sign in first to post a question");
        }

        UserEntity userEntity = userAuthEntity.getUser();
        List<Question> allQuestions = questionService.getAllQuestions();
        if (allQuestions.size() <= 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String content = allQuestions.toString();

        QuestionDetailsResponse questionDetailsResponse =
                new QuestionDetailsResponse().id(userEntity.getUuid()).content(content);

        return new ResponseEntity<QuestionDetailsResponse>( questionDetailsResponse, HttpStatus.OK);
    }

    //This method will validate the user and give list of all the question sorted by userId.
    @RequestMapping(method = RequestMethod.POST,path="/questions/all/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException, UserNotFoundException {

        // Get all questions
        List<Question> allQuestions = questionService.getAllQuestionsByUser(userId,authorization);

        // Create response
        List<QuestionDetailsResponse> allQuestionDetailsResponses = new ArrayList<QuestionDetailsResponse>();

        for (int i = 0; i < allQuestions.size(); i++) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .content(allQuestions.get(i).getContent())
                    .id(allQuestions.get(i).getUuid());
            allQuestionDetailsResponses.add(questionDetailsResponse);

        }
        // Return response
        return  new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponses, HttpStatus.OK);
    }

}
>>>>>>> upstream/main
