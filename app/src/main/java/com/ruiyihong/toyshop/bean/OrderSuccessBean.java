/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.bean;

/**
 * Created by Burt on 2017/8/16 0016.
 */

public class OrderSuccessBean {

    /**
     * status : 1
     * msg : 订单生成成功
     * uid : 23
     * hao : c90432345e84fba6
     * total : 4512
     */

    private int status;
    private String msg;
    private String uid;
    private String hao;
    private long total;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHao() {
        return hao;
    }

    public void setHao(String hao) {
        this.hao = hao;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
