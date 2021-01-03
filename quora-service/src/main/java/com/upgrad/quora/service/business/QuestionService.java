package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;


@Service
public class QuestionService {

    //Required services are autowired to enable access to methods defined in respective Business services
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private UserDao userDao;


    /*
        This service is used to create a question in the Quora Application.
        Any logged-in user can access this endpoint
     */
    @Transactional
    public QuestionEntity createQuestion(QuestionEntity newQuestion) {
        return questionDao.createQuestion(newQuestion);
    }

    /*
        This service is used to fetch all the questions that have been posted in the application
        by any user. Any logged-in user can access this endpoint.
     */
    @Transactional
    public List<QuestionEntity> getAllQuestions(final String authorizationToken) throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorizationToken);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // Validate if user has signed out
        ZonedDateTime now = ZonedDateTime.now();
        if (userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        return questionDao.getAllQuestions();

    }


    /*
        This service is used to get a question that has been posted by a user.
     */
    @Transactional
    public QuestionEntity getQuestion(String uuid) {
        return questionDao.getQuestionByUuid(uuid);
    }

    /*
        This service is used to delete a question that has been posted by a user. Only the owner
         or admin of the question can delete the question.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(final String questionId, final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException, SignOutRestrictedException {

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


    /*
        This service is used to fetch all the questions posed by a specific user.
        Any user can access this endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUser(String userUuid, final String authorizationToken) throws AuthorizationFailedException,
            UserNotFoundException {

        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime loggedOutTime = userAuthEntity.getLogoutAt();
        final long difference = now.compareTo(loggedOutTime);

        if (difference > 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        return questionDao.getAllQuestionsByUser(userUuid);
    }

    /*
        This service is used to edit a question that has been posted by a user. Only the owner
        of the question can edit the question.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(final String questionUuid, final QuestionEntity question,
                                       final String authorizationToken) throws AuthorizationFailedException,
            InvalidQuestionException {
        UserAuthEntity userAuthEntity = userDao.getUserByAccessToken(authorizationToken);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        QuestionEntity existingQuestion = questionDao.getQuestionByUuid(questionUuid);
        if (existingQuestion == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime loggedOutTime = userAuthEntity.getLogoutAt();
        final long difference = now.compareTo(loggedOutTime);
        if (difference > 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        long userId = userAuthEntity.getUser().getId();

        if (userId != existingQuestion.getUser().getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        question.setUuid(existingQuestion.getUuid());
        question.setId(existingQuestion.getId());
        question.setDate(existingQuestion.getDate());
        question.setUser(existingQuestion.getUser());
        return questionDao.editQuestion(question);

    }

}




