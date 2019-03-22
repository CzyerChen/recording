package com.frame.springboot.task;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -03 - 22 18:57
 */
public class CronInfo {

    private int id;
    private String cronClass;
    private String cronExpr;
    private String cronParam;
    private String remark;
    private boolean status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCronClass() {
        return cronClass;
    }

    public void setCronClass(String cronClass) {
        this.cronClass = cronClass;
    }

    public String getCronExpr() {
        return cronExpr;
    }

    public void setCronExpr(String cronExpr) {
        this.cronExpr = cronExpr;
    }

    public String getCronParam() {
        return cronParam;
    }

    public void setCronParam(String cronParam) {
        this.cronParam = cronParam;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
