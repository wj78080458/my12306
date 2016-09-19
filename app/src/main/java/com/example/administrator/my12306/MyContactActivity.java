package com.example.administrator.my12306;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class MyContactActivity extends Activity
{
    private ListView listView = null;
    List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String,Object>>();
    HashMap<String,Object> map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        //修改标题
        getActionBar().setTitle("我的联系人");
        dataInit();
        viewInit();

    }
    void dataInit()
    {
        //将一个人的所有信息放入map
        map = new HashMap<String, Object>();
        map.put("name","张三");
        map.put("cardType","身份证");
        map.put("cardID","123456789909090909");
        map.put("nameType","学生");
        map.put("telNum","11110101010");
        //将map增加到list
        mapList.add(map);
        map = new HashMap<String, Object>();
        map.put("name","李四");
        map.put("cardType","身份证");
        map.put("cardID","123456789909090909");
        map.put("nameType","乘客");
        map.put("telNum","22220202020");
        mapList.add(map);
    }
    void viewInit()
    {
        listView = (ListView)findViewById(R.id.lvMyContact);
        listView.setAdapter(new MyAdapter(MyContactActivity.this, R.layout.item_my_contact,mapList));
        listView.setOnItemClickListener(new MyOnItemClickListener());
    }
    class MyAdapter extends BaseAdapter
    {
        View view;
        LayoutInflater inflater;
        Context context;
        int resID;
        List<HashMap<String, Object>> list;
        public MyAdapter(Context context, int resID, List<HashMap<String, Object>> list )
        {
            this.context = context;
            this.resID = resID;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount()
        {
            return list.size();
        }
        @Override
        public Object getItem(int position)
        {
            return null;
        }
        @Override
        public long getItemId(int position)
        {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            //加载布局 inflate
            view = inflater.inflate(resID, null);
            //找到控件 findViewById
            TextView nameText = (TextView)view.findViewById(R.id.tvNameContact);
            TextView cardIdText = (TextView)view.findViewById(R.id.tvIdCardContact);
            TextView telNumText = (TextView)view.findViewById(R.id.tvTelContact);
            //修改数据  .setxxx()
            nameText.setText(list.get(position).get("name").toString());
            cardIdText.setText(list.get(position).get("cardID").toString());
            telNumText.setText(list.get(position).get("telNum").toString());
            return view;
        }
    }
    class MyOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Intent intent = new Intent();
            intent.setClass(MyContactActivity.this, );
            startActivity(intent);
        }
    }
}
