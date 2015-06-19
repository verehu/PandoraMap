package com.map.pandora;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.map.pandora.contain.Constant;
import com.map.pandora.contain.Environment;
import com.map.pandora.resp.RegisterResp;
import com.map.pandora.util.HttpHandler;
import com.map.pandora.util.HttpUtil;
import com.map.pandora.util.PandoraParams;
import com.map.pandora.util.ValidateUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * @author jayce
 * @date 2015/05/28
 */
@EActivity(R.layout.activity_regi_indentify_pass)
public class RegiIndentifyAndPassActivity extends Activity implements Handler.Callback {

    public static final String TAG="RegiIndentifyAndPassActivity";

    public static final String SMSKEY = "7bfe2fec0a60";
    public static final String SMSSECRET = "0f7c263c5f0e24f9428fbc9e53257c51";

    public static final String CODE = "86";

    @ViewById
    TextView tv_phone, tv_identify_notify;
    @ViewById
    EditText et_identifynum, et_password;


    ProgressDialog pbarDialog;


    String phonenum;

    boolean identify_num_ok;

    @AfterViews
    void init() {
        initView();
        identify_num_ok=false;
        // 初始化短信SDK
        SMSSDK.initSDK(this, SMSKEY, SMSSECRET);
        final Handler handler = new Handler(this);
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };

        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
        SMSSDK.getVerificationCode(CODE, phonenum, null);
    }

    void initView() {
        phonenum = getIntent().getStringExtra("phonenumber");
        tv_phone.setText(phonenum);
        tv_identify_notify.setText(Html.fromHtml(getResources().getString(R.string.smssdk_make_sure_mobile_detail)));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    @Override
    public boolean handleMessage(Message msg) {
        int event = msg.arg1;
        int result = msg.arg2;
        Object data = msg.obj;

        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
            /** 提交验证码 */
            if (result == SMSSDK.RESULT_COMPLETE) {
                identify_num_ok = true;
                et_identifynum.setEnabled(false);
                register();
            } else if(result == SMSSDK.RESULT_ERROR) {
                Toast.makeText(getBaseContext(), "验证码不正确", Toast.LENGTH_LONG).show();
            }else if(result == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                tv_identify_notify.setText(Html.fromHtml(getResources().getString(R.string.smssdk_send_mobile_detail)));
            }else{
                Log.i(TAG,"result:"+result);
            }
        }
        return true;
    }

    public void onBackPressed(View view) {
        finish();
    }

    public void to_register(View view) {
        pbarDialog = android.app.ProgressDialog.show(this, "", "正在注册");
        if(identify_num_ok){
            register();
        }else {
            SMSSDK.submitVerificationCode(CODE, phonenum, et_identifynum.getText().toString());
        }
    }

    void register(){
        String password=et_password.getText().toString();
        if (ValidateUtil.password(this,password)) {

            PandoraParams params=new PandoraParams();
            params.add("phonenum",phonenum);
            params.add("password",password);
            HttpUtil.post(Constant.REGISTER,params, new HttpHandler(){
                @Override
                public void onSuccess(String response) {

                    RegisterResp resp=new Gson().fromJson(response,RegisterResp.class);
                    Toast.makeText(getBaseContext(), resp.getMsg(), Toast.LENGTH_LONG).show();
                    if(resp.isRegisterSuccess()){
                        //todo 注册请求
                        Environment.setPhonenum(getBaseContext(),phonenum);
                        Environment.setLoginState(getBaseContext(),true);

                        Intent intent=new Intent(RegiIndentifyAndPassActivity.this, PersonalActivity_.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    pbarDialog.dismiss();
                }
            });
        }
    }
}
