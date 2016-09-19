package com.example.administrator.my12306;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Administrator on 2016/9/18 0018.
 */
public class MyFragment extends Fragment
{
    private View view = null;
    private ListView listView = null;
    private Button btn = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_my, container, false);
        viewInit();
        return view;
    }
    void viewInit()
    {
        //找到控件，设置监听器、适配器
        listView = (ListView)view.findViewById(R.id.lvMyList);
        btn = (Button)view.findViewById(R.id.btnLogout);
        listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, new String[]{"我的联系人","我的账户","我的密码"}));
        listView.setOnItemClickListener(new MyOnItemClickListener());
        btn.setOnClickListener(new MyBtnListener());
    }
    class MyOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Intent intent = new Intent();
            //根据item的位置来决定跳转到某个activity
            switch (position)
            {
                case 0:intent.setClass(getContext(), MyContactActivity.class);break;
                case 1:intent.setClass(getContext(), MyAccountActivity.class);break;
                case 2:intent.setClass(getContext(), MyPassActivity.class); break;
                default:break;
            }
            startActivity(intent);
        }
    }
    class MyBtnListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            //清空自动登录标志
            SharedPreferences pref = getContext().getSharedPreferences("user",
                Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isAuto", false);
            editor.commit();
            //清除用户名，密码
            //跳转到登录界面
            Intent intent = new Intent();
            intent.setClass(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }
}
