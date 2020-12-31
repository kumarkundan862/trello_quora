package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATH-001","User has not signed in");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if(userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATH-002","User is signed out.Sign in first to post a question");
        }
        QuestionEntity newQuestion = new QuestionEntity();
        newQuestion.setUser(userAuthEntity.getUser());
        newQuestion.setUuid(UUID.randomUUID().toString());
        newQuestion.setDate(now);
        newQuestion.setContent(questionRequest.getContent());
        QuestionEntity createdQuestion = questionService.createQuestion(newQuestion);
        return new ResponseEntity<QuestionResponse>(new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED"), HttpStatus.OK);
    }

    @RequestMapping(path = "/question/all", method = RequestMethod.GET, produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
            @RequestHeader("authorization") final String auth) throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userBusinessService.getUserByAuthToken(auth,false);
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if(userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
        }

        List<QuestionEntity> allQuestions = questionService.getAllQuestions();

        List<QuestionDetailsResponse> allQuestionResponses =
                new ArrayList<QuestionDetailsResponse>();

        for (int i = 0; i < allQuestions.size(); i++) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .id(allQuestions.get(i).getUuid())
                    .content(allQuestions.get(i).getContent());
            allQuestionResponses.add(questionDetailsResponse);

        }

        return new ResponseEntity<List<QuestionDetailsResponse>>( allQuestionResponses,
                HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
            @PathVariable("questionId") final String questionId,
            @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final QuestionEntity question = questionService.deleteQuestion(questionId, authorization);
        QuestionDeleteResponse questionDeleteResponse =
               new QuestionDeleteResponse().id(UUID.fromString(question.getUuid()).toString())
                       .status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }


    //This method will validate the user and give list of all the question sorted by userId.
    @RequestMapping(method = RequestMethod.POST,path="/questions/all/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(
            @PathVariable("userId") final String userId,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException, UserNotFoundException {

        // Get all questions
        List<QuestionEntity> allQuestions = questionService.getAllQuestionsByUser(userId,authorization);

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

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final QuestionEntity question = new QuestionEntity();
        question.setContent(questionEditRequest.getContent());

        QuestionEntity editedQuestion = questionService.editQuestion(questionUuid, question, authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(UUID.fromString(editedQuestion.getUuid()).toString()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
}

