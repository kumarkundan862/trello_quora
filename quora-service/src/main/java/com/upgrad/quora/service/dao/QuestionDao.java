package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

<<<<<<< HEAD
    public Question getQuestionByUuid(final String Uuid) {
        try {
            return entityManager.createNamedQuery("questionByUuid", Question.class).setParameter(
                    "uuid", Uuid).getSingleResult();
=======
    public Question createQuestion(Question newQuestion) {
        entityManager.persist(newQuestion);
        return newQuestion;
    }

    public List<Question> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", Question.class).getResultList();
>>>>>>> upstream/main
        } catch (NoResultException nre) {
            return null;
        }
    }

<<<<<<< HEAD
    public void editQuestion(final Question question) {
        entityManager.merge(question);
    }
=======


    public List<Question> getAllQuestionsByUser(final String uuid) {
        try {
            return entityManager.createNamedQuery("allQuestionsByUserId", Question.class).setParameter("uuid", uuid).getResultList();

        } catch (NoResultException nre) {
            return null;
        }
    }

>>>>>>> upstream/main
}

