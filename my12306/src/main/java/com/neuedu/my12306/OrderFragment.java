package com.neuedu.my12306;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
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
import com.google.gson.reflect.TypeToken;
import com.neuedu.my12306.bean.Order;
import com.neuedu.my12306.bean.Passenger;
import com.neuedu.my12306.order.OrderPayDoneActivity;
import com.neuedu.my12306.order.OrderWaitPayActivity;
import com.neuedu.my12306.utils.CONSTANT;
import com.neuedu.my12306.utils.NetUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class OrderFragment extends Fragment {
	TextView tvOrderWaitPay;
	TextView tvOrderAll;
	ListView lvOrder;
	List<Map<String, Object>> data;
	OrderAdapter adapter = null;
	ProgressDialog pDialog = null;
	String status = "0";
	List<Order> orders = null;

	public OrderFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_order, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		tvOrderWaitPay = (TextView) getActivity().findViewById(
				R.id.tvOrderWaitPay);
		tvOrderAll = (TextView) getActivity().findViewById(R.id.tvOrderAll);
		lvOrder = (ListView) getActivity().findViewById(R.id.lvOrder);

		tvOrderWaitPay.setOnClickListener(new OrderHandler());
		tvOrderAll.setOnClickListener(new OrderHandler());

		data = new ArrayList<Map<String, Object>>();
		// Map<String, Object> row1 = new HashMap<String, Object>();
		// row1.put("orderId", "订单编号:20143Yg");
		// row1.put("orderStatus", "未支付");
		// row1.put("orderTrainNo", "G108");
		// row1.put("orderDateFrom", "2014-8-18");
		// row1.put("orderStationFrom", "北京-上海 2人");
		// row1.put("orderPrice", "￥1000.5元");
		// row1.put("orderFlg", R.drawable.forward_25);
		// data.add(row1);

		adapter = new OrderAdapter();
		lvOrder.setAdapter(adapter);

		lvOrder.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String orderStatus = (String) data.get(position).get(
						"orderStatus");

				if ("已支付".equals(orderStatus)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), OrderPayDoneActivity.class);
					startActivity(intent);
				} else if ("未支付".equals(orderStatus)) {
					Intent intent = new Intent();
					intent.putExtra("order", orders.get(position));
					intent.setClass(getActivity(), OrderWaitPayActivity.class);
					startActivity(intent);
				}

			}
		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (!NetUtils.check(getActivity())) {
			Toast.makeText(getActivity(), getString(R.string.network_check),
					Toast.LENGTH_SHORT).show();
			return; // 后续代码不执行
		}

		new OrderTask().execute(status);
	}

	class OrderTask extends AsyncTask<String, String, Object> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(getActivity(), null, "正在加载中...",
					false, true);
		}

		@Override
		protected Object doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/OrderList");

			// 发送请求
			DefaultHttpClient client = new DefaultHttpClient();
			String result = "";

			try {
				// jsessionid
				SharedPreferences pref = getActivity().getSharedPreferences(
						"user", Context.MODE_PRIVATE);
				String value = pref.getString("Cookie", "");
				// 1
				BasicHeader header = new BasicHeader("Cookie", value);
				post.setHeader(header);

				// 参数
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("status", p[0]));

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
					Type listType = new TypeToken<List<Order>>() {
					}.getType();
					List<Order> orders = gson.fromJson(json, listType);
					return orders;

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
				// 服务器挂
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

			if (result instanceof List) {
				orders = (List<Order>) result;

				for (Order order : orders) {
					Map<String, Object> row1 = new HashMap<String, Object>();
					row1.put("orderId", "订单编号:" + order.getId());

					String orderStatus = order.getStatus() == 0 ? "未支付" : order
							.getStatus() == 1 ? "已支付" : "已取消";
					row1.put("orderStatus", orderStatus);
					row1.put("orderTrainNo", order.getTrain().getTrainNo());
					row1.put("orderDateFrom", order.getTrain()
							.getStartTrainDate());
					row1.put("orderStationFrom", order.getTrain()
							.getFromStationName()
							+ "-"
							+ order.getTrain().getToStationName()
							+ " "
							+ order.getPassengerList().size() + "人");
					row1.put("orderPrice", "￥" + order.getOrderPrice() + "元");

					if (order.getStatus() != 2) {
						row1.put("orderFlg", R.drawable.forward_25);
					}
					data.add(row1);
				}

				adapter.notifyDataSetChanged();

			} else if (result instanceof String) {
				if ("3".equals(result.toString())) {
					Toast.makeText(getActivity(), "请重新登录", Toast.LENGTH_SHORT)
							.show();

				} else {
					Toast.makeText(getActivity(), "服务器错误，请重试",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	class OrderAdapter extends BaseAdapter {

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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_order_list, null);

				holder.tvOrderId = (TextView) convertView
						.findViewById(R.id.tvOrderId);
				holder.tvOrderStatus = (TextView) convertView
						.findViewById(R.id.tvOrderStatus);
				holder.tvOrderTrainNo = (TextView) convertView
						.findViewById(R.id.tvOrderTrainNo);
				holder.tvOrderDateFrom = (TextView) convertView
						.findViewById(R.id.tvOrderDateFrom);
				holder.tvOrderStationFrom = (TextView) convertView
						.findViewById(R.id.tvOrderStationFrom);
				holder.tvOrderPrice = (TextView) convertView
						.findViewById(R.id.tvOrderPrice);
				holder.imgOrderFlg = (ImageView) convertView
						.findViewById(R.id.imgOrderFlg);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 赋值
			if (!data.isEmpty()) {
				holder.tvOrderId.setText(data.get(position).get("orderId")
						.toString());

				holder.tvOrderStatus.setText(data.get(position)
						.get("orderStatus").toString());
				if ("未支付".equals(holder.tvOrderStatus.getText().toString())) {
					holder.tvOrderStatus.setTextColor(getResources().getColor(
							R.color.orange));

				} else if ("已支付".equals(holder.tvOrderStatus.getText()
						.toString())) {
					holder.tvOrderStatus.setTextColor(getResources().getColor(
							R.color.blue));
				} else if ("已取消".equals(holder.tvOrderStatus.getText()
						.toString())) {
					holder.tvOrderStatus.setTextColor(getResources().getColor(
							R.color.grey));
				}

				holder.tvOrderTrainNo.setText(data.get(position)
						.get("orderTrainNo").toString());
				holder.tvOrderDateFrom.setText(data.get(position)
						.get("orderDateFrom").toString());
				holder.tvOrderStationFrom.setText(data.get(position)
						.get("orderStationFrom").toString());
				holder.tvOrderPrice.setText(data.get(position)
						.get("orderPrice").toString());

				Integer resId = (Integer) (data.get(position).get("orderFlg"));
				if (resId != null) {
					holder.imgOrderFlg.setImageDrawable(getResources()
							.getDrawable(resId));
				} else {
					holder.imgOrderFlg.setImageDrawable(null);
				}
			}

			return convertView;
		}
	}

	class ViewHolder {
		TextView tvOrderId;
		TextView tvOrderStatus;
		TextView tvOrderTrainNo;
		TextView tvOrderDateFrom;
		TextView tvOrderStationFrom;
		TextView tvOrderPrice;
		ImageView imgOrderFlg;
	}

	class OrderHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			data.clear();
			switch (v.getId()) {
			case R.id.tvOrderWaitPay:
				tvOrderWaitPay
						.setBackgroundResource(R.drawable.cab_background_top_mainbar);
				tvOrderAll.setBackgroundResource(0);

				status = "0";
				new OrderTask().execute(status);

				// Map<String, Object> row1 = new HashMap<String, Object>();
				// row1.put("orderId", "订单编号:20143Yg");
				// row1.put("orderStatus", "未支付");
				// row1.put("orderTrainNo", "G108");
				// row1.put("orderDateFrom", "2014-8-18");
				// row1.put("orderStationFrom", "北京-上海 2人");
				// row1.put("orderPrice", "￥1000.5元");
				// row1.put("orderFlg", R.drawable.forward_25);
				// data.add(row1);
				// adapter.notifyDataSetChanged();
				break;
			case R.id.tvOrderAll:
				tvOrderAll
						.setBackgroundResource(R.drawable.cab_background_top_mainbar);
				tvOrderWaitPay.setBackgroundResource(0);

				status = "1";
				new OrderTask().execute(status);

				// Map<String, Object> row2 = new HashMap<String, Object>();
				// row2.put("orderId", "订单编号:20143Yg");
				// row2.put("orderStatus", "未支付");
				// row2.put("orderTrainNo", "G108");
				// row2.put("orderDateFrom", "2014-8-18");
				// row2.put("orderStationFrom", "北京-上海 2人");
				// row2.put("orderPrice", "￥1000.5元");
				// row2.put("orderFlg", R.drawable.forward_25);
				// data.add(row2);
				//
				// Map<String, Object> row3 = new HashMap<String, Object>();
				// row3.put("orderId", "订单编号:20143Yg");
				// row3.put("orderStatus", "已支付");
				// row3.put("orderTrainNo", "G108");
				// row3.put("orderDateFrom", "2014-8-18");
				// row3.put("orderStationFrom", "北京-上海 2人");
				// row3.put("orderPrice", "￥1000.5元");
				// row3.put("orderFlg", R.drawable.forward_25);
				// data.add(row3);
				//
				// Map<String, Object> row4 = new HashMap<String, Object>();
				// row4.put("orderId", "订单编号:20143Yg");
				// row4.put("orderStatus", "已取消");
				// row4.put("orderTrainNo", "G108");
				// row4.put("orderDateFrom", "2014-8-18");
				// row4.put("orderStationFrom", "北京-上海 2人");
				// row4.put("orderPrice", "￥1000.5元");
				// row4.put("orderFlg", null);
				// data.add(row4);
				//
				// adapter.notifyDataSetChanged();

				break;
			}
		}

	}

}
