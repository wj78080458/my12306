package com.neuedu.my12306.order;

import com.neuedu.my12306.R;
import com.neuedu.my12306.R.layout;
import com.neuedu.my12306.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class OrderPayDoneActivity extends Activity {
	Button btnOrderPayDone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_pay_done);
		
		btnOrderPayDone = (Button)findViewById(R.id.btnOrderPayDone);
		btnOrderPayDone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageView view = new ImageView(OrderPayDoneActivity.this);
				view.setImageDrawable(getResources().getDrawable(R.drawable.qr));
				
				new AlertDialog.Builder(OrderPayDoneActivity.this)
				.setTitle("查看二维码")
				.setView(view)
				.setPositiveButton("确定", null)
				.show();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order_pay_done, menu);
		return true;
	}

}
