package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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

        List<Question> allQuestions = questionService.getAllQuestions();

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

        final Question question = questionService.deleteQuestion(questionId, authorization);
        QuestionDeleteResponse questionDeleteResponse =
               new QuestionDeleteResponse().id(UUID.fromString(question.getUuid()).toString())
                       .status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }
}
