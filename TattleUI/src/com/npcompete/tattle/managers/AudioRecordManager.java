package com.npcompete.tattle.managers;
/*

Copyright (c) 2014 TattleUI (http://www.npcompete.com/)



Permission is hereby granted, free of charge, to any person obtaining a copy

of this software and associated documentation files (the "Software"), to deal

in the Software without restriction, including without limitation the rights

to use, copy, modify, merge, publish, distribute, sublicense, and/or sell

copies of the Software, and to permit persons to whom the Software is

furnished to do so, subject to the following conditions:



The above copyright notice and this permission notice shall be included in

all copies or substantial portions of the Software.



THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR

IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,

FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE

AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER

LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,

OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN

THE SOFTWARE.

*/
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.npcompete.tattle.utils.AudioRecordStatus;
import com.npcompete.tattle.utils.RecordStatusListener;
import com.npcompete.tattle.utils.TattleConstants;

public class AudioRecordManager {

	private boolean isRecording = false;
	private boolean isPlaying = false;
	private MediaPlayer mediaPlayer = null;
	private MediaRecorder mediaRecorder = null;
	private String filePath;
	private static AudioRecordManager audioRecordManager;
	private static SDCardManager sdCardManager;
	private RecordStatusListener recordListener;

	/** Singleton 
	 * @param context
	 * @return
	 */
	public static AudioRecordManager getInstance(Context context) {
		if (audioRecordManager == null) {
			audioRecordManager = new AudioRecordManager();
			sdCardManager = SDCardManager.getInstance(context);
		}
		return audioRecordManager;
	}

	/** Intitiate values
	 * @param status
	 */
	public void init(RecordStatusListener status) {
		createMediaInstances();
		this.recordListener = status;
	}

	
	/** Method to record audio
	 * @return
	 */
	public boolean startAudioRecording() {
		isRecording = true;
		try {
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			File destDir = sdCardManager.getFolderNameToSave();
			SimpleDateFormat s = new SimpleDateFormat(TattleConstants.SIMPLE_DATE_FORMAT,
					java.util.Locale.getDefault());
			String timestamp = s.format(new Date());
			filePath = destDir + File.separator + timestamp + TattleConstants.AUDIO_FORMAT_EXTENSION;
			mediaRecorder.setOutputFile(filePath);
			mediaRecorder.prepare();
			mediaRecorder.start();
			recordListener.recordingStatus(AudioRecordStatus.START_RECORDING,
					null);
		} catch (IllegalStateException e) {
			isRecording = false;
			e.printStackTrace();
		} catch (IOException e) {
			isRecording = false;
			e.printStackTrace();
		} catch (Exception e) {
			isRecording = false;
			e.printStackTrace();
		}
		return isRecording;
	}

	/**Method to stop recording
	 * @return
	 */
	public boolean stopAudioRecording() {
		if (isRecording) {
			mediaRecorder.stop();
			mediaRecorder.reset();
			recordListener.recordingStatus(AudioRecordStatus.STOP_RECORDING,
					null);
			isRecording = false;
		}
		return isRecording;
	}

	/**Method to start playing audio
	 * @return
	 */
	public boolean startPlayingRecordedAudio() {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.start();
			recordListener.recordingStatus(AudioRecordStatus.START_PLAY,
					mediaPlayer);
			isPlaying = true;
		} catch (IOException e) {
			e.printStackTrace();
			isPlaying = false;
		}
		return isPlaying;
	}

	/**Method to stop playing audio
	 * @return
	 */
	public boolean stopPlayingRecordedAudio() {
		if (mediaPlayer != null) {
			isPlaying = false;
		}
		mediaPlayer.reset();
		recordListener.recordingStatus(AudioRecordStatus.STOP_PLAY, null);
		isPlaying = false;
		return isPlaying;
	}


	/**
	 * Method to intiate media player instances
	 */
	private void createMediaInstances() {
		if (mediaRecorder == null) {
			mediaRecorder = new MediaRecorder();
		}
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							if (mediaPlayer != null) {
								mediaPlayer.reset();
								isPlaying = false;
								recordListener
										.recordingStatus(
												AudioRecordStatus.COMPLETION_PLAY,
												null);
							}
						}
					});
		}
	}

	/**
	 * Method to release media player instances 
	 */
	public void releaseResoureces() {
		isRecording = false;
		isPlaying = false;
		if (mediaPlayer != null) {
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		if (mediaRecorder != null) {
			mediaRecorder.reset();
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}

}
