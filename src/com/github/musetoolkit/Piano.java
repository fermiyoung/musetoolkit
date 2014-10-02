package com.github.musetoolkit;
//该类用于实现钢琴调律功能

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
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Piano extends Activity implements OnClickListener, OnSharedPreferenceChangeListener{
	private static final String TAG = "Piano Tuner";

	private TextView tv_NavigateBack;	
	
	private Button piano_Button;

	private RadioGroup standard;
    private RadioButton radio1,radio2,radio3,radio4; 

    private TextView description;   
    private TextView freq;
    private SeekBar seekBar;    
	private PdService pdService = null;
	
	private Toast toast = null;
	
	private int pitch;
	
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
//				freq.append(s + ((s.endsWith("\n")) ? " " : " "));
				freq.append(s);				
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
		
//        seekBar=(SeekBar)findViewById(R.id.seekBar);
        description = (TextView) findViewById(R.id.description);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        //设置seekBar的最大值
        seekBar.setMax(127);
        //设置监听器，监听进度条的改变状态
        seekBar.setProgress(69);
        //此处设置默认音高为标准音A
        description.setText("当前音高：A");
        //此处为初次运行时的显示内容
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 拖动条进度改变的时候调用
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
            	description.setText("当前音高：" + progress);
                pitch = progress;	//将取得的值存入pitch，传递给拖动结束时显示数值
            }
            /**
             * 拖动条开始拖动的时候调用
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                description.setText("开始拖动");
            }
            /**
             * 拖动条停止拖动的时候调用
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                description.setText("当前音高：" + pitch);
                PdBase.sendFloat("midinote", pitch);
            }
        });

        standard = (RadioGroup)findViewById(R.id.standard);  
        radio1 = (RadioButton)findViewById(R.id.radio1);  
        radio2 = (RadioButton)findViewById(R.id.radio2);  
        radio3 = (RadioButton)findViewById(R.id.radio3);  
        radio4 = (RadioButton)findViewById(R.id.radio4);   

        standard.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = (RadioButton) findViewById(checkedId);

                Log.i(TAG, String.valueOf(radioButton.getText()));

                if(checkedId==R.id.radio1){                    
        			PdBase.sendBang("422");
                }  
                else if(checkedId==R.id.radio2){  
        			PdBase.sendBang("423");
                }
                else if (checkedId==R.id.radio3){
        			PdBase.sendBang("435");
                }
                else{  
        			PdBase.sendBang("440");
                }                 

            }
        });
        
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
		setContentView(R.layout.piano);
		piano_Button = (Button) findViewById(R.id.piano_button);
		piano_Button.setOnClickListener(this);
		freq = (TextView) findViewById(R.id.freq_box);
		freq.setMovementMethod(null);	

		tv_NavigateBack = (TextView)findViewById(R.id.NavigateBack);
		tv_NavigateBack.setOnClickListener(new ButtionBackOnClick());  	
	}
	
	private void initPd() {
		Resources res = getResources();
		File patchFile = null;
		try {
			PdBase.sendBang("trigger");
			PdBase.setReceiver(receiver);			
			PdBase.subscribe("freq");			
//			PdBase.sendFloat("midinote", 64);
			InputStream in = res.openRawResource(R.raw.pianotuner);
			patchFile = IoUtils.extractResource(in, "pinaotuner.pd", getCacheDir());
			PdBase.openPatch(patchFile);
	        PdBase.sendFloat("midinote", 69); 	//此处确保初始化时，按下按钮能听到标准音A发声
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
//			pdService.startAudio(new Intent(this, Piano.class), R.drawable.icon, name, "Return to " + name + ".");
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
		case R.id.piano_button:
			startAudio();
			PdBase.sendBang("Bang");
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

	private void evaluateMessage(String s) {
		String dest = "freq", symbol = null;
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
	
	
}
