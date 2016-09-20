package com.neuedu.my12306.ticket;

import com.neuedu.my12306.MainActivity;
import com.neuedu.my12306.R;
import com.neuedu.my12306.R.layout;
import com.neuedu.my12306.R.menu;
import com.neuedu.my12306.bean.Order;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TicketOrderSuccessStep4Activity extends Activity {

	TextView tvTicketOrderSuccessStep4Back;
	TextView tvTicketOrderSuccessStep4Pay;
	TextView tvTicketOrderSuccessStep4;
	Order order = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_order_success_step4);

		tvTicketOrderSuccessStep4Back = (TextView) findViewById(R.id.tvTicketOrderSuccessStep4Back);
		tvTicketOrderSuccessStep4Pay = (TextView) findViewById(R.id.tvTicketOrderSuccessStep4Pay);
		tvTicketOrderSuccessStep4 = (TextView) findViewById(R.id.tvTicketOrderSuccessStep4);

		Intent intent = getIntent();
		order = (Order) intent.getSerializableExtra("order");
		tvTicketOrderSuccessStep4.setText(order.toString());

		tvTicketOrderSuccessStep4Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TicketOrderSuccessStep4Activity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		tvTicketOrderSuccessStep4Pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("order", order);
				intent.setClass(TicketOrderSuccessStep4Activity.this,
						TicketPaySuccessStep5Activity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(TicketOrderSuccessStep4Activity.this,
					MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ticket_order_success_step4, menu);
		return true;
	}

}
