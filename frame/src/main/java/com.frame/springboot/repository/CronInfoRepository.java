package com.frame.springboot.repository;

import com.frame.springboot.task.CronInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CronInfoRepository extends JpaRepository<CronInfo,Integer> {

    CronInfo findById(int id);
    List<CronInfo> findByIdIn(List<Integer> ids);
}
