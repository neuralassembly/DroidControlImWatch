package com.example.android.DroidControlImWatch;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.apache.http.client.ClientProtocolException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class DroidControlImWatch extends Activity {

	CheckBox mCheckBox1;
	CheckBox mCheckBox2;
	SeekBar mSeekBar;
	Socket mSocket = null;
	PrintWriter mOStream = null;
	    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	        
		// new DoRead().execute("192.168.11.14", "8081");
		new DoRead().execute("192.168.43.50", "8081");
	       
		mCheckBox1 = (CheckBox) findViewById(R.id.led1);
		mCheckBox1.setChecked(false);
		mCheckBox1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;
				boolean checked = checkBox.isChecked();
				if(checked)
				{
		       		if(mOStream != null){
		       			mOStream.println("l11");
		       			mOStream.flush();
	                }
				}else{
		       		if(mOStream != null){
		       			mOStream.println("l10");
		       			mOStream.flush();
	                }
				}
			}
		});
		
		mCheckBox2 = (CheckBox) findViewById(R.id.led2);
		mCheckBox2.setChecked(false);
		mCheckBox2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;
				boolean checked = checkBox.isChecked();
				if(checked)
				{
		       		if(mOStream != null){
		       			mOStream.println("l21");
		       			mOStream.flush();
	                }
				}else{
		       		if(mOStream != null){
		       			mOStream.println("l20");
		       			mOStream.flush();
	                }
				}
			}
		});
		
		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(mOStream != null){
					mOStream.println(String.format("s%02d", seekBar.getProgress()));
					mOStream.flush();
				}
			}
			public void onStartTrackingTouch(SeekBar seekBar) { 
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});        
	}
	    
	@Override
	public void onDestroy() {
		if(mOStream !=null){
			mOStream.close();
		}
		if(mSocket !=null){
			try {
				mSocket.close();
			}catch(IOException e){}
		}
		super.onDestroy();
	}

	public class DoRead extends AsyncTask<String, Void, Void> {
	    	 
		protected Void doInBackground(String... str) {
			try {
				mSocket = new Socket(str[0], Integer.parseInt(str[1]));
				return null;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			if(mSocket!=null){
				try{
					mOStream = new PrintWriter(mSocket.getOutputStream(), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}   
}