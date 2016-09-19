package com.example.administrator.my12306;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class MyContactEditActivity extends Activity
{
    private ListView listView = null;
    List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String,Object>>();
    HashMap<String,Object> map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact_edit);
        //修改标题
        getActionBar().setTitle("编辑联系人信息");
    }

}
