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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.npcompete.tattle.config.Tattle_Config_Constants;

public class SDCardManager {

	private static SDCardManager sdCardMgr = null;
	private static Context ctxt;

	public static SDCardManager getInstance(Context context) {

		if (sdCardMgr == null) {
			sdCardMgr = new SDCardManager();
		}
		ctxt = context;
		return sdCardMgr;
	}

	public void saveAudioFilesToSdCard() {

	}

	/** Method to save the tattle images in particular folder
	 * @param bitmap
	 * @return
	 */
	public String saveCapturedImagesToSdcard(Bitmap bitmap) {
		File destDir = ctxt.getExternalFilesDir(Tattle_Config_Constants.SD_FOLDER_NAME + File.separator);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		String filePath = destDir + File.separator + Tattle_Config_Constants.SD_SCREENSHOT_IMAGE_NAME;
		File imagePath = new File(filePath);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(imagePath);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("GREC", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("GREC", e.getMessage(), e);
		}
		return destDir.getAbsolutePath();
	}

	/**Method to retreive the folder name to save the files in sdcard
	 * @return
	 */
	public File getFolderNameToSave() {
		File destDir = ctxt.getExternalFilesDir(Tattle_Config_Constants.SD_FOLDER_NAME + File.separator);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		return destDir;
	}

	/**
	 * Method to remove previous taken screen shots and audio files
	 */
	public void removePreviouslySavedFilesFromSDCard() {
		File file = ctxt.getExternalFilesDir(Tattle_Config_Constants.SD_FOLDER_NAME + File.separator);
		if (file.exists()) {
			File[] files = file.listFiles();
			for (File f : files) {
				f.delete();
			}
		}
		file.delete();
	}

}
