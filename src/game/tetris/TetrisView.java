package game.tetris;

import android.content.Context;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TetrisView extends SurfaceView implements SurfaceHolder.Callback {
	private TetrisThread thread;
	private boolean mSavedGameFound;
	private String mGameString;

	/***
	 * 
	 * @param context
	 * @param gameString
	 * @param foundGame
	 */
	public TetrisView(Context context, String gameString, boolean foundGame ) {
		super(context);
		mGameString = gameString;
		mSavedGameFound = foundGame;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		if(mSavedGameFound)
			thread = new TetrisThread(holder, context, mGameString);
		else
			thread = new TetrisThread(holder, context);
		setFocusable(true);
		setFocusableInTouchMode(true); // Fixed: Now registers the first input. In reponse to "No keyboard for id 0."

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return thread.doKeyDown(keyCode, event);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return thread.onTouchEvent(event);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setCanvasSize(width, height);
	}


	public void surfaceCreated(SurfaceHolder holder) {
		thread.setMode(TetrisThread.RUNNING);

		if (!thread.getRunning()) {
			thread.initGame();
			thread.setRunning(true);
			thread.start();
		}
	}

	/***
	 * Returns TetrisThread class
	 * @return TetrisThread
	 */
	public TetrisThread getThread() {
		return thread;
	}
	
	public void killThread(){
		boolean done = false;

		thread.setRunning(false);

		while (!done) {
			try {
				thread.join();
				done = true;
			} catch (InterruptedException e) {
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setMode(TetrisThread.PAUSED);
	}
}
