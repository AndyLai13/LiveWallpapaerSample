package com.viewsonic.vlauncher.livewallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class VideoWallPaperService extends WallpaperService {
	private static final String SERVICE_NAME = "com.example.videowallpaper.VideoWallPaperService";
	@Override
	public Engine onCreateEngine() {
		return new VideoEngine();
	}

	private class VideoEngine extends WallpaperService.Engine {
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
				switch(action) {
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
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {

		}

		public void rePlayPlayer(SurfaceHolder holder){
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
			LoopMediaPlayer.create(VideoWallPaperService.this, R.raw.education_animation, holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			Log.e("LoopMediaPlayer", "onSurfaceDestroyed");
//			if (mMPCurrPlayer.isPlaying()) {
//				mMPCurrPlayer.stop();
//			}
//			mMPCurrPlayer.release();
//			mMPCurrPlayer  = null;
		}
	}
}