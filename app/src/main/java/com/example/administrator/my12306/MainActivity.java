package com.example.administrator.my12306;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.DatePicker;
import android.widget.ImageView;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class MainActivity extends FragmentActivity
{
    private ImageView imageView = null;
    private ActionBar actionBar = null;
    private ViewPager viewPager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewpagerInit();
        actionbarInit();
    }
    void actionbarInit()
    {
        //获取实例
        actionBar = getActionBar();
        //选择tab模式
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //增加tab
        actionBar.addTab(actionBar.newTab().setText("车票预订").setTabListener(new MyTabListener()));
        actionBar.addTab(actionBar.newTab().setText("订单").setTabListener(new MyTabListener()));
        actionBar.addTab(actionBar.newTab().setText("@我的").setTabListener(new MyTabListener()));
    }
    class MyTabListener implements ActionBar.TabListener
    {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            Log.v("hehe", String.valueOf(tab.getPosition()));
            //同步viewpager
            viewPager.setCurrentItem(tab.getPosition());
        }
        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
        {

        }
        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
        {

        }
    }
    void viewpagerInit()
    {
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new MyPageListener());
    }
    class MyPagerAdapter extends FragmentPagerAdapter
    {
        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
                {
                    Fragment fragment = null;
                    switch (position)
                    {
                        case 0: fragment = new TicketFragment();break;
                        case 1: fragment = new OrderFragment();break;
                        case 2: fragment = new MyFragment();break;
                        default:break;
                    }
            return fragment;
        }
        @Override
        public int getCount()
        {
            return 3;
        }
    }
    class MyPageListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageSelected(int position)
        {
            //actiobar同步
            actionBar.setSelectedNavigationItem(position);	//修改tab的位置
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

        }
        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    }
}
