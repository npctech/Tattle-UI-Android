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
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class TattleControl extends LinearLayout {
	
	private int windowWidth, windowHeight;
	private int componentStatus;
	private final static int START_DRAGGING = 0;
	private final static int STOP_DRAGGING = 1;
	private Context ctxt;
	int xPos, yPos;
	float mLastX, mLastY, mStartY;
	private android.widget.FrameLayout.LayoutParams componentParams;
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		componentParams = (android.widget.FrameLayout.LayoutParams) getLayoutParams();
		Display display = ((Activity) ctxt).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		windowWidth = size.x;
		windowHeight = size.y;
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			componentStatus = START_DRAGGING;
			break;
		case MotionEvent.ACTION_UP:
			componentStatus = STOP_DRAGGING;
			break;
		case MotionEvent.ACTION_MOVE:
			if (componentStatus == START_DRAGGING) {
				xPos = ((int) ev.getRawX());
				yPos = ((int) ev.getRawY());
				if (xPos > windowWidth) {
					xPos = windowWidth;
				}
				if (yPos > windowHeight) {
					yPos = windowHeight;
				}
				componentParams.leftMargin = xPos - 25;
				componentParams.topMargin = yPos - 25;
				this.setLayoutParams(componentParams);
				this.invalidate();
			}
			break;
		}
		return true;
	}

	

	public TattleControl(Context context) {
		super(context);
		ctxt = context;
	}

	public TattleControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctxt = context;
	}

	public TattleControl(Context context, AttributeSet attrs,
			int defStyle) {
		this(context, attrs);
		ctxt = context;

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			componentStatus = START_DRAGGING;
			mLastX = event.getX();
			mLastY = event.getY();
			mStartY = mLastY;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			componentStatus = STOP_DRAGGING;
			break;
		case MotionEvent.ACTION_MOVE:
			float x = event.getX();
			float y = event.getY();
			if (x - mLastX > 5 || y - mLastY > 5) {
				mStartY = y;
				return true;
			}
			break;
		}
		return false;
	}

}
