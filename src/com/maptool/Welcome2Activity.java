package com.maptool;

import java.util.Timer;
import java.util.TimerTask;

import com.maptool.util.NetworkStateManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Welcome2Activity extends Activity {
	private ProgressBar pb;
	private int myTime;
	private int evTime = 30;
	private int totalTime;
	Timer timer = new Timer(); 
	Timer timer2 = new Timer(); 
	private boolean isTime2Schedule = false;
	private TextView tvMsg;
	private Button btnSettiog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome2);
		tvMsg = (TextView) findViewById(R.id.tv_buttom_msg);
		btnSettiog = (Button) findViewById(R.id.btn_opensetting);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		btnSettiog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
				startActivityForResult(intent, 0);
			}
		});
		initViewByNet();
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer2.cancel();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		initViewByNet();
	}
	private void initViewByNet(){
		NetworkStateManager.instance().init(this);
		boolean isNetworkConnected = NetworkStateManager.instance()
				.isNetworkConnected();
		if (!isNetworkConnected) {
			tvMsg.setText("现在没网络，是否去打开");
			btnSettiog.setVisibility(View.VISIBLE);
			pb.setVisibility(View.GONE);
			if(!isTime2Schedule)
			{
				isTime2Schedule = true;
				timer2.schedule(task2, 0, 500);
			}
			
		}else{
			timer2.cancel();
			tvMsg.setText("正在定位我的位置");
			btnSettiog.setVisibility(View.GONE);
			pb.setVisibility(View.VISIBLE);
			pb.setMax(5000);
			startTime(5000); 
		}
	}
	private void startTime(final int total){
		myTime = 0;
		totalTime = total;
		timer.cancel();
		timer = new Timer();
		timer.schedule(task, 0, evTime);       // timeTask    
	}
	TimerTask task = new TimerTask() {    
        @Override    
        public void run() {    
   
            runOnUiThread(new Runnable() {      // UI thread    
                @Override    
                public void run() {  
                	myTime+=evTime; 
                	pb.setProgress(myTime);
                	   
                    if(myTime >=totalTime){    
                        timer.cancel();    
                        Intent intent = new Intent(Welcome2Activity.this,
        						MapActivity.class);
        				startActivity(intent);
        				finish();
                    }    
                }    
            });    
        }    
    };  
    TimerTask task2 = new TimerTask() {    
        @Override    
        public void run() {    
   
            runOnUiThread(new Runnable() {      // UI thread    
                @Override    
                public void run() {  
                	NetworkStateManager.instance().init(Welcome2Activity.this);
            		boolean isNetworkConnected = NetworkStateManager.instance()
            				.isNetworkConnected();
            		if (isNetworkConnected) {
            			initViewByNet();
            		}
                }    
            });    
        }    
    }; 
}
