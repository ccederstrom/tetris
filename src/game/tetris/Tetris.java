package game.tetris;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

public class Tetris extends Activity {
	private static final int MENU_RESUME = 1;
	private static final int MENU_NEWGAME = 2;
	private static final int MENU_SAVE = 3;
	private static final int MENU_OPTIONS = 4;
	private static final int MENU_EXIT = 5;
	
	//variables for saved game
	private static final int SAVED_GAME = 1;	  //Denotes a saved game by user request.
	private static final int TEMP_SAVED_GAME = 2; //Denotes a saved game by rotation.

	/** The drawable to use as the background of the animation canvas */
	// private Bitmap mBackgroundImage;
	private TetrisView mContentView;

	// filename for savegamesettigns
	public static final String PREFS_NAME = "TetrisGameSettings";
	private String mGameSavedString;
	private boolean mFoundSavedGame;
	private boolean  mMenuClosedWithoutSelectingResume;

	/** Called when the activity is first created. */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// turn off the window's title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// tell system to use the layout defined in our XML file
		mGameSavedString = checkForSavedGame(TEMP_SAVED_GAME);
		if(mFoundSavedGame == true)
			mContentView = new TetrisView(this, mGameSavedString, mFoundSavedGame);
		else
		{
			mGameSavedString = checkForSavedGame(SAVED_GAME);
			mContentView = new TetrisView(this, mGameSavedString, mFoundSavedGame);
			
		}

		mContentView.getThread().setTouchDelay(Settings.getTouchSpeed(this));
		mContentView.getThread().setTouchType(Settings.getTouchType(this));
		mContentView.getThread().setSoundEffect(Settings.getSoundEffect(this));		
		setOrientation(Settings.getOrientation(this));
		setContentView(mContentView);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		//mContentView.getThread().
		
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * Restart music
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Music.play(this, R.raw.tatrisa);
		mContentView.getThread().setRotation(Settings.isClockwise(this));
		mContentView.getThread().setPrefilledRows(Settings.getPrefilledRows(this));
		mContentView.getThread().setRowDensity(Settings.getRowDensity(this));
		mContentView.getThread().setTouchDelay(Settings.getTouchSpeed(this));
		mContentView.getThread().setTouchType(Settings.getTouchType(this));
		mContentView.getThread().setSoundEffect(Settings.getSoundEffect(this));
		setOrientation(Settings.getOrientation(this));
	}

	/**
	 * Pause music
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mContentView.getThread().setMode(TetrisThread.PAUSED);
		Music.stop(this);
	}


	/**
	 * onPrepareOptionsMenu is the in-game menu it is called EVERYTIME the user presses the MENU button
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mContentView.getThread().setMode(TetrisThread.PAUSED);
		mContentView.getThread().showPaused.sendEmptyMessage(0); 
		mMenuClosedWithoutSelectingResume = true;
		Music.stop(this);
		return true;
	}

	/**
	 *  handle back button pressed during game
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		mContentView.getThread().setMode(TetrisThread.PAUSED);

		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_ENDCALL) {
			displaySaveGameDialog();
			return false;
		}
				
		return super.onKeyDown(keyCode, event);

	}
	public void displaySaveGameDialog()
	{
		new AlertDialog.Builder(this).setIcon(
				android.R.drawable.ic_dialog_alert).setTitle(
				R.string.in_game_back_title).setMessage(
				R.string.in_game_back_msg).setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// user wants to save
						saveGameSettings(SAVED_GAME);
						finish();
					}
				}).setNegativeButton(R.string.in_game_back_no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// user wants to not save
						finish();

					}
				}).show();
	}
	/***
	 * onCreateOptionsMenu method is called the FIRST time the user creates the MENU in the game
	 */ 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		
		menu.add(0, MENU_RESUME, 0, R.string.in_game_resume).setIcon(
				android.R.drawable.ic_media_play);
		menu.add(0, MENU_NEWGAME, 0, R.string.in_game_newgame).setIcon(
				android.R.drawable.star_big_off);

		menu.add(0, MENU_SAVE, 0, R.string.in_game_save).setIcon(
				android.R.drawable.ic_menu_save);
		menu.add(0, MENU_OPTIONS, 0, R.string.in_game_options).setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_EXIT, 0, R.string.in_game_exit).setIcon(
				android.R.drawable.ic_lock_power_off);

		return true;
	}
	/***
	 * onOptionsItemSelected is called when the user selects an item on the MENU
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_RESUME:
			mMenuClosedWithoutSelectingResume = false;
			mContentView.getThread().setMode(TetrisThread.RUNNING);
			Music.play(this, R.raw.tatrisa);
			return true;
		case MENU_NEWGAME:
			mMenuClosedWithoutSelectingResume = false;
			Music.play(this, R.raw.tatrisa);
			startNewGame();
			return true;
		case MENU_SAVE:
			saveGameSettings(SAVED_GAME);
			return true;
		case MENU_OPTIONS:
			Intent opt = new Intent(this, Settings.class);
			startActivity(opt);
			return true;
		case MENU_EXIT:
			mMenuClosedWithoutSelectingResume = false;
			displaySaveGameDialog();
			return true;

		}

		return false;
	}
	
	@Override
	public void onOptionsMenuClosed (Menu menu) {
		if( mMenuClosedWithoutSelectingResume == true)
			mContentView.getThread().showPaused.sendEmptyMessage(0); 

			return;
	}

	/***
	 * Start a new game. It is called when the user click new game button. 
	 * It will do:
	 * 		+ Kill the thread to avoid problems
	 * 		+ Destroy the drawing cache, free the resources.
	 * 		+ Create new Tetris View
	 * 		+ Adjust the touch screen delay
	 * 		+ show the View.
	 */
	private void startNewGame() {
		mContentView.killThread(); 
		mContentView.destroyDrawingCache();

		mContentView = new TetrisView(this, mGameSavedString, false);
		mContentView.getThread().setTouchDelay(Settings.getTouchSpeed(this));
		mContentView.getThread().setTouchType(Settings.getTouchType(this));
		setOrientation(Settings.getOrientation(this));
		setContentView(mContentView);

	}

	/***
	 * Save Game. It is called when the user selects Save Game on the MENU
	 * 		+ Call getSaveGameString() to get the save game string, which contains all details of the game
	 * 		+ Show the "Saving Game", "Game Saved".. dialogs.
	 * 	
	 */
	private void saveGameSettings(int gameStringType) {
		
		String tempGameString = mContentView.getThread().getSaveGameString();

		SharedPreferences GameSettings = getSharedPreferences(PREFS_NAME,
			MODE_PRIVATE);

		SharedPreferences.Editor editor = GameSettings.edit();
		
		if( gameStringType == SAVED_GAME )
		{
			ProgressDialog dialog = ProgressDialog.show(Tetris.this, "",
				"Saving Game. Please wait...", true);

			editor.clear();
			editor.putBoolean("gameSaved", true);
			editor.putString("gameString", tempGameString);
			editor.commit();

			dialog.dismiss();

			Toast.makeText(getApplicationContext(), "Game Saved",
				Toast.LENGTH_SHORT).show();
		}
		else
		{
			editor.clear();
			editor.putBoolean("tempGameSaved", true);
			editor.putString("tempGameString", tempGameString);
			editor.commit();
			
		
		}

		// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		// settings.putBoolean("silentMode", true);

		// Music.stop(this);
	}

	/***
	 * Check if we have saved game
	 * @return
	 * 	+ if we have a saved game, return that saved game
	 * 	+ return a empty string if we do not
	 * 	
	 */
	private String checkForSavedGame(int gameStringType) {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		mFoundSavedGame = false;
		
		if( gameStringType == SAVED_GAME )
		{		
			if (settings.contains("gameSaved")) {
				mFoundSavedGame = settings.getBoolean("gameSaved", false);
			if (mFoundSavedGame)
				return settings.getString("gameString", "");
			}
		}
		else if (gameStringType == TEMP_SAVED_GAME)
		{
			if (settings.contains("tempGameSaved")) {
				mFoundSavedGame = settings.getBoolean("tempGameSaved", false);
			if (mFoundSavedGame)
				return settings.getString("tempGameString", "");
			}
		
		}
		return "";
	}
	
	/**
	 * Set the type of orientation to display.
	 * @param orientation
	 */
	public void setOrientation(int orientation) {
		if(orientation == 2)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
		 else if(orientation == 3) 
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 else
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);	
	}

}