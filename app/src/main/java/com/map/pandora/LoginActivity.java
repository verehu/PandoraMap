package com.map.pandora;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.map.pandora.contain.Constant;
import com.map.pandora.contain.Environment;
import com.map.pandora.resp.BaseResp;
import com.map.pandora.util.HttpHandler;
import com.map.pandora.util.HttpUtil;
import com.map.pandora.util.PandoraParams;
import com.map.pandora.util.ValidateUtil;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Random;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * @author jayce
 * @date 2015/05/27
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {
    @ViewById
    EditText et_phonenum, et_password;

    ProgressDialog proDialog;

    public void to_register(View view) {
        Intent intent = new Intent(this, RegiPhoneInputActivity_.class);
        startActivity(intent);
    }

    public void onBackPressed(View view) {
        finish();
    }

    public void onLogin(View view) {
        final String phonenum = et_phonenum.getText().toString();
        String password = et_password.getText().toString();
        if (ValidateUtil.phone(this, phonenum) && ValidateUtil.password(this, password)) {

            proDialog = android.app.ProgressDialog.show(this, "", "正在注册");

            PandoraParams params = new PandoraParams();
            params.add("phonenum", phonenum);
            params.add("password", password);
            HttpUtil.post(Constant.LOGIN, params, new HttpHandler() {
                @Override
                public void onSuccess(String response) {
                    BaseResp baseResp = new Gson().fromJson(response, BaseResp.class);
                    Toast.makeText(getBaseContext(), baseResp.getErrormsg(), Toast.LENGTH_LONG).show();
                    if (baseResp.isLogin()) {
                        Environment.setPhonenum(getBaseContext(), phonenum);
                        Environment.setLoginState(getBaseContext(), true);
                        finish();
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    proDialog.dismiss();
                }
            });
        }
    }
}
