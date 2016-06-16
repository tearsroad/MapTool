package com.maptool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;

import com.maptool.util.NetworkStateManager;

/**
 * 欢迎界面
 * 
 * @author maxw
 * @date 2015-4-17
 * 
 */
public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		// 判断网络连接
		NetworkStateManager.instance().init(this);
		boolean isNetworkConnected = NetworkStateManager.instance()
				.isNetworkConnected();

		if (!isNetworkConnected) {
			dialog();
		}else{
			showWelcomePage();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		showWelcomePage();
	}
	
	protected void showWelcomePage(){
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(WelcomeActivity.this,
						MapActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2000);
	}

	protected void dialog() {
		AlertDialog.Builder builder = new Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Dialog));
		builder.setMessage("没有网络连接，去打开吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("去打开", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
//				WelcomeActivity.this.finish();
				
				Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
				startActivityForResult(intent, 0);
			}
		});
		builder.setNegativeButton("退出", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				WelcomeActivity.this.finish();
			}
		});
		builder.setCancelable(false);
		builder.create().show();
	}


}
