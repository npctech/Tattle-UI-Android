package com.npcompete.tattle.utils;
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
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import com.npcompete.tattle.managers.TattleManager;

public class ActivityLifeCycle implements ActivityLifecycleCallbacks {

	private boolean activityOnCreated = false;

	@Override
	public void onActivityCreated(Activity arg0, Bundle arg1) {
		activityOnCreated = true;
		TattleManager tattleManager = TattleManager
				.getInstance();
		tattleManager.assignVariables(arg0, null);
		tattleManager.inflatingTattleViewOnCurrentView();

	}

	@Override
	public void onActivityDestroyed(Activity arg0) {

	}

	@Override
	public void onActivityPaused(Activity arg0) {
		activityOnCreated = false;
	}

	@Override
	public void onActivityResumed(Activity arg0) {
		if (!activityOnCreated) {
			TattleManager tattleManager = TattleManager
					.getInstance();
			tattleManager.assignVariables(arg0, null);
			tattleManager.removeTattleViewAndThenAdd();
		}
	}
	@Override
	public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {

	}

	@Override
	public void onActivityStarted(Activity arg0) {

	}

	@Override
	public void onActivityStopped(Activity arg0) {
		activityOnCreated = false;
	}

}
