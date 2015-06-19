package com.map.pandora;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.map.pandora.util.ValidateUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * @author jayce
 * @date 2015/05/27
 */
@EActivity(R.layout.activity_regi_phone_input)
public class RegiPhoneInputActivity extends Activity {

    @ViewById
    EditText et_phonenum;

    @AfterViews
    void init(){

    }



    public void onBackPressed(View view){
        finish();
    }

    public void to_input_indentify_num(View view){
        if(ValidateUtil.phone(this,et_phonenum.getText().toString().trim().replaceAll("\\s*", ""))) {
            Intent intent = new Intent(this, RegiIndentifyAndPassActivity_.class);
            intent.putExtra("phonenumber", et_phonenum.getText().toString().trim().replaceAll("\\s*", ""));
            startActivity(intent);
        }
    }
}
