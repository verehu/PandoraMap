package com.map.pandora.resp;

/**
 * 返回解析类  基础类
 *
 * @author jayce
 * @date 2015/05/29
 */
public class BaseResp {

    public static final int ISLOGIN = 1;
    public static final int ISNOTLOGIN = 0;

    private String respstatue;
    private int islogin;
    private String errormsg;

    public String getRespstatue() {
        return respstatue;
    }

    public void setRespstatue(String respstatue) {
        this.respstatue = respstatue;
    }

    public int getIslogin() {
        return islogin;
    }

    public void setIslogin(int islogin) {
        this.islogin = islogin;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public boolean isLogin(){
        return islogin == ISLOGIN;
    }
}
