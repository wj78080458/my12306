package com.neuedu.my12306.my;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import com.neuedu.my12306.bean.Account;
import com.neuedu.my12306.bean.Passenger;
import com.neuedu.my12306.utils.CONSTANT;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.Toast;

public class MyAccountActivity extends Activity {
	ProgressDialog pDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account);

		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		
		// �첽����
		new AccountTask().execute();
	}

	class AccountTask extends AsyncTask<String, String, Object> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(MyAccountActivity.this, null,
					"���ڼ�����...", false, true);
		}

		@Override
		protected Object doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Account");

			// ��������
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

				// �������
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("action", "query"));			
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
						"UTF-8");
				post.setEntity(entity);

				// ��ʱ����
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT,
						CONSTANT.REQUEST_TIMEOUT);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);

				HttpResponse response = client.execute(post);

				// ������
				if (response.getStatusLine().getStatusCode() == 200) {

					String json = EntityUtils.toString(response.getEntity());
					Gson gson = new GsonBuilder().create();
					Account account = gson.fromJson(json, Account.class);

					return account;

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
				result = "3"; // ���µ�¼
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

			if (result instanceof Account) {
				Toast.makeText(MyAccountActivity.this, result.toString(),
						Toast.LENGTH_SHORT).show();
				// ...

			} else if (result instanceof String) {
				if ("3".equals(result.toString())) {
					Toast.makeText(MyAccountActivity.this, "�����µ�¼",
							Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(MyAccountActivity.this, "����������������",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_account, menu);
		return true;
	}

}
