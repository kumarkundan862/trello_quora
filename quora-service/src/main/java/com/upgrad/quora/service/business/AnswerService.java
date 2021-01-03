package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerService {

    //Required services are autowired to enable access to methods defined in respective Answer services
    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserBusinessService userBusinessService;

    /*
        This service is used to create an answer from the Quora Application. Any logged-in user can
        access this endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final String questionUuid, final AnswerEntity answer,
                                     final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        QuestionEntity existingQuestion = questionDao.getQuestionByUuid(questionUuid);
        if (existingQuestion == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime loggedOutTime = userAuthEntity.getLogoutAt();
        final long difference = now.compareTo(loggedOutTime);
        if (difference > 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }
        answer.setQuestion(existingQuestion);
        answer.setUuid(UUID.randomUUID().toString());
        answer.setUser(userAuthEntity.getUser());
        answer.setDate(now);
        return answerDao.createAnswer(answer);
    }


    /*
        This service is used to edit an answer. Only the owner of the answer can edit the answer.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(final String answerId, final String authoriztaion,
                                   final AnswerEntity answer) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authoriztaion);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first " +
                    "to edit an answer");
        }
        UserEntity loggedInUser = userAuthEntity.getUser();

        AnswerEntity originalAnswer = answerDao.getAnswerByUuid(answerId);
        if (originalAnswer == null) {
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }
        if (loggedInUser.getUuid() != originalAnswer.getUser().getUuid()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the " +
                    "answer");
        }

        answer.setUuid(originalAnswer.getUuid());
        answer.setDate(originalAnswer.getDate());
        answer.setId(originalAnswer.getId());
        answer.setQuestion(originalAnswer.getQuestion());
        answer.setUser(originalAnswer.getUser());

        return answerDao.editAnswer(answer);
    }


    /*
        This service is used to get all answers to a particular question. Any logged-in user can
        access this endpoint
     */
    public List<AnswerEntity> getAllAnswersForQuestionId(String q_uuid) {
        return answerDao.getAllAnswersForQuestionId(q_uuid);
    }


    /*
        This endpoint is used to delete an answer. Only the owner of the answer or admin
        can delete an answer.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(final String answerId, final String authorization)
            throws AuthorizationFailedException,
            InvalidQuestionException, AnswerNotFoundException{

        UserAuthEntity userAuthEntity = userBusinessService.getUserByAuthToken(authorization, false);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        AnswerEntity answerEntity = answerDao.getAnswerByAnsUuid(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        UserEntity loggedInUser = userAuthEntity.getUser();
        if (loggedInUser.getUuid() != answerEntity.getUser().getUuid()) {
            if (loggedInUser.getRole().equalsIgnoreCase("nonadmin")) {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            } else {
                answerDao.deleteAnswer(answerEntity, answerId);
                return answerEntity;
            }
        }

        answerDao.deleteAnswer(answerEntity, answerId);
        return answerEntity;
    }


}
