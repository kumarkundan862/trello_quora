package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity newAnswer) {
        entityManager.persist(newAnswer);
        return newAnswer;
    }


    public AnswerEntity getAnswerByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity editAnswer(final AnswerEntity answer) {
        return entityManager.merge(answer);
    }
  
    public List<AnswerEntity> getAllAnswersForQuestionId(String q_uuid) {
        return entityManager.createNamedQuery("answersByQid",AnswerEntity.class).setParameter("qid",q_uuid).getResultList();
    }
}
