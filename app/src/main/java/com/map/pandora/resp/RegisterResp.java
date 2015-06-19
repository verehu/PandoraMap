package com.map.pandora.resp;

/**
 * @author jayce
 * @date 2015/05/29
 */
public class RegisterResp extends BaseResp{

    public static final int REGISTER_OK=1;
    public static final int REGISTER_ERROR=0;

    private int resultcode;
    private String msg;

    public int getResultcode() {
        return resultcode;
    }

    public void setResultcode(int resultcode) {
        this.resultcode = resultcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isRegisterSuccess(){
        return resultcode == REGISTER_OK;
    }
}
