package com.cs429.amadeus.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.views.NoteView.Highlight;

/**
 * This class represents an interactive note used in combination with
 * {@link StaffLayout}.
 */
public class NoteView extends View {
    private StaffLayout parent;
    private Note note;
    private Bitmap bitmap;
    private Rect transformation;
    private Rect sharpTransformation;

    public NoteView(Context context, StaffLayout parent, Note note) {
	super(context);

	this.parent = parent;
	this.note = note;
	this.bitmap = StaffLayout.getBitmap(note.type);

	makeHighlightBitmaps(bitmap);

	int width = parent.getNoteWidth();
	int height = parent.getNoteHeight();

	if (note.isSharp) {
	    int sharpHeight = parent.getSharpHeight();
	    int noteStartX = (int) (width * 1.1f);
	    int noteStartY = (int) (sharpHeight * .33f);
	    sharpTransformation = new Rect(0, 0, width, sharpHeight);
	    transformation = new Rect(noteStartX, noteStartY, noteStartX
		    + width, noteStartY + height);
	} else {
	    transformation = new Rect(0, 0, width, height);
	}

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	// If the parent is disabled, also disable touch events on this note.
	if (!parent.isEnabled()) {
	    return true;
	}

	if (event.getAction() == MotionEvent.ACTION_UP) {
	    parent.removeView(NoteView.this);
	}

	return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
	if (note.isSharp) {
	    canvas.drawBitmap(StaffLayout.sharpBitmap, null,
		    sharpTransformation, null);
	}

	// If this note should be highlighted for feedback to the user for
	// play-along
	if (this.highlight == Highlight.Green)
	    canvas.drawBitmap(greenBitmap[this.note.type], null,
		    transformation, null);
	else if (this.highlight == Highlight.Red)
	    canvas.drawBitmap(redBitmap[this.note.type], null, transformation,
		    null);
	else
	    canvas.drawBitmap(bitmap, null, transformation, null);

	super.onDraw(canvas);
    }

    public Note getNote() {
	return note;
    }

    /*
     * This method is used to generate green and red bitmaps for notes
     * 
     * It is called every time a note object is made, but it only makes bitmaps
     * if colored bitmaps have note already been made for this note type
     * (As-needed generation)
     */
    private void makeHighlightBitmaps(Bitmap bitmap) {

	if (greenBitmap[this.note.type] != null) { // A colored bitmap has
						   // already been generated
						   // for this note type
	    return;
	}

	greenBitmap[this.note.type] = Bitmap.createBitmap(bitmap.getWidth(),
		bitmap.getHeight(), bitmap.getConfig());
	redBitmap[this.note.type] = Bitmap.createBitmap(bitmap.getWidth(),
		bitmap.getHeight(), bitmap.getConfig());

	for (int i = 0; i < bitmap.getWidth(); i++) {
	    for (int j = 0; j < bitmap.getHeight(); j++) {
		int pixel = bitmap.getPixel(i, j);
		redBitmap[this.note.type].setPixel(i, j, pixel | (511 << 16));
		greenBitmap[this.note.type].setPixel(i, j, pixel | (511 << 8));
	    }
	}
    }

    static private Bitmap[] greenBitmap = new Bitmap[5];
    static private Bitmap[] redBitmap = new Bitmap[5];

    public void highlight(Highlight highlight) {
	this.highlight = highlight;
    }

    public static Bitmap[] getGreenBitmaps() {
	return greenBitmap;
    }

    public static Bitmap[] getRedBitmaps() {
	return redBitmap;
    }

    public Bitmap getBitmap() {
	return bitmap;
    }

    protected enum Highlight {
	Red, Green, None
    }

    private Highlight highlight = Highlight.None;

}
