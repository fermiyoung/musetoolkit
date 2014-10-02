package com.github.musetoolkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SignalActivity extends Activity {  

    private GridView mGridView;   //MyGridView
    //定义图标数组
   private int[] imageRes = { R.drawable.png01, R.drawable.png02,
   R.drawable.png03, R.drawable.png04, R.drawable.png05, R.drawable.png06 };
   //定义标题数组
   private String[] itemName = { "正弦波", "三角波", "锯齿波", "白噪声", "粉红噪声", "扫频信号" };	
	
	
    public void onCreate(Bundle savedInstanceState) {  
  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.signaltab);  
   
  
//        TextView textview = new TextView(this);  
  
//        textview.setText("This is the Artists tab");  
  
//        setContentView(textview);  

        
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
        SimpleAdapter simpleAdapter = new SimpleAdapter(SignalActivity.this,
        data, R.layout.item, new String[] { "ItemImageView","ItemTextView" }, new int[] { R.id.ItemImageView,R.id.ItemTextView });
        mGridView.setAdapter(simpleAdapter);
        //为mGridView添加点击事件监听器
        mGridView.setOnItemClickListener(new GridViewItemOnClick());
}
//定义点击事件监听器
public class GridViewItemOnClick implements OnItemClickListener {
@Override
public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) {
//     Toast.makeText(getApplicationContext(), position + "",
//     Toast.LENGTH_SHORT).show();
  
  /* 以下代码实现点击GridView图标后的响应
   * 通过构建Intent来实现功能选择
   * 调用相应的类来进行操作
   */    	  
     Intent intent = new Intent();
     switch(position){
     case 0:
    	 Toast.makeText(getApplicationContext(), "正弦波", 
    			 Toast.LENGTH_SHORT).show();
    	 intent.setClassName("com.github.musetoolkit", 
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

     }             
    
}
}        
        
        
  
}  
