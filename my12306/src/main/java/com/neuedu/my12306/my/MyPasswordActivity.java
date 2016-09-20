package com.neuedu.my12306.my;

import com.neuedu.my12306.R;
import com.neuedu.my12306.R.layout;
import com.neuedu.my12306.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MyPasswordActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_password);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_password, menu);
		return true;
	}

}
