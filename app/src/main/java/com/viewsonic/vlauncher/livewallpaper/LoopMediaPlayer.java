package com.viewsonic.vlauncher.livewallpaper;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;

public class LoopMediaPlayer {
	public static final String TAG = LoopMediaPlayer.class.getSimpleName();

	private Context mContext = null;
	private int mResId = 0;
	private int mCounter = 1;

	private MediaPlayer mCurrentPlayer;
	private MediaPlayer mNextPlayer;
	private SurfaceHolder mHolder;
	final AssetFileDescriptor afd;


	public static LoopMediaPlayer create(Context context, int resId, SurfaceHolder holder) {
		return new LoopMediaPlayer(context, resId, holder);
	}

	private LoopMediaPlayer(Context context, int resId, final SurfaceHolder holder) {
		mContext = context;
		mResId = resId;
		mHolder = holder;
		afd = mContext.getResources().openRawResourceFd(R.raw.education_animation);

		mCurrentPlayer = MediaPlayer.create(mContext, mResId);
//		mNextPlayer = MediaPlayer.create(mContext, mResId);
		mCurrentPlayer.setSurface(mHolder.getSurface());
//		mNextPlayer.setSurface(mHolder.getSurface());
		try {
			mCurrentPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			mCurrentPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		mCurrentPlayer.setNextMediaPlayer(mNextPlayer);
		mCurrentPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
//				mediaPlayer.reset();
				try {
//					mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//					mNextPlayer.setSurface(mHolder.getSurface());
//					mediaPlayer.prepare();
					mediaPlayer.seekTo(0);
					mediaPlayer.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
//				mNextPlayer.setNextMediaPlayer(mCurrentPlayer);

			}
		});

		mCurrentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				mediaPlayer.start();
			}
		});


//		mNextPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//			@Override
//			public void onCompletion(MediaPlayer mediaPlayer) {
//				mediaPlayer.reset();
//
//				try {
//					mCurrentPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
//					mCurrentPlayer.setSurface(mHolder.getSurface());
//					mCurrentPlayer.prepare();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				mCurrentPlayer.setNextMediaPlayer(mNextPlayer);
//			}
//		});
//
//		mNextPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//			@Override
//			public void onPrepared(MediaPlayer mediaPlayer) {
//				mediaPlayer.start();
//			}
//		});


	}

//	private void createNextMediaPlayer() {
//		Log.e(TAG, "createNextMediaPlayer");
//
//		mNextPlayer.setSurface(mHolder.getSurface());
//
//		mCurrentPlayer.setNextMediaPlayer(mNextPlayer);
//		mCurrentPlayer.setOnCompletionListener(onCompletionListener);
//		mCurrentPlayer.start();
//	}

//	private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
//		@Override
//		public void onCompletion(MediaPlayer mediaPlayer) {
//			Log.e(TAG, "onCompletion");
//			mediaPlayer.release();
//			mCurrentPlayer = mNextPlayer;
//			createNextMediaPlayer();
//			Log.d(TAG, String.format("Loop #%d", ++mCounter));
//		}
//	};
}
