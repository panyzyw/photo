package com.yongyida.robot.photos.utils;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;

/**
 * 媒体播放类.
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("unused")
public class MediaPlayBiz {
	private MediaPlayer mPlayer;

	private static MediaPlayBiz mMediaPlay;

	public MediaPlayBiz() {
		if (mPlayer == null) {
			mPlayer = new MediaPlayer();

			mPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mPlayer.start();

				}
			});
		}
	}

	/**
	 * 播放音乐.
	 * 
	 * @param url
	 */
	public void playMusic(String url) {
		if (mPlayer != null) {

			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.reset();
			try {
				mPlayer.setDataSource(url);
				mPlayer.prepare();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();

			} catch (SecurityException e) {
				e.printStackTrace();

			} catch (IllegalStateException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();

			}
			mPlayer.start();
		}
	}

	public void playMusic(Context context, String path,
			final CompleteListener listener) {
		try {
			if (mPlayer != null) {
				AssetFileDescriptor fileDescriptor = context.getResources()
						.getAssets().openFd(path);
				mPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						listener.complete();
					}
				});
				if (mPlayer.isPlaying()) {
					mPlayer.stop();
				}
				mPlayer.reset();
				mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				mPlayer.prepareAsync();
				// mPlayer.start();
			}
		} catch (Throwable e) {

		}
	}

	public void playMusic(Context context, String path) {
		try {
			if (mPlayer != null) {
				AssetFileDescriptor fileDescriptor = context.getResources()
						.getAssets().openFd(path);
				if (mPlayer.isPlaying()) {
					mPlayer.stop();
				}
				mPlayer.reset();
				mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				mPlayer.prepareAsync();
				// mPlayer.start();
			}
		} catch (Throwable e) {

		}
	}

	/**
	 * 播放音乐.
	 * 
	 * @param url
	 */
	public void playMusic(int id) {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.reset();
			try {
				mPlayer.setAudioSessionId(id);
				mPlayer.prepare();
				mPlayer.start();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();

			} catch (SecurityException e) {
				e.printStackTrace();

			} catch (IllegalStateException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();

			}

		}
	}

	/**
	 * 播放音乐.
	 * 
	 * @param url
	 */
	public void playMusic(FileDescriptor fd) {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.reset();
			try {
				mPlayer.setDataSource(fd);
				mPlayer.prepare();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();

			} catch (SecurityException e) {
				e.printStackTrace();

			} catch (IllegalStateException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();

			}
			mPlayer.start();
		}
	}

	/**
	 * 暂停.
	 */
	public void pauseMusic() {
		if (mPlayer != null) {
			mPlayer.pause();
		}
	}

	/**
	 * 暂停.
	 */
	public void stopMusic() {
		if (mPlayer != null) {
			mPlayer.stop();
		}
	}

	/**
	 * 是否在播放.
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			return true;
		}
		return false;
	}

	/**
	 * 获取实例.
	 * 
	 * @param view
	 * @return
	 */
	public static MediaPlayBiz getInstance() {
		if (mMediaPlay == null) {
			mMediaPlay = new MediaPlayBiz();
		}
		return mMediaPlay;
	}

	public interface CompleteListener {
		public void complete();
	}

}
