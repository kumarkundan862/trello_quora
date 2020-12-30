package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Transactional
    public Question createQuestion(Question newQuestion) {
        return questionDao.createQuestion(newQuestion);
    }

    @Transactional
    public List<Question> getAllQuestions() {
        return questionDao.getAllQuestions();
    }
}
