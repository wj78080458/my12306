package com.neuedu.my12306.order;

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
import com.neuedu.my12306.R;
import com.neuedu.my12306.R.layout;
import com.neuedu.my12306.R.menu;
import com.neuedu.my12306.bean.Order;
import com.neuedu.my12306.utils.CONSTANT;
import com.neuedu.my12306.utils.NetUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OrderWaitPayActivity extends Activity {
	TextView tvOrderWaitPay;
	Button btnOrderWaitPay;
	ProgressDialog pDialog;
	Order order;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_wait_pay);

		tvOrderWaitPay = (TextView) findViewById(R.id.tvOrderWaitPay);
		btnOrderWaitPay = (Button) findViewById(R.id.btnOrderWaitPay);

		order = (Order) getIntent().getSerializableExtra("order");
		tvOrderWaitPay.setText(order.toString());

		btnOrderWaitPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!NetUtils.check(OrderWaitPayActivity.this)) {
					Toast.makeText(OrderWaitPayActivity.this,
							getString(R.string.network_check),
							Toast.LENGTH_SHORT).show();
					return; // 后续代码不执行
				}

				new OrderWaitPayTask().execute("Pay");
			}
		});

	}

	class OrderWaitPayTask extends AsyncTask<String, String, Object> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(OrderWaitPayActivity.this, null,
					"正在加载中...", false, true);
		}

		@Override
		protected Object doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/" + p[0]);

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
				params.add(new BasicNameValuePair("orderId", order.getId()));

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
					String status = gson.fromJson(json, String.class);
					return status; // 0, 1

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

			if (pDialog != null) {
				pDialog.dismiss();
			}
			if (result instanceof String) {
				if ("0".equals(result.toString())) {
					Toast.makeText(OrderWaitPayActivity.this, "支付失败，请重试",
							Toast.LENGTH_SHORT).show();

				} else if ("1".equals(result.toString())) {
					Toast.makeText(OrderWaitPayActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					finish();

				} else if ("3".equals(result.toString())) {
					Toast.makeText(OrderWaitPayActivity.this, "请重新登录",
							Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(OrderWaitPayActivity.this, "服务器错误，请重试",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order_wait_pay, menu);
		return true;
	}

}
