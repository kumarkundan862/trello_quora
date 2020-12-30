package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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

    @Transactional
    public QuestionEntity createQuestion(QuestionEntity newQuestion) {
        return questionDao.createQuestion(newQuestion);
    }

    @Transactional
    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    @Transactional
    public QuestionEntity getQuestion(String uuid) {
        return questionDao.getQuestion(uuid);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(final String questionId, final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userBusinessService.getUserByAuthToken(authorization,false);
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if(userAuthEntity.getLogoutAt().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete a question");
        }

        QuestionEntity question = getQuestion(questionId);
        if (question == null){
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        UserEntity userEntity = userAuthEntity.getUser();
        if (userEntity.getUuid() != question.getUser().getUuid()) {
            if (userEntity.getRole().equalsIgnoreCase("nonadmin")) {
                throw new AuthorizationFailedException("ATHR-003","Only the question owner or " +
                        "admin can delete the question");
            } else {
                questionDao.deleteQuestion(question, questionId);
                return question;
            }
        }

        questionDao.deleteQuestion(question, questionId);
        return question;
    }
}
