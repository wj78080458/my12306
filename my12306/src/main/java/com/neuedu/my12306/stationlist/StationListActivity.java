package com.neuedu.my12306.stationlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.neuedu.my12306.R;
import com.neuedu.my12306.stationlist.LetterListView.OnTouchingLetterChangedListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class StationListActivity extends Activity {
	private BaseAdapter adapter;
	private ListView mStationListView;
	private TextView overlay;
	private LetterListView letterListView;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private Handler handler;
	private OverlayThread overlayThread;
	private ArrayList<Station> stations;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_list);

		mStationListView = (ListView) findViewById(R.id.station_list);
		letterListView = (LetterListView) findViewById(R.id.stationLetterListView);

		stations = StationUtils.getAllStations(this);

		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());

		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		
		initOverlay();
		setAdapter(stations);
		mStationListView.setOnItemClickListener(new StationListOnItemClick());

	}

	class StationListOnItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			Station station = (Station) mStationListView.getAdapter().getItem(
					pos);
			Intent intent = getIntent();
			intent.putExtra("name", station.getStation_name());
			setResult(200, intent);
			finish();
		}

	}

	private void setAdapter(List<Station> list) {
		if (list != null) {
			adapter = new ListAdapter(this, list);
			mStationListView.setAdapter(adapter);
		}

	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<Station> list;

		public ListAdapter(Context context, List<Station> list) {

			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				// ��ǰ����ƴ������ĸ
				String currentStr = list.get(i).getSort_order();
				// ��һ������ƴ������ĸ�����������Ϊ" "
				String previewStr = (i - 1) >= 0 ? list.get(i - 1)
						.getSort_order() : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).getSort_order();
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.station_list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(list.get(position).getStation_name());
			String currentStr = list.get(position).getSort_order();
			String previewStr = (position - 1) >= 0 ? list.get(position - 1)
					.getSort_order() : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView name;
		}

	}

	// ��ʼ������ƴ������ĸ������ʾ��
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.station_overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);

	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				mStationListView.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				handler.postDelayed(overlayThread, 1500);
			}
		}
	}

	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = getIntent();
			intent.putExtra("name", "");
			setResult(200, intent);
			
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}