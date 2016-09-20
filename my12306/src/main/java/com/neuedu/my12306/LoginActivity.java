package com.neuedu.my12306;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.neuedu.my12306.utils.CONSTANT;
import com.neuedu.my12306.utils.Md5Utils;
import com.neuedu.my12306.utils.NetUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	Button btnLogin;
	TextView tvLostPassword = null;
	EditText edtUsername = null;
	EditText edtPassword = null;
	CheckBox ckLogin = null;
	ProgressDialog pDialog = null;

	Handler handler = new Handler() {
		// �ص�����
		public void handleMessage(android.os.Message msg) {
			// �رնԻ���
			if (pDialog != null) {
				pDialog.dismiss();
			}

			switch (msg.what) {
			case 1:
				String jsessionid = (String) msg.obj;
				int result = msg.arg1;

				if (0 == result) {
					edtUsername.selectAll();
					edtUsername.setError("�û������������");
					edtUsername.requestFocus();
				} else if (1 == result) {
					SharedPreferences pref = getSharedPreferences("user",
							Context.MODE_PRIVATE);
					Editor editor = pref.edit();

					// ��¼JSESSIONID
					editor.putString("Cookie", jsessionid);

					// ��¼�û���������
					if (ckLogin.isChecked()) {
						editor.putString("username", edtUsername.getText()
								.toString());
						editor.putString("password",
								Md5Utils.MD5(edtPassword.getText().toString()));
					} else {
						// �����ǰ�ĵ�¼��Ϣ
						editor.remove("username");
						editor.remove("password");
					}

					editor.commit();

					// ��ʾ��ͼ
					Intent intent = new Intent(LoginActivity.this,
							MainActivity.class);
					startActivity(intent);

					// �ر�
					LoginActivity.this.finish();
				}

				break;
			case 2:
				Toast.makeText(LoginActivity.this, "����������������",
						Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		tvLostPassword = (TextView) findViewById(R.id.tvLostPassword);
		edtUsername = (EditText) findViewById(R.id.edtUsername);
		edtPassword = (EditText) findViewById(R.id.edtPassword);
		ckLogin = (CheckBox) findViewById(R.id.ckLogin);

		// ������������?
		tvLostPassword
				.setText(Html
						.fromHtml("<a href=\"http://www.12306.cn/lostPassword\">�������룿</a>"));
		tvLostPassword.setMovementMethod(LinkMovementMethod.getInstance());

		// ��ȡ�������
		btnLogin = (Button) findViewById(R.id.btnLogin);
		// �󶨼�����
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("My12306", "Login Button Click");

				if (TextUtils.isEmpty(edtUsername.getText().toString())) {
					edtUsername.setError("�������û���");
					edtUsername.requestFocus();
				} else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
					edtPassword.setError("����������");
					edtPassword.requestFocus();
				} else {
					if (!NetUtils.check(LoginActivity.this)) {
						Toast.makeText(LoginActivity.this,
								getString(R.string.network_check),
								Toast.LENGTH_SHORT).show();
						return; // �������벻ִ��
					}

					// ���ȶԻ���
					pDialog = ProgressDialog.show(LoginActivity.this, null,
							"���ڼ�����...", false, true);

					new Thread() {
						public void run() {
							Message msg = handler.obtainMessage();

							// ���ʷ������ˣ���֤�û���/����
							HttpPost post = new HttpPost(CONSTANT.HOST
									+ "/Login");
							// ���ò���
							List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
							params.add(new BasicNameValuePair("username",
									edtUsername.getText().toString()));
							params.add(new BasicNameValuePair("password",
									Md5Utils.MD5(edtPassword.getText()
											.toString())));

							UrlEncodedFormEntity entity;
							try {
								entity = new UrlEncodedFormEntity(params,
										"UTF-8");

								post.setEntity(entity);

								// ��������
								DefaultHttpClient client = new DefaultHttpClient();
								// ��ʱ����
								client.getParams()
										.setParameter(
												CoreConnectionPNames.CONNECTION_TIMEOUT,
												CONSTANT.REQUEST_TIMEOUT);
								client.getParams().setParameter(
										CoreConnectionPNames.SO_TIMEOUT,
										CONSTANT.SO_TIMEOUT);
								HttpResponse response = client.execute(post);

								// ������
								if (response.getStatusLine().getStatusCode() == 200) {
									// ��ӡ��Ӧ�Ľ��
									// Log.v("My12306",
									// EntityUtils.toString(response.getEntity()));

									// xml����
									XmlPullParser parser = Xml.newPullParser(); // pull������
									parser.setInput(response.getEntity()
											.getContent(), "UTF-8");

									int type = parser.getEventType();
									String result = null;
									while (type != XmlPullParser.END_DOCUMENT) {
										switch (type) {
										case XmlPullParser.START_TAG:
											if ("result".equals(parser
													.getName())) {
												result = parser.nextText();
												Log.d("My12306", "result:"
														+ result);
											}
											break;
										}

										type = parser.next();
									}

									// ��¼JSESSIONID
									String value = "";
									List<Cookie> cookies = client
											.getCookieStore().getCookies();
									for (Cookie cookie : cookies) {
										if ("JSESSIONID".equals(cookie
												.getName())) {
											value = cookie.getValue();
											Log.d("My12306", "JSESSIONID:"
													+ value);
											break;
										}
									}

									// ������Ϣ
									msg.what = 1;
									msg.arg1 = Integer.parseInt(result);
									msg.obj = "JSESSIONID=" + value;
								} else {
									msg.what = 2;
								}

								// �ر�����
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
								// TODO Auto-generated catch block
								e.printStackTrace();
								msg.what = 2;
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								msg.what = 2;
							} catch (XmlPullParserException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								msg.what = 2;
							}

							handler.sendMessage(msg);
						};
					}.start();
				}

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
