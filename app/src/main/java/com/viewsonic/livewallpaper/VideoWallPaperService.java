package com.viewsonic.livewallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoWallPaperService extends WallpaperService {
	private static final String SERVICE_NAME = "com.example.videowallpaper.VideoWallPaperService";

	@Override
	public Engine onCreateEngine() {
		return new VideoEngine();
	}

	public class VideoEngine extends WallpaperService.Engine {
		private MediaPlayer player1;
		private MediaPlayer player2;
		private boolean mLoop;
		private boolean mVolume;
		private boolean isPapered = false;
		private int mCompletedLoops = 0;
		private int mNumLoops = 1000000;
		final AssetFileDescriptor afd = VideoWallPaperService.this.getResources().openRawResourceFd(R.raw.education_animation);


		private VideoEngine() {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(VideoWallPaperService.this);
			mLoop = preferences.getBoolean("loop", true);
			mVolume = preferences.getBoolean("volume", false);
		}

		private BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int action = intent.getIntExtra(Constant.BROADCAST_SET_VIDEO_PARAM, -1);
				switch (action) {
					case Constant.ACTION_SET_VIDEO: {
						Log.e("Pan ", "ACTION_SET_VIDEO ....");
						break;
					}
					case Constant.ACTION_VOICE_NORMAL: {
						mVolume = true;
						break;
					}
					case Constant.ACTION_VOICE_SILENCE: {
						mVolume = false;
						break;
					}
				}
			}
		};

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			glSurfaceView = new MyGLSurfaceView(VideoWallPaperService.this);
			Log.d("Andy", "glSurfaceView");

		}

		@Override
		public void onDestroy() {
			super.onDestroy();
		}


		private MyGLSurfaceView glSurfaceView;

		@Override
		public void onVisibilityChanged(boolean visible) {
			if (visible) {
				glSurfaceView.onResume();
			} else {
				glSurfaceView.onPause();
			}
		}

		public void rePlayPlayer(SurfaceHolder holder) {
			player1 = MediaPlayer.create(VideoWallPaperService.this, R.raw.education_animation);
			player1.setSurface(getSurfaceHolder().getSurface());
			player1.start();

			player2 = MediaPlayer.create(VideoWallPaperService.this, R.raw.education_animation);
			player2.setSurface(getSurfaceHolder().getSurface());
			//player1.setNextMediaPlayer(player2);


			player1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.reset();
					try {
						mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
						mp.prepareAsync();
					} catch (Exception e) {
						e.printStackTrace();
					}
					//player2.setNextMediaPlayer(player1);
				}
			});

			player2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.reset();
					try {
						mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
						mp.prepare();
					} catch (Exception e) {
						e.printStackTrace();
					}

					//player1.setNextMediaPlayer(player2);
				}
			});

		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			Log.e("LoopMediaPlayer", "onSurfaceCreated");
//			LoopMediaPlayer.create(VideoWallPaperService.this, R.raw.education_animation, holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
//			Log.e("LoopMediaPlayer", "onSurfaceDestroyed");
//			if (mMPCurrPlayer.isPlaying()) {
//				mMPCurrPlayer.stop();
//			}
//			mMPCurrPlayer.release();
//			mMPCurrPlayer  = null;
		}

		class MyGLSurfaceView extends GLSurfaceView {
			private final MyGLRenderer mRenderer;
			private Context mContext;

			public MyGLSurfaceView(Context context) {
				super(context);
				mContext = context;
				// Create an OpenGL ES 2.0 context
				setEGLContextClientVersion(2);

				mRenderer = new MyGLRenderer();

				// Set the Renderer for drawing on the GLSurfaceView
				setRenderer(mRenderer);
			}

			@Override
			public SurfaceHolder getHolder() {
				return getSurfaceHolder();
			}

			class MyGLRenderer implements GLSurfaceView.Renderer {
				private GL.Triangle mTriangle;
				private GL.Square mSquare;

				@Override
				public void onSurfaceCreated(GL10 unused, EGLConfig config) {
					// Set the background frame color
					GLES20.glClearColor(0.5F, 0.5F, 0.5F, 0.5F);
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

					// Draw shape
					mTriangle.draw(mMVPMatrix);


					// 创建一个旋转矩阵
					long time = SystemClock.uptimeMillis() % 4000L;
					float angle = 0.090f * ((int) time);
					Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);

					// 将旋转矩阵与投影和相机视图组合在一起
					// Note that the mMVPMatrix factor *must be first* in order
					// for the matrix multiplication product to be correct.
					Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

					// Draw triangle
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

	public static int loadShader(int type, String shaderCode) {

		// 创造顶点着色器类型(GLES20.GL_VERTEX_SHADER)
		// 或者是片段着色器类型 (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);
		// 添加上面编写的着色器代码并编译它
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		return shader;
	}

}