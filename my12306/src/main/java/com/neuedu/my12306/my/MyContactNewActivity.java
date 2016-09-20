package com.neuedu.my12306.my;

import java.util.ArrayList;
import java.util.List;

import com.neuedu.my12306.R;
import com.neuedu.my12306.R.layout;
import com.neuedu.my12306.R.menu;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MyContactNewActivity extends Activity {
	TextView tvMyContactNew1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_contact_new);

		tvMyContactNew1 = (TextView) findViewById(R.id.tvMyContactNew1);

		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_contact_new, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.mn_finduser_contact:

			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI,
					new String[] { "_id", "display_name" }, null, null, null);

			List<String> contacts = new ArrayList<String>();
			while (c.moveToNext()) {
				int _id = c.getInt(c
						.getColumnIndex(ContactsContract.Contacts._ID));
				String display_name = c.getString(c
						.getColumnIndex("display_name"));
				
				// 查找电话
				Cursor c2 = cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ "=?", new String[] { _id + "" }, null);
				String number = null;
				while (c2.moveToNext()) {
					number = c2
							.getString(c2
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				c2.close();

				contacts.add(display_name + "("
						+ number.replaceAll(" ", "").replaceAll("-", "") + ")");
			}
			c.close();

			if (contacts.size() == 0) {
				new AlertDialog.Builder(MyContactNewActivity.this)
						.setTitle("请选择").setMessage("通讯录为空")
						.setNegativeButton("取消", null).show();
			} else {

				final String[] items = new String[contacts.size()];
				contacts.toArray(items);

				// AlertDialog
				new AlertDialog.Builder(MyContactNewActivity.this)
						.setTitle("请选择")
						.setItems(items, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								tvMyContactNew1.setText(items[which]);
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
			}

			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
