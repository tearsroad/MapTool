package com.maptool.view;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.maptool.R;


public class MyToastC extends Toast {

	private static Toast result;

	public MyToastC(Context context) {
		super(context);
	}

	public static Toast makeText(Context context, CharSequence text,
			int duration) {
		result = new Toast(context);

		// 获取LayoutInflater对象
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 由layout文件创建一个View对象
		View layout = inflater.inflate(R.layout.my_toast_c, null);

		// 实例化ImageView和TextView对象
		TextView tvContent = (TextView) layout.findViewById(R.id.tv_content);

		tvContent.setText(text);

		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        // 获取屏幕高度
        int height = display.getHeight();
		result.setView(layout);
		result.setGravity(Gravity.TOP, 0, height*3 / 5);
		result.setDuration(duration);

		return result;
	}

}
