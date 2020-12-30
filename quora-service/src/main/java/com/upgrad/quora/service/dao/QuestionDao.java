package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Question getQuestionByUuid(final String Uuid) {
        try {
            return entityManager.createNamedQuery("questionByUuid", Question.class).setParameter(
                    "uuid", Uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void editQuestion(final Question question) {
        entityManager.merge(question);
    }
}
