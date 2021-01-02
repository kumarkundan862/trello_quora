package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
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

    @Autowired
    private QuestionService questionService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final AnswerEntity answer = new AnswerEntity();
        answer.setAnswer(answerRequest.getAnswer());


        AnswerEntity createdAnswer = answerService.createAnswer(questionUuid, answer, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(UUID.fromString(createdAnswer.getUuid()).toString()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@PathVariable("answerId") final String answerId,
                                                         @RequestHeader("authorization") final String authorization,
                                                         final AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, 
                                                         AnswerNotFoundException, AnswerNotFoundException {

        final AnswerEntity answer = new AnswerEntity();
        answer.setAnswer(answerEditRequest.getContent());

        AnswerEntity editedAnswer = answerService.editAnswer(answerId, authorization, answer);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(UUID
                .fromString(editedAnswer.getUuid()).toString()).status("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }
  
    @RequestMapping(path = "/answer/all/{questionId}", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersForQuestionId(@RequestHeader("authorization") final String auth,
                                                               String q_uuid) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userBusinessService.getUserByAuthToken(auth,false);
        if(userAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");

        if(userAuthEntity.getLogoutAt().isBefore(ZonedDateTime.now()))
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get the answers");
        QuestionEntity questionEntity = questionService.getQuestion(q_uuid);
        List<AnswerEntity> allAnswers = answerService.getAllAnswersForQuestionId(q_uuid);
        if(questionEntity == null)
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

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity <AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException,
            InvalidQuestionException {

        final AnswerEntity answerEntity = answerService.deleteAnswer(answerId, authorization);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerId).status("ANSWER DELETED");
        return new ResponseEntity < AnswerDeleteResponse > (answerDeleteResponse, HttpStatus.OK);
    }
}
