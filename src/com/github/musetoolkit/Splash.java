package com.github.musetoolkit;
//本类用于实现软件启动界面

import android.app.Activity; 
import android.content.Intent; 
import android.os.Bundle; 
import android.os.Handler; 
import android.view.Window;
import android.view.WindowManager;

  
public class Splash extends Activity {    
  
    private final int SPLASH_DISPLAY_LENGHT = 3000; //延迟三秒  
  
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Activity标题不显示
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏显示
        setContentView(R.layout.splash); 

        new Handler().postDelayed(new Runnable(){ 
  
         @Override 
         public void run() { 
             Intent mainIntent = new Intent(Splash.this,MainActivity.class); 
             Splash.this.startActivity(mainIntent); 
                 Splash.this.finish(); 
         } 
            
        }, SPLASH_DISPLAY_LENGHT); 

        
    } 
}
