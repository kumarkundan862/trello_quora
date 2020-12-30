package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
<<<<<<< HEAD
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
=======
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
>>>>>>> upstream/main
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
<<<<<<< HEAD
=======

import java.util.List;
>>>>>>> upstream/main

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

<<<<<<< HEAD
    @Transactional(propagation = Propagation.REQUIRED)
    public Question editQuestion(final String questionUuid, final Question question, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorizationToken);
=======
    @Transactional
    public Question createQuestion(Question newQuestion) {
        return questionDao.createQuestion(newQuestion);
    }

    @Transactional
    public List<Question> getAllQuestions() {
        return questionDao.getAllQuestions();
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public List<Question> getAllQuestionsByUser(String userUuid, final String authorizationToken) throws AuthorizationFailedException,
            UserNotFoundException {

        UserAuthEntity userAuthEntity=userDao.getUserByAccessToken(authorizationToken);

>>>>>>> upstream/main
        if (userAuthEntity != null) {

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime loggedOutTime = userAuthEntity.getLogoutAt();
            final long difference = now.compareTo(loggedOutTime);

            if (difference < 0) {
<<<<<<< HEAD
                long userId = userAuthEntity.getUser().getId();
                Question existingQuestion = questionDao.getQuestionByUuid(questionUuid);
                if (existingQuestion != null){
                    if (userId == existingQuestion.getUser().getId()) {

                    //Question existingQuestion = questionDao.getQuestionByUuid(questionUuid);
                        //question.setContent(existingQuestion.getContent());
                        question.setUuid(existingQuestion.getUuid());
                        question.setId(existingQuestion.getId());
                        question.setDate(existingQuestion.getDate());
                        question.setUser(existingQuestion.getUser());

                        questionDao.editQuestion(question);
                        return question;
                    }
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                }
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
            }
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        throw new AuthorizationFailedException("USR-001", "User has not signed in");
=======
                UserEntity userEntity = userDao.getUserByUuid(userUuid);
                if (userEntity != null) {
                    return questionDao.getAllQuestionsByUser(userUuid);
                }
                throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
            }
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }
        throw new AuthorizationFailedException("USR-001", "User has not signed in");

>>>>>>> upstream/main
    }
}



