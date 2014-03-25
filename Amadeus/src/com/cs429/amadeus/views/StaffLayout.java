package com.cs429.amadeus.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.HorizontalScrollView;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.R;

/**
 * This class represents a layout, on which {@link NoteView}s can be added.
 */
@SuppressWarnings("deprecation")
public class StaffLayout extends AbsoluteLayout implements OnTouchListener
{
	public static Bitmap wholeNoteBitmap;
	public static Bitmap halfNoteBitmap;
	public static Bitmap quarterNoteBitmap;
	public static Bitmap eighthNoteBitmap;
	public static Bitmap sixteenthNoteBitmap;
	
	public final int NUM_STAFF_LINES = 41;

	private int noteMarginRight; // horizontal distance between notes
	private int noteWidth;
	private int noteHeight;
	private int spaceHeight; // vertical distance between two staff lines
	private int lineHeight;
	private int addNoteType = Note.QUARTER_NOTE; 
	private LinkedList<Integer> lineTops = new LinkedList<Integer>(); // y positions of staff lines
	private Paint paint = new Paint();
	
	// maps notes to their y positions on the staff
	private HashMap<String, Integer> noteToYPos = new HashMap<String, Integer>(); 

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

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		setRight(Integer.MAX_VALUE);

		lineTops.clear();

		lineHeight = (int)(getHeight() * .005f);
		spaceHeight = (getHeight() - (lineHeight * 5)) / 6;
		noteMarginRight = calculateNoteMarginRight();

		initNoteToYPosMap();

		// Add the top values for all staff lines and spaces.
		int startY = (int)(-15.0 * (getHeight() / 6.0));
		for (int i = 1; i <= NUM_STAFF_LINES; i++)
		{
			int lineTop = (int)(startY + (i * getHeight() / 6.0));
			lineTops.add(lineTop);
		}

		paint.setStrokeWidth(lineHeight);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		// Draw staff lines behind note views.
		drawLines(canvas);

		// Draw added note views.
		super.onDraw(canvas);
	}
	
	private void drawLines(Canvas canvas)
	{
		// Only draw E5, G5, B5, D6, F6.
		int start = (int)(NUM_STAFF_LINES / 2.0) - 5;	
		for (int i = 0; i < lineTops.size(); i++)
		{
			if(i >= start && i <= start + 4)
			{
				int y = lineTops.get(i);
				canvas.drawLine(0, y, getWidth(), y, paint);
			}
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		if(!isEnabled())
		{
			return true;
		}
		
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
		adjustNoteSize(addNoteType);
		
		// Need to add correction to account for human error when touching screen.
		int correction = -getHeight() / 12; 
		Vector2<Integer, Integer> snappedPosition = getSnappedNotePos(x, y + correction);
		x = snappedPosition.x;
		y = snappedPosition.y;
		
		if(noteExistsAtSnappedPos(x, y))
		{
			return;
		}
		
		Note note = getNoteFromSnappedYPos(y);
		addNote(note, x, y);
	}

	/**
	 * Adds a note to the right of the rightmost previously added note. Used
	 * ONLY for adding recorded notes.
	 * @param note - the note to add
	 */
	public void addNote(Note note)
	{
		adjustNoteSize(note.type);
		
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
		int y = getSnappedYPosFromNote(note);	
		
		if(noteExistsAtSnappedPos(x, y))
		{
			return;
		}
		
		addNote(note, x, y);
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
	 * @param addNoteType - the type of note used for future adds (use {@link Note} constants)
	 */
	public void setAddNoteType(int addNoteType)
	{
		this.addNoteType = addNoteType;
	}

	/**
	 * Gets all {@link NoteView}s ordered from left to right on the staff.
	 * @return - all note views
	 */
	public ArrayList<NoteView> getAllNoteViews()
	{
		ArrayList<NoteView> noteViews = new ArrayList<NoteView>();
		for(int i = 0; i < getChildCount(); i++)
		{
			NoteView noteView = (NoteView)getChildAt(i);
			noteViews.add(noteView);
		}
		
		return noteViews;
	}
	
	/**
	 * Clears the staff of all {@link NoteView}s.
	 */
	public void clearAllNoteViews()
	{
		removeAllViews();	
		invalidate();
	}
	
	/**
	 * Scrolls to the right by the given amount.
	 * This assumes the parent is {@link HorizontalScrollView}.
	 * If amount is null, the default, 1.5 * screen width, will be used.
	 * @param amount - x amount to scroll by 
	 */
	public void scrollRight(Integer amount)
	{
		if(amount == null)
		{
			int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
			amount = (screenWidth / 3) * 2;
		}
		
		HorizontalScrollView parent = ((HorizontalScrollView)getParent());
		parent.scrollBy(amount, 0);
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
	
	private void init()
	{
		paint.setColor(Color.BLACK);

		setOnTouchListener(this);
		setWillNotDraw(false);

		wholeNoteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.whole_note);
		halfNoteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.half_note_down);
		quarterNoteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.quarter_note_down);
		eighthNoteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eighth_note_down);
		sixteenthNoteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sixteenth_note_down);
	}

	private void addNote(Note note, int x, int y)
	{	
		NoteView noteView = new NoteView(getContext(), this, note);
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(noteWidth, noteHeight, x, y);
		
		addView(noteView, getAddPos(x), lp); 
		invalidate();

		// This assumes that the parent of this view is a HorizontalScrollView.
		int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
		HorizontalScrollView parent = ((HorizontalScrollView)getParent());
		if(x - parent.getScrollX() > screenWidth)
		{
			scrollRight(null);
		}
	}
	
	private Vector2<Integer, Integer> getSnappedNotePos(int x, int y)
	{
		int snappedX = (int)(x / noteMarginRight) * noteMarginRight;

		// Find out which of the y positions in our map most closely matches the input y.
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
	
	private Note getNoteFromSnappedYPos(int y)
	{
		// We need to find the note key based on the y value in our map.
		for(Entry<String, Integer> entry : noteToYPos.entrySet())
		{
			int currY = entry.getValue();
			if(currY == y)
			{
				// This method is called when a note is manually being added.
				// Thus, we can use the selected note type as the added note's type.
				return new Note(entry.getKey(), addNoteType);
			}
		}

		return null;
	}

	private int getSnappedYPosFromNote(Note note)
	{
		String noteOctave = note.note + "" + note.octave;
		Integer y = noteToYPos.get(noteOctave);
		if(y == null)
		{
			y = Integer.MAX_VALUE;
		}

		return y;
	}
	
	private boolean noteExistsAtSnappedPos(int x, int y)
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
	
	private int getAddPos(int x)
	{
		// Calculate where to place a view with this x coord in the children list 
		// in order to keep the children list ordered by increasing x coord?
		int childCount = getChildCount();
		for(int i = 0; i < childCount; i++)
		{
			View child = getChildAt(i);
			if(child.getX() > x)
			{
				return i;
			}

			if(i == childCount - 1)
			{
				return childCount;
			}
		}
		
		return 0;
	}
	
	private void adjustNoteSize(int noteType)
	{
		Bitmap addNoteBitmap = StaffLayout.getBitmap(noteType);
		float bitmapWidth = addNoteBitmap.getWidth();
		float bitmapHeight = addNoteBitmap.getHeight();
		float whRatio = bitmapWidth / bitmapHeight;

		// Adjust the size of the notes to fit properly on the staff.
		switch(noteType)
		{
			case Note.WHOLE_NOTE:
				noteHeight = spaceHeight;
				break;
			case Note.HALF_NOTE:
				noteHeight = spaceHeight * 3;				
				break;
			case Note.QUARTER_NOTE:
				noteHeight = spaceHeight * 3;
				break;
			case Note.EIGHTH_NOTE:
				noteHeight = spaceHeight * 3;
				break;
			case Note.SIXTEENTH_NOTE:
				noteHeight = spaceHeight * 3;
				break;
		}

		noteWidth = (int)(whRatio * noteHeight);
	}

	private int calculateNoteMarginRight()
	{
		// The whole note has the widest bitmap.
		// Thus, its width determines the note margin.
		Bitmap wholeNoteBitmap = StaffLayout.wholeNoteBitmap;
		float width = wholeNoteBitmap.getWidth();
		float height = wholeNoteBitmap.getHeight();
		float whRatio = width / height;

		return (int)(whRatio * spaceHeight);
	}

	private void initNoteToYPosMap()
	{
		noteToYPos.clear();

		// Start at A8 and move down the staff.
		
		int step = (int)(getHeight() / 6.0);
		float multiplier = -15.0f;
		char currNote = 'A';
		int currOctave = 8;
		for(int i = 0; i < NUM_STAFF_LINES; i++)
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
	
	public static Bitmap getBitmap(int noteType)
	{
		switch(noteType)
		{
			case Note.WHOLE_NOTE:
				return wholeNoteBitmap;
			case Note.HALF_NOTE:
				return halfNoteBitmap;
			case Note.QUARTER_NOTE:
				return quarterNoteBitmap;
			case Note.EIGHTH_NOTE:
				return eighthNoteBitmap;
			case Note.SIXTEENTH_NOTE:
				return sixteenthNoteBitmap;
		}
		
		return null;
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