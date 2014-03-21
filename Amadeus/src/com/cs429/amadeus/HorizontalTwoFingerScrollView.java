
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
	private boolean isScrolling = false;
	private int startScrollX;

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

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getAction();
		int x = (int)event.getX();

		if(action == MotionEvent.ACTION_UP)
		{
			isScrolling = false;
		}
		else if(event.getPointerCount() > 1)
		{
			if(action == MotionEvent.ACTION_MOVE)
			{
				if(isScrolling)
				{
					int dist = startScrollX - x;
					scrollBy(dist, 0);
					startScrollX = x;
				}
				else
				{
					isScrolling = true;
					startScrollX = x;
				}
			}
		}

		return true;
	}
}
