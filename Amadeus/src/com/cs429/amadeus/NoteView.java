
package com.cs429.amadeus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class NoteView extends View
{
	private StaffLayout parent;
	private Bitmap bitmap;
	private Rect transformation;

	public NoteView(Context context, StaffLayout parent, Note note, Bitmap bitmap)
	{
		super(context);

		this.parent = parent;
		this.bitmap = bitmap;

		int width = parent.getNoteWidth();
		int height = parent.getNoteHeight();
		transformation = new Rect(0, 0, width, height);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			parent.removeView(this);
		}

		return true;
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		canvas.drawBitmap(bitmap, null, transformation, null);
		super.onDraw(canvas);
	}
}
