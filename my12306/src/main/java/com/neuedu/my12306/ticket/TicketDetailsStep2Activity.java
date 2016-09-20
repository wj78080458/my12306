package com.neuedu.my12306.ticket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.neuedu.my12306.R;
import com.neuedu.my12306.R.layout;
import com.neuedu.my12306.R.menu;
import com.neuedu.my12306.bean.Seat;
import com.neuedu.my12306.bean.Train;
import com.neuedu.my12306.utils.CONSTANT;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TicketDetailsStep2Activity extends Activity {

	ListView lvTicketDetailsStep2;
	List<Map<String, Object>> data = null;
	TextView tvTicketDetailsStep2TrainNo;
	TextView tvTicketDetailsStep2DurationTime;
	TextView tvTicketDetailsStep2After;
	TextView tvTicketDetailsStep2Before;
	ProgressDialog pDialog = null;
	Train train = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_details_step2);
		lvTicketDetailsStep2 = (ListView) findViewById(R.id.lvTicketDetailsStep2);
		tvTicketDetailsStep2TrainNo = (TextView) findViewById(R.id.tvTicketDetailsStep2TrainNo);
		tvTicketDetailsStep2DurationTime = (TextView) findViewById(R.id.tvTicketDetailsStep2DurationTime);
		tvTicketDetailsStep2Before = (TextView) findViewById(R.id.tvTicketDetailsStep2Before);
		tvTicketDetailsStep2After = (TextView) findViewById(R.id.tvTicketDetailsStep2After);

		train = (Train) getIntent().getSerializableExtra("train");
		tvTicketDetailsStep2TrainNo.setText(train.getTrainNo());
		tvTicketDetailsStep2DurationTime.setText(train.getStartTime() + " - "
				+ train.getArriveTime() + ", 历时" + train.getDurationTime());

		data = new ArrayList<Map<String, Object>>();

		Map<String, Seat> seats = train.getSeats();
		for (String key : seats.keySet()) {
			Seat seat = seats.get(key);

			Map<String, Object> row1 = new HashMap<String, Object>();
			row1.put("seatName", seat.getSeatName());
			row1.put("seatNum", seat.getSeatNum() + "张");
			row1.put("seatPrice", "￥" + seat.getSeatPrice());
			data.add(row1);
		}

		lvTicketDetailsStep2.setAdapter(new TicketDetailsStep2Adapter());

		// 前一天
		tvTicketDetailsStep2Before.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 略
			}
		});

		// 后一天
		tvTicketDetailsStep2After.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 略
			}
		});

	}

	class TicketDetailsStep2Adapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// 如果组件对象不存在，创建；存在；取出（行）
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				// 创建convertView(加载行布局)
				convertView = LayoutInflater.from(
						TicketDetailsStep2Activity.this).inflate(
						R.layout.item_ticket_details_step2, null);

				holder.tvTicketDetailsStep2SeatName = (TextView) convertView
						.findViewById(R.id.tvTicketDetailsStep2SeatName);
				holder.tvTicketDetailsStep2SeatNum = (TextView) convertView
						.findViewById(R.id.tvTicketDetailsStep2SeatNum);
				holder.tvTicketDetailsStep2SeatPrice = (TextView) convertView
						.findViewById(R.id.tvTicketDetailsStep2SeatPrice);
				holder.btnTicketDetailsStep2Order = (Button) convertView
						.findViewById(R.id.btnTicketDetailsStep2Order);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 赋值
			holder.tvTicketDetailsStep2SeatName.setText(data.get(position)
					.get("seatName").toString());
			holder.tvTicketDetailsStep2SeatNum.setText(data.get(position)
					.get("seatNum").toString());
			holder.tvTicketDetailsStep2SeatPrice.setText(data.get(position)
					.get("seatPrice").toString());
			holder.btnTicketDetailsStep2Order
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setClass(TicketDetailsStep2Activity.this,
									TicketPassengerStep3Activity.class);
							intent.putExtra("train", train);
							
							// key
							String key = data.get(position).get("seatName").toString();
							intent.putExtra("seat", train.getSeats().get(key));
							
							startActivity(intent);
						}
					});

			return convertView;
		}

	}

	class ViewHolder {
		TextView tvTicketDetailsStep2SeatName;
		TextView tvTicketDetailsStep2SeatNum;
		TextView tvTicketDetailsStep2SeatPrice;
		Button btnTicketDetailsStep2Order;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ticket_details_step2, menu);
		return true;
	}

}
