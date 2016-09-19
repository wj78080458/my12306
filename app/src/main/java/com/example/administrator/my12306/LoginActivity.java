package com.example.administrator.my12306;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/9/18 0018.
 */
public class LoginActivity extends Activity
{
    private EditText userNameEt = null;
    private EditText userPassEt = null;
    private Button btnLogin = null;
    private CheckBox autoLogin = null;
    private String useName = null;
    private String userPass = null;
    private boolean isRemeber = true;
    private SharedPreferences preferences = null;
    private boolean isAutoLogin = true;
    SharedPreferences pref = null;
    SharedPreferences.Editor editor = null; // 编辑器
    private String line = null;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        Log.v("hehe","login");
        pref = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        editor = pref.edit();
        viewInit();
        //自动登录
        autoLogin();
    }
    void viewInit()
    {
        userNameEt = (EditText)findViewById(R.id.edtUsername);
        userPassEt = (EditText)findViewById(R.id.edtPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        autoLogin = (CheckBox)findViewById(R.id.ckLogin);
        btnLogin.setOnClickListener(new BtnListener());
    }
    class BtnListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            //获取登录用户名和密码
            useName = userNameEt.getText().toString();
            userPass = userPassEt.getText().toString();
            //比较,返回一个flag，标记是否可以登录

            //如果可以登录，跳转MainActivity，保存用户名密码
            isRemeber = autoLogin.isChecked();
            if(isRemeber)
            {
                //保存用户名和密码
                isAutoLogin = true;
                editor.putString("username", userNameEt.getText()
                        .toString());
                editor.putString("password", userPassEt.getText()
                        .toString());
            }
            else
                isAutoLogin = false;
            //保存自动登录标志，下次登录可以读取此标记，用来判断是否要自动登录
            editor.putBoolean("isAuto", isAutoLogin);
            editor.commit(); // 一定要提交
            //跳转
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
    void autoLogin()
    {
        //读取数据库的标志，如果之前已经勾选“自动登录”，那么就直接登录
        isAutoLogin = pref.getBoolean("isAuto", false);
        if(isAutoLogin)
        {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
