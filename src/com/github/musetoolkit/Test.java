package com.github.musetoolkit;
//该类用于实现Android系统音频系统检测功能

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import com.github.musetoolkit.Sine.ButtionBackOnClick;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Test extends Activity implements OnClickListener, OnSharedPreferenceChangeListener {
	private static final String TAG = "Libpd Test";

	private TextView tv_NavigateBack;	
	
	private Button play;
	private CheckBox left, right, mic;
	private TextView logs;
	
	private PdService pdService = null;
	
	private Toast toast = null;
	
	private void toast(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run(){
				if (toast == null) {
					toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
				}
				toast.setText(TAG + ": " + msg);
				toast.show();
			}
		});
	}
	
	
	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			initPd();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			//this method will never be called
		}
	};
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Activity标题不显示
		
		AudioParameters.init(this);
		PdPreferences.initPreferences(getApplicationContext());
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		initGui();
		bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanup();
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (pdService.isRunning()) {
			startAudio();
		}
	}
	
	private void initGui() {
		setContentView(R.layout.test);
		play = (Button) findViewById(R.id.play_button);
		play.setOnClickListener(this);
		left = (CheckBox) findViewById(R.id.left_box);
		left.setOnClickListener(this);
		right = (CheckBox) findViewById(R.id.right_box);
		right.setOnClickListener(this);
		mic = (CheckBox) findViewById(R.id.mic_box);
		mic.setOnClickListener(this);
		logs = (TextView) findViewById(R.id.log_box);
		logs.setMovementMethod(new ScrollingMovementMethod());		

		tv_NavigateBack = (TextView)findViewById(R.id.NavigateBack);
		tv_NavigateBack.setOnClickListener(new ButtionBackOnClick()); 		
	}
	
	private void initPd() {
		Resources res = getResources();
		File patchFile = null;				//import java.io.File
		try {
//			PdBase.setReceiver(receiver);
			PdBase.subscribe("android");
			InputStream in = res.openRawResource(R.raw.test);	//import java.io.InputStream
			patchFile = IoUtils.extractResource(in, "test.pd", getCacheDir());
			PdBase.openPatch(patchFile);
		} catch (IOException e) {			//import java.io.IOException
			Log.e(TAG, e.toString());		//import android.util.Log
			finish();
		} finally {
			if (patchFile != null) patchFile.delete();
		}
	}

	private void CloseCurrUI(){
		finish();
	}
	
	class ButtionBackOnClick implements OnClickListener{
		@Override
		public void onClick(View v) {
			CloseCurrUI();
		}
	}	
	
	private void startAudio() {
//		String name = getResources().getString(R.string.app_name);
		try {
			pdService.initAudio(-1, -1, -1, -1);		//取负值可以使用系统默认参数自动代替
//			pdService.startAudio(new Intent(this, Test.class), R.drawable.icon, name, "Return to " + name + ".");
			pdService.startAudio();
		} catch (IOException e) {
			toast(e.toString());
		}
	}
	
	private void stopAudio() {
		pdService.stopAudio();
	}
	
	private void cleanup() {
		try {
			unbindService(pdConnection);
			
		} catch (IllegalArgumentException e){
			//already unbound
			pdService = null;
		}
	}
	
	@Override
	public void onClick(View v) {		//import android.view.View
		switch (v.getId()) {
		case R.id.play_button:
			if (pdService.isRunning()) {
				stopAudio();
			} 
			else {
				startAudio();
			}
		case R.id.left_box:
			PdBase.sendFloat("left", left.isChecked() ? 1 : 0);
			break;
		case R.id.right_box:
			PdBase.sendFloat("right", right.isChecked() ? 1 : 0);
			break;
		case R.id.mic_box:
			PdBase.sendFloat("mic", mic.isChecked() ? 1 : 0);
			break;
		}
	}

}


//使用PdService服务，必须在AndroidManifest.xml文件中插入如下声明：
//<service android:name="org.puredata.android.service.PdService" />