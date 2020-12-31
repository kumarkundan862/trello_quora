package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
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
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserBusinessService userBusinessService;
//    private Object AnswerDetailsResponse;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final AnswerEntity answer = new AnswerEntity();
        answer.setAnswer(answerRequest.getAnswer());


        AnswerEntity createdAnswer = answerService.createAnswer(questionUuid, answer, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(UUID.fromString(createdAnswer.getUuid()).toString()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);


    }

    @RequestMapping(path = "/answer/all/{questionId}", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersForQuestionId(@RequestHeader("authorization") final String auth,
                                                               String q_uuid) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userBusinessService.getUserByAuthToken(auth,false);
        if(userAuthEntity == null)
            throw new AuthorizationFailedException("ATH-001","User has not signed in");
        if(userAuthEntity.getLogoutAt().isBefore(ZonedDateTime.now()))
            throw new AuthorizationFailedException("ATH-002","User is signed out.Sign in first to get the answers");
        List<AnswerEntity> allAnswers = answerService.getAllAnswersForQuestionId(q_uuid);
        if(allAnswers.size() == 0)
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
        List<AnswerDetailsResponse> detailsResponse = new ArrayList<AnswerDetailsResponse>();
        for(int i=0;i<allAnswers.size();i++)
        {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(allAnswers.get(i).getUuid()).questionContent(allAnswers.get(i).getQuestion().
                    getContent()).answerContent(allAnswers.get(i).getAnswer());
            detailsResponse.add(answerDetailsResponse);
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(detailsResponse,HttpStatus.OK);
    }

    }
