package com.cs429.amadeus.views;

import java.util.Calendar;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FallingNotesView extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final float NOTE_COOLDOWN = 500;
	private static final int NOTE_SPEED = 4;

	private long lastNoteTime = 0;
	private int noteWidth;
	private int noteHeight;
	private int screenWidth;
	private int screenHeight;
	private LinkedList<Bitmap> bitmaps = new LinkedList<Bitmap>();
	private LinkedList<Rect> transformations = new LinkedList<Rect>();
	private LinkedList<Paint> paints = new LinkedList<Paint>();
	private MainThread thread;

	public FallingNotesView(Activity activity) {
		super(activity);

		thread = new MainThread(this);
		getHolder().addCallback(this);

		StaffLayout.initBitmaps(activity.getResources());

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;

		Bitmap testBitmap = StaffLayout.quarterNoteBitmap;
		float bitmapWidth = testBitmap.getWidth();
		float bitmapHeight = testBitmap.getHeight();
		float whRatio = bitmapWidth / bitmapHeight;
		noteHeight = screenHeight / 10;
		noteWidth = (int) (whRatio * noteHeight);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (thread == null) {
			thread = new MainThread(this);
		}

		thread.isRunning = true;
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		if (thread != null) {
			thread.isRunning = false;
			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		thread = null;
	}

	public void update() {
		moveBitmaps();

		long currTime = Calendar.getInstance().getTimeInMillis();
		if (currTime - lastNoteTime > NOTE_COOLDOWN) {
			lastNoteTime = currTime;
			bitmaps.add(getRandBitmap());
			transformations.add(getRandTransformation());
			paints.add(new Paint());
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (canvas == null) {
			return;
		}

		canvas.drawColor(Color.WHITE);

		for (int i = 0; i < bitmaps.size(); i++) {
			Bitmap bitmap = bitmaps.get(i);
			Rect transformation = transformations.get(i);
			Paint paint = paints.get(i);
			canvas.drawBitmap(bitmap, null, transformation, paint);
		}
	}

	private Bitmap getRandBitmap() {
		int choice = (int) (Math.random() * 4);
		switch (choice) {
		case 0:
			return StaffLayout.sixteenthNoteBitmap;
		case 1:
			return StaffLayout.eighthNoteBitmap;
		case 2:
			return StaffLayout.quarterNoteBitmap;
		case 3:
			return StaffLayout.halfNoteBitmap;
		}

		return null;
	}

	private Rect getRandTransformation() {
		int left = (int) (Math.random() * (screenWidth - noteWidth));
		int right = left + noteWidth;
		int top = -noteHeight - 10;
		int bottom = top + noteHeight;
		return new Rect(left, top, right, bottom);
	}

	private void moveBitmaps() {
		int dAlpha = 255 / ((getHeight() - (int) (noteHeight * 1.5)) / NOTE_SPEED);
		for (int i = 0; i < transformations.size(); i++) {
			Paint paint = paints.get(i);
			paint.setAlpha(paint.getAlpha() - dAlpha);

			Rect transformation = transformations.get(i);
			transformation.offset(0, NOTE_SPEED);

			if (transformation.top > getHeight()) {
				bitmaps.remove(i);
				transformations.remove(i);
				paints.remove(i);
				i--;
			}
		}
	}

	public class MainThread extends Thread {
		public boolean isRunning = false;
		private FallingNotesView view;
		private SurfaceHolder surfaceHolder;

		public MainThread(FallingNotesView view) {
			this.view = view;
			surfaceHolder = view.getHolder();
		}

		@SuppressLint("WrongCall")
		@Override
		public void run() {
			Canvas canvas;
			while (isRunning) {
				canvas = null;
				try {
					canvas = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder) {
						try {
							view.update();
							view.onDraw(canvas);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
	}
}
