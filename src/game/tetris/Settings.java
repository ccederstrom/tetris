package game.tetris;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
	// Option names and default values
	private static final String OPT_MUSIC = "music";
	private static final boolean OPT_MUSIC_DEFAULT = true;
	
	private static final String OPT_SOUND_EFFECT = "sound_effect";
	private static final boolean OPT_SOUND_EFFECT_DEFAULT = true;
	
	// set board orientation
	private static final String OPT_BOARD_ORIENTATION = "board_orientation";
	private static final String OPT_BOARD_ORIENTATION_DEFAULT = "0";

		
	private static final String OPT_TOUCH_SPEED = "delay";
	private static final String OPT_TOUCH_SPEED_DEFAULT = "3";
	
	private static final String OPT_TOUCH_TYPE = "touch_type";
	private static final String OPT_TOUCH_TYPE_DEFAULT = "1";

	private static final String OPT_ROTATE = "piece_rotation";
	private static final boolean OPT_ROTATE_CLOCKWISE_DEFAULT = true;
	
	
	// Row density
	private static final String OPT_ROW_DENSITY = "row_density";
	private static final String OPT_ROW_DENSITY_DEFAULT = "0";

	// number of prefilled rows
	private static final String OPT_PREFILLED_ROW = "prefilled_row";
	private static final String OPT_PREFILLED_ROW_DEFAULT = "0";

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

	/** Get number of block per row */
	public static int getRowDensity(Context context) {
		String s = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_ROW_DENSITY, OPT_ROW_DENSITY_DEFAULT);
		return Integer.parseInt(s);
	}

	/** Get number of prefilled rows */
	public static int getPrefilledRows(Context context) {
		String s = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_PREFILLED_ROW, OPT_PREFILLED_ROW_DEFAULT);
		return Integer.parseInt(s);
	}

	/**
	 * Get rotation direction. Default rotation set to clockwise
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isClockwise(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(OPT_ROTATE, OPT_ROTATE_CLOCKWISE_DEFAULT);
	}

	/** Get the current value of the music option */
	public static boolean getMusic(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(OPT_MUSIC, OPT_MUSIC_DEFAULT);
	}
	
	/** Get the current value of the music option */
	public static boolean getSoundEffect(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(OPT_SOUND_EFFECT, OPT_SOUND_EFFECT_DEFAULT);
	}

	/** Get value of touch screen speed */
	public static int getTouchSpeed(Context context) {
		String s = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_TOUCH_SPEED, OPT_TOUCH_SPEED_DEFAULT);
		return Integer.parseInt(s);
	}
	
	/** Get value of touch type */
	public static int getTouchType(Context context) {
		String s = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(OPT_TOUCH_TYPE, OPT_TOUCH_TYPE_DEFAULT);
		return Integer.parseInt(s);
	}
	
	public static int getOrientation(Context context) {
		String s = PreferenceManager.getDefaultSharedPreferences(context)
			.getString(OPT_BOARD_ORIENTATION, OPT_BOARD_ORIENTATION_DEFAULT);
		return Integer.parseInt(s);
	}
	
}
