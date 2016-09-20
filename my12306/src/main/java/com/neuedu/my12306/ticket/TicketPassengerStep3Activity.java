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
import com.neuedu.my12306.bean.Order;
import com.neuedu.my12306.bean.Passenger;
import com.neuedu.my12306.bean.Seat;
import com.neuedu.my12306.bean.Train;
import com.neuedu.my12306.utils.CONSTANT;
import com.neuedu.my12306.utils.NetUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TicketPassengerStep3Activity extends Activity {
	ListView lvTicketPassengerStep3;
	List<Map<String, Object>> data = null;
	TextView tvTicketPassengerStep3PassengerList;
	TextView tvTicketPassengerStep3Submit;

	TextView tvTicketPassengerStep3StationFrom;
	TextView tvTicketPassengerStep3StationTo;
	TextView tvTicketPassengerStep3TimeFrom;
	TextView tvTicketPassengerStep3TimeTo;
	TextView tvTicketPassengerStep3TrainNo;
	TextView tvTicketPassengerStep3Date;

	TextView tvTicketPassengerStep3SeatName;
	TextView tvTicketPassengerStep3SeatPrice;

	TextView tvTicketPassengerStep3OrderSum;

	Train train = null;
	Seat seat = null;
	ProgressDialog pDialog = null;
	ArrayList<Passenger> returnData = null;

	TicketPassengerStep3Adapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_passenger_step3);

		lvTicketPassengerStep3 = (ListView) findViewById(R.id.lvTicketPassengerStep3);
		tvTicketPassengerStep3PassengerList = (TextView) findViewById(R.id.tvTicketPassengerStep3PassengerList);
		tvTicketPassengerStep3Submit = (TextView) findViewById(R.id.tvTicketPassengerStep3Submit);

		tvTicketPassengerStep3StationFrom = (TextView) findViewById(R.id.tvTicketPassengerStep3StationFrom);
		tvTicketPassengerStep3StationTo = (TextView) findViewById(R.id.tvTicketPassengerStep3StationTo);
		tvTicketPassengerStep3TimeFrom = (TextView) findViewById(R.id.tvTicketPassengerStep3TimeFrom);
		tvTicketPassengerStep3TimeTo = (TextView) findViewById(R.id.tvTicketPassengerStep3TimeTo);
		tvTicketPassengerStep3TrainNo = (TextView) findViewById(R.id.tvTicketPassengerStep3TrainNo);
		tvTicketPassengerStep3Date = (TextView) findViewById(R.id.tvTicketPassengerStep3Date);
		tvTicketPassengerStep3SeatName = (TextView) findViewById(R.id.tvTicketPassengerStep3SeatName);
		tvTicketPassengerStep3SeatPrice = (TextView) findViewById(R.id.tvTicketPassengerStep3SeatPrice);
		tvTicketPassengerStep3OrderSum = (TextView) findViewById(R.id.tvTicketPassengerStep3OrderSum);

		train = (Train) getIntent().getSerializableExtra("train");
		seat = (Seat) getIntent().getSerializableExtra("seat");

		// 赋值
		tvTicketPassengerStep3StationFrom.setText(train.getFromStationName());
		tvTicketPassengerStep3StationTo.setText(train.getToStationName());
		tvTicketPassengerStep3TimeFrom.setText(train.getStartTime());
		tvTicketPassengerStep3TimeTo.setText(train.getArriveTime());
		tvTicketPassengerStep3TrainNo.setText(train.getTrainNo());
		tvTicketPassengerStep3Date.setText(train.getStartTrainDate() + "("
				+ train.getDayDifference() + "日)");
		tvTicketPassengerStep3SeatName.setText(seat.getSeatName() + "("
				+ seat.getSeatNum() + "张)");
		tvTicketPassengerStep3SeatPrice.setText("￥" + seat.getSeatPrice());

		data = new ArrayList<Map<String, Object>>();

		// Map<String, Object> row1 = new HashMap<String, Object>();
		// row1.put("name", "李四");
		// row1.put("idCard", "身份证:10010019990101012X");
		// row1.put("tel", "电话:13812345578");
		// data.add(row1);
		//
		// Map<String, Object> row2 = new HashMap<String, Object>();
		// row2.put("name", "订单");
		// row2.put("idCard", "身份证:20010019990101012X");
		// row2.put("tel", "电话:13912345578");
		// data.add(row2);
		//
		// Map<String, Object> row3 = new HashMap<String, Object>();
		// row3.put("name", "中午");
		// row3.put("idCard", "身份证:30010019990101012X");
		// row3.put("tel", "电话:13712345578");
		// data.add(row3);

		adapter = new TicketPassengerStep3Adapter();
		lvTicketPassengerStep3.setAdapter(adapter);

		tvTicketPassengerStep3PassengerList
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(TicketPassengerStep3Activity.this,
								TicketPassengerListStep3Activity.class);
						startActivityForResult(intent, 100);
					}
				});

		tvTicketPassengerStep3Submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!NetUtils.check(TicketPassengerStep3Activity.this)) {
					Toast.makeText(TicketPassengerStep3Activity.this,
							getString(R.string.network_check),
							Toast.LENGTH_SHORT).show();
					return; // 后续代码不执行
				}

				new TicketPassengerStep3Task().execute();
			}
		});
	}

	class TicketPassengerStep3Task extends AsyncTask<String, String, Object> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(TicketPassengerStep3Activity.this,
					null, "正在加载中...", false, true);
		}

		@Override
		protected Object doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Order");

			// 发送请求
			DefaultHttpClient client = new DefaultHttpClient();
			String result = "";

			try {
				// jsessionid
				SharedPreferences pref = getSharedPreferences("user",
						Context.MODE_PRIVATE);
				String value = pref.getString("Cookie", "");
				// 1
				BasicHeader header = new BasicHeader("Cookie", value);
				post.setHeader(header);

				// 参数
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("trainNo", train.getTrainNo()));
				params.add(new BasicNameValuePair("startTrainDate", train
						.getStartTrainDate()));
				params.add(new BasicNameValuePair("seatName", seat
						.getSeatName()));

				// 数组
				for (Passenger passenger : returnData) {
					params.add(new BasicNameValuePair("id", passenger.getId()));
					params.add(new BasicNameValuePair("idType", passenger
							.getIdType()));
				}

				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
						"UTF-8");
				post.setEntity(entity);

				// 超时设置
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT,
						CONSTANT.REQUEST_TIMEOUT);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);

				HttpResponse response = client.execute(post);

				// 处理结果
				if (response.getStatusLine().getStatusCode() == 200) {

					String json = EntityUtils.toString(response.getEntity());
					Gson gson = new GsonBuilder().create();
					Order order = gson.fromJson(json, Order.class);

					return order;

				} else {
					result = "2";
				}

				client.getConnectionManager().shutdown();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = "2";
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = "2";
			} catch (IOException e) {
				e.printStackTrace();
				result = "2";
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = "2";
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				result = "3"; // 重新登录
			}

			return result;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			data.clear();

			if (pDialog != null) {
				pDialog.dismiss();
			}

			if (result instanceof Order) {
				Intent intent = new Intent();
				intent.putExtra("order", (Order) result);
				intent.setClass(TicketPassengerStep3Activity.this,
						TicketOrderSuccessStep4Activity.class);
				startActivity(intent);

			} else if (result instanceof String) {
				if ("3".equals(result.toString())) {
					Toast.makeText(TicketPassengerStep3Activity.this, "请重新登录",
							Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(TicketPassengerStep3Activity.this,
							"服务器错误，请重试", Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		data.clear();

		returnData = (ArrayList<Passenger>) intent
				.getSerializableExtra("passengers");

		double sum = 0.0;

		for (Passenger passenger : returnData) {
			Map<String, Object> row1 = new HashMap<String, Object>();
			row1.put("name", passenger.getName());
			row1.put("idCard", passenger.getIdType() + ":" + passenger.getId());
			row1.put("tel", "电话:" + passenger.getTel());
			data.add(row1);

			// 订单总额
			if ("学生".equals(passenger.getType())
					|| "儿童".equals(passenger.getType())) {
				sum += seat.getSeatPrice() / 2;
			} else {
				sum += seat.getSeatPrice();
			}
		}
		adapter.notifyDataSetChanged();
		tvTicketPassengerStep3OrderSum.setText("订单总额:￥" + sum + "元");
	}

	class TicketPassengerStep3Adapter extends BaseAdapter {

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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = LayoutInflater.from(
						TicketPassengerStep3Activity.this).inflate(
						R.layout.item_ticket_passenger_step3, null);

				holder.tvTicketPassengerStep3Name = (TextView) convertView
						.findViewById(R.id.tvTicketPassengerStep3Name);
				holder.tvTicketPassengerStep3IdCard = (TextView) convertView
						.findViewById(R.id.tvTicketPassengerStep3IdCard);
				holder.tvTicketPassengerStep3Tel = (TextView) convertView
						.findViewById(R.id.tvTicketPassengerStep3Tel);
				holder.imgTicketPassengerStep3Del = (ImageView) convertView
						.findViewById(R.id.imgTicketPassengerStep3Del);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 赋值
			holder.tvTicketPassengerStep3Name.setText(data.get(position)
					.get("name").toString());
			holder.tvTicketPassengerStep3IdCard.setText(data.get(position)
					.get("idCard").toString());
			holder.tvTicketPassengerStep3Tel.setText(data.get(position)
					.get("tel").toString());
			holder.imgTicketPassengerStep3Del
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							data.remove(position);

							if (returnData != null) {
								returnData.remove(position);

								double sum = 0.0;

								for (Passenger passenger : returnData) {
									// 订单总额
									if ("学生".equals(passenger.getType())
											|| "儿童".equals(passenger.getType())) {
										sum += seat.getSeatPrice() / 2;
									} else {
										sum += seat.getSeatPrice();
									}
								}
								tvTicketPassengerStep3OrderSum.setText("订单总额:￥"
										+ sum + "元");
							}

							notifyDataSetChanged();
						}
					});

			return convertView;
		}

	}

	class ViewHolder {
		TextView tvTicketPassengerStep3Name;
		TextView tvTicketPassengerStep3IdCard;
		TextView tvTicketPassengerStep3Tel;
		ImageView imgTicketPassengerStep3Del;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ticket_passenger_step3, menu);
		return true;
	}

}
