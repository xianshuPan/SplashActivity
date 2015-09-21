package com.hylg.igolf;

import android.util.Log;

/**
 * sdf
 */
public class DebugTools
{
	public boolean isDebug = true;

	private static DebugTools debug;

	private DebugTools()
	{

	}

	public static DebugTools getDebug()
	{
		if (debug == null)
			debug = new DebugTools();
		return debug;
	}

	public void debug_v(String tag, String content)
	{
		if (isDebug)
		{
			Log.v(tag, content);
		}
	}
	
	public void debug_d(String tag, String content)
	{
		if (isDebug)
		{
			Log.d(tag, content);
		}
	}
	
	public void debug_e(String tag, String content)
	{
		if (isDebug)
		{
			Log.e(tag, content);
		}
	}
}
