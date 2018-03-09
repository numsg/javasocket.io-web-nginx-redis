package com.gsafety.pivs.notice.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;


/**
 * Created by Administrator on 2017/3/2.
 */
@RestController
@RequestMapping(value = "api/hellocontroller",produces = "application/json")
@Api(value = "api/hellocontroller")
public class HelloController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String index() {
        logger.error("this is test error msg1");
        String msg = messageSource.getMessage("welcome", null, Locale.ENGLISH);

        logger.error("this is test XseedSettings msg" );
        return "Greetings from Spring Boot!";
    }
}
