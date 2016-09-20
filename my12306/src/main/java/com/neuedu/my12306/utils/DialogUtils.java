package com.neuedu.my12306.utils;

import java.lang.reflect.Field;

import android.content.DialogInterface;

public class DialogUtils {
	public static void setClosable(DialogInterface dialog, boolean b) {
		Field field;
		try {
			field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, b);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
}
