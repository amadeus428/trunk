package com.cs429.amadeus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

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
	private HashMap<String, Integer> noteToYPos = new HashMap<String, Integer>();
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
		
		initNoteToYPos();

		// Add the top values for all staff lines and spaces.
		int startY = (int)(-15.0 * (getHeight() / 6.0));
		for (int i = 1; i <= 41; i++)
		{
			int lineTop = (int)(startY + (i * getHeight() / 6.0));
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
			addNote(x, y);
		}

		return true;
	}

	/**
	 * Adds note at the closest snapped position to (x, y).
	 * @param x - the x coordinate in the layout
	 * @param y - the y coordinate in the layout
	 */
	public void addNote(int x, int y)
	{
		addNote(null, x, y, true);
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
		int y = calculateYPos(note);		
		addNote(note, x, y, false);
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
	
	private void addNote(Note note, int x, int y, boolean addCorrection)
	{
		// Adjust the size of the note based on the current note bitmap.
		adjustNoteSize();
		
		int correction = addCorrection ? -getHeight() / 12 : 0;
		Vector2<Integer, Integer> snappedPosition = getSnappedNotePosition(x, y + correction);
		x = snappedPosition.x;
		y = snappedPosition.y;
		
		if(noteExistsAt(x, y))
		{
			return;
		}
		
		if(note == null)
		{
			note = getNoteFromSnappedYPos(y);
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
	
	private Note getNoteFromSnappedYPos(int y)
	{
		for(Entry<String, Integer> entry : noteToYPos.entrySet())
		{
			int currY = entry.getValue();
			if(currY == y)
			{
				return new Note(entry.getKey());
			}
		}
		
		return null;
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

		int closestDiff = Integer.MAX_VALUE;
		int snappedY = Integer.MAX_VALUE;
		for(Entry<String, Integer> entry : noteToYPos.entrySet())
		{
			int yPos = entry.getValue();
			int diff = Math.abs(y - yPos);
			if(diff < closestDiff)
			{
				closestDiff = diff;
				snappedY = yPos;
			}
		}

		return new Vector2<Integer, Integer>(snappedX, snappedY);
	}
	
	private Integer calculateYPos(Note note)
	{
		String noteOctave = note.note + "" + note.octave;
		Integer y = noteToYPos.get(noteOctave);
		if(y == null)
		{
			y = Integer.MAX_VALUE;
		}
		
		return y;
	}
	
	private int calculateNoteMarginRight()
	{
		// The whole note has the widest bitmap.
		// Thus, its width determines the note margin.
		Bitmap wholeNoteBitmap = StaffLayout.wholeNote;
		float width = wholeNoteBitmap.getWidth();
		float height = wholeNoteBitmap.getHeight();
		float whRatio = width / height;

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
	
	private void initNoteToYPos()
	{
		noteToYPos.clear();
		
		int step = (int)(getHeight() / 6.0);
		
		float multiplier = -15.0f;
		char currNote = 'A';
		int currOctave = 8;
		for(int i = 0; i < 41; i++)
		{
			String noteOctave = currNote + "" + currOctave;
			noteToYPos.put(noteOctave, (int)(step * (multiplier / 2.0)));

			currNote = (char)(currNote - 1);
			if(currNote < 'A')
			{
				currNote = 'G';
			}
			if(currNote == 'B')
			{
				currOctave--;
			}
			
			multiplier += 1.0f;
		}
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
