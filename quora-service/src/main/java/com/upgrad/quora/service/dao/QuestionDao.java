package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;


    public QuestionEntity createQuestion(QuestionEntity newQuestion) {
        entityManager.persist(newQuestion);
        return newQuestion;
    }

    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void deleteQuestion(QuestionEntity question, final String uuid){
        entityManager.remove(question);
        entityManager.createNamedQuery("deleteQuestion").setParameter(
                "uuid", uuid);
    }


    public QuestionEntity editQuestion(final QuestionEntity question) {
        return entityManager.merge(question);
    }


    public List<QuestionEntity> getAllQuestionsByUser(final String uuid) {
        try {
            return entityManager.createNamedQuery("allQuestionsByUserId", QuestionEntity.class).setParameter("uuid", uuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity getQuestionByUuid(final String Uuid) {
        try {
            return entityManager.createNamedQuery("getQuestionByUuid", QuestionEntity.class).setParameter(
                    "uuid", Uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}



