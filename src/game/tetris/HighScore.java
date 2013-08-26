package game.tetris;

import static android.provider.BaseColumns._ID;
import static game.tetris.HighScoreConstants.DATE;
import static game.tetris.HighScoreConstants.SCORE;
import static game.tetris.HighScoreConstants.TABLE_NAME;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HighScore extends Activity {
	
	private HighScoreData highScores;
	private GraphicalView mChartView;
	private HighScoreChart mChart;
	static double[]mScore = new double[10];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.highscore); // display

	    //High Score Table
		highScores = new HighScoreData(this);
		try {
			Cursor cursor = getScore();
			showScores(cursor);
		} finally {
			highScores.close();
		}
		//High Score Chart
		getScorebyDate();
		mChart =new HighScoreChart();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.highscore_chart);
			mChartView = mChart.execute(this,mScore);
			layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		} else {
		    mChartView.repaint();
		    }
	}

	private static String[] FROM = { _ID, SCORE, DATE, };
	private static String ORDER_BY = SCORE + " DESC";
	private static String LIMIT = "10"; // 

	/***
	 *  Return Cursor exposing results from "scores.db" database.
	 */
	private Cursor getScore() {
		// Perform a managed query. The Activity will handle closing
		// and re-querying the cursor when needed.
		SQLiteDatabase db = highScores.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null, null,
				ORDER_BY, LIMIT);
		startManagingCursor(cursor);
		return cursor;
	}

	/***
	 *  Displays scores from "scores.db" to TextView in order from highest 
	 *  to lowest to lowest
	 */
/*	private void showScores(Cursor cursor) {
		int rank = 1; // rank order
		// Stuff them all into a big string
		StringBuilder builder = new StringBuilder();
		while (cursor.moveToNext()) {
			int score = cursor.getInt(1); // Get the 1 column - SCORE
			long date = cursor.getLong(2); // Get the 2 column - DATE
			String date_string =java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.LONG,java.text.DateFormat.SHORT).format(new java.util.Date(date));
			builder.append(rank + ": ");
			rank++; // increment rank
			builder.append(score).append(": \t");
			builder.append(date_string).append("\n");
		}
		// Display on the screen
		TextView text = (TextView) findViewById(R.id.highscore_results);
		text.setText(builder);
	}
*/
	private void showScores(Cursor cursor) {
		
		TableLayout mTable = (TableLayout) findViewById(R.id.highscore_table);
		mTable.setColumnStretchable(0, true);
		mTable.setColumnStretchable(1, true);
		mTable.setColumnStretchable(2, true);
		int rank = 1; // rank order
		// Stuff them all into a big string
		StringBuilder builder = new StringBuilder();
		while (cursor.moveToNext()) {
			int score = cursor.getInt(1); // Get the 1 column - SCORE
			long date = cursor.getLong(2); // Get the 2 column - DATE
			String date_string =java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.LONG,java.text.DateFormat.SHORT).format(new java.util.Date(date));
			/*
			builder.append(rank + ": ");			
			builder.append(score).append(": \t");
			builder.append(date_string).append("\n");
			*/
			//Create a row
			TableRow mRow = new TableRow(this);
            mRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT)); 
            
            //Create cells in the row
            TextView rankView = new TextView (this);
            rankView.setText(Integer.toString(rank));
            rankView.setGravity(android.view.Gravity.CENTER);
            
            TextView scoreView = new TextView (this);
            scoreView.setText(Integer.toString(score));
            scoreView.setGravity(android.view.Gravity.CENTER);
            
            TextView dateView = new TextView (this);
            dateView.setText(date_string);
            dateView.setGravity(android.view.Gravity.CENTER);
            
            //Add views to Row     
            mRow.addView(rankView);
            mRow.addView(scoreView);
            mRow.addView(dateView);
	       /* Add row to TableLayout. */
	       mTable.addView(mRow,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
	       
	       rank++; // increment rank
		}
		
		View spacer = new View(this);
		spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,10)); 
		mTable.addView(spacer);
		// Display on the screen
	//	TextView text = (TextView) findViewById(R.id.highscore_results);
	//	text.setText(builder);
	}

	private void getScorebyDate(){
		highScores = new HighScoreData(this);
		String order = DATE + " DESC";
		SQLiteDatabase db = highScores.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null, null,
				order, LIMIT);
		startManagingCursor(cursor);
		int i = 0;
		while (cursor.moveToNext()) {
			double score = cursor.getDouble(1); // Get the 1 column - SCORE
			mScore[i]=score;
			i++;
		}
		cursor.close();
		highScores.close();
	}
	/** Restart music */
	@Override
	protected void onResume() {
		super.onResume();
		Music.play(this, R.raw.battletoads);
	    //High Score Chart
		if (mChartView == null) {
			 LinearLayout layout = (LinearLayout) findViewById(R.id.highscore_chart);
		      mChartView = mChart.execute(this,mScore);
		      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			} else {
			      mChartView.repaint();
			    }
	}

	/** Pause music */
	@Override
	protected void onPause() {
		super.onPause();
		Music.stop(this);
	}
}