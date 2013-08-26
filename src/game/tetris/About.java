package game.tetris;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class About extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
	}

	
	/***
	 * Restart music
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Music.play(this, R.raw.tatrisa);
	}

	/***
	 * Pause music
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Music.stop(this);
	}
}