package com.example.administrator.my12306;

import com.example.administrator.my12306.MainActivity;
import com.example.administrator.my12306.R;
import com.example.administrator.my12306.R.layout;
import com.example.administrator.my12306.R.menu;
import com.example.administrator.my12306.Order;
import com.example.administrator.my12306.ZxingUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class TicketPaySuccessStep5Activity extends Activity {

	Button btnTicketPaySuccessStep5;
	ImageView ivTicketPaySuccessStep5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_pay_success_step5);

		btnTicketPaySuccessStep5 = (Button) findViewById(R.id.btnTicketPaySuccessStep5);
		ivTicketPaySuccessStep5 = (ImageView) findViewById(R.id.ivTicketPaySuccessStep5);

		// ������ά��
		Order order = (Order) getIntent().getSerializableExtra("order");
		ZxingUtils.createQRImage(
				order.getId() + "," + order.getTrain().getTrainNo() + ","
						+ order.getTrain().getStartTrainDate() + ","
						+ order.getPassengerList(), ivTicketPaySuccessStep5,
				160, 160);

		btnTicketPaySuccessStep5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TicketPaySuccessStep5Activity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ticket_pay_success_step5, menu);
		return true;
	}

}
