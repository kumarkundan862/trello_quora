package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;


@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private UserDao userDao;

    @Transactional
    public QuestionEntity createQuestion(QuestionEntity newQuestion) {
        return questionDao.createQuestion(newQuestion);
    }

    @Transactional
    public List<QuestionEntity> getAllQuestions(final String authorizationToken)  throws AuthorizationFailedException
             {
        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorizationToken);

        if (userAuthEntity != null) {

                    return questionDao.getAllQuestions();

        }throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }



    @Transactional
    public QuestionEntity getQuestion(String uuid) {
        return questionDao.getQuestionByUuid(uuid);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(final String questionId, final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userBusinessService.getUserByAuthToken(authorization, false);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        QuestionEntity question = getQuestion(questionId);
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        UserEntity loggedInUser = userAuthEntity.getUser();
        if (loggedInUser.getUuid() != question.getUser().getUuid()) {
            if (loggedInUser.getRole().equalsIgnoreCase("nonadmin")) {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or " +
                        "admin can delete the question");
            } else {
                questionDao.deleteQuestion(question, questionId);
                return question;
            }
        }

        questionDao.deleteQuestion(question, questionId);
        return question;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUser(String userUuid, final String authorizationToken) throws AuthorizationFailedException,
            UserNotFoundException {

        UserAuthEntity userAuthEntity=userDao.getUserByAccessToken(authorizationToken);


        if (userAuthEntity != null) {

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime loggedOutTime = userAuthEntity.getLogoutAt();
            final long difference = now.compareTo(loggedOutTime);

            if (difference < 0) {

                UserEntity userEntity = userDao.getUserByUuid(userUuid);
                if (userEntity != null) {
                    return questionDao.getAllQuestionsByUser(userUuid);
                }
                throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
            }
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }
        throw new AuthorizationFailedException("USR-001", "User has not signed in");

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(final String questionUuid, final QuestionEntity question,
                                  final String authorizationToken) throws AuthorizationFailedException,
                                  InvalidQuestionException {
        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorizationToken);

        if (userAuthEntity != null) {

            QuestionEntity existingQuestion = questionDao.getQuestionByUuid(questionUuid);
            if (existingQuestion != null) {

                final ZonedDateTime now = ZonedDateTime.now();
                final ZonedDateTime loggedOutTime = userAuthEntity.getLogoutAt();

                if (now != null && loggedOutTime != null) {
                    final long difference = now.compareTo(loggedOutTime);

                    if (difference < 0) {
                        long userId = userAuthEntity.getUser().getId();
                        if (userId == existingQuestion.getUser().getId()) {

                            //Question existingQuestion = questionDao.getQuestionByUuid(questionUuid);
                            //question.setContent(existingQuestion.getContent());
                            question.setUuid(existingQuestion.getUuid());
                            question.setId(existingQuestion.getId());
                            question.setDate(existingQuestion.getDate());
                            question.setUser(existingQuestion.getUser());

                            return questionDao.editQuestion(question);
                            //question;
                        }
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                    }

                 }throw new AuthorizationFailedException("ATHR-003", "User is signed out.Sign in first to edit the question");

            }throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");

        } throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

        /*

        if (userAuthEntity != null) {

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime loggedOutTime = userAuthEntity.getLogoutAt();

            if(now!=null && loggedOutTime!=null){
                final long difference = now.compareTo(loggedOutTime);

                if (difference < 0) {
                    long userId = userAuthEntity.getUser().getId();
                    QuestionEntity existingQuestion = questionDao.getQuestionByUuid(questionUuid);
                    if (existingQuestion != null){
                        if (userId == existingQuestion.getUser().getId()) {

                            //Question existingQuestion = questionDao.getQuestionByUuid(questionUuid);
                            //question.setContent(existingQuestion.getContent());
                            question.setUuid(existingQuestion.getUuid());
                            question.setId(existingQuestion.getId());
                            question.setDate(existingQuestion.getDate());
                            question.setUser(existingQuestion.getUser());

                            return questionDao.editQuestion(question);
                            //question;
                        }
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                    }
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
                }
                throw new AuthorizationFailedException("ATHR-003", "User is signed out.Sign in first to edit the question");

            }throw new AuthorizationFailedException("ATHR-003", "User is signed out.Sign in first to get user details");

        }throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        /*
        if (userAuthEntity != null) {

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime loggedOutTime = userAuthEntity.getLogoutAt();
            final long difference = now.compareTo(loggedOutTime);

            if (difference < 0) {
                long userId = userAuthEntity.getUser().getId();
                QuestionEntity existingQuestion = questionDao.getQuestionByUuid(questionUuid);
                if (existingQuestion != null){
                    if (userId == existingQuestion.getUser().getId()) {

                        //Question existingQuestion = questionDao.getQuestionByUuid(questionUuid);
                        //question.setContent(existingQuestion.getContent());
                        question.setUuid(existingQuestion.getUuid());
                        question.setId(existingQuestion.getId());
                        question.setDate(existingQuestion.getDate());
                        question.setUser(existingQuestion.getUser());

                        return questionDao.editQuestion(question);
                         //question;
                    }
                    throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                }
                throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
            }
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        throw new AuthorizationFailedException("USR-001", "User has not signed in");

         */
    }
}



