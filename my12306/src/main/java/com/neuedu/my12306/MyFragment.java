package com.neuedu.my12306;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.neuedu.my12306.my.MyAccountActivity;
import com.neuedu.my12306.my.MyContactActivity;
import com.neuedu.my12306.my.MyPasswordActivity;
import com.neuedu.my12306.utils.CONSTANT;
import com.neuedu.my12306.utils.NetUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class MyFragment extends Fragment {

	Button btnLogout = null;
	ListView lvMyList = null;
	ProgressDialog pDialog = null;

	public MyFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_my, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		btnLogout = (Button) getActivity().findViewById(R.id.btnLogout);
		lvMyList = (ListView) getActivity().findViewById(R.id.lvMyList);

		// ÿ�в���(ϵͳ�Դ�)
		// android.R.layout.simple_list_item_1

		// ����
		// String[] data = { "�ҵ���ϵ��", "�ҵ��˻�", "�ҵ�����"};
		String[] data = getResources().getStringArray(R.array.my_list_data);

		// ������
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data);

		// ��
		lvMyList.setAdapter(adapter);

		// �¼�����
		lvMyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();

				switch (position) {
				case 0:
					intent.setClass(getActivity(), MyContactActivity.class);
					break;
				case 1:
					intent.setClass(getActivity(), MyAccountActivity.class);
					break;
				case 2:
					intent.setClass(getActivity(), MyPasswordActivity.class);
					break;
				}
				startActivity(intent);
			}
		});

		MyButtonListener listener = new MyButtonListener();
		btnLogout.setOnClickListener(listener);

	}

	class MyButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!NetUtils.check(getActivity())) {
				Toast.makeText(getActivity(),
						getString(R.string.network_check), Toast.LENGTH_SHORT)
						.show();
				return;
			}

			new LogoutTask().execute();
		}
	}

	class LogoutTask extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			pDialog = ProgressDialog.show(getActivity(), null, "���ڼ�����...",
					false, true);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String result = null;

			HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Logout");

			// ��������
			DefaultHttpClient client = new DefaultHttpClient();

			try {
				// ����jsessionid
				SharedPreferences pref = getActivity().getSharedPreferences(
						"user", Context.MODE_PRIVATE);
				String value = pref.getString("Cookie", "");
				BasicHeader header = new BasicHeader("Cookie", value);
				post.setHeader(header);

				// ��ʱ����
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT,
						CONSTANT.REQUEST_TIMEOUT);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);

				HttpResponse response = client.execute(post);

				// ������
				if (response.getStatusLine().getStatusCode() == 200) {
					result = EntityUtils.toString(response.getEntity());
					Log.d("My12306", "Logout:" + result);
				}

				client.getConnectionManager().shutdown();

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog != null)
				pDialog.dismiss();
			
			// "1"
			if ("\"1\"".equals(result)) {
				Toast.makeText(getActivity(), "�˳��ɹ�", Toast.LENGTH_SHORT)
						.show();

				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			} else if ("\"0\"".equals(result)) {
				Toast.makeText(getActivity(), "�˳���¼ʧ��", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(getActivity(), "����������������", Toast.LENGTH_SHORT)
						.show();
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			}

		}
	}

}
