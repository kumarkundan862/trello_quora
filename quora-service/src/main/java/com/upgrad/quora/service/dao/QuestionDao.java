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

    public Question createQuestion(Question newQuestion) {
        entityManager.persist(newQuestion);
        return newQuestion;
    }

    public List<Question> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", Question.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Question getQuestion(String uuid) {
        try {
            return entityManager.createNamedQuery("getQuestion", Question.class).setParameter(
                    "uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void deleteQuestion(Question question,final String uuid) {
        entityManager.remove(question);
        entityManager.createNamedQuery("deleteQuestion").setParameter(
                "uuid", uuid);
    }
}

