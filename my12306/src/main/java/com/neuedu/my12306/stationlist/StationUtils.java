package com.neuedu.my12306.stationlist;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class StationUtils {
	public static final String DB_NAME = "station.db";
	public static final String DB_PATH = "/data/data/com.neuedu.my12306/databases";

	private static void init(Context ctx) {
		// ת�������ļ�
		try {
			// ����Ŀ¼
			File file = new File(DB_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}

			// �ж�station.db�Ƿ����
			File file2 = new File(DB_PATH + "/" + DB_NAME);

			if (!file2.exists()) {
				// ��
				InputStream is = ctx.getAssets().open(DB_NAME);
				BufferedInputStream bi = new BufferedInputStream(is);

				// д
				FileOutputStream fo = new FileOutputStream(DB_PATH + "/"
						+ DB_NAME);
				BufferedOutputStream bo = new BufferedOutputStream(fo);

				// ��д
				byte[] buff = new byte[1024];
				int len = 0;
				while ((len = bi.read(buff)) != -1) {
					bo.write(buff, 0, len);
				}

				// �ر�
				bi.close();
				is.close();
				bo.close();
				fo.close();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<Station> getAllStations(Context ctx) {
		init(ctx);
		
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/"
				+ DB_NAME, null);
		ArrayList<Station> stations = new ArrayList<Station>();

		Cursor c = db.rawQuery("SELECT * FROM station ORDER BY sort_order",
				null);

		while (c.moveToNext()) {
			Station station = new Station();
			station.setStation_name(c.getString(c
					.getColumnIndex("station_name")));

			String tmp = c.getString(c.getColumnIndex("sort_order"));
			if (TextUtils.isEmpty(tmp)) {
				station.setSort_order("����");
			} else {
				station.setSort_order(tmp);
			}
			stations.add(station);
		}

		c.close();
		db.close();

		return stations;
	}

	public static Station getStationFromCity(Context ctx, String city) {
		init(ctx);
		
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/"
				+ DB_NAME, null);
		Station station = null;

		Cursor c = db
				.rawQuery(
						"SELECT * FROM station WHERE city=? ORDER BY sort_order limit 1",
						new String[] { city });

		if (c.moveToNext()) {
			station = new Station();
			station.setStation_name(c.getString(c
					.getColumnIndex("station_name")));
		}

		c.close();
		db.close();

		return station;
	}

}
