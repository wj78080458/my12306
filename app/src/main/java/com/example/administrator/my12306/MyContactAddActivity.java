package com.example.administrator.my12306;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class MyContactAddActivity extends Activity
{
    private ListView listView = null;
    private Button btn = null;
    List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String,Object>>();
    HashMap<String,Object> map = null;
    private String[] keyStr = {"姓名","证件类型","证件号码","乘客类型","联系电话"};
    private String [] info = {"","","","",""};
    private MyAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact_add);
        //修改标题
        getActionBar().setTitle("增加联系人");
        viewInit();
    }
    void viewInit()
    {
        adapter = new MyAdapter(MyContactAddActivity.this, R.layout.item_my_contact_add, info);
        listView = (ListView)findViewById(R.id.lvMyContactAdd);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyOnItemClickListener());
        btn = (Button)findViewById(R.id.btnMyContactAddSave);
    }
    class MyAdapter extends BaseAdapter
    {
        View view;
        LayoutInflater inflater;
        Context context;
        int resID;
        String[] value = new String[5];
        public MyAdapter(Context context, int resID, String[] value)
        {
            this.context = context;
            this.resID = resID;
            inflater = LayoutInflater.from(context);
            this.value = value;
        }
        @Override
        public int getCount()
        {
            return 5;
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
            TextView keyTv = (TextView)view.findViewById(R.id.tvMyContactEditKey);
            TextView valueTv = (TextView)view.findViewById(R.id.tvMyContactEditValue);
            ImageView imageView = (ImageView)view.findViewById(R.id.imgMyContactEditFlg);
            //修改数据  .setxxx()
            keyTv.setText(keyStr[position]);
            valueTv.setText(value[position]);
            imageView.setImageResource(R.drawable.forward_25);
            return view;
        }
    }
    class MyOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            final String[] cardType = new String[]{"身份证", "港澳台通信证", "护照"};
            final String[] nameType = new String[]{"儿童","学生","军人","成人",};
            final AlertDialog.Builder builder =  new AlertDialog.Builder(MyContactAddActivity.this);
            final View dialogView = LayoutInflater.from(MyContactAddActivity.this).inflate(R.layout.dialog,null);
            //根据不同的情况弹出不同的对话框
            switch (position)
            {
                case 0:builder.setTitle("请输入姓名")
                        .setView(dialogView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                EditText et = ((EditText) dialogView.findViewById(R.id.et));
                                info[0] = et.getText().toString();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                            }
                        });
                    break;
                case 1:builder.setTitle("请选择证件类型")
                        .setSingleChoiceItems(cardType, 0, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                info[1] = cardType[which];
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        break;
                case 2:builder.setTitle("请输入证件号码")
                        .setView(dialogView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                EditText et = ((EditText) dialogView.findViewById(R.id.et));
                                info[2] = et.getText().toString();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                            }
                        });
                    break;
                case 3:builder.setTitle("请选择乘客类型")
                        .setSingleChoiceItems(nameType, 0, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                info[3] = nameType[which];
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });break;
                case 4:builder.setTitle("请输入电话号码")
                        .setView(dialogView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                EditText et = ((EditText) dialogView.findViewById(R.id.et));
                                info[4] = et.getText().toString();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                            }
                        });
                    break;
                default:break;
            }
            builder.show();
        }
    }
}
