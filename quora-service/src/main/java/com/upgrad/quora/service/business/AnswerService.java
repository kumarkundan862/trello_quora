package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    @Autowired
    private AnswerDao answerDao;
}
