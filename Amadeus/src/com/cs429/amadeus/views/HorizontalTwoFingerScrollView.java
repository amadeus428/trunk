
package com.cs429.amadeus.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
/**
 * This doesn't work correctly... yet.
 * @deprecated
 */
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
