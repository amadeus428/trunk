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
	public static final int WHOLE_NOTE = 0;
	public static final int HALF_NOTE_DOWN = 1;
	public static final int QUARTER_NOTE_DOWN = 2;
	public static final int EIGHTH_NOTE_DOWN = 3;
	public static final int SIXTEENTH_NOTE_DOWN = 4;
	
	// Should probably put decoded bitmap resources somewhere else...
	public static Bitmap wholeNote;
	public static Bitmap halfNoteDown;
	public static Bitmap quarterNoteDown;
	public static Bitmap eighthNoteDown;
	public static Bitmap sixteenthNoteDown;

	private Bitmap currNoteBitmap;
	private int currNoteId = StaffLayout.QUARTER_NOTE_DOWN;
	private int noteMarginRight;
	private int noteWidth;
	private int noteHeight;
	private int spaceHeight;
	private int lineHeight;
	private LinkedList<Integer> lineTops = new LinkedList<Integer>();
	private LinkedList<Integer> spaceTops = new LinkedList<Integer>();
	private HashMap<Character, Integer> noteToStepIndex = new HashMap<Character, Integer>();
	private Paint paint = new Paint();

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

		setOnTouchListener(this);
		setWillNotDraw(false);

		wholeNote = BitmapFactory.decodeResource(getResources(), R.drawable.whole_note);
		halfNoteDown = BitmapFactory.decodeResource(getResources(), R.drawable.half_note_down);
		quarterNoteDown = BitmapFactory.decodeResource(getResources(), R.drawable.quarter_note_down);
		eighthNoteDown = BitmapFactory.decodeResource(getResources(), R.drawable.eighth_note_down);
		sixteenthNoteDown = BitmapFactory.decodeResource(getResources(), R.drawable.sixteenth_note_down);
		currNoteBitmap = quarterNoteDown; // default is quarter note
		
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
		noteMarginRight = calculateNoteMarginRight();

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
			addNote(null, x, y);
		}

		return true;
	}

	/**
	 * Adds note at the closest snapped position to (x, y).
	 * @param note - the note to add
	 * @param x - the x coordinate in the layout
	 * @param y - the y coordinate in the layout
	 */
	public void addNote(Note note, int x, int y)
	{
		// Adjust the size of the note based on the current note bitmap.
		adjustNoteSize();
		
		Vector2<Integer, Integer> snappedPosition = getSnappedNotePosition(x, y);
		x = snappedPosition.x;
		y = snappedPosition.y;

		if(noteExistsAt(x, y))
		{
			return;
		}

		NoteView noteView = new NoteView(getContext(), this, note, currNoteBitmap);
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
	 * @param note - the note to add
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
			if(currNoteId == StaffLayout.WHOLE_NOTE)
			{
				// Whole notes look different than the other notes so needs to be handled differently.
				amountDown += (noteHeight / 2);
			}
			else
			{
				amountDown += noteHeight / 6;
			}
		}		
		int y = spaceHeight + lineHeight + amountDown;
		
		addNote(note, x, y + 50);
	}
	
	/**
	 * Saves the current sheet to an XML file with the given fileName.
	 * @param fileName - the name of the file to save the sheet to (without the extension)
	 */
	public void saveSheet(String fileName)
	{
		// TODO: How do we want to implement this?
	}
	
	/**
	 * Sets the current note type for future adds.
	 * @param addNoteType - the type of note used for future adds (use StaffLayout constants)
	 */
	public void setCurrAddNoteType(int addNoteType)
	{
		currNoteId = addNoteType;
		switch(addNoteType)
		{
			case WHOLE_NOTE:
				currNoteBitmap = StaffLayout.wholeNote;
				break;
			case HALF_NOTE_DOWN:
				currNoteBitmap = StaffLayout.halfNoteDown;
				break;
			case QUARTER_NOTE_DOWN:
				currNoteBitmap = StaffLayout.quarterNoteDown;
				break;
			case EIGHTH_NOTE_DOWN:
				currNoteBitmap = StaffLayout.eighthNoteDown;
				break;
			case SIXTEENTH_NOTE_DOWN:
				currNoteBitmap = StaffLayout.sixteenthNoteDown;
				break;
		}
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
	
	private void adjustNoteSize()
	{
		float bitmapWidth = currNoteBitmap.getWidth();
		float bitmapHeight = currNoteBitmap.getHeight();
		float whRatio = bitmapWidth / bitmapHeight;
		
		// Adjust the size of the notes to fit properly on the staff.
		switch(currNoteId)
		{
			case WHOLE_NOTE:
				noteHeight = spaceHeight;
				break;
			case HALF_NOTE_DOWN:
				noteHeight = spaceHeight * 3;				
				break;
			case QUARTER_NOTE_DOWN:
				noteHeight = spaceHeight * 3;
				break;
			case EIGHTH_NOTE_DOWN:
				noteHeight = spaceHeight * 3;
				break;
			case SIXTEENTH_NOTE_DOWN:
				noteHeight = spaceHeight * 3;
				break;
		}
		
		noteWidth = (int)(whRatio * noteHeight);
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
				if(currNoteId == StaffLayout.WHOLE_NOTE)
				{
					// Whole notes look different than the other notes so needs to be handled differently.
					snappedY = lineY - (noteHeight / 2);
				}
				else
				{
					snappedY = lineY - (noteHeight / 6);
				}
			}
		}

		return new Vector2<Integer, Integer>(snappedX, snappedY);
	}
	
	private int calculateNoteMarginRight()
	{
		// The whole note has the widest bitmap.
		// Thus, its width determines the note margin.
		Bitmap wholeNoteBitmap = StaffLayout.wholeNote;
		float width = wholeNoteBitmap.getWidth();
		float height = wholeNoteBitmap.getHeight();
		float whRatio = width / height;
		
		Log.e("schimpf", "" + ((int)(whRatio * spaceHeight)));
		
		return (int)(whRatio * spaceHeight);
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
