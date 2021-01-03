package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
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

    /*
        This endpoint is used to create a question in the Quora Application.
        Any logged-in user can access this endpoint
     */
    @RequestMapping(path = "/question/create", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String auth,
                                                           QuestionRequest questionRequest) throws AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userBusinessService.getUserByAuthToken(auth,false);
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if(userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }
        QuestionEntity newQuestion = new QuestionEntity();
        newQuestion.setUser(userAuthEntity.getUser());
        newQuestion.setUuid(UUID.randomUUID().toString());
        newQuestion.setDate(now);
        newQuestion.setContent(questionRequest.getContent());
        QuestionEntity createdQuestion = questionService.createQuestion(newQuestion);
        return new ResponseEntity<QuestionResponse>(new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED"), HttpStatus.OK);
    }


    /*
        This endpoint is used to fetch all the questions that have been posted in the application
        by any user. Any logged-in user can access this endpoint.
     */
    @RequestMapping(path = "/question/all", method = RequestMethod.GET, produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
            @RequestHeader("authorization") final String auth) throws AuthorizationFailedException {


        List<QuestionEntity> allQuestions = questionService.getAllQuestions(auth);

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


    /*
        This endpoint is used to delete a question that has been posted by a user. Only the owner
         or admin of the question can delete the question.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
            @PathVariable("questionId") final String questionId,
            @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException, SignOutRestrictedException {

        final QuestionEntity question = questionService.deleteQuestion(questionId, authorization);
        QuestionDeleteResponse questionDeleteResponse =
               new QuestionDeleteResponse().id(UUID.fromString(question.getUuid()).toString())
                       .status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }


    /*
        This endpoint is used to fetch all the questions posed by a specific user.
        Any user can access this endpoint.
     */
    @RequestMapping(method = RequestMethod.GET,path="/question/all/{userId}",produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(
            @PathVariable("userId") final String userId,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {

        List<QuestionEntity> allQuestions = questionService.getAllQuestionsByUser(userId,authorization);

        List<QuestionDetailsResponse> allQuestionDetailsResponses = new ArrayList<QuestionDetailsResponse>();

        for (int i = 0; i < allQuestions.size(); i++) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .content(allQuestions.get(i).getContent())
                    .id(allQuestions.get(i).getUuid());
            allQuestionDetailsResponses.add(questionDetailsResponse);

        }
        return  new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponses, HttpStatus.OK);
    }


    /*
        This endpoint is used to edit a question that has been posted by a user. Only the owner
        of the question can edit the question.
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(
            final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionUuid,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        final QuestionEntity question = new QuestionEntity();
        question.setContent(questionEditRequest.getContent());

        QuestionEntity editedQuestion = questionService.editQuestion(questionUuid, question,
                authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse()
                .id(UUID.fromString(editedQuestion.getUuid()).toString()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
}

