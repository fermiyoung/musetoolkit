package com.github.musetoolkit;
//该类用于实现Android系统触摸屏检测功能

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
//import java.util.Properties;







import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;
import org.puredata.android.utils.Properties;

import com.github.musetoolkit.Sine.ButtionBackOnClick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

public class Touch extends Activity implements OnTouchListener, SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String TAG = "Touch";

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
				logs.append(s + ((s.endsWith("\n"))?"" : "\n"));
			}
		});
	}
	
	private PdReceiver reciever = new PdReceiver() {
		
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
	
	private final ServiceConnection connection = new ServiceConnection() {
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
	
	@Override protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Activity标题不显示
		
		PdPreferences.initPreferences(getApplicationContext());
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
		initGui();
		bindService(new Intent(this, PdService.class), connection, BIND_AUTO_CREATE);
		//
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanup();		
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		startAudio();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		CharSequence logsc = logs.getText();
		initGui();
		logs.setText(logsc);
	}
	
	private void initGui() {
		setContentView(R.layout.touch);
		logs = (TextView) findViewById(R.id.log_box);
		logs.setOnTouchListener(this);
		logs.setMovementMethod(new ScrollingMovementMethod());

		tv_NavigateBack = (TextView)findViewById(R.id.NavigateBack);
		tv_NavigateBack.setOnClickListener(new ButtionBackOnClick());	
	}
	
	private void initPd() {
		Resources res = getResources();
		File patchFile = null;
		try {
			PdBase.setReceiver(reciever);
			PdBase.subscribe("android");
			InputStream in = res.openRawResource(R.raw.touch);
			patchFile = IoUtils.extractResource(in, "touch.pd",getCacheDir());
			PdBase.openPatch(patchFile);
			startAudio();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			finish();
		} finally {
			if (patchFile != null)
				patchFile.delete();
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
//			pdService.startAudio(new Intent(this, Touch.class), R.drawable.icon, name, "Return to " + name + ".");
			pdService.startAudio();	
		} catch (IOException e) {
			toast(e.toString());
		}
	}
	
	private void cleanup() {
		try {
			unbindService(connection);
		} catch (IllegalArgumentException e) {
			//already unbound
			pdService = null;
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return (v == logs) && VersionedTouch.evaluateTouch(event, logs.getWidth(), logs.getHeight());
	}


}

final class VersionedTouch {
	private static final String TOUCH_SYMBOL = "#touch", DOWN = "down", UP = "up", XY = "xy";
	private static final boolean hasEclair = Properties.version >= 5;
	private static final float XS = 319.0f, YS = 319.0f;
	
	private VersionedTouch() {
		//do nothing
	}
	
	public static boolean evaluateTouch(MotionEvent event, int xImg, int yImg) {
		return (hasEclair) ? TouchEclair.evaluateTouch(event, xImg, yImg) : TouchCupcake.evaluateTouch(event, xImg, yImg);
	}
	
	private static class TouchEclair {
		
		public static boolean evaluateTouch(MotionEvent event, int xImg, int yImg) {
			int action = event.getAction();
			String actionTag = null;
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_DOWN:
				actionTag = DOWN;
			case MotionEvent.ACTION_POINTER_UP:
				if (actionTag == null) actionTag = UP;
				@SuppressWarnings("deprecation")
				int pointerIndex = (action & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
				int pointerId = event.getPointerId(pointerIndex);
				float x = normalize(event.getX(pointerIndex), XS, xImg);
				float y = normalize(event.getX(pointerIndex), YS, yImg);
				sendMessage(actionTag, pointerId, x, y);
				break;
			case MotionEvent.ACTION_DOWN:
				actionTag = DOWN;
			case MotionEvent.ACTION_MOVE:
				if (actionTag == null) actionTag = XY;
			default:
				if (actionTag == null) actionTag = UP;
				for (int i = 0; i < event.getPointerCount(); i++) {
					x = normalize(event.getX(i), XS, xImg);
					y = normalize(event.getY(i), YS, yImg);
					sendMessage(actionTag, event.getPointerId(i), x, y);
				}
				break;
			}
			return true;
		}
	}
	
	private static class TouchCupcake {
		public static boolean evaluateTouch(MotionEvent event, int xImg, int yImg) {
			String actionTag;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				actionTag = DOWN;
				break;
			case MotionEvent.ACTION_MOVE:
				actionTag = XY;
				break;
			default:
				actionTag = UP;
				break;				
			}
			float x = normalize(event.getX(), XS, xImg);
			float y = normalize(event.getY(), YS, yImg);
			sendMessage(actionTag, 0, x, y);
			return true;
		}
	}
	
	private static float normalize(float v, float vm, int dim) {
		float t = v * vm /dim;
		if (t < 0) t = 0;
		else if (t > vm) t = vm;
		return t;
	}
	
	private static void sendMessage(String actionTag, int pointerId, float x, float y) {
		PdBase.sendMessage(TOUCH_SYMBOL, actionTag, pointerId + 1, x, y);
	}
}