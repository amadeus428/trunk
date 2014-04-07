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

	int width = parent.getNoteWidth();
	int height = parent.getNoteHeight();

	if (note.isSharp) {
	    int sharpHeight = parent.getSharpHeight();
	    int noteStartX = (int) (width * 1.1f);
	    int noteStartY = (int) (sharpHeight * .33f);
	    sharpTransformation = new Rect(0, 0, width, sharpHeight);
	    transformation = new Rect(noteStartX, noteStartY, noteStartX + width, noteStartY + height);
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
	    canvas.drawBitmap(StaffLayout.sharpBitmap, null, sharpTransformation, null);
	}
	canvas.drawBitmap(bitmap, null, transformation, null);

	super.onDraw(canvas);
    }

    public Note getNote() {
	return note;
    }
}
