package com.cs429.amadeus;

import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;

@SuppressWarnings("deprecation")
public class StaffLayout extends AbsoluteLayout implements OnTouchListener
{
	// Only need to decode this bitmap once.
	// Should probably put decoded bitmap resources somewhere else...
	public static Bitmap quarterNoteDown;

	private int noteMarginRight;
	private int noteWidth;
	private int noteHeight;
	private int spaceHeight;
	private int lineHeight;
	private LinkedList<Integer> lineTops = new LinkedList<Integer>();
	private LinkedList<Integer> spaceTops = new LinkedList<Integer>();
	private HashMap<Character, Integer> noteToStepIndex = new HashMap<Character, Integer>();
	private Paint paint = new Paint();
	private Paint dragBitmapPaint = new Paint();

	public StaffLayout(Context context)
	{
		super(context);

		init();
	}

	public StaffLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		init();
	}

	public StaffLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		init();
	}

	private void init()
	{
		paint.setColor(Color.BLACK);
		dragBitmapPaint.setAlpha(122);

		setOnTouchListener(this);
		setWillNotDraw(false);

		quarterNoteDown = BitmapFactory.decodeResource(getResources(), R.drawable.quarter_note_down);
		
		noteToStepIndex.put('D', 0);
		noteToStepIndex.put('C', 1);
		noteToStepIndex.put('B', 2);
		noteToStepIndex.put('A', 3);
		noteToStepIndex.put('G', 4);
		noteToStepIndex.put('F', 5);
		noteToStepIndex.put('E', 6);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		setRight(Integer.MAX_VALUE);

		lineTops.clear();
		spaceTops.clear();

		lineHeight = (int)(getHeight() * .005f);
		spaceHeight = (getHeight() - (lineHeight * 5)) / 6;
		noteHeight = spaceHeight * 3;
		noteWidth = noteHeight / 3;
		noteMarginRight = noteWidth * 2;

		// Add the top values for all staff lines and spaces.
		spaceTops.add(0);
		for (int i = 1; i <= 5; i++)
		{
			int lineTop = i * getHeight() / 6;
			lineTops.add(lineTop);
			spaceTops.add(lineTop + lineHeight);
		}

		paint.setStrokeWidth(lineHeight);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		// Draw staff lines behind note views.
		for (float lineTop : lineTops)
		{
			canvas.drawLine(0, lineTop, getWidth(), lineTop, paint);
		}

		// Draw added note views.
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		int action = event.getAction();
		int x = (int)event.getX();
		int y = (int)event.getY();

		if(action == MotionEvent.ACTION_UP)
		{
			// TODO: Which kind of note should be added?
			// TODO: How do we know where to place it?
			addNote(null, x, y);
		}

		return true;
	}

	/**
	 * Adds note at the closest snapped position to (x, y).
	 * 
	 * @param note
	 * @param x
	 * @param y
	 */
	public void addNote(Note note, int x, int y)
	{
		Vector2<Integer, Integer> snappedPosition = getSnappedNotePosition(x, y);
		x = snappedPosition.x;
		y = snappedPosition.y;

		if(noteExistsAt(x, y))
		{
			return;
		}

		NoteView noteView = new NoteView(getContext(), this, note);
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(noteWidth, noteHeight, x, y);
		addView(noteView, lp);
		invalidate();
		
		int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
		HorizontalScrollView parent = ((HorizontalScrollView)getParent());
		if(x - parent.getScrollX() > screenWidth)
		{
			parent.scrollBy((screenWidth / 3) * 2, 0);
		}
	}

	/*
	 * Adds a note to the right of the rightmost previously added note. Used
	 * ONLY for adding recorded notes.
	 * 
	 * @param note
	 */
	public void addNote(Note note)
	{
		int lastNoteRight = 0;
		for (int i = 0; i < getChildCount(); i++)
		{
			View child = getChildAt(i);

			int right = child.getRight();
			if(right > lastNoteRight)
			{
				lastNoteRight = right;
			}
		}
		
		int x = lastNoteRight + noteMarginRight;
		
		// TODO: This is hacky. Only does one octave. Change that shit.
		int numSteps = noteToStepIndex.get(note.note);		
		int numSpaces = (numSteps + 1) / 2;
		int amountDown = (numSpaces * spaceHeight) + (numSpaces * lineHeight);
		if(numSteps % 2 == 0)
		{
			amountDown += noteHeight / 6;
		}		
		int y = spaceHeight + lineHeight + amountDown;
		
		addNote(note, x, y + 50);
	}

	public int getLineHeight()
	{
		return lineHeight;
	}

	public int getSpaceHeight()
	{
		return spaceHeight;
	}

	public int getNoteMarginRight()
	{
		return noteMarginRight;
	}

	public int getNoteWidth()
	{
		return noteWidth;
	}

	public int getNoteHeight()
	{
		return noteHeight;
	}

	private Vector2<Integer, Integer> getSnappedNotePosition(int x, int y)
	{
		int snappedX = (int)(x / noteMarginRight) * noteMarginRight;

		// Go through all staff line and space tops to see what the note's y
		// position should be.
		int snappedY = 0;
		for (int spaceY : spaceTops)
		{
			if(y >= spaceY && y <= spaceY + spaceHeight)
			{
				snappedY = spaceY;
			}
		}
		for (int lineY : lineTops)
		{
			float correction = (spaceHeight * .15f);
			if(y >= lineY - correction && y <= lineY + lineHeight + correction)
			{
				snappedY = lineY - (noteHeight / 6);
			}
		}

		return new Vector2<Integer, Integer>(snappedX, snappedY);
	}

	private boolean noteExistsAt(int x, int y)
	{
		for (int i = 0; i < getChildCount(); i++)
		{
			View child = getChildAt(i);
			if(child.getX() == x && child.getY() == y)
			{
				return true;
			}
		}

		return false;
	}

	private class Vector2<X, Y>
	{
		public final X x;
		public final Y y;

		public Vector2(X x, Y y)
		{
			this.x = x;
			this.y = y;
		}
	}
}
