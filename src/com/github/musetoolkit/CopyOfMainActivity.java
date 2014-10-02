package com.github.musetoolkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
//包含TextView的类定义

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;		//该类为后面使用Toast信息做准备

public class CopyOfMainActivity extends Activity implements OnClickListener { 
	/** Called when the activity is first created. */
	//此段程序用于实现软件启动全屏界面
	public static void updateFullscreenStatus(Activity activity, Boolean bUseFullscreen){
	//全屏显示
	  if(bUseFullscreen){
		  activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		  activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	  }
	  //已经进入主界面，需要取消全屏显示
	  else{
		  activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		  activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	  }

       private GridView mGridView;   //MyGridView
        //定义图标数组
       private int[] imageRes = { R.drawable.png01, R.drawable.png02,
       R.drawable.png03, R.drawable.png04, R.drawable.png05, R.drawable.png06,
       R.drawable.png07, R.drawable.png08, R.drawable.png09, R.drawable.png10,
       R.drawable.png11, R.drawable.png12 };
       //定义标题数组
       private String[] itemName = { "正弦波", "三角波", "锯齿波", "白噪声", "粉红噪声", "扫频信号", "声压测量",
       "音频测试", "钢琴调律", "触摸屏", "关于", "退出" };

      @Override
      public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Activity标题不显示
//                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏显示
                setContentView(R.layout.main);
 
                mGridView = (GridView) findViewById(R.id.MyGridView);
                List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
                int length = itemName.length;
                for (int i = 0; i < length; i++) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("ItemImageView", imageRes[i]);
                        map.put("ItemTextView", itemName[i]);
                        data.add(map);
                }
                //为itme.xml添加适配器
                SimpleAdapter simpleAdapter = new SimpleAdapter(CopyOfMainActivity.this,
                data, R.layout.item, new String[] { "ItemImageView","ItemTextView" }, new int[] { R.id.ItemImageView,R.id.ItemTextView });
                mGridView.setAdapter(simpleAdapter);
                //为mGridView添加点击事件监听器
                mGridView.setOnItemClickListener(new GridViewItemOnClick());
      }
      //定义点击事件监听器
      public class GridViewItemOnClick implements OnItemClickListener {
      @Override
      public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) {
//             Toast.makeText(getApplicationContext(), position + "",
//             Toast.LENGTH_SHORT).show();
    	  
    	  /* 以下代码实现点击GridView图标后的响应
    	   * 通过构建Intent来实现功能选择
    	   * 调用相应的类来进行操作
    	   */    	  
             Intent intent = new Intent();
             switch(position){
             case 0:
                 Toast.makeText(getApplicationContext(), "正弦波",
                         Toast.LENGTH_SHORT).show();    
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.Sine" );
                  startActivity(intent);
                   break;
             case 1:
                 Toast.makeText(getApplicationContext(), "三角波",
                         Toast.LENGTH_SHORT).show();  
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.Triangle" );
                  startActivity(intent);
                   break;
             case 2:
                 Toast.makeText(getApplicationContext(), "锯齿波",
                         Toast.LENGTH_SHORT).show();  
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.Sawtooth" );
                  startActivity(intent);                          
                   break;
             case 3:
                 Toast.makeText(getApplicationContext(), "白噪声",
                         Toast.LENGTH_SHORT).show();  
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.Wnoise" );
                  startActivity(intent);                          
                   break;
             case 4:
                 Toast.makeText(getApplicationContext(), "粉红噪声",
                         Toast.LENGTH_SHORT).show();  
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.Pnoise" );
                  startActivity(intent);                          
                   break; 
             case 5:
                 Toast.makeText(getApplicationContext(), "扫频信号",
                         Toast.LENGTH_SHORT).show();  
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.Sweep" );
                  startActivity(intent);                          
                   break;  
             case 6:
                 Toast.makeText(getApplicationContext(), "声压级",
                         Toast.LENGTH_SHORT).show();  
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.Spl" );
                  startActivity(intent);                          
                   break;                   
             case 7:
                 Toast.makeText(getApplicationContext(), "音频测试",
                         Toast.LENGTH_SHORT).show();  
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.Test" );
                  startActivity(intent);                          
                   break;                   
             case 8:
                 Toast.makeText(getApplicationContext(), "钢琴调律",
                         Toast.LENGTH_SHORT).show();  
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.Piano" );
                  startActivity(intent);                          
                   break;                   
             case 9:
                 Toast.makeText(getApplicationContext(), "触摸屏",
                         Toast.LENGTH_SHORT).show();  
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.Touch" );
                  startActivity(intent);                          
                   break;                   
             case 10:
                 Toast.makeText(getApplicationContext(), "关于",
                         Toast.LENGTH_SHORT).show();  
            	 intent.setClassName( "com.github.musetoolkit",
                             "com.github.musetoolkit.About" );
                  startActivity(intent);                          
                   break;                   
             case 11:
            	 finish();                        
                   break;                   
            
             
             }             
            
      }
   }

      //当点击图片时进入相应的操作，暂时只实现了其中的两个其他的都相似。

	
/*	
	//此段代码用于解决setOnClickListener()方法传递点击变量的功能
	//此处按钮事件响应顺序必须与layout中定义的按钮顺序一致，否则运行时将出错
	public void onClick(View v){
		switch (v.getId()){
		case R.id.sine_button:
			Intent sine = new Intent(this,Sine.class);
			startActivity(sine);
			break;
		case R.id.triangle_button:
			Intent triangle = new Intent(this,Triangle.class);
			startActivity(triangle);
			break;			
		case R.id.sawtooth_button:
			Intent sawtooth = new Intent(this,Sawtooth.class);
			startActivity(sawtooth);
			break;
		case R.id.wnoise_button:
			Intent wnoise = new Intent(this,Wnoise.class);
			startActivity(wnoise);
			break;
//		case R.id.pnoise_button:
//			Intent pnoise = new Intent(this,Pnoise.class);
//			startActivity(pnoise);
//			break;
		case R.id.spl_button:
			Intent spl = new Intent(this,Spl.class);
			startActivity(spl);
			break;
		case R.id.test_button:
			Intent test = new Intent(this,Test.class);
			startActivity(test);
			break;
		case R.id.piano_button:
			Intent piano = new Intent(this,Piano.class);
			startActivity(piano);
			break;
		case R.id.touch_button:
			Intent touch = new Intent(this,Touch.class);
			startActivity(touch);
			break;
		case R.id.about_button:
			Intent about = new Intent(this,About.class);
			startActivity(about);
			break;
		case R.id.exit_button:	//该段代码处理退出按钮，用于终止当前Activity，将控制权交给Android应用程序栈
			finish();
			break;

		//case the other buttons...	
		}
	}	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//Set up click listeners for all buttons
		//本节设置对各个主页按钮的处理方式
//		View guitarButton = findViewById(R.id.guitar_button);
//		guitarButton.setOnClickListener(this);
//		View violinButton = findViewById(R.id.violin_button);
//		violinButton.setOnClickListener(this);
		View sineButton = findViewById(R.id.sine_button);
		sineButton.setOnClickListener(this);		
		View triangleButton = findViewById(R.id.triangle_button);
		triangleButton.setOnClickListener(this);
		View sawtoothButton = findViewById(R.id.sawtooth_button);
		sawtoothButton.setOnClickListener(this);
		View wnoiseButton = findViewById(R.id.wnoise_button);
		wnoiseButton.setOnClickListener(this);		
//		View pnoiseButton = findViewById(R.id.pnoise_button);
//		pnoiseButton.setOnClickListener(this);	
		View splButton = findViewById(R.id.spl_button);
		splButton.setOnClickListener(this);	
//		View sensorButton = findViewById(R.id.sensor_button);
//		sensorButton.setOnClickListener(this);			
		View testButton = findViewById(R.id.test_button);
		testButton.setOnClickListener(this);	
		View pianoButton = findViewById(R.id.piano_button);
		pianoButton.setOnClickListener(this);
		View touchButton = findViewById(R.id.touch_button);
		touchButton.setOnClickListener(this);
		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
	}
*/	
	//此处开始为检测Back按钮，提示程序退出部分代码
	//使用该功能需要import相应的Android库
	//import android.view.KeyEvent;
	//import android.widget.Toast;
    private long exitTime = 0;  
    
    /** 
     * 捕捉返回事件按钮 
     *  
     * 因为此 Activity 继承 TabActivity 用 onKeyDown 无响应，所以改用 dispatchKeyEvent 
     * 一般的 Activity 用 onKeyDown 就可以了 
     */  
      
    @Override  
    public boolean dispatchKeyEvent(KeyEvent event) {  
      if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {  
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {  
          this.exitApp();  
        }  
        return true;  
      }  
      return super.dispatchKeyEvent(event);  
    }  
      
    /** 
     * 退出程序 
     */  
    private void exitApp() {  
      // 判断2次点击事件时间  
      if ((System.currentTimeMillis() - exitTime) > 2000) {  
        Toast.makeText(CopyOfMainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
        exitTime = System.currentTimeMillis();  
      } else {  
        finish();  
      }  
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}  
}