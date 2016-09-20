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
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashActivity extends Activity {

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				String jsessionid = (String) msg.obj;
				int result = msg.arg1;

				if (0 == result) {
					// 跳转到LoginActivity
					Intent intent = new Intent();
					intent.setClass(SplashActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				} else if (1 == result) {
					// 记录JSESSIONID
					SharedPreferences pref = getSharedPreferences("user",
							Context.MODE_PRIVATE);
					Editor editor = pref.edit();
					editor.putString("Cookie", jsessionid);
					editor.commit();

					Intent intent = new Intent();
					intent.setClass(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}

				break;
			case 2:
				Toast.makeText(SplashActivity.this, "服务器错误，请重试",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置全屏模式
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 去除标题行
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_splash);

		// 自动登录
		SharedPreferences pref = getSharedPreferences("user", 0);
		final String username = pref.getString("username", "");
		final String password = pref.getString("password", "");

		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			Intent intent = new Intent();
			intent.setClass(SplashActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			if (!NetUtils.check(SplashActivity.this)) {
				Toast.makeText(SplashActivity.this,
						getString(R.string.network_check), Toast.LENGTH_SHORT)
						.show();
				return; // 后续代码不执行
			}

			new Thread() {
				public void run() {
					Message msg = handler.obtainMessage();

					// 访问服务器端，验证用户名/密码
					HttpPost post = new HttpPost(CONSTANT.HOST + "/Login");
					// 设置参数
					List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("username", username));
					params.add(new BasicNameValuePair("password", password));

					UrlEncodedFormEntity entity;
					try {
						entity = new UrlEncodedFormEntity(params, "UTF-8");

						post.setEntity(entity);

						// 发送请求
						DefaultHttpClient client = new DefaultHttpClient();
						// 超时设置
						client.getParams().setParameter(
								CoreConnectionPNames.CONNECTION_TIMEOUT,
								CONSTANT.REQUEST_TIMEOUT);
						client.getParams().setParameter(
								CoreConnectionPNames.SO_TIMEOUT,
								CONSTANT.SO_TIMEOUT);
						HttpResponse response = client.execute(post);

						// 处理结果
						if (response.getStatusLine().getStatusCode() == 200) {
							// xml解析
							XmlPullParser parser = Xml.newPullParser(); // pull解析器
							parser.setInput(response.getEntity().getContent(),
									"UTF-8");

							int type = parser.getEventType();
							String result = null;
							while (type != XmlPullParser.END_DOCUMENT) {
								switch (type) {
								case XmlPullParser.START_TAG:
									if ("result".equals(parser.getName())) {
										result = parser.nextText();
										Log.d("My12306", "result:" + result);
									}
									break;
								}

								type = parser.next();
							}

							// 记录JSESSIONID
							String value = "";
							List<Cookie> cookies = client.getCookieStore()
									.getCookies();
							for (Cookie cookie : cookies) {
								if ("JSESSIONID".equals(cookie.getName())) {
									value = cookie.getValue();
									Log.d("My12306", "JSESSIONID:" + value);
									break;
								}
							}

							// 发送消息
							msg.what = 1;
							msg.arg1 = Integer.parseInt(result);
							msg.obj = "JSESSIONID=" + value;
						} else {
							msg.what = 2;
						}

						// 关闭连接
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
