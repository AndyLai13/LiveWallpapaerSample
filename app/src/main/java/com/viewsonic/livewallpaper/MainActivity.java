package com.viewsonic.livewallpaper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

	MyGLSurfaceView glSurfaceView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		glSurfaceView = new MyGLSurfaceView(this);
		setContentView(glSurfaceView);

		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

		Log.d("Andy", "" + Double.parseDouble(configurationInfo.getGlEsVersion()));
		Log.d("Andy", "" + (configurationInfo.reqGlEsVersion >= 0x20000));
		Log.d("Andy", "" + String.format("%X", configurationInfo.reqGlEsVersion));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	class MyGLSurfaceView extends GLSurfaceView {
		private Context mContext;

		public MyGLSurfaceView(Context context) {
			super(context);
			mContext = context;
			// Create an OpenGL ES 2.0 context
			setEGLContextClientVersion(2);
			// Set the Renderer for drawing on the GLSurfaceView
			setRenderer(new MyGLRenderer());
		}

		class MyGLRenderer implements GLSurfaceView.Renderer {
			private GL.Triangle mTriangle;
			private GL.Square mSquare;

			@Override
			public void onSurfaceCreated(GL10 unused, EGLConfig config) {
				// Set the background frame color
				GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
				// 初始化triangle
				mTriangle = new GL.Triangle();
				// 初始化 square
				mSquare = new GL.Square();
			}

			private float[] mRotationMatrix = new float[16];

			@Override
			public void onDrawFrame(GL10 unused) {
				float[] scratch = new float[16];

				// Set the camera position (View matrix)
				Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

				// Calculate the projection and view transformation
				Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

//				// Draw shape
				mTriangle.draw(mMVPMatrix);


//				// 创建一个旋转矩阵
//				long time = SystemClock.uptimeMillis() % 4000L;
//				float angle = 0.090f * ((int) time);
//				Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
//
//				// 将旋转矩阵与投影和相机视图组合在一起
//				// Note that the mMVPMatrix factor *must be first* in order
//				// for the matrix multiplication product to be correct.
//				Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
//
//				// Draw triangle
				mTriangle.draw(scratch);
			}

			private final float[] mMVPMatrix = new float[16];
			private final float[] mProjectionMatrix = new float[16];
			private final float[] mViewMatrix = new float[16];

			@Override
			public void onSurfaceChanged(GL10 unused, int width, int height) {
				GLES20.glViewport(0, 0, width, height);

				float ratio = (float) width / height;

				// 这个投影矩阵被应用于对象坐标在onDrawFrame（）方法中
				Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
			}
		}
	}
}

