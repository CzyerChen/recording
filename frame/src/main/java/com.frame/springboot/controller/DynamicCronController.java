package com.frame.springboot.controller;

import com.frame.springboot.repository.CronInfoRepository;
import com.frame.springboot.task.CronInfo;
import com.frame.springboot.task.DynamicSchedulingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @Autowired
    private CronInfoRepository cronInfoRepository;


    @RequestMapping(value = "/change/{ids}/{cron}", method = RequestMethod.GET)
    public String changeCron(@PathVariable String ids, @PathVariable String cron) {
        List<String> idList = Arrays.asList(ids.split(","));
        //修改数据库配置
        List<Integer> integers = new ArrayList<>();
        for(String id : idList){
            integers.add(Integer.valueOf(id));
        }
        List<CronInfo> cronList = cronInfoRepository.findByIdIn(integers);
        String cronExpr = "0/10 * * * * ?";
        for(CronInfo cronInfo : cronList){
            cronInfo.setCronExpr(cronExpr);
        }
        //更新
        cronInfoRepository.saveAll(cronList);
        //调用计划配置管理器，重启该计划
        dynamicSchedulingConfig.startCronJob(cronList);
        return "SUCCESS";
    }


    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public String getCronInfoById(@PathVariable int id) {
        CronInfo byId = cronInfoRepository.findById(id);
        return byId.toString();
    }

    @RequestMapping(value = "/start/{id}", method = RequestMethod.GET)
    public String startCron(@PathVariable int id) {
        List<CronInfo> list = new ArrayList<>();
        CronInfo byId = cronInfoRepository.findById(id);
        if (byId != null) {
            list.add(byId);
        }
        if (list.size() != 0) {
            dynamicSchedulingConfig.startCronJob(list);
        }
        return "SUCCESS";
    }



}
