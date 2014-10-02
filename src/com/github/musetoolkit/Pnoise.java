package com.github.musetoolkit;
//本类文件通过File --> New --> Class操作创建
//本类用于实现显示PinkNoise对话框中的内容

import java.io.File;				//File功能需要使用
import java.io.IOException;			//IOException处理需要使用
import java.io.InputStream;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View.OnClickListener;		//OnClickListener需用使用
import android.widget.Button;			//Button需要使用
import android.widget.TextView;
import android.widget.Toast;

import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;
import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;	//PdUiDispatcher需要使用

import com.github.musetoolkit.Sine.ButtionBackOnClick;

import android.view.View;
import android.view.Window;
import android.util.Log;				//Log需要使用

public class Pnoise extends Activity implements OnClickListener, OnSharedPreferenceChangeListener{
	private static final String TAG = "Pink Noise Wave";

	private TextView tv_NavigateBack;		
	
	private Button pnoise_Button;
	
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
		setContentView(R.layout.pnoise);
		pnoise_Button = (Button) findViewById(R.id.pnoise_button);
		pnoise_Button.setOnClickListener(this);

		tv_NavigateBack = (TextView)findViewById(R.id.NavigateBack);
		tv_NavigateBack.setOnClickListener(new ButtionBackOnClick());	
	}
	
	private void initPd() {
		Resources res = getResources();
		File patchFile = null;
		try {
			PdBase.sendBang("trigger");
//			PdBase.sendFloat("midinote", 64);
//			InputStream in = res.openRawResource(R.raw.pnoise);
//			patchFile = IoUtils.extractResource(in, "pnoise.pd", getCacheDir());
//			PdBase.openPatch(patchFile);
			File dir = getFilesDir();
			IoUtils.extractZipResource(
					getResources().openRawResource(R.raw.pnoise), dir, true);
			File pFile = new File(dir, "pnoise.pd");
			PdBase.openPatch(pFile.getAbsolutePath());
		
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
//			pdService.startAudio(new Intent(this, Sine.class), R.drawable.icon, name, "Return to " + name + ".");
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
		case R.id.pnoise_button:
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

/*
public class Pnoise extends Activity implements OnClickListener {
	private static final String TAG = "Pnoise";
	private PdUiDispatcher dispatcher;
	private Button pnoise_Button;		//定义Pink Noise试听按钮

	private PdService pdService = null;	
	
	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				initPd();			//在后面需要构建initPd()方法；
				loadPatch();		//在后面需要构建loadPatch()方法；
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				finish();
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		initGui();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	//此处开始构建initGui()方法
	private void initGui() {
		setContentView(R.layout.pnoise);	//此处pnoise根据类名称进行修改
		pnoise_Button = (Button)findViewById(R.id.pnoise_button);
		//定义按钮
		pnoise_Button.setOnClickListener(this);		//初始化按钮
		//系统可能会提示setOnClickListener()方法出错，可通过将OnClickListener接口应用到所
		//设计的Activity来解决错误，本例中创建triggerNote()方法来实现
		//解决该错误，可以在定义该类时，在声明中加入implements OnClickListener方法
		//public class Sine extends Activity implements OnClickListener
	}
	
	//此处开始构建initPd()方法
	private void initPd() throws IOException {
		//Configure the audio glue
		//此处配置Audio Glue层
		AudioParameters.init(this);
		int sampleRate = AudioParameters.suggestSampleRate();
		//定义变量，采用系统默认采样率
		pdService.initAudio(sampleRate, 1, 2, 10.0f);
		//设置PdAudio音频格式
		
		//Create and install the dispatcher
		//此处创建并安装dispatcher
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
	}
	
	//此处开始创建loadPatch()方法
	private void loadPatch() throws IOException {
		File dir = getFilesDir();			//获取文件路径
		IoUtils.extractZipResource(
				getResources().openRawResource(R.raw.pnoise),dir,true);
				//自动解压缩文件设置
		File patchFile = new File(dir,"pnoise.pd");
		//指定该类所需访问的patch名称
		PdBase.openPatch(patchFile.getAbsolutePath());
		//获取文件绝对路径并打开patch文件

		Resources res = getResources();
		File patchFile = null;		
		InputStream in = res.openRawResource(R.raw.pnoise);
		patchFile = IoUtils.extractResource(in, "pnoise.pd", getCacheDir());
		PdBase.openPatch(patchFile);
	}
	
	//此处开始创建triggerNote()方法
//	private void triggerNote(int n){
//		PdBase.sendFloat("midinote", n);	//libpd向Pd发送MIDI Note信息
//		PdBase.sendBang("trigger");			//libpd向Pd发送Bang信号
//	}
	
	//此段代码用于响应按钮的点击行为
	public void onClick(View v){
		switch (v.getId()){
		case R.id.pnoise_button:
			PdBase.sendBang("trigger");
//			PdBase.sendFloat("midinote",64);	//此处64为MIDI Note E
//			triggerNote(64);
			break;
		}
	}
}
*/
/*	
@Override
protected void onResume(){
	super.onResume();
	PdAudio.startAudio(this);		//Activity可见时，自动激活音频线程
}

@Override
protected void onPause(){
	super.onPause();
	PdAudio.stopAudio();			//Activity不可见时，自动挂起音频线程
}
*/	
//此段代码用于在Activity运行结束之后，清除patch进程，并清除Pd所有状态
//避免再次打开patch时，同时出现两个进程而出现声音混杂的情况

/*
@Override
public void onDestroy(){
	super.onDestroy();
	PdAudio.release();
	PdBase.release();
}
*/

//本文件设计完成后，需创建sine.xml文件，设计Sine页的显示布局
//在string.xml文件中，需要加入Sine页相关按钮和文本的信息
//需要编辑AndroidManifest.xml文件，注册当前Activity
