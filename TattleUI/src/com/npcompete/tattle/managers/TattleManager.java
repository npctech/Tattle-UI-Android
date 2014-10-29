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
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.npcompete.tattle.R;
import com.npcompete.tattle.views.TattleView;

public class TattleManager implements View.OnTouchListener {

	private View screenShotView;
	private Bitmap bitmapOverlay;
	private FrameLayout mainLayoutOfCurrentView;
	private View tattleLayoutToInflate;
	private View tattleView;
	private Dialog dialog = null;
	private PopupWindow popUpWindow = null;
	private Activity activity;
	private Object currentWindowObj;
	private static TattleManager screenShotManager = null;
	private ArrayList<Object> viewLists;

	private TattleManager() {
		viewLists = new ArrayList<Object>();
	}

	public static TattleManager getInstance() {

		if (screenShotManager == null) {
			screenShotManager = new TattleManager();
		}

		return screenShotManager;
	}

	/** Initial Method to assign values
	 * @param act
	 * @param object
	 */
	public void assignVariables(Activity act, Object object) {
		activity = act;
		currentWindowObj = object;
		if (object != null) {
			viewLists.add(object);
		}
	}

	/**
	 * This is method to capure the screenshot of current window and convert to bitmap
	 */
	public void capturingScreenShotOfCurrentWindow() {
		screenShotView = activity.getWindow().getDecorView();
		int initialLocation[] = new int[2];
		int secondaryLocation[] = new int[2];
		screenShotView.setDrawingCacheEnabled(true);
		screenShotView.getLocationOnScreen(initialLocation);
		Bitmap myBitmap = screenShotView.getDrawingCache();
		int contentViewTop = getStatusBarHeight();
		bitmapOverlay = Bitmap.createBitmap(myBitmap, 0, contentViewTop,
				myBitmap.getWidth(), myBitmap.getHeight() - contentViewTop,
				null, true);
		iteratingViews(initialLocation, secondaryLocation, contentViewTop);
		new TattleView(activity, bitmapOverlay);
		screenShotView.setDrawingCacheEnabled(false);

	}
	

	/**Method to iterate the views added currently eg: dialog,popup (null for activity)
	 * @param initialLocation
	 * @param secondaryLocation
	 * @param statusBarHeight
	 */
	private void iteratingViews(int initialLocation[],int secondaryLocation[],int statusBarHeight ) {
		View currentRootView = null;
		if (viewLists != null && viewLists.size() > 0) {
			for (int i = 0; i < viewLists.size(); i++) {
				if (viewLists.get(i) != null
						&& viewLists.get(i) instanceof Dialog) {
					Dialog dialogWindow = (Dialog) viewLists.get(i);
					if (dialogWindow.isShowing()) {
						currentRootView = dialogWindow.getWindow().getDecorView()
								.getRootView();
					} else {
						currentRootView = null;
					}
				} else if (viewLists.get(i) != null
						&& viewLists.get(i) instanceof PopupWindow) {
					PopupWindow popupWindow = (PopupWindow) viewLists.get(i);
					if (popupWindow.isShowing()) {
						currentRootView = popupWindow.getContentView()
								.getRootView();
					} else {
						currentRootView = null;
					}
				}

				if (currentRootView != null) {
					currentRootView.setDrawingCacheEnabled(true);
					currentRootView.getLocationOnScreen(secondaryLocation);
					try {

						Bitmap backBitmap = bitmapOverlay;
						Bitmap bmOverlay = Bitmap.createBitmap(
								backBitmap.getWidth(), backBitmap.getHeight(),
								backBitmap.getConfig());
						Canvas canvas = new Canvas(bmOverlay);
						canvas.drawColor(Color.DKGRAY);
						Paint paint = new Paint();
						paint.setAlpha(60);
						canvas.drawBitmap(backBitmap, new Matrix(), paint);
						canvas.drawBitmap(currentRootView.getDrawingCache(),
								secondaryLocation[0] - initialLocation[0], secondaryLocation[1]
										- (initialLocation[1] + statusBarHeight),
								new Paint());
						currentRootView.setDrawingCacheEnabled(false);
						bitmapOverlay = bmOverlay;

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	/**
	 * Method to get status bar height to include that in tattle screen shot
	 * @return
	 */
	public int getStatusBarHeight() {
		Rect r = new Rect();
		Window w = activity.getWindow();
		w.getDecorView().getWindowVisibleDisplayFrame(r);
		return r.top;
	}

	/**
	 * Method to inflate tattle view on top current window
	 */
	public void inflatingTattleViewOnCurrentView() {

		if (currentWindowObj != null) {
			if (currentWindowObj instanceof Dialog) {
				dialog = (Dialog) currentWindowObj;
				mainLayoutOfCurrentView = (FrameLayout) dialog.getWindow()
						.getDecorView().getRootView();

			} else if (currentWindowObj instanceof PopupWindow) {
				popUpWindow = (PopupWindow) currentWindowObj;
				View view = popUpWindow.getContentView();
				mainLayoutOfCurrentView = (FrameLayout) view.getRootView();

			}
		} else {
			mainLayoutOfCurrentView = (FrameLayout) activity.getWindow()
					.getDecorView().getRootView();
		}

		tattleLayoutToInflate = View.inflate(activity,
				R.layout.tattle_view, null);
		tattleView = (View) tattleLayoutToInflate
				.findViewById(R.id.tattle_floating_view);
		tattleView.setOnTouchListener(this);
		mainLayoutOfCurrentView.addView(tattleLayoutToInflate);

	}

	/**
	 * Method to remove the tattle view from screen and then add again
	 */
	public void removeTattleViewAndThenAdd() {
		FrameLayout currentView = (FrameLayout) activity.getWindow()
				.getDecorView().getRootView();
		currentView.removeViewAt(currentView.getChildCount() - 1);
		inflatingTattleViewOnCurrentView();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int id = v.getId();
		if (id == R.id.tattle_floating_view) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				return true;
			case MotionEvent.ACTION_UP:
				int pos = mainLayoutOfCurrentView.getChildCount() - 1;
				View view = mainLayoutOfCurrentView.getChildAt(pos);
				mainLayoutOfCurrentView.removeViewAt(pos);
				capturingScreenShotOfCurrentWindow();
				mainLayoutOfCurrentView.addView(view);
				return true;
			case MotionEvent.ACTION_MOVE:
				return true;
			}
		}
		return true;
	}
}
