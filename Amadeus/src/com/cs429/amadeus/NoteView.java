
package com.cs429.amadeus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NoteView extends View
{
	private StaffLayout parent;
	private Note note;
	private Bitmap bitmap;
	private Rect transformation;
	private GestureDetector doubleTapListener;
	private Toast toast;

	public NoteView(Context context, StaffLayout parent, Note note, Bitmap bitmap)
	{
		super(context);

		this.parent = parent;
		this.note = note;
		this.bitmap = bitmap;

		int width = parent.getNoteWidth();
		int height = parent.getNoteHeight();
		transformation = new Rect(0, 0, width, height);
		
		doubleTapListener = new GestureDetector(context, new DoubleTapListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return doubleTapListener.onTouchEvent(event);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		canvas.drawBitmap(bitmap, null, transformation, null);
		super.onDraw(canvas);
	}
	
	private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener 
	{
        @Override
        public boolean onDown(MotionEvent e) 
        {
        	if(toast != null)
        	{
        		toast.cancel();
        	}
        	
        	toast = Toast.makeText(getContext(), note.toString(), Toast.LENGTH_SHORT);
        	toast.show();
            return true;
        }
        
        @Override
        public boolean onDoubleTap(MotionEvent e) 
        {
        	NoteView.this.parent.removeView(NoteView.this);
            return true;
        }
    }
}
