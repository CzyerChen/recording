package com.frame.springboot.controller;

import com.frame.springboot.task.DynamicSchedulingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 22 19:18
 */
@RequestMapping("/dynamic")
@RestController
public class DynamicCronController {

    @Autowired
    private DynamicSchedulingConfig dynamicSchedulingConfig;


    @RequestMapping(value = "/change",method = RequestMethod.GET)
    public String changeCron(@PathVariable String id,@PathVariable String cron){
        //修改数据库配置
        //调用计划配置管理器，重启该计划
        return "SUCCESS";
    }
}
