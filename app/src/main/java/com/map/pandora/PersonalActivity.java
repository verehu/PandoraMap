package com.map.pandora;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.map.pandora.contain.Environment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.Text;

/**
 * @author jayce
 * @date 2015/05/26
 */
@EActivity(R.layout.activity_personal)
public class PersonalActivity extends Activity{

    Context context;
    @ViewById
    TextView tv_phonenum, tv_logout;


    @AfterViews
    void init(){
        context = getBaseContext();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateViewByLoginState();
    }

    public void onBackPressed(View view){
        finish();
    }

    public void onAvatarClicked(View view){
        if(Environment.isLogin(context)){

        }else{
            Intent intent=new Intent(this,LoginActivity_.class);
            startActivity(intent);
        }
    }

    public void onLogout(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("是否要注销登录？");
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //todo 注销请求
                Environment.setLoginState(context,false);
                Environment.setPhonenum(context,"");
                updateViewByLoginState();
            }
        }).setNegativeButton("取消",null);
        builder.show();
    }

    void updateViewByLoginState(){
        if(Environment.isLogin(context)){
            tv_phonenum.setText(Environment.getPhonenum(context));
            tv_logout.setVisibility(View.VISIBLE);
        }else{
            tv_phonenum.setText("未登录");
            tv_logout.setVisibility(View.GONE);
        }
    }
}
