package com.cs429.amadeus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class StaffView extends View {

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

	public StaffView(Context context) {
		super(context);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(10);
		updateMeasurements();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(!isMeasured) updateMeasurements();
		for (int i = 1; i <= 5; i++) {
			canvas.drawLine(leftEdge, i*margin, width, i*margin, paint);
		}
		Log.i("APP-DATA", "Width: "+width+", Height: "+height);
	}

	boolean isMeasured = false;
	private void updateMeasurements() {
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

}
