package game.tetris;

import static android.provider.BaseColumns._ID;
import static game.tetris.HighScoreConstants.DATE;
import static game.tetris.HighScoreConstants.SCORE;
import static game.tetris.HighScoreConstants.TABLE_NAME;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HighScoreData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "scores.db";
	private static final int DATABASE_VERSION = 1;

	/** Create a helper object for the Scores database */
	public HighScoreData(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Database stored in the /data/data/game.tetris/databases directory
	/*
	 * File Explorer view in Eclipse (Window > Show View > Other... > Android >
	 * File Explorer) to view, move, or delete it.
	 */

	/** Create database if it doesn't exit */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + SCORE
				+ " INTEGER NOT NULL," + DATE + " LONG NOT NULL);");

	}

	// fasdfasdfs

	/** Upgrade database to new version */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	/** Adds entry to database */
	public void addScore(int score, long date) {
		// Insert a new record into the Events data source.
		// You would do something similar for delete and update.
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SCORE, score);
		values.put(DATE, date);
		db.insertOrThrow(TABLE_NAME, null, values);
	}

	private static String[] FROM = { _ID, SCORE, DATE, };
	private static String ORDER_BY = SCORE + " DESC";

	/** Get the Highest Score */
	public int getHighestScore() {
		// Perform a managed query. The Activity will handle closing
		// and re-querying the cursor when needed.
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null, null,
				ORDER_BY);

		// get highest score
		int highestScore;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			highestScore = cursor.getInt(1);
		} else {
			highestScore = 0;
		} 
		cursor.close();
		db.close(); //XXX: may have fixed open database error.
		

		return highestScore;
	}
	
}
