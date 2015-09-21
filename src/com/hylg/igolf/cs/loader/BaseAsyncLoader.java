package com.hylg.igolf.cs.loader;

import android.os.AsyncTask;

public abstract class BaseAsyncLoader {
	public abstract void requestData();
	
	protected AsyncTask<Object, Object, Integer> mTask;
	protected boolean mRunning;
	
	public BaseAsyncLoader() {
		mRunning = false;
	}
	
	public boolean isRunning() {
		return mRunning;
	}
	
	public void stopTask(boolean mayInterruptIfRunning) {
		if(null == mTask) return;
		if(mRunning) {
			mTask.cancel(mayInterruptIfRunning);
			
			mRunning = false;
		}
		mTask = null;
	}
}
