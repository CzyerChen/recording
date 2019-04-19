package com.example.controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -02 - 13 13:32
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public String testPage(ModelMap map){
        map.addAttribute("msg","ssssssssss");
        return "info";
    }

    @RequestMapping(value = "/form")
    public ModelAndView testModel( String name){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name",name);
        modelAndView.setViewName("form");
        return  modelAndView;
    }

}
