package com.neuedu.my12306.my;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.neuedu.my12306.LoginActivity;
import com.neuedu.my12306.MainActivity;
import com.neuedu.my12306.R;
import com.neuedu.my12306.R.layout;
import com.neuedu.my12306.R.menu;
import com.neuedu.my12306.bean.Passenger;
import com.neuedu.my12306.utils.CONSTANT;
import com.neuedu.my12306.utils.Md5Utils;
import com.neuedu.my12306.utils.NetUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MyContactActivity extends Activity {
	ListView lvMyContact = null;
	List<Map<String, Object>> data = null;
	SimpleAdapter adapter = null;
	ProgressDialog pDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_contact);

		// ���ز˵�
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);

		lvMyContact = (ListView) findViewById(R.id.lvMyContact);

		// ����
		data = new ArrayList<Map<String, Object>>();

		// ������
		// context: ������
		// data: ����
		// resource: ÿһ�еĲ��ַ�ʽ
		// from: Map�е�key
		// to: �����е����id
		adapter = new SimpleAdapter(this, data, R.layout.item_my_contact,
				new String[] { "name", "idCard", "tel" }, new int[] {
						R.id.tvNameContact, R.id.tvIdCardContact,
						R.id.tvTelContact });

		// ��
		lvMyContact.setAdapter(adapter);

		// �¼�����
		lvMyContact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MyContactActivity.this,
						MyContactEditActivity.class);
				// ��������
				intent.putExtra("row", (Serializable) data.get(position)); // Map
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// ���ʷ�����
		// Toast.makeText(MyContactActivity.this, "ˢ��",
		// Toast.LENGTH_SHORT).show();

		if (!NetUtils.check(MyContactActivity.this)) {
			Toast.makeText(MyContactActivity.this,
					getString(R.string.network_check), Toast.LENGTH_SHORT)
					.show();
			return; // �������벻ִ��
		}

		// ���ȶԻ���
		pDialog = ProgressDialog.show(MyContactActivity.this, null, "���ڼ�����...",
				false, true);

		new Thread() {
			public void run() {
				// ��ȡmessage
				Message msg = handler.obtainMessage();

				HttpPost post = new HttpPost(CONSTANT.HOST
						+ "/otn/PassengerList");

				// ��������
				DefaultHttpClient client = new DefaultHttpClient();

				try {
					// jsessionid
					SharedPreferences pref = getSharedPreferences("user",
							Context.MODE_PRIVATE);
					String value = pref.getString("Cookie", "");
					BasicHeader header = new BasicHeader("Cookie", value);
					post.setHeader(header);

					// ��ʱ����
					client.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT,
							CONSTANT.REQUEST_TIMEOUT);
					client.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT,
							CONSTANT.SO_TIMEOUT);

					HttpResponse response = client.execute(post);

					// ������
					if (response.getStatusLine().getStatusCode() == 200) {

						String json = EntityUtils
								.toString(response.getEntity());

						Gson gson = new GsonBuilder().create();

						Passenger[] passengers = gson.fromJson(json,
								Passenger[].class);

						// ������Ϣ
						msg.obj = passengers;
						msg.what = 1;

					} else {
						msg.what = 2;
					}

					client.getConnectionManager().shutdown();

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = 2;
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = 2;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = 2;
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = 2;
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
					msg.what = 3; // ���µ�¼
				}

				// ������Ϣ
				handler.sendMessage(msg);

			};
		}.start();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// �رնԻ���
			if (pDialog != null) {
				pDialog.dismiss();
			}

			// ���data
			data.clear();

			switch (msg.what) {
			case 1:
				// Passenger[] => data
				Passenger[] passengers = (Passenger[]) msg.obj;
				for (Passenger passenger : passengers) {
					Map<String, Object> row = new HashMap<String, Object>();
					row.put("name",
							passenger.getName() + "(" + passenger.getType()
									+ ")");
					row.put("idCard",
							passenger.getIdType() + ":" + passenger.getId());
					row.put("tel", "�绰:" + passenger.getTel());
					data.add(row);
				}
				adapter.notifyDataSetChanged();

				break;
			case 2:
				Toast.makeText(MyContactActivity.this, "����������������",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(MyContactActivity.this, "�����µ�¼",
						Toast.LENGTH_SHORT).show();
				break;
			}

		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_contact, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		// �˵��¼�����
		switch (item.getItemId()) {
		case R.id.mn_contact_add:
			// ��ת������û�
			Intent intent = new Intent(MyContactActivity.this,
					MyContactNewActivity.class);
			startActivity(intent);
			break;
		case android.R.id.home:
			finish();
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

}
