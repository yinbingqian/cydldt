package lnpdit.stategrid.informatization.tools;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SimpleZoomListener implements View.OnTouchListener {

	public enum ControlType {
		PAN, ZOOM
	}

	private ControlType mControlType = ControlType.PAN;

	private ZoomState mState;

	private float mX;
	private float mY;
	private float mGap;

	public void setZoomState(ZoomState state) {
		mState = state;
	}

	public void setControlType(ControlType controlType) {
		mControlType = controlType;
	}

	public boolean onTouch(View v, MotionEvent event) {
		final int action = event.getAction();
		int pointCount = event.getPointerCount();
		if (pointCount == 1) {
			final float x = event.getX();
			final float y = event.getY();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mX = x;
				mY = y;
				break;
			case MotionEvent.ACTION_MOVE: {
				final float dx = (x - mX) / v.getWidth();
				final float dy = (y - mY) / v.getHeight();
//				Log.d("dx = ", "" + dx);
//				Log.d("dy = ", "" + dy);
//
//				Log.d("x = ", "" + x);
//				Log.d("y = ", "" + y);
//				Log.d("mState.getPanX() = ", "" + mState.getPanX());
//				Log.d("mState.getPanY() = ", "" + mState.getPanY());
//				
//				Log.d("final x = ", "" + (mState.getPanX() - dx));
//				Log.d("final y = ", "" + (mState.getPanY() - dy));
				float fx = mState.getPanX() - dx;
				float fy = mState.getPanY() - dy;
				
				try {					
					final float x0 = event.getX(event.getPointerId(0));
					final float y0 = event.getY(event.getPointerId(0));
					
					final float gap = getGap(x0, x0, y0, y0);
					float zoom = mState.getZoom() * gap / mGap;
					Log.d("zoom = ", ""+zoom);
					if (zoom > 1.001) {
						if(fx >= 0&& fx <= 1 && fy >= 0 && fy <= 1){
							mState.setPanX(fx);
							mState.setPanY(fy);
							
							mState.notifyObservers();
							mX = x;
							mY = y;
						}
					}
					mGap = gap;
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				break;
			}
			}
		}
		if (pointCount == 2) {
			final float x0 = event.getX(event.getPointerId(0));
			final float y0 = event.getY(event.getPointerId(0));

			final float x1 = event.getX(event.getPointerId(1));
			final float y1 = event.getY(event.getPointerId(1));

			final float gap = getGap(x0, x1, y0, y1);
			switch (action) {
			case MotionEvent.ACTION_POINTER_2_DOWN:
			case MotionEvent.ACTION_POINTER_1_DOWN:
				mGap = gap;
				break;
			case MotionEvent.ACTION_POINTER_1_UP:
				mX = x1;
				mY = y1;
				break;
			case MotionEvent.ACTION_POINTER_2_UP:
				mX = x0;
				mY = y0;
				break;
			case MotionEvent.ACTION_MOVE: {
				final float dgap = (gap - mGap) / mGap;
				float zoom = mState.getZoom() * gap / mGap;
				if (zoom < 4.001 && zoom > 1.001) {
					mState.setZoom(mState.getZoom() * (float) Math.pow(5, dgap));
				}
				mState.notifyObservers();
				mGap = gap;
				break;
			}
			}
		}

		return true;
	}

	private float getGap(float x0, float x1, float y0, float y1) {
		return (float) Math.pow(
				Math.pow((x0 - x1), 2) + Math.pow((y0 - y1), 2), 0.5);
	}

}
