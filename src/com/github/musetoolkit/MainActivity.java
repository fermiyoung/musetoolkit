package com.github.musetoolkit;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends TabActivity {
	public void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState); 
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Activity标题不显示
	    setContentView(R.layout.main); 
	  
	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab 
	    Intent intent;  // Reusable Intent for each tab  

//        tabHost = (TabHost) findViewById(R.id.tabhost);

        TabWidget tabWidget = tabHost.getTabWidget();
        tabHost.setup();
 
        LayoutInflater.from(this).inflate(R.layout.main, tabHost.getTabContentView(), true);
        tabHost.setBackgroundColor(Color.argb(150, 22, 70, 150));
        updateTab(tabHost);//初始化Tab的颜色，和字体的颜色

        
	    // Create an Intent to launch an Activity for the tab (to be reused)  
	    intent = new Intent().setClass(this, SignalActivity.class);  	//定义当前Tab对应Activity执行的Intent
	  
	    // Initialize a TabSpec for each tab and add it to the TabHost  
	  
	    //此处开始新建一个标签页
	    spec = tabHost.newTabSpec("signal").setIndicator("信号", 
	    		res.getDrawable(R.drawable.ic_tab_signal)).setContent(intent);  //给标签页设置具体内容
	    tabHost.addTab(spec);  	//将生成的标签也添加到TabHost中去
 
	    // Do the same 	  
	    intent = new Intent().setClass(this, TestActivity.class);  
	  
	    spec = tabHost.newTabSpec("test").setIndicator("测试", 
	    		res.getDrawable(R.drawable.ic_tab_test)).setContent(intent);  
	    tabHost.addTab(spec);  
	  

	    intent = new Intent().setClass(this, CalcActivity.class);  
	    spec = tabHost.newTabSpec("calc").setIndicator("计算", 
	    		res.getDrawable(R.drawable.ic_tab_calc)).setContent(intent);  
	    tabHost.addTab(spec);  

	    intent = new Intent().setClass(this, TheoryActivity.class);  
	    spec = tabHost.newTabSpec("theory").setIndicator("原理", 
	    		res.getDrawable(R.drawable.ic_tab_theory)).setContent(intent);  
	    tabHost.addTab(spec);  	    

	    intent = new Intent().setClass(this, MoreActivity.class);  
	    spec = tabHost.newTabSpec("more").setIndicator("更多", 
	    		res.getDrawable(R.drawable.ic_tab_more)).setContent(intent);  
	    tabHost.addTab(spec);  		    
/*
 * 开发过程中，有时候图标稍微大点，比如48×48的时候，文字就会和图标叠加起来，解决方法如下：
 */
	    TabWidget tw = tabHost.getTabWidget();
	    for (int i = 0; i < tw.getChildCount(); i++)
	    {    
	    TextView tv=(TextView)tw.getChildAt(i).findViewById(android.R.id.title);    
	    ImageView iv=(ImageView)tw.getChildAt(i).findViewById(android.R.id.icon);    
	    iv.setPadding(0, -8, 0, 0);    
	    tv.setPadding(0, 0, 0, -2);    
	    tv.setTextSize(12); } 	    
	    
	    
//      注意这个就是改变Tabhost默认样式的地方，一定将这部分代码放在上面这段代码的下面，不然样式改变不了

      for (int i =0; i < tabWidget.getChildCount(); i++) {  

       tabWidget.getChildAt(i).getLayoutParams().height = 120;  

       tabWidget.getChildAt(i).getLayoutParams().width = 65;

       TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);

       tv.setTextSize(12);

       tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));

      }		    
	    
//	    tabHost.setCurrentTab(2);  
	  
	}

	
    /**
     * 更新Tab标签的颜色，和字体的颜色
     * @param tabHost
     */ 

	private void updateTab(final TabHost tabHost) { 
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) { 
            View view = tabHost.getTabWidget().getChildAt(i); 
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); 
            tv.setTextSize(12); 
            tv.setTypeface(Typeface.SERIF, 2); // 设置字体和风格  
            if (tabHost.getCurrentTab() == i) {//选中  
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.png02));//选中后的背景  
                tv.setTextColor(this.getResources().getColorStateList( 
                        android.R.color.black)); 
            } else {//不选中  
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.png01));//非选择的背景  
//                view.setBackground(getResources().getDrawable(R.drawable.sine_bg));
                tv.setTextColor(this.getResources().getColorStateList( 
                        android.R.color.white)); 
            } 
        } 
    } 
	
    /** 
     * 捕捉返回事件按钮 
     *  
     * 因为此 Activity 继承 TabActivity 用 onKeyDown 无响应，所以改用 dispatchKeyEvent 
     * 一般的 Activity 用 onKeyDown 就可以了 
     */
	
	//此处开始为检测Back按钮，提示程序退出部分代码
	//使用该功能需要import相应的Android库
	//import android.view.KeyEvent;
	//import android.widget.Toast;
    private long exitTime = 0;  	
      
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
        Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
        exitTime = System.currentTimeMillis();  
      } else {  
        finish();  
      }  
    }

}