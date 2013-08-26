package game.tetris;

import android.provider.BaseColumns;

public interface HighScoreConstants extends BaseColumns {
	public static final String TABLE_NAME = "highscores";

	// Columns in Scores database
	public static final String NAME = "name";
	public static final String SCORE = "score";
	public static final String DATE = "date";
}
