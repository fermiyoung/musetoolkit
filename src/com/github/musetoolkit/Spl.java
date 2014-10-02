package com.github.musetoolkit;
//该类用于实现SPL声压级检测功能

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import com.github.musetoolkit.Sine.ButtionBackOnClick;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Spl extends Activity implements OnClickListener, OnSharedPreferenceChangeListener{
	private static final String TAG = "SPL";
	
//	private Button spl_Button;
	private TextView tv_NavigateBack;
	
	private TextView logs;
	
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
	
	private void post(final String s) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				logs.append(s + ((s.endsWith("dB\n")) ? "" : "\n"));
			}			
		});
	}
	
	private PdReceiver receiver = new PdReceiver() {

		private void pdPost(String msg) {
			toast("Pure Data says, \"" + msg + "\"");
		}

		@Override
		public void print(String s) {
			post(s);
		}

		@Override
		public void receiveBang(String source) {
			pdPost("bang");
		}

		@Override
		public void receiveFloat(String source, float x) {
			pdPost("float: " + x);
		}

		@Override
		public void receiveList(String source, Object... args) {
			pdPost("list: " + Arrays.toString(args));
		}

		@Override
		public void receiveMessage(String source, String symbol, Object... args) {
			pdPost("message: " + Arrays.toString(args));
		}

		@Override
		public void receiveSymbol(String source, String symbol) {
			pdPost("symbol: " + symbol);
		}
	};

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
		setContentView(R.layout.spl);
		logs = (TextView) findViewById(R.id.spl_box);
		logs.setMovementMethod(null);

		tv_NavigateBack = (TextView)findViewById(R.id.NavigateBack);
		tv_NavigateBack.setOnClickListener(new ButtionBackOnClick()); 	
	}
	
	private void initPd() {
		Resources res = getResources();
		File patchFile = null;
		try {
			PdBase.setReceiver(receiver);
			PdBase.subscribe("spl");
//			PdBase.sendBang("trigger");
			InputStream in = res.openRawResource(R.raw.spl);
			patchFile = IoUtils.extractResource(in, "spl.pd", getCacheDir());
			PdBase.openPatch(patchFile);
			startAudio();
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
//			pdService.startAudio(new Intent(this, Spl.class), R.drawable.icon, name, "Return to " + name + ".");
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
/*	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.spl_button:
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
	*/	
		private void evaluateMessage(String s) {
			String dest = "spl", symbol = null;
			boolean isAny = s.length() > 0 && s.charAt(0) == ';';
			Scanner sc = new Scanner(isAny ? s.substring(1) : s);
			if (isAny) {
				if (sc.hasNext()) dest = sc.next();
				else {
					toast("Message not sent (empty recipient)");
					return;
				}
				if (sc.hasNext()) symbol = sc.next();
				else {
					toast("Message not sent (empty symbol)");
				}
			}
			List<Object> list = new ArrayList<Object>();
			while (sc.hasNext()) {
				if (sc.hasNextInt()) {
					list.add(Float.valueOf(sc.nextInt()));
				} else if (sc.hasNextFloat()) {
					list.add(sc.nextFloat());
				} else {
					list.add(sc.next());
				}
			}
			if (isAny) {
				PdBase.sendMessage(dest, symbol, list.toArray());
			} else {
				switch (list.size()) {
				case 0:
					PdBase.sendBang(dest);
					break;
				case 1:
					Object x = list.get(0);
					if (x instanceof String) {
						PdBase.sendSymbol(dest, (String) x);
					} else {
						PdBase.sendFloat(dest, (Float) x);
					}
					break;
				default:
					PdBase.sendList(dest, list.toArray());
					break;
				}
			}
		
	}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
}
