
package com.cs429.amadeus;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HorizontalTwoFingerScrollView extends HorizontalScrollView
{
	public HorizontalTwoFingerScrollView(Context context)
	{
		super(context);
	}

	public HorizontalTwoFingerScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public HorizontalTwoFingerScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event)
//	{
//		if(event.getPointerCount() > 1)
//		{
//			return super.onTouchEvent(event);
//		}
//		
//		return true;
//	}
//	
//	@Override
//	public boolean onGenericMotionEvent(MotionEvent event)
//	{
//		if(event.getPointerCount() > 1)
//		{
//			return super.onGenericMotionEvent(event);
//		}
//		
//		return true;
//	}
}
