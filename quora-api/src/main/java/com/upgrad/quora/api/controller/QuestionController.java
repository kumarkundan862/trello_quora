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

        Question updatedQuestion = questionService.updateQuestion(questionUuid, question, authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(UUID.fromString(updatedQuestion.getUuid()).toString()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
}
