package com.npcompete.tattle.config;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.npcompete.tattle.R;
import com.npcompete.tattle.views.TattleControl;

public class Tattle_Configuration {

	private static Tattle_Configuration tattle_config = null;
	private View view;
	private int yourColor = 0;
	private int yourPaintStrokeWidth = 0;
	private int yourAudioRecordingDuration = 0;
	private String mailSubject = null;
	private String[] mailRecipients = null;
	private ArrayList<String> recipientList = new ArrayList<String>();

	private Tattle_Configuration() {

	}

	public static Tattle_Configuration getConfigurationInstance() {

		if (tattle_config == null) {
			tattle_config = new Tattle_Configuration();
		}

		return tattle_config;
	}

	public void loadConfigurationValues(View v, Context ctxt) {
		this.view = v;
	}

	public void setSpotImageIcon(Drawable yourSpotIcon) {
		if (view != null) {
			View spotView = (View) view.findViewById(R.id.tattle_floating_view);
			spotView.setBackground(yourSpotIcon);
		} else {
			Log.e("Tattle Configuration : ",
					"Unable to update Spot Image Icon ");
		}
	}

	public void setMovableControlBackgroundColor(int yourColor) {
		if (view != null) {
			TattleControl tattleControl = (TattleControl) view
					.findViewById(R.id.tattle_main_root_layout);
			tattleControl.setBackgroundColor(yourColor);
		} else {
			Log.e("Tattle Configuration : ",
					"Unable to change movable control background color");
		}
	}

	public void setMovableControlMailIcon(Drawable yourMailIcon) {
		RadioButton mailOption = (RadioButton) view
				.findViewById(R.id.option_email);
		mailOption.setButtonDrawable(yourMailIcon);
	}

	public void setMovableControlRecordIcon(Drawable yourRecordIcon) {
		if (view != null) {
			RadioButton mailOption = (RadioButton) view
					.findViewById(R.id.option_record);
			mailOption.setButtonDrawable(yourRecordIcon);
		} else {
			Log.e("Tattle Configuration : ",
					"Unable to change movable control's record icon");
		}
	}

	public void setMovableControlPlayIcon(Drawable yourPlayIcon) {
		if (view != null) {
			RadioButton mailOption = (RadioButton) view
					.findViewById(R.id.option_record);
			mailOption.setButtonDrawable(yourPlayIcon);
		} else {
			Log.e("Tattle Configuration : ",
					"Unable to change movable control's play icon");

		}
	}

	public void setMovableControlStopIcon(Drawable yourStopIcon) {
		if (view != null) {
			RadioButton mailOption = (RadioButton) view
					.findViewById(R.id.option_record);
			mailOption.setButtonDrawable(yourStopIcon);
		} else {
			Log.e("Tattle Configuration : ",
					"Unable to change movable control's stop icon");

		}
	}

	public void setScribbleColor(int yourColor) {
		this.yourColor = yourColor;
	}

	public int getScribbleColor() {
		if (yourColor == 0) {
			return Tattle_Config_Constants.COLOR_BLACK;
		} else {
			return yourColor;
		}
	}

	public int getScribbleStrokeWidth() {
		if (this.yourPaintStrokeWidth == 0) {
			return Tattle_Config_Constants.PAINT_DEFAULT_STROKE_WIDTH;
		} else {
			return this.yourPaintStrokeWidth;
		}
	}

	public void setScribbleStrokeWidth(int yourPaintStrokeWidth) {
		this.yourPaintStrokeWidth = yourPaintStrokeWidth;
	}

	public int getAudioRecordingDuration() {
		if (this.yourAudioRecordingDuration == 0) {
			return Tattle_Config_Constants.AUDIO_DURATION;
		} else {
			return this.yourAudioRecordingDuration;
		}
	}

	public void setAudioRecordingDuration(int yourAudioRecordingDuration) {
		this.yourAudioRecordingDuration = yourAudioRecordingDuration;
	}

	public String[] getMailRecipient() {
		if (mailRecipients == null) {
			return Tattle_Config_Constants.MAIL_RECIPIENT;
		} else {
			return mailRecipients;
		}
	}

	public void setMailRecipient(String mailRecipient) {
		this.recipientList.add(mailRecipient);
		if (recipientList != null && recipientList.size() > 0) {
			for (int i = 0; i < recipientList.size(); i++) {
				mailRecipients[i] = recipientList.get(i);
			}
		} else {
			mailRecipients = null;
		}
	}

	public String getMailSubject() {
		if (mailSubject == null) {
			return Tattle_Config_Constants.MAIL_SUBJECT;
		} else {
			return mailSubject;
		}
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

}
