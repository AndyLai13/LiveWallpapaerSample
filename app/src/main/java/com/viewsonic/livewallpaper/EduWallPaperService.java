package com.viewsonic.livewallpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class EduWallPaperService extends WallpaperService {

	private final byte[] mLock = new byte[0];

	@Override
	public Engine onCreateEngine() {
		return new EduEngine();
	}

	@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess", "unused"})
	private class EduEngine extends Engine {
		private Paint paint = new Paint();
		private int mWidth;
		private int mHeight;
		private boolean mVisible = true;
		private	Bitmap mBitmapBG;


		private HandlerThread thread = new HandlerThread("WallPaperService");
		private Handler handler;
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};

		private float mDegree;
		// Used for rotation at right top of screen with a rounded rectangle
		private float mDegree1;
		// Used for rotation at left bottom of screen with a circle
		private float mDegree2;

		// Config for rounded rectangle
		private int[] mRotationPivot1 = {1800, -500};
		private int mCornerRadius = 408;
		private int mSideLength = 2044;
		private int mHalfSideLength = mSideLength /2;
		// Config for circle
		private int[] mRotationPivot2 = {450, 1560};
		private int[] mOffset = {-50, 60};
		private int mRadius = 1091;

		public EduEngine() {
			mBitmapBG = BitmapFactory.decodeResource(getResources(), R.mipmap.gra_bg_gradual);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (mVisible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			handler.removeCallbacks(drawRunner);
			thread.quit();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			thread.start();
			handler = new Handler(thread.getLooper());
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			mWidth = width;
			mHeight = height;
			super.onSurfaceChanged(holder, format, width, height);
		}

		private void draw() {
			synchronized (mLock) {
				calculateRotationDegree();
				SurfaceHolder holder = getSurfaceHolder();
				Canvas canvas = null;
				paint.setColor(Color.WHITE);
				paint.setAntiAlias(true);
				paint.setFilterBitmap(true);
				paint.setAlpha(30);

				try {
					canvas = holder.lockCanvas();
					PaintFlagsDrawFilter filter = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
					canvas.setDrawFilter(filter);
					// clear canvas
					canvas.drawColor(0, PorterDuff.Mode.CLEAR);
					// draw original mBitmapBG
					canvas.drawBitmap(mBitmapBG, 0, 0, null);
					// draw right top rounded rectangle
					canvas.save();
					canvas.rotate(mDegree1, mRotationPivot1[0], mRotationPivot1[1]);
					canvas.drawRoundRect(
							mRotationPivot1[0] - mHalfSideLength,
							mRotationPivot1[1] - mHalfSideLength,
							mRotationPivot1[0] + mHalfSideLength,
							mRotationPivot1[1] + mHalfSideLength,
							mCornerRadius, mCornerRadius, paint);
					canvas.restore();
					// draw left bottom circle
					canvas.save();
					canvas.rotate(mDegree2, mRotationPivot2[0], mRotationPivot2[1]);
					canvas.drawCircle(mRotationPivot2[0]+ mOffset[0], mRotationPivot2[1] + mOffset[1], mRadius, paint);
					canvas.restore();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (canvas != null)
						holder.unlockCanvasAndPost(canvas);
				}

				handler.removeCallbacks(drawRunner);
				handler.postDelayed(drawRunner, 30);
			}
		}

		private void calculateRotationDegree() {
			mDegree = (mDegree + 1) % 360;
			mDegree1 = mDegree * 0.1f;
			mDegree2 = mDegree;
		}
	}
}