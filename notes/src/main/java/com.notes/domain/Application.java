package com.notes.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by claire on 2019-08-09 - 18:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ExcelTarget("申请表")
public class Application {
    @Excel(name = "申请人",width = 25)
    private String applicant;
    private String approver;
    private Date applyTimeFrom;
    private Date applyTimeTo;
    private Double days;
    private String reason;
}
