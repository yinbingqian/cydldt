package com.lnpdit.chatuidemo.activity;

import java.util.Iterator;
import java.util.Set;

import com.lnpdit.chatuidemo.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;



public class MicActivity extends Activity {

	Context context;

	// Button recordButton, stopButton, exitButton;
	ToggleButton mic_tb;
	Button return_bt;

	boolean isRecording = false;
	static final int frequency = 44100;
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	int recBufSize, playBufSize;
	AudioRecord audioRecord;
	AudioTrack audioTrack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mic);

		context = this;

		viewInit();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isRecording = false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isRecording = false;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isRecording = false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		isRecording = false;
	}

	private void viewInit() {
		mic_tb = (ToggleButton) this.findViewById(R.id.mic_tb);
		mic_tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
							.getDefaultAdapter();
//					if(mBluetoothAdapter != null){
//						if(!mBluetoothAdapter.isEnabled()){
//							Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//							startActivity(intent);
//						}
//					}
					Set<BluetoothDevice> set = mBluetoothAdapter.getBondedDevices();
					if(set.size()>0){
						isRecording = true;
						new RecordPlayThread().start();
					}else{
						Toast.makeText(context, "未检测到您连接了蓝牙设备，请连接后重试", Toast.LENGTH_SHORT).show();
						mic_tb.setChecked(false);
					}
				} else {
					isRecording = false;
				}
			}
		});

		return_bt = (Button) this.findViewById(R.id.return_bt);
		return_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isRecording = false;
				finish();
			}
		});

		// recordButton = (Button) this.findViewById(R.id.open_mic);
		// stopButton = (Button) this.findViewById(R.id.close_mic);
		// exitButton = (Button) this.findViewById(R.id.exit_mic);

		recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		playBufSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		// 实例化AudioRecord(声音来源，采样率，声道设置，采样声音编码，缓存大小）
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, audioEncoding, recBufSize);
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
				channelConfiguration, audioEncoding, playBufSize,
				AudioTrack.MODE_STREAM);

		// recordButton.setOnClickListener(new ClickEvent());
		// stopButton.setOnClickListener(new ClickEvent());
		// exitButton.setOnClickListener(new ClickEvent());
		audioTrack.setStereoVolume(0.7f, 0.7f);
	}

	// class ClickEvent implements View.OnClickListener {
	// public void onClick(View v) {
	// if (v == recordButton) {
	// isRecording = true;
	// new RecordPlayThread().start();
	// } else if (v == stopButton) {
	// isRecording = false;
	// } else if (v == exitButton) {
	// isRecording = false;
	// MicActivity.this.finish();
	// }
	// }
	// }

	class RecordPlayThread extends Thread {
		public void run() {
			try {
				// byte 文件来存储声音
				byte[] buffer = new byte[recBufSize];

				// 开始采集声音
				audioRecord.startRecording();

				// 播放声音
				audioTrack.play();
				while (isRecording) {

					// 从MIC存储到缓存区
					int bufferReadResult = audioRecord.read(buffer, 0,
							recBufSize);
					byte[] tmpBuf = new byte[bufferReadResult];
					System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);

					// 播放缓存区的数据
					audioTrack.write(tmpBuf, 0, tmpBuf.length);
				}
				audioTrack.stop();
				audioRecord.stop();
			} catch (Throwable t) {
				// Toast.makeText(context, t.getMessage(), 1000);
			}
		}
	}

}
