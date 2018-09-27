package com.myadmob.util;

import android.content.Context;

import java.util.Calendar;

public class Utils {

	public static int convertDpToPx(Context context, int dp) {
		return (int) (dp * context.getResources().getDisplayMetrics().density);
	}

	public static String getDateToday() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);
	}
}
