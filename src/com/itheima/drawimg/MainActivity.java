package com.itheima.drawimg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener, OnTouchListener {

	private ImageView ivResult;
	private SeekBar seekBar;
	private Bitmap bitmap;
	private Canvas canvas;
	private Paint paint;
	private Matrix matrix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.iv_red).setOnClickListener(MainActivity.this);
		findViewById(R.id.iv_green).setOnClickListener(MainActivity.this);
		findViewById(R.id.iv_blue).setOnClickListener(MainActivity.this);
		findViewById(R.id.iv_yellow).setOnClickListener(MainActivity.this);
		findViewById(R.id.iv_purple).setOnClickListener(MainActivity.this);

		ivResult = (ImageView) findViewById(R.id.iv_result);
		seekBar = (SeekBar) findViewById(R.id.seekBar);

		// 设置监听进度
		seekBar.setOnSeekBarChangeListener(MainActivity.this);
		// 设置屏幕触摸
		ivResult.setOnTouchListener(MainActivity.this);

		bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_4444);
		canvas = new Canvas(bitmap);
		//默认画板背景都是黑色的
		canvas.drawColor(Color.WHITE);
		paint = new Paint();
		paint.setStrokeCap(Cap.ROUND);//设置画笔形状
		matrix = new Matrix();

	}

	//初始化菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//点击菜单item时调用
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save:
			
			File file = new File("/mnt/sdcard/"+System.currentTimeMillis()+".jpg");
			OutputStream stream;
			try {
				stream = new FileOutputStream(file);
				//保存图片
				/**
				 * 参数1：图片格式（jpg、png）
				 * 参数2：图片质量0-100
				 * 参数3：输出流
				 */
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent();
//				intent.setAction(Intent.ACTION_MEDIA_MOUNTED);//android4.4以上，谷歌不允许发送该广播
//				intent.setData(Uri.parse("file://"));//刷新所有文件，耗性能
				
				intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//刷新单个文件
				//intent.setData(Uri.parse("/mnt/sdcar/xxx.jpg"));//刷新所有文件，耗性能
				intent.setData(Uri.fromFile(file));//刷新所有文件，耗性能
				
				//
				//通知相册刷新
				sendBroadcast(intent);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			break;
		case R.id.action_clear:
			Toast.makeText(this, "清空", Toast.LENGTH_SHORT).show();
			
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// 如何判断点击那个id
		switch (v.getId()) {
		case R.id.iv_red:
			paint.setColor(Color.RED);
			break;
		case R.id.iv_green:
			paint.setColor(Color.GREEN);

			break;
		case R.id.iv_blue:
			paint.setColor(Color.BLUE);

			break;
		case R.id.iv_yellow:
			paint.setColor(Color.YELLOW);

			break;
		case R.id.iv_purple:
			paint.setColor(0xFFFF00FF);

			break;
		}
	}

	/**
	 * 状态改变调用
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		showLog("onProgressChanged");
	}

	/**
	 * 触摸时调用
	 */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		showLog("onStartTrackingTouch");

	}

	/**
	 * 触摸结束调用
	 */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		showLog("onStopTrackingTouch");
		int progress = seekBar.getProgress();// 获取当前进度
		Toast.makeText(this, "progress=" + progress, Toast.LENGTH_SHORT).show();
		paint.setStrokeWidth(progress);//设置画笔粗细
	}

	private void showLog(String msg) {
		Log.d("MainActivity", msg);
	}

	/**
	 * 参数1：当前触摸的控件
	 * 参数2：触摸事件（坐标）
	 */
	private float downX, downY;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN://按下
			showLog("ACTION_DOWN");
			//获取x、y轴按下坐标
			downX = event.getX();
			downY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE://移动
			showLog("ACTION_MOVE");
			float moveX = event.getX();
			float moveY = event.getY();
			//画线
			// 5.开始作画
			/**
			 * 参数1：x轴开始坐标 参数2：y轴开始坐标 参数3：x轴结束坐标 参数4：y轴结束坐标
			 */
			canvas.drawLine(downX, downY, moveX, moveY, paint);
			// 6.显示图像
			ivResult.setImageBitmap(bitmap);
			//重置起点坐标
			downX = moveX;
			downY = moveY;
			break;
		case MotionEvent.ACTION_UP://抬起
			showLog("ACTION_UP");
			
			break;
		}
		
		
		//true-当前控件消耗该事件，不往上传递，false-往上传递事件，后面有《自定义控件》
		return true;
	}
}
