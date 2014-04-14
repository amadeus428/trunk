package com.cs429.amadeus.test;

import junit.framework.TestCase;
import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.views.NoteView;
import com.cs429.amadeus.views.StaffLayout;

public class NoteViewTest extends ActivityInstrumentationTestCase2<MainActivity>  {
	public MainActivity mActivity;
	
	StaffLayout mStaff;
	
	public NoteViewTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);

		mActivity = getActivity();
		
	}

	public void testGreenBitmap(){
		mStaff = new StaffLayout(mActivity);
		
		Note note = new Note('A', 5, false, Note.QUARTER_NOTE);
		
		NoteView noteView = new NoteView(mActivity, mStaff, note); 
		
		Bitmap [] greenBitmap = NoteView.getGreenBitmaps();
		Bitmap noteBitmap = noteView.getBitmap();
		
		for(int i = 0; i< noteBitmap.getWidth(); i++){
			for(int j = 0; j< noteBitmap.getHeight(); j++){
				if(noteBitmap.getPixel(i, j) == 0x0){
					continue;
				}
				assertTrue((noteBitmap.getPixel(i, j) & (511 << 8)) < (greenBitmap[note.type].getPixel(i, j) & (511 << 8)));
			}
		}
	}
	public void testRedBitmap(){
		mStaff = new StaffLayout(mActivity);
		
		Note note = new Note('A', 5, false, Note.QUARTER_NOTE);
		
		NoteView noteView = new NoteView(mActivity, mStaff, note); 
		
		Bitmap [] redBitmap = NoteView.getRedBitmaps();
		Bitmap noteBitmap = noteView.getBitmap();
		
		for(int i = 0; i< noteBitmap.getWidth(); i++){
			for(int j = 0; j< noteBitmap.getHeight(); j++){
				if(noteBitmap.getPixel(i, j) == 0x0){
					continue;
				}
				assertTrue((noteBitmap.getPixel(i, j) & (511 << 16)) < (redBitmap[note.type].getPixel(i, j) & (511 << 16)));
			}
		}
	}
}
