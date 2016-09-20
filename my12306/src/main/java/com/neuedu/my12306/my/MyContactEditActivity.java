package com.neuedu.my12306.my;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
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
import com.neuedu.my12306.MainActivity;
import com.neuedu.my12306.R;
import com.neuedu.my12306.R.layout;
import com.neuedu.my12306.R.menu;
import com.neuedu.my12306.bean.Passenger;
import com.neuedu.my12306.utils.CONSTANT;
import com.neuedu.my12306.utils.DialogUtils;
import com.neuedu.my12306.utils.NetUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MyContactEditActivity extends Activity {
	ListView lvMyContactEdit;
	List<Map<String, Object>> data = null;
	SimpleAdapter adapter = null;
	Button btnMyContactEditSave = null;
	ProgressDialog pDialog = null;
	String action = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_contact_edit);

		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);

		lvMyContactEdit = (ListView) findViewById(R.id.lvMyContactEdit);
		btnMyContactEditSave = (Button) findViewById(R.id.btnMyContactEditSave);

		// ��������
		Intent intent = getIntent();
		Map<String, Object> contact = (Map<String, Object>) intent
				.getSerializableExtra("row");

		// ���ݲ���
		data = new ArrayList<Map<String, Object>>();

		// row1: ����
		Map<String, Object> row1 = new HashMap<String, Object>();
		String name = (String) contact.get("name");
		row1.put("key1", "����");
		row1.put("key2", name.split("\\(")[0]);
		row1.put("key3", R.drawable.forward_25);
		data.add(row1);

		// row2: ֤������
		Map<String, Object> row2 = new HashMap<String, Object>();
		String idCard = (String) contact.get("idCard");
		row2.put("key1", "֤������");
		row2.put("key2", idCard.split(":")[0]);
		row2.put("key3", null);
		data.add(row2);

		// row3: ֤������
		Map<String, Object> row3 = new HashMap<String, Object>();
		row3.put("key1", "֤������");
		row3.put("key2", idCard.split(":")[1]);
		row3.put("key3", null);
		data.add(row3);

		// row4: �˿�����
		Map<String, Object> row4 = new HashMap<String, Object>();
		row4.put("key1", "�˿�����");
		row4.put("key2", name.split("\\(")[1].split("\\)")[0]);
		row4.put("key3", R.drawable.forward_25);
		data.add(row4);

		// row5: �绰
		Map<String, Object> row5 = new HashMap<String, Object>();
		String tel = (String) contact.get("tel");
		row5.put("key1", "�绰");
		row5.put("key2", tel.split(":")[1]);
		row5.put("key3", R.drawable.forward_25);
		data.add(row5);

		// ������
		adapter = new SimpleAdapter(MyContactEditActivity.this, data,
				R.layout.item_my_contact_edit, new String[] { "key1", "key2",
						"key3" }, new int[] { R.id.tvMyContactEditKey,
						R.id.tvMyContactEditValue, R.id.imgMyContactEditFlg });
		lvMyContactEdit.setAdapter(adapter);

		lvMyContactEdit.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					final EditText edtName = new EditText(
							MyContactEditActivity.this);
					edtName.setText((String) (data.get(position).get("key2")));
					edtName.selectAll(); // Ĭ��ѡ��

					new AlertDialog.Builder(MyContactEditActivity.this)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle("����������")
							.setView(edtName)
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// ��֤
											String name = edtName.getText()
													.toString();
											if (TextUtils.isEmpty(name)) {
												// ���öԻ������Զ��ر�
												DialogUtils.setClosable(dialog,
														false);

												edtName.setError("����������");
												edtName.requestFocus();

											} else {
												// ���öԻ����Զ��ر�
												DialogUtils.setClosable(dialog,
														true);

												data.get(position).put(
														"key2",
														edtName.getText()
																.toString());
												// ����ListView
												adapter.notifyDataSetChanged();
											}
										}
									})
							.setNegativeButton("ȡ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// ���öԻ����Զ��ر�
											DialogUtils.setClosable(dialog,
													true);

										}
									}).show();

					break;
				case 3:
					final String[] types = new String[] { "����", "ѧ��", "��ͯ",
							"����" };
					String key2 = (String) (data.get(position).get("key2"));
					int idx = 0;
					for (int i = 0; i < types.length; i++) {
						if (types[i].equals(key2)) {
							idx = i;
							break;
						}
					}

					new AlertDialog.Builder(MyContactEditActivity.this)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("��ѡ��˿�����")
							.setSingleChoiceItems(types, idx,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											data.get(position).put("key2",
													types[which]);
											adapter.notifyDataSetChanged();

											dialog.dismiss();
										}
									}).setNegativeButton("ȡ��", null).show();

					break;
				case 4:
					break;
				}
			}

		});

		btnMyContactEditSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 1.���ݱ��浽��������
				if (!NetUtils.check(MyContactEditActivity.this)) {
					Toast.makeText(MyContactEditActivity.this,
							getString(R.string.network_check),
							Toast.LENGTH_SHORT).show();
					return; // �������벻ִ��
				}

				// ���ȶԻ���
				pDialog = ProgressDialog.show(MyContactEditActivity.this, null,
						"���ڼ�����...", false, true);

				// ��ʼ�߳�
				action = "update";
				contactThread.start();
			}
		});
	}

	Thread contactThread = new Thread() {
		public void run() {
			// ��ȡmessage
			Message msg = handler.obtainMessage();

			HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Passenger");

			// ��������
			DefaultHttpClient client = new DefaultHttpClient();

			try {
				// jsessionid
				SharedPreferences pref = getSharedPreferences("user",
						Context.MODE_PRIVATE);
				String value = pref.getString("Cookie", "");
				BasicHeader header = new BasicHeader("Cookie", value);
				post.setHeader(header);

				// �������
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("����", (String) data.get(0)
						.get("key2")));
				params.add(new BasicNameValuePair("֤������", (String) data.get(1)
						.get("key2")));
				params.add(new BasicNameValuePair("֤������", (String) data.get(2)
						.get("key2")));
				params.add(new BasicNameValuePair("�˿�����", (String) data.get(3)
						.get("key2")));
				params.add(new BasicNameValuePair("�绰", (String) data.get(4)
						.get("key2")));
				params.add(new BasicNameValuePair("action", action));

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

					Gson gson = new GsonBuilder().create();
					String result = gson.fromJson(
							EntityUtils.toString(response.getEntity()),
							String.class);
					// ������Ϣ
					msg.obj = result;
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
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// �رնԻ���
			if (pDialog != null) {
				pDialog.dismiss();
			}

			switch (msg.what) {
			case 1:
				String result = (String) msg.obj;
				String info = "�޸�";
				if ("remove".equals(action)) {
					info = "ɾ��";
				}

				if ("1".equals(result)) {

					Toast.makeText(MyContactEditActivity.this, info + "�ɹ�",
							Toast.LENGTH_SHORT).show();
					finish();
				} else if ("-1".equals(result)) {
					Toast.makeText(MyContactEditActivity.this, info + "ʧ�ܣ�������",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				Toast.makeText(MyContactEditActivity.this, "����������������",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(MyContactEditActivity.this, "�����µ�¼",
						Toast.LENGTH_SHORT).show();
				break;
			}

		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_contact_edit, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.mn_removeuser_contact:

			// ��ʼ�߳�
			action = "remove";
			contactThread.start();

			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

}
