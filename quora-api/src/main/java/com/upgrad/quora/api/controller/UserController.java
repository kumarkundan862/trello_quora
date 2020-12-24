package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.UserBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    private UserBusinessService userBusinessService;
}
