package com.npcompete.tattle.views;
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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.npcompete.tattle.R;
import com.npcompete.tattle.config.Tattle_Config_Constants;
import com.npcompete.tattle.config.Tattle_Configuration;
import com.npcompete.tattle.managers.AudioRecordManager;
import com.npcompete.tattle.managers.SDCardManager;
import com.npcompete.tattle.utils.AudioRecordStatus;
import com.npcompete.tattle.utils.RecordStatusListener;
import com.npcompete.tattle.utils.TattleConstants;
import com.npcompete.tattle.utils.Utilities;

public class TattleView extends View implements OnClickListener,
		RecordStatusListener {

	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	private Bitmap mBitmap;
	private Paint mPaint;
	private static AudioRecordStatus audioRecordStatus = AudioRecordStatus.START_RECORDING;
	private RadioGroup tattleComponents;
	private Context ctxt;
	private RadioButton recordAudio;
	private TattleControl floatingTattleControls;
	private Dialog tattleDialog;
	private FrameLayout markingLayout;
	private LinearLayout audioDurationLayout;
	private AudioRecordManager audioRecordManager;
	private TextView audioDurationTextView;
	private long startTime = 0L;
	private long timeInMilliseconds = 0L;
	private long timeSwapBuff = 0L;
	private long updatedTime = 0L;
	private ProgressBar progressBar;
	private MediaPlayer mediaPlayer;
	private boolean isMoved = false;
	private Timer timer;
	private CustomizedTimerTask myTimerTask;
	private Tattle_Configuration tattle_config;
	private int limitedDuration = 0;

	/** Constructor of TattleView to assign intial settings
	 * @param c - context of cuurent activity
	 * @param bimap - Current window's bitmap
	 */
	public TattleView(Context c, Bitmap bimap) {
		super(c);
		ctxt = c;
		View v = View.inflate(c, R.layout.tattle_controls, null);
		tattle_config = Tattle_Configuration.getConfigurationInstance();
		tattle_config.loadConfigurationValues(v,c);
		this.setBackgroundColor(Color.TRANSPARENT);
		assignPaintSettings();
		registerListeners(v);
		clearSDCardContents();
		showDialog(v, bimap, c);
	}
	/**
	 * Method to assign paint settings
	 */

	private void assignPaintSettings() {
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		int paintColor = tattle_config.getScribbleColor();
		mPaint.setColor(paintColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(tattle_config.getScribbleStrokeWidth());

	}
	
	/**
	 * Method to clear previous screenshots in SDCard 
	 */
	
	private void clearSDCardContents() {
		SDCardManager sdCardMgr = SDCardManager.getInstance(ctxt);
		sdCardMgr.removePreviouslySavedFilesFromSDCard();
	}

	
	/** Method to set screenshot as background and display dialog to scribble
	 * @param v
	 * @param b
	 * @param c
	 */
	@SuppressWarnings("deprecation")
	private void showDialog(View v, Bitmap b, Context c) {
		FrameLayout lay = (FrameLayout) v.findViewById(R.id.tattle_marking_layout);
		BitmapDrawable background = new BitmapDrawable(c.getResources(), b);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			lay.setBackground(background);
	    } else {
			lay.setBackgroundDrawable(background);
	    }
		if(lay.getChildCount() == 0) {
			lay.addView(this);
		}
		tattleDialog = new Dialog(c, R.style.popupTheme);
		tattleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		tattleDialog.setContentView(v);
		tattleDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		tattleDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				audioRecordStatus = AudioRecordStatus.START_RECORDING;
				AudioRecordManager.getInstance(ctxt).releaseResoureces();
			}
		});
		if(!tattleDialog.isShowing()) {
			tattleDialog.show();
		}
	}

	
	
	/**Method to register listeners
	 * @param v - current ciew
	 */
	private void registerListeners(View v) {
		tattleComponents = (RadioGroup) v.findViewById(R.id.tattle_components);
		recordAudio = (RadioButton) v.findViewById(R.id.option_record);
		floatingTattleControls = (TattleControl) v
				.findViewById(R.id.tattle_main_root_layout);
		floatingTattleControls.setVisibility(View.GONE);
		markingLayout = (FrameLayout) v.findViewById(R.id.tattle_marking_layout);
		audioDurationLayout = (LinearLayout) v.findViewById(R.id.audio_duration);
		audioDurationTextView = (TextView) v
				.findViewById(R.id.audio_duration_text);
		progressBar = ((ProgressBar) v.findViewById(R.id.audio_progress_bar));
		progressBar.setProgress(0);
		progressBar.setMax(120);
		audioRecordManager = AudioRecordManager.getInstance(ctxt);
		audioRecordManager.init(this);
		recordAudio.setOnClickListener(this);
		tattleComponents.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup parent, int arg1) {
				mPaint.setXfermode(null);
				int id = parent.getCheckedRadioButtonId();
				if (id == R.id.option_email) {
					RadioButton radioButton = (RadioButton) parent
							.findViewById(R.id.option_email);
						radioButton.setChecked(false);
					if(audioDurationLayout.getVisibility() == View.INVISIBLE)
					{
						captureCurrentWindowIntoBitmap();
					}
				}
			}
		});
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// This color makes the background as transparent
		canvas.drawColor(TattleConstants.TRANSPARENT_COLOR);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath(mPath, mPaint);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		mCanvas.drawPath(mPath, mPaint);
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		int i = 1;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isMoved = false;
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			isMoved = true;
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			if (i == 1 && isMoved == true) {
				floatingTattleControls.setVisibility(View.VISIBLE);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				params.setMargins((int) event.getRawX(), (int) event.getRawY(), 0, 0);
				floatingTattleControls.setLayoutParams(params);

			} else if(isMoved == false) {
			} else {
				i = 0;
				floatingTattleControls.setVisibility(View.GONE);
			}
			break;
		}
		return true;
	}

	
	/** Method to save Tattle in particular location and send mail
	 * @param bitmap
	 */
	public void saveBitmap(Bitmap bitmap) {
		SDCardManager sdcardMgr = SDCardManager.getInstance(ctxt);
		String absolutePathOfImage = sdcardMgr.saveCapturedImagesToSdcard(bitmap);
		sendMail(absolutePathOfImage);
	}

	
	/** Method to send mail
	 * @param path = Temporary path of the saved tattle images
	 */
	public void sendMail(String path) {
		ArrayList<Uri> filesList = new ArrayList<Uri>();
		File desFile = new File(path);
		if (desFile.exists() && desFile.isDirectory()) {
			File[] listFiles = desFile.listFiles();
			for (int i = 0; i < listFiles.length; i++) {
				if (listFiles[i].exists()) {
					Uri data = Uri.fromFile(listFiles[i]);
					filesList.add(data);
				}
			}
		}

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		if (filesList.size() == 1) {
			sharingIntent.putExtra(Intent.EXTRA_STREAM, filesList.get(0));
		} else if (filesList.size() > 1) {
			sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
			sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
					filesList);
		}

		sharingIntent.setType(Tattle_Config_Constants.MAIL_FORMAT);
		String mailSubject = tattle_config.getMailSubject();
		sharingIntent.putExtra(Intent.EXTRA_EMAIL, tattle_config.getMailRecipient());		  
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
		ctxt.startActivity(Intent.createChooser(sharingIntent, Tattle_Config_Constants.SEND_MAIL));

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.option_record) {
			performFloatingViewOperation(v);
		}

	}

	
	/**Method to peform actions of floating tattle controls
	 * @param view
	 */
	private void performFloatingViewOperation(View view) {
		if (audioRecordManager == null) {
			audioRecordManager = AudioRecordManager.getInstance(ctxt);
			audioRecordManager.init(this);
		}
		switch (audioRecordStatus) {
		case START_RECORDING:
			audioRecordManager.startAudioRecording();
			break;
		case STOP_RECORDING:
			audioRecordManager.stopAudioRecording();
			break;
		case START_PLAY:
			audioRecordManager.startPlayingRecordedAudio();
			break;
		case STOP_PLAY:
			audioRecordManager.stopPlayingRecordedAudio();
			break;
		default:
			break;
		}
	}

	/**
	 * capturing the current window screen into bitmap
	 */
	private void captureCurrentWindowIntoBitmap() {
		markingLayout.setDrawingCacheEnabled(true);
		Bitmap myBitmap = markingLayout.getDrawingCache();
		saveBitmap(myBitmap);
		markingLayout.setDrawingCacheEnabled(false);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	}

	@Override
	public void recordingStatus(AudioRecordStatus status, MediaPlayer mediaPlyr) {
		switch (status) {
		case COMPLETION_PLAY:
			limitedDuration = 0;
			tattle_config.setMovableControlRecordIcon(((Activity)ctxt).getResources().
					getDrawable(R.drawable.record));
			audioRecordStatus = AudioRecordStatus.START_RECORDING;
			progressBar.setProgress(0);
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			audioDurationLayout.startAnimation(AnimationUtils.loadAnimation(
					ctxt, R.anim.slide_down));
			audioDurationLayout.setVisibility(View.INVISIBLE);
			break;
		case START_RECORDING:
			tattle_config.setMovableControlRecordIcon(((Activity)ctxt).getResources().
					getDrawable(R.drawable.stop_selected));
			audioRecordStatus = AudioRecordStatus.STOP_RECORDING;
			startTime = SystemClock.uptimeMillis();
			audioDurationLayout.startAnimation(AnimationUtils.loadAnimation(
					ctxt, R.anim.slide_up));
			audioDurationLayout.setVisibility(View.VISIBLE);
			progressBar.setProgress(0);
			progressBar.setMax(120);
			if (timer != null) {
				timer.cancel();
			}
			timer = new Timer();
			myTimerTask = new CustomizedTimerTask();
			timer.schedule(myTimerTask, 0, 1000);
			break;
		case STOP_RECORDING:
			tattle_config.setMovableControlRecordIcon(((Activity)ctxt).getResources().
					getDrawable(R.drawable.play_selected));
			audioRecordStatus = AudioRecordStatus.START_PLAY;
			progressBar.setProgress(0);
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			audioDurationLayout.startAnimation(AnimationUtils.loadAnimation(
					ctxt, R.anim.slide_down));
			audioDurationLayout.setVisibility(View.INVISIBLE);
			break;
		case START_PLAY:
			tattle_config.setMovableControlRecordIcon(((Activity)ctxt).getResources().
					getDrawable(R.drawable.stop_selected));
			audioRecordStatus = AudioRecordStatus.STOP_PLAY;
			mediaPlayer = mediaPlyr;
			progressBar.setProgress(0);
			if (timer != null) {
				timer.cancel();
			}
			timer = new Timer();
			myTimerTask = new CustomizedTimerTask();
			timer.schedule(myTimerTask, 0, 1000);
			audioDurationLayout.startAnimation(AnimationUtils.loadAnimation(
					ctxt, R.anim.slide_up));
			audioDurationLayout.setVisibility(View.VISIBLE);
			break;
		case STOP_PLAY:
			tattle_config.setMovableControlRecordIcon(((Activity)ctxt).getResources().
					getDrawable(R.drawable.record));
			audioRecordStatus = AudioRecordStatus.START_RECORDING;
			progressBar.setProgress(0);
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			audioDurationLayout.startAnimation(AnimationUtils.loadAnimation(
					ctxt, R.anim.slide_down));
			audioDurationLayout.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
	}

	class CustomizedTimerTask extends TimerTask {
		@Override
		public void run() {

			((Activity) ctxt).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					switch (audioRecordStatus) {
					case STOP_RECORDING:
						timeInMilliseconds = SystemClock.uptimeMillis()
								- startTime;
						updatedTime = timeSwapBuff + timeInMilliseconds;
						int secs = (int) (updatedTime / 1000);
						progressBar.setProgress(secs);
						int mins = secs / 60;
						secs = secs % 60;
						if(secs == 59) {
							limitedDuration = limitedDuration + secs;
						}
						int audioDuration = tattle_config.getAudioRecordingDuration();//2 minutes
						if (limitedDuration == audioDuration) {
							audioDurationTextView.setText("" + mins + ":"
									+ String.format("%02d", secs));
							audioRecordManager.stopAudioRecording();
							progressBar.setProgress(0);
							if (timer != null) {
								timer.cancel();
								timer = null;
							}
						} else {
							audioDurationTextView.setText("" + mins + ":"
									+ String.format("%02d", secs));
						}
						break;
					case STOP_PLAY:
						int mediaTotalDuration = mediaPlayer.getDuration()/1000;
						if (progressBar.getMax() != mediaTotalDuration) {
							progressBar.setProgress(0);
							progressBar.setMax(mediaTotalDuration);
							audioDurationTextView.setText(Utilities
									.milliSecondsToTimer(mediaPlayer
											.getDuration()));
						} else {
							progressBar.setProgress(mediaPlayer.getCurrentPosition()/1000);
							long valueToSet = mediaPlayer.getDuration()
									- mediaPlayer.getCurrentPosition();
							String value = Utilities
									.milliSecondsToTimer(valueToSet);
							audioDurationTextView.setText((CharSequence) value);
						}
						break;
					default:
						break;
					}
				}
			});
		}

	}

}
