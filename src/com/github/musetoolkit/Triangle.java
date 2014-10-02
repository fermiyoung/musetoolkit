package com.github.musetoolkit;
//该类用于实三角波信号功能

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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Triangle extends Activity implements OnClickListener, OnSharedPreferenceChangeListener{
	private static final String TAG = "Triangle";
	
	private TextView tv_NavigateBack;	
	
	private Button triangle_Button;
	
	private PdService pdService = null;
	
	private Toast toast = null;
	
	private void toast(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
			//This method will never be called
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
		setContentView(R.layout.triangle);
		triangle_Button = (Button) findViewById(R.id.triangle_button);
		triangle_Button.setOnClickListener(this);

		tv_NavigateBack = (TextView)findViewById(R.id.NavigateBack);
		tv_NavigateBack.setOnClickListener(new ButtionBackOnClick()); 
		
	}
	
	private void initPd() {
		Resources res = getResources();
		File patchFile = null;
		try {
			PdBase.sendBang("trigger");
//			PdBase.sendFloat("midinote", 64);
			InputStream in = res.openRawResource(R.raw.triangle);
			patchFile = IoUtils.extractResource(in, "triangle.pd", getCacheDir());
			PdBase.openPatch(patchFile);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
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
			pdService.initAudio(-1, -1, -1, -1);
//			pdService.startAudio(new Intent(this, Triangle.class), R.drawable.icon, name, "Return to " + name + ".");
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
		} catch (IllegalArgumentException e) {
			//already unbound
			pdService = null;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.triangle_button:
			startAudio();
			PdBase.sendBang("trigger");
//			PdBase.sendFloat("midinote", 64);
			break;
//			if (pdService.isRunning()) {
//				stopAudio();
//			} else {
//				startAudio();
//			}
//		default:
//			stopAudio();
//			break;		
		}
	}

}
