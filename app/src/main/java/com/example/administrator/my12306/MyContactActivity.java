package com.example.administrator.my12306;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    private MyAdapter adapter = null;
    private int pos=0;
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
        adapter = new MyAdapter(MyContactActivity.this, R.layout.item_my_contact,mapList);
        listView = (ListView)findViewById(R.id.lvMyContact);
        listView.setAdapter(adapter);
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
            pos = position;
            Intent intent = new Intent();
            Log.v("hehe", String.valueOf(position));
            //跳转到MyContactEditActivity
            intent.setClass(MyContactActivity.this, MyContactEditActivity.class);
            //传递数据
            intent.putExtra("name", mapList.get(position).get("name").toString());
            intent.putExtra("cardType",mapList.get(position).get("cardType").toString() );
            intent.putExtra("cardID", mapList.get(position).get("cardID").toString());
            intent.putExtra("nameType",mapList.get(position).get("nameType").toString() );
            intent.putExtra("telNum",mapList.get(position).get("telNum").toString() );
            startActivityForResult(intent,2);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.my_contact, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.v("hehe", String.valueOf(item.getItemId()));
        Intent intent = new Intent();
        intent.setClass(MyContactActivity.this,MyContactAddActivity.class );
        //等待新的activity返回结果
        startActivityForResult(intent, 1);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //接收MyContactAddActivity数据
        if(requestCode==1&&resultCode==100)
        {
            String[] info = data.getStringArrayExtra("info");
            map = new HashMap<String, Object>();
            map.put("name",info[0]);
            map.put("cardType",info[1]);
            map.put("cardID",info[2]);
            map.put("nameType",info[3]);
            map.put("telNum",info[4]);
            mapList.add(map);
            //通知适配器修改数据
            adapter.notifyDataSetChanged();
        }
        if(requestCode==2&&resultCode==200)
        {
            String[] info = data.getStringArrayExtra("info");
            map = new HashMap<String, Object>();
            map.put("name",info[0]);
            map.put("cardType",info[1]);
            map.put("cardID",info[2]);
            map.put("nameType",info[3]);
            map.put("telNum",info[4]);
            //修改数据,位置是item被点击的位置，可以从itemClickListner获取位置
            mapList.set(pos ,map);
            //通知适配器修改数据
            adapter.notifyDataSetChanged();
        }
        if(requestCode==2&&resultCode==250)
        {
            //输出数据
            mapList.remove(pos);
            adapter.notifyDataSetChanged();
        }
    }
}
