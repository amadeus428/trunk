package com.cs429.amadeus.views;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.R;

public class StaffView extends View implements OnTouchListener {

    private boolean isFingerDown;
    private int touchX;
    private int touchY;
    private int touchType;

    private int leftEdge;
    private int rightEdge;
    private int bottomEdge;
    private int topEdge;
    private float xpad;
    private float ypad;
    Paint paint = new Paint();
    float margin;
    float width;
    float height;
    Rect eRect;
    Bitmap noteBitmap;

    ArrayList<Note> notes = new ArrayList<Note>(10);

    public StaffView(Context context) {
	super(context);
	paint.setColor(Color.BLACK);
	paint.setStrokeWidth(10);
	updateMeasurements();
	noteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.quarter_note_down);
	this.setOnTouchListener(this);

    }

    public void displayNote(String noteString) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
	if (!isMeasured)
	    updateMeasurements();
	for (int i = 1; i <= 5; i++) {
	    canvas.drawLine(leftEdge, i * margin, width, i * margin, paint);
	}
	if (isFingerDown) {
	    canvas.drawCircle(touchX, touchY, margin / 2, paint);
	}
	for (int i = 0; i < notes.size(); i++) {
	    drawNote(notes.get(i), canvas, (int) (i * 2 * margin));
	}

	if (displayNote != null) {
	    drawNote(displayNote, canvas, (int) (notes.size() * 2 * margin));
	}
    }

    /*
     * If a note was clicked, this returns the index that it
     */
    public int getClickedNoteIndex(MotionEvent event) {
	for (int i = 0; i < notes.size(); i++) {
	    if (!(margin / 2.0 + margin * 2 * i < event.getX() && event.getX() < margin * 2 * i + 3 / 2.0 * margin)) {
		continue;
	    }

	    Note note = notes.get(i);
	    if (getNoteCoordinate(note) * margin / 2 + margin / 2.0 < event.getY()
		    && event.getY() < getNoteCoordinate(note) * margin / 2 + margin * 3 / 2.0) {
		return i;
	    } else {
		break;
	    }
	}

	return -1;

    }

    public void drawNote(Note note, Canvas canvas, int xCoord) {
	// canvas.drawCircle(xCoord, getNoteCoordinate(note)*margin/2 +margin,
	// margin/2, paint);
	eRect.set(xCoord, (int) ((getNoteCoordinate(note) - 3) * margin / 2 + margin), xCoord + 200,
		(int) ((getNoteCoordinate(note) - 3) * margin / 2 + 6 * margin));
	canvas.drawBitmap(noteBitmap, null, eRect, null);
    }

    boolean isMeasured = false;

    private void updateMeasurements() {
	eRect = new Rect(0, 0, 150, (int) (250));

	xpad = (float) (getPaddingLeft() + getPaddingRight());
	ypad = (float) (getPaddingLeft() + getPaddingRight());
	leftEdge = this.getLeft();
	rightEdge = this.getRight();
	bottomEdge = this.getBottom();
	topEdge = this.getTop();
	margin = getHeight() / 6;
	height = getHeight();
	width = getWidth();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
	getTouchInfo(event);
	int ind;

	if (touchType != MotionEvent.ACTION_UP) {
	    isFingerDown = true;
	} else {
	    isFingerDown = false;

	    if ((ind = getClickedNoteIndex(event)) != -1) {
		notes.remove(ind);
	    } else {

		Note noteType = getNote();

		insertNote(noteType);
	    }
	}

	invalidate();
	return true;
    }

    /*
     * This method gets relevant data out of motion events received by StaffView
     * through its OnClickListener interface. This is then used for getNote()
     */
    private void getTouchInfo(MotionEvent event) {
	touchX = (int) event.getX();
	touchY = (int) event.getY();
	touchType = event.getAction();

    }

    /*
     * Adds a note object to the end of the staff
     */
    private void insertNote(Note note) {
	notes.add(note);
    }

    /*
     * @DEPRECATED Used to temporarily display a note that is not a permanent
     * part of the staff. Used for displaying possible positions of a note not
     * yet added to the staff.
     */
    private Note displayNote;

    public void makeDisplayNote(Note note) {
	this.displayNote = note;
	invalidate();
    }

    /**
     * Gets the number of 'steps' from the top of the staff that the touch event
     * is closest to
     * 
     * @return the number of steps from top line on staff
     */
    private int getStaffStepsFromTopLine() {
	int topOfStaff = (int) margin;

	int interval = (int) Math.round((((double) (touchY - topOfStaff)) / (margin / 2.0)));
	Log.i("APPDATA", "Interval: " + interval);

	return interval;
    }

    /*
     * This method uses the variables that are set by touch events to create a
     * note object
     * 
     * @return the note generated from the most recent touch data
     */
    private Note getNote() {

	int steps = getStaffStepsFromTopLine();

	char noteLetter = getNoteLetterFromInterval(steps);
	int octave = getOctave(steps);
	boolean isSharp = getIsSharp(steps);

	Note note = new Note(noteLetter, octave, isSharp, Note.QUARTER_NOTE);

	return note;
    }

    /*
     * Used to tell if a position is sharped or flatted by the key signature for
     * the staff.
     */
    private boolean getIsSharp(int interval) {
	// TODO Auto-generated method stub
	return false;
    }

    // TODO This method suggests that the A in FACE on Treble is A5
    private int getOctave(Integer interval) {
	if (interval > 5)
	    return 4;
	else
	    return 5;
    }

    /*
     * @Assumes Treble cleft
     * 
     * @param interval An integer representing the number of 'steps' down the
     * staff lines starting from the topmost line (F in treble)
     * 
     * @return The letter representing the note [A-G]
     * 
     * TODO No support for sharps/flats
     */

    private char getNoteLetterFromInterval(Integer interval) {
	int numNoteNames = 7;
	char letter = (char) ('A' + (('F' - 'A') - interval + numNoteNames) % numNoteNames);
	return letter;
    }

    /*
     * @Assumes Treble cleft
     * 
     * @param note a note object whose position on the staff you are interested
     * in
     * 
     * @return The number of 'steps' down from the top of the staff this note
     * should be written ( 0 for F, 1 for E, etc...)
     */
    private int getNoteCoordinate(Note note) {
	int thisOctave = 5;
	int staffStepsAboveA5 = note.note - 'A' + 7 * (note.octave - thisOctave);
	int staffSteps = 5 - staffStepsAboveA5;
	Log.i("APPDATA", "Coord: " + staffSteps);
	return staffSteps;
    }
}
