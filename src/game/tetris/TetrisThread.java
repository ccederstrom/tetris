package game.tetris;

import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TetrisThread extends Thread {
	private TetrisBoard mBoard = null;
	private TetrisPiece mPiece = null;
	private TetrisPiece mNextPiece = null;
	private Coord mPiecePos = null;
	private Coord mDropPos = null;
	private long mLastTime;
	private long mLastMoveTime;
	private SurfaceHolder mSurfaceHolder = null;
	private Context mContext = null;
	private boolean mRunning;
	private boolean mDisplayPiece;
	private int mCanvasWidth;
	private int mCanvasHeight;
	private int mBlockSize;
	private int mSmallBlockSize;
	private Bitmap[] mBlockBitmaps;
	private Bitmap[] mSmallBlockBitmaps;
	private Bitmap[][] mFadeBitmaps = new Bitmap[6][7];
	private int mScores;
	private int mHighScore;
	private int mTotalLines;
	private int mLevel;
	private boolean[] mFilledLines = new boolean[TetrisBoard.HEIGHT];
	private boolean mFadingLines;
	private int mFadingStage;
	private long mLastFadeTime;
	Resources mResources;
	//private size of an unit for process bar
	private float mUnitLandscape;
	private float mUnitHorizontal;
	//private String saveStringSettings;
	private Drawable mArrow;
	private Drawable mBackground;
	private boolean mClockwiseRotation;
	private int mPrefilledRows;
	private int mRowDensity;
	private boolean mCanvasSizeKnown = false;	
	private Typeface Kooky;
	
	// drawing coordinates
	private int mStartX;
	private int mStartY;
	private int mStopX;
	private int mStopY;
	
	
	private static int LEVEL_UP_LINES = 10;

	//touch input variables and constant
	private static int mTouchType;
	private int mX;
	private int mXDown;
	private int mYDown;
	private int mCounter;
	public static int mTouchDelay;
	private Rect mDropButton=new Rect();
	
	//state variables
	private int mMode;
    public static final int PAUSED = 0;
    public static final int RUNNING = 1;


    // database
	private HighScoreData highScores;
	
	//saved game variables
	private boolean mSavedGameFound;
	private String[] mGameString;
	
	//constants used in methods related with save game reloading
    public static final int BOARD = 0;
    
    public static final int PIECESHAPE = 1;
    public static final int PIECEROTATION = 2;
    
    public static final int PIECE_X = 3;
    public static final int PIECE_Y = 4;

    public static final int NEXTPIECESHAPE = 5;
    public static final int NEXTPIECEROTATION = 6;
    
    public static final int SCORE = 7;
    public static final int TOTALLINES = 8;
    public static final int LEVEL = 9;
    
    
    private boolean first_level_display ;

    
    
    // initial background variables
    private Canvas canvasBackground; // used to draw into bitmapBackground
	private Bitmap bitmapBackground; // used for background
	Matrix matrix = new Matrix();
	Rect r = new Rect();
	Paint paint = new Paint();
	int shapeId;

    //nextpiece overlap fix
    int tempRotation;
   
    //Sound for the piece.
    static boolean mSoundEffect;
    static SoundPool mSoundPool;
    static int mSoundId;
    /***
     * Default constructor for main thread.
     * @param holder - SurfaceHolder
     * @param context - Context
     */
	public TetrisThread(SurfaceHolder holder, Context context) {
		tempRotation = -1;
		first_level_display = false;
		mLevel = 1;
		mSurfaceHolder = holder;
		mContext = context;
		mBoard = new TetrisBoard();
		mPiece = new TetrisPiece();
		mNextPiece = new TetrisPiece();
		
		highScores = new HighScoreData(mContext); // Initialize with Context
		mHighScore = highScores.getHighestScore();
		
		mResources = mContext.getResources();
		mArrow = mResources.getDrawable(R.drawable.tetrisarrow);
		mBackground = mResources.getDrawable(R.drawable.tetrisbg);
		
		Kooky = Typeface.createFromAsset(mContext.getAssets(), "Fonts/kooky.otf"); 
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		mSoundId=mSoundPool.load(mContext, R.raw.sound76, 1);
	} // end of constructor

	
	/***
	 * This constructor is used to restore board display.
	 * @param holder - SurfaceHolder
	 * @param context - Context
	 * @param gameString - String of previous game 
	 */
	public TetrisThread(SurfaceHolder holder, Context context, String gameString) {
		tempRotation = -1;
		first_level_display = false;

		mSurfaceHolder = holder;
		mContext = context;
		mSavedGameFound = true;
		
		//parsed out string with saved game variables(seperated with space)
		String delims = "[ ]+";
		mGameString = gameString.split(delims);
		//String = board piece piecex piecey nextpiece scores totallines

		mBoard = new TetrisBoard(mGameString[BOARD]);
		mPiece = new TetrisPiece();
		mNextPiece = new TetrisPiece();
		
		mPiece.set(mGameString[PIECESHAPE],mGameString[PIECEROTATION]);
		mNextPiece.set(mGameString[NEXTPIECESHAPE],mGameString[NEXTPIECEROTATION]);		
		mScores = Integer.parseInt(mGameString[SCORE]);
		mTotalLines = Integer.parseInt(mGameString[TOTALLINES]);
		mLevel =Integer.parseInt(mGameString[LEVEL]); 
		
		highScores = new HighScoreData(mContext); // Initialize with Context
		mHighScore = highScores.getHighestScore();
		
		mResources = mContext.getResources();
		mArrow = mResources.getDrawable(R.drawable.tetrisarrow);
		mBackground = mResources.getDrawable(R.drawable.tetrisbg);
		 
		Kooky = Typeface.createFromAsset(mContext.getAssets(), "Fonts/kooky.otf"); 
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		mSoundId=mSoundPool.load(mContext, R.raw.sound76, 1);
	}


	@Override
	public void run() {

		while (mRunning) {
			if( mMode == RUNNING && mCanvasSizeKnown){
			Canvas canvas = null;
			try {
				canvas = mSurfaceHolder.lockCanvas();

				synchronized (mSurfaceHolder) {
					updateGame();
					doDraw(canvas);
				}
			} finally {
				if (canvas != null)
					mSurfaceHolder.unlockCanvasAndPost(canvas);
			}
			}
		
		}
	}

	/***
	 * Set the 
	 * @param running - Set the state of the thread.
	 */
	public void setRunning(boolean running) {
		mRunning = running;
	}

	/***
	 * Returns state of the thread.
	 * @return boolean
	 */
	public boolean getRunning() {
		return mRunning;
	}

	public void setMode(int mode) {
		mMode = mode;
	}


	/***
	 * Handles the surface's size being initialized or changed
	 * @param width - int
	 * @param height - int
	 */
	public void setCanvasSize(int width, int height) {
		synchronized (mSurfaceHolder) {
			mCanvasWidth = width;
			mCanvasHeight = height;
			chooseBlockSize(); // set the block size based on canvas size
			mSmallBlockSize = (mBlockSize * 3) / 4;
			mBlockBitmaps = generateBlockBitmaps(mBlockSize);
			mSmallBlockBitmaps = generateBlockBitmaps(mSmallBlockSize);
			generateFadeBitmaps();
			mDropButton.left =mCanvasWidth- 4*mBlockSize;
			mDropButton.top = mCanvasHeight-7*mBlockSize;
			mDropButton.right =mCanvasWidth;
			mDropButton.bottom = mCanvasHeight-3*mBlockSize;
			mArrow.setBounds(mDropButton);
			mBackground.setBounds(0, 0, width, height); //old background
			generateBackgroundBitmap(); // new background!
			
			mCanvasSizeKnown = true;
		}
	}
	
	/**
	 * Generates block size according to the screen resolution.
	 */
	private void chooseBlockSize() {
		// get the largest block size that will fit the screen
		mBlockSize = Math.min(mCanvasHeight / TetrisBoard.DISPLAYED_HEIGHT,
		                      (mCanvasWidth*4/7) / TetrisBoard.WIDTH);
		// make it an even number
		mBlockSize &= ~1;
	}

	public void setRotation(boolean isClockwise) {
		mClockwiseRotation = isClockwise;
	}
	
	

	public void setPrefilledRows(int prefilledRows) {
		mPrefilledRows = prefilledRows;
	}

	/***
	 * 
	 * @param rowDensity
	 */
	public void setRowDensity(int rowDensity) {
		mRowDensity = rowDensity;
	}

	/***
	 * Sets the touch screen delay.
	 * @param touchDelay
	 */
	public void setTouchDelay(int touchDelay) {
		mTouchDelay = touchDelay;
	}
	
	/***
	 * Sets the touch type.
	 * @param touchType
	 */
	public void setTouchType(int touchType) {
		mTouchType = touchType;
	}
	
	/***
	 * Sets the sound effect.
	 * @param touchType
	 */
	public void setSoundEffect(boolean soundEffect) {
		mSoundEffect = soundEffect;
	}
	// generates block bitmaps to match the block size
	private Bitmap[] generateBlockBitmaps(int blockSize) {
		Bitmap[] blockBitmaps = new Bitmap[7];

		for (int i = 0; i < 7; i++) {
			int[] colors = new int[blockSize * blockSize];
			int lightColor = TetrisPiece.lightColors[i];
			int mediumColor = TetrisPiece.mediumColors[i];
			int darkColor = TetrisPiece.darkColors[i];

			// fill the color array of the bitmap
			// the left and top borders are the light color
			// the right and bottom borders are dark color
			// the center is the medium color
			for (int x = 0; x < blockSize; x++)
				for (int y = 0; y < blockSize; y++) {
					int colorIndex = (y * blockSize) + x;

					if (x >= 2 && x < blockSize - 2
					 && y >= 2 && y < blockSize - 2)
						colors[colorIndex] = lightColor;//mediumColor;
					else if ((x + y) < (blockSize - 1))
						colors[colorIndex] = lightColor;
					else
						colors[colorIndex] = darkColor;//mediumColor;//;
				}

			// create the bitmap from the color array
			blockBitmaps[i] = Bitmap.createBitmap(colors,
			                                       blockSize,
			                                       blockSize,
			                                       Bitmap.Config.ARGB_8888);
		}

		return blockBitmaps;
	}

	private void generateFadeBitmaps() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				int[] colors = new int[mBlockSize * mBlockSize];
				int alpha = (((i + 1) * 32) << 24);
				int lightColor = ((TetrisPiece.lightColors[j] & 0xFFFFFF) | alpha);
				int mediumColor = ((TetrisPiece.mediumColors[j] & 0xFFFFFF) | alpha);
				int darkColor = ((TetrisPiece.darkColors[j] & 0xFFFFFF) | alpha);
	
				// fill the color array of the bitmap
				// the left and top borders are the light color
				// the right and bottom borders are dark color
				// the center is the medium color
				for (int x = 0; x < mBlockSize; x++)
					for (int y = 0; y < mBlockSize; y++) {
						int colorIndex = (y * mBlockSize) + x;
	
						if (x >= 2 && x < mBlockSize - 2
						 && y >= 2 && y < mBlockSize - 2)
							colors[colorIndex] = lightColor;//mediumColor;
						else if ((x + y) < (mBlockSize - 1))
							colors[colorIndex] = lightColor;
						else
							colors[colorIndex] = lightColor;//darkColor;
					}
	
				// create the bitmap from the color array
				mFadeBitmaps[i][j] = Bitmap.createBitmap(colors,
				                                       mBlockSize,
				                                       mBlockSize,
				                                       Bitmap.Config.ARGB_8888);
			}
		}
	}

	// initializes the game
	public void initGame() {
		mLastTime = System.currentTimeMillis() + 100;
		mLastMoveTime = mLastTime;
		mDisplayPiece = true;
		mFadingLines = false;
		if(mSavedGameFound)
			reStorePiece();
		else {
			mBoard.fillRows(mPrefilledRows, mRowDensity);
			mNextPiece.randomize();
			newPiece();
		}
	}

	// updates the game
	private void updateGame() {
		long currentTime = System.currentTimeMillis();
		int moveDelay = 500 - ((mTotalLines / 10) * 20);

		if (mFadingLines) {
			if (currentTime >= (mLastFadeTime + 100)) {
				if (mFadingStage == 0) {
					mFadingLines = false;
					mBoard.deleteLines(mFilledLines);
					newPiece();
					mLastTime = currentTime;
				} else {
					mFadingStage--;
					mLastFadeTime = currentTime;
				}
			}
		} else if (currentTime >= (mLastTime + moveDelay)) {
			mPiecePos.y--;

			if (mBoard.checkCollision(mPiece.getCoords(mPiecePos))) {
				mPiecePos.y++;

				if (currentTime >= (mLastMoveTime + 500)) {
					mBoard.mergePiece(mPiece.getShapeId(), mPiece.getCoords(mPiecePos));
					int lines = mBoard.checkFilledLines(mFilledLines);
					updateScores(lines);

					if (lines > 0) {
						mFadingLines = true;
						mFadingStage = 7;
						mLastFadeTime = currentTime;
					} else {
						newPiece();
						mLastTime = currentTime;
					}
				}
			} else
				mLastTime = currentTime;
		}
	}

	// moves the piece left upon user input
	private void moveLeft() {
		mPiecePos.x--;

		// disallow the move if there is a collision
		if (mBoard.checkCollision(mPiece.getCoords(mPiecePos)))
			mPiecePos.x++; // restore original position
		else {
			calcDropPos(); // recalculate drop position
			mLastMoveTime = System.currentTimeMillis();
		}
	}

	// moves the piece right upon user input
	private void moveRight() {
		mPiecePos.x++;

		// disallow the move if there is a collision
		if (mBoard.checkCollision(mPiece.getCoords(mPiecePos)))
			mPiecePos.x--; // restore original position
		else {
			calcDropPos(); // recalculate drop position
			mLastMoveTime = System.currentTimeMillis();
		}
	}

	// rotates the piece upon user input
	// "wall kick" is used to allow the user to rotate pieces against a wall
	private void rotate() {
		int piecePosX = mPiecePos.x;

		if (mClockwiseRotation)
			mPiece.rotateClockwise();
		else
			mPiece.rotateCounterclockwise();

		if (mBoard.checkCollision(mPiece.getCoords(mPiecePos))) {
			mPiecePos.x = piecePosX + 1;

			// if there is no space, try moving to the right
			if (mBoard.checkCollision(mPiece.getCoords(mPiecePos))) {
				mPiecePos.x = piecePosX - 1;

				// try the moving to the left
				if (mBoard.checkCollision(mPiece.getCoords(mPiecePos))) {
					mPiecePos.y++;

					// try moving up
					if (mBoard.checkCollision(mPiece.getCoords(mPiecePos))) {
						// rotate failed, so restore original state
						if (mClockwiseRotation)
							mPiece.rotateCounterclockwise();
						else
							mPiece.rotateClockwise();
						mPiecePos.x = piecePosX;
						mPiecePos.y--;
						return;
					}
				}
			}
		}

		calcDropPos();
		mLastMoveTime = System.currentTimeMillis();
	}

	private void drop() {		
		mPiecePos = mDropPos;
		mLastMoveTime = System.currentTimeMillis();
	//	mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);//TODO:sada
	}

	private void calcDropPos() {
		mDropPos = new Coord(mPiecePos.x, mPiecePos.y);
		do {
			mDropPos.y--;
		} while (!mBoard.checkCollision(mPiece.getCoords(mDropPos)));
		mDropPos.y++;
	}

	// randomizes the piece's shape and rotation and moves it to the top
	private void newPiece() {
		if(tempRotation != -1){
			mNextPiece.setRotation(tempRotation);
			tempRotation = -1;
		}

		TetrisPiece temp = mNextPiece;
		mNextPiece = mPiece;
		mPiece = temp;
		mNextPiece.randomize();
		mPiecePos = new Coord(5, 20);

		if (mBoard.checkCollision(mPiece.getCoords(mPiecePos))) {
			endOfGameByLoss();
		} else
			calcDropPos();
	}
	//if there was a saved game reStorePiece is called instead of newPiece to initialize coordinates
	private void reStorePiece(){
		mPiecePos = new Coord(Integer.parseInt(mGameString[PIECE_X].trim()), Integer.parseInt(mGameString[PIECE_Y].trim()));
		if (mBoard.checkCollision(mPiece.getCoords(mPiecePos))) {
			endOfGameByLoss();
		} else
			calcDropPos();	
	}
	
	
	
	private void endOfGameByLoss(){
		// good place to call methods that need to be done when the game is over. Ie: hs
		highScores.addScore(mScores, new Date().getTime() ); 
		highScores.close(); //XXX: May have fixed open database crash.
		showEndGameScores.sendEmptyMessage(0); 
		mRunning = false;
		mDisplayPiece = false;				
		((Activity) mContext).finish();	  // finishes the tetrisview which houses this thread - goes back to mm

	}
	
	
	/**
	 * Generates the static background image
	 */
	private void generateBackgroundBitmap(){
		// create bitmap for background
		bitmapBackground = Bitmap.createBitmap(mCanvasWidth, mCanvasHeight, Bitmap.Config.ARGB_8888);
		canvasBackground = new Canvas(bitmapBackground); //draw into the bitmap
		mBackground.draw(canvasBackground);
		//clear screen
		/*paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL);
		r.bottom = mCanvasHeight;
		r.right = mCanvasWidth;
		r.left = 0;
		r.top = 0;
		canvasBackground.drawRect(r, paint); 
		
		// draw background
		int gr=(mCanvasHeight/256)+1;
		for (int gradient = 0; gradient < 256; ++gradient) {
			paint.setColor(Color.argb(255, gradient,0,0));
			paint.setStyle(Style.FILL);
			canvasBackground.drawLine(0, gradient*gr , mCanvasWidth, gradient*gr, paint);
		}
		
		// might want to draw a black board behind the opacic board so the background is not seen.
		// draw background for board
		paint.setColor(Color.BLACK);
		//paint.setAlpha(50);
		paint.setStyle(Style.FILL);
		
		canvasBackground.drawRect((mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2,
                (mCanvasHeight - mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 ,
                mBlockSize * 10 + (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2,
                mCanvasHeight -  (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 + 1,
                paint);
		
		*/
		// draw OPAIC board
	
		// draw verticle grid lines
		paint.setColor(Color.WHITE);
		paint.setAlpha(75);
		paint.setStyle(Style.FILL);
	
		for( int gridLine=1; gridLine < 10; gridLine++){
			mStartX = (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2 + mBlockSize * gridLine ;
			mStartY = mCanvasHeight - mBlockSize * TetrisBoard.DISPLAYED_HEIGHT - (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2;
			mStopX = (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2 + mBlockSize * gridLine;
			mStopY = mCanvasHeight - (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 +1;
			canvasBackground.drawLine(mStartX, mStartY, mStopX, mStopY, paint);
		}
		
		// draw horizontal grid lines
		paint.setColor(Color.WHITE);
		paint.setAlpha(75);
		paint.setStyle(Style.FILL);
	
		for( int gridLine=1; gridLine < 20; gridLine++){
			mStartX = (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2;
			mStartY = mCanvasHeight - mBlockSize * gridLine -(mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2;
			mStopX = mBlockSize * 10 + (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2;
			mStopY = mCanvasHeight - mBlockSize * gridLine -(mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2;
			canvasBackground.drawLine(mStartX, mStartY, mStopX, mStopY, paint);
		}	
		
		// draw board boarder

		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		canvasBackground.drawRect((mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2,
		                (mCanvasHeight - mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 ,
		                mBlockSize * 10 + (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2+1,
		                mCanvasHeight -  (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 +1,
		                paint);		
		
		// draw progress bar boarder
		Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();  
		int mOrientation = display.getOrientation();
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		RectF p1 = new RectF();
		if(mOrientation==0||mOrientation==2){
			p1.left = mCanvasWidth/7;
			p1.right = mCanvasWidth- mCanvasWidth/7;
			p1.bottom =mCanvasHeight*3/4 + mBlockSize * TetrisBoard.DISPLAYED_HEIGHT/4 + 8 ;
			p1.top = p1.bottom - 8;	
			mUnitHorizontal=(p1.right-p1.left)/10;
		}		
		else{
			p1.left = 3*(mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/8;
			p1.right = p1.left+8;
			p1.bottom =mCanvasHeight - (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 +1;
			p1.top=(mCanvasHeight - mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2;
			mUnitLandscape=(p1.top-p1.bottom)/10;
		}
		canvasBackground.drawRoundRect(p1, 2, 2, paint);
		paint.setAntiAlias(false);
		
		//draw play text
		
		Paint paintText2 = new Paint();
		paintText2.setColor(Color.WHITE);
		paintText2.setTextAlign(Paint.Align.CENTER);
		paintText2.setTypeface(Kooky);
		paintText2.setTextSize(13);
		float [] pos = new float[3];
		pos[0]= (mCanvasWidth-(mBlockSize*10))/2+mBlockSize*11;
		pos[1]= mBlockSize*12;		
		pos[2] = mBlockSize*11;
		paintText2.setAntiAlias(true);
		
		//draw Level
		canvasBackground.drawText("Level", mCanvasWidth-(mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-6*mBlockSize, paintText2); 
		
		//draw Lines
		canvasBackground.drawText("Lines", mCanvasWidth-(mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-9*mBlockSize, paintText2); 
		//canvasBackground.drawText("Lines", (mCanvasWidth/2)+5*mBlockSize, mBlockSize, paintText2);
		
		//draw Score
		canvasBackground.drawText("Score", (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-9*mBlockSize, paintText2);
		//canvasBackground.drawText("SCORE", (mCanvasWidth/2)+5*mBlockSize, mBlockSize, paintText2);
		
		//draw High Scores
		//canvasBackground.drawText("High Score", (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-6*mBlockSize, paintText2); 
		canvasBackground.drawText("High", (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-6*mBlockSize, paintText2); 
		canvasBackground.drawText("Score", (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-5*mBlockSize, paintText2); 
		//canvasBackground.drawText("High Score", (mCanvasWidth/2)-5*mBlockSize, mBlockSize, paintText2);
		
		//draw Next
		canvasBackground.drawText("Next",  mCanvasWidth-(mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-3*mBlockSize, paintText2); 
	}
	
	

	/***
	 *  Draws the piece 	
	 * @param canvas
	 */
	private void doDraw(Canvas canvas) {

		Coord[] pieceCoords = mPiece.getCoords(mPiecePos);
		Coord[] dropCoords = mPiece.getCoords(mDropPos);
		shapeId = mPiece.getShapeId();
		
	    canvas.drawBitmap(bitmapBackground, 0, 0, null); //draw background

		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(TetrisPiece.lightColors[shapeId - 1]);
		if (mLevel == 1 && !first_level_display) {
			first_level_display = true;
			showLevelUp.sendEmptyMessage(0); 
		}
		if (mDisplayPiece && !mFadingLines) {
			// draw drop indicator
			for (int i = 0; i < 4; i++) {
				if (dropCoords[i].y < TetrisBoard.DISPLAYED_HEIGHT) {
					int screenX = dropCoords[i].x * mBlockSize;
					int screenY = mCanvasHeight - (dropCoords[i].y * mBlockSize);
					r.left = screenX + (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2 +1;
					r.right = screenX + mBlockSize - 2 + (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2 +1 ;
					r.bottom = screenY - 2 - (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 +1;
					r.top = screenY - mBlockSize - (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 +1;
					canvas.drawRect(r, paint);
				}
			}
			
			
			// draw piece
			for (int i = 0; i < 4; i++) {
				if (pieceCoords[i].y < TetrisBoard.DISPLAYED_HEIGHT) {
					int screenX = pieceCoords[i].x * mBlockSize;
					int screenY = mCanvasHeight - (pieceCoords[i].y * mBlockSize);
					r.left = screenX + (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2 +1;
					r.right = screenX + mBlockSize + (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2 +1;
					r.bottom = screenY - (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 +1;
					r.top = screenY - mBlockSize - (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 +1;
					canvas.drawBitmap(mBlockBitmaps[shapeId - 1], null, r, null);
				}
			}			
		}

		// draw board
		for (int x = 0; x < TetrisBoard.WIDTH; x++)
			for (int y = 0; y < TetrisBoard.DISPLAYED_HEIGHT; y++) {
				int block = mBoard.getBlock(x, y);

				if (block != 0) {
					int screenX = x * mBlockSize;
					int screenY = mCanvasHeight - (y * mBlockSize);
					r.left = screenX + (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2+1 ;
					r.right = screenX + mBlockSize + (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/2+1 ;
					r.bottom = screenY - (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 +1;
					r.top = screenY - mBlockSize - (mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 +1;

					if (mFadingLines && mFilledLines[y]) {
						if (mFadingStage == 7)
							canvas.drawBitmap(mBlockBitmaps[block - 1], null, r, null);
						else if (mFadingStage > 0)
							canvas.drawBitmap(mFadeBitmaps[mFadingStage - 1][block - 1], null, r, null);
					} else
						canvas.drawBitmap(mBlockBitmaps[block - 1], null, r, null);
				}
			}
		
		
		//draw play text
		Paint paintText = new Paint();
		paintText.setColor(Color.WHITE);
		paintText.setTextAlign(Paint.Align.CENTER);
		paintText.setTypeface(Kooky);
		paintText.setAntiAlias(true);
		paintText.setTextSize(15);
		//paintText.setTextSize(14);
		float [] pos = new float[3];
		pos[0]= (mCanvasWidth-(mBlockSize*10))/2+mBlockSize*11;
		pos[1]= mBlockSize*12;		
		pos[2] = mBlockSize*11;

		//draw Level	
		canvas.drawText(Integer.toString(mLevel),  mCanvasWidth-(mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-5*mBlockSize, paintText); 
		
		//draw Lines
		canvas.drawText(Integer.toString(mTotalLines), mCanvasWidth-(mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-8*mBlockSize, paintText);
		
		//draw Score
		canvas.drawText(Integer.toString(mScores), (mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-8*mBlockSize, paintText); 
		
		//draw High Scores
		canvas.drawText(Integer.toString(mHighScore),(mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4, pos[1]-4*mBlockSize, paintText); 

		
		
		//draw Progress Bar	 
		Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();  
		int mOrientation = display.getOrientation();		
		Paint paint1 = new Paint();
		paint1.setStyle(Paint.Style.FILL);
		paint1.setColor(Color.WHITE);
		paint1.setAlpha(150);
		RectF r1 = new RectF();	
		if(mOrientation==0||mOrientation==2){
			r1.left =  mCanvasWidth/7-1;
			r1.right =  mCanvasWidth/7-1 + mUnitHorizontal*(mTotalLines%10);	
			r1.bottom = mCanvasHeight*3/4 + mBlockSize * TetrisBoard.DISPLAYED_HEIGHT/4 + 8 ;
			r1.top = r1.bottom - 8;
		}
		else{			
			r1.left = 3*(mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/8;
			r1.right = r1.left+8;
			r1.bottom =mCanvasHeight -(mCanvasHeight-mBlockSize * TetrisBoard.DISPLAYED_HEIGHT)/2 +1-1;
			r1.top=r1.bottom + (mUnitLandscape*(mTotalLines%10));
		}
		canvas.drawRoundRect(r1, 2, 2, paint1);

		// draw next piece		
	/*	int nextPieceX = (int) pos[0];
		int nextPieceY = (int) (pos[1] + 7*mBlockSize);
*/
		int nextPieceX = (int) (mCanvasWidth-(mCanvasWidth-mBlockSize*TetrisBoard.WIDTH)/4-5);
		int nextPieceY = (int) (pos[1]-0*mBlockSize);
		
		Coord[] nextPieceCoords = mNextPiece.getCoords(new Coord(0, 0));
		
		int nextShapeId = mNextPiece.getShapeId();
		for (int i = 0; i < 4; i++) {			
				int screenX = nextPieceX + nextPieceCoords[i].x * mSmallBlockSize;
				int screenY = nextPieceY - nextPieceCoords[i].y * mSmallBlockSize;
				r.left = screenX;
				r.right = screenX + mSmallBlockSize;
				r.bottom = screenY;
				r.top = screenY - mSmallBlockSize;
				canvas.drawBitmap(mSmallBlockBitmaps[nextShapeId - 1], null, r, null);
		}
		
		//draw arrow image
		if(mTouchType==2) mArrow.draw(canvas); //TODO: has the coordinates for the MotionEvent been removed?
	}
	
	private Handler showLevelUp = new Handler() { 
		public void handleMessage(Message msg) { 
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View layout = inflater.inflate(R.layout.toast_layout, null);

			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(android.R.drawable.btn_star_big_on);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setTextSize(16);
			text.setText("Level " + Integer.toString(mLevel));

			Toast toast = new Toast(mContext);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layout);
			toast.show();
		}
	};
	
	public Handler showPaused = new Handler() { 
		public void handleMessage(Message msg) { 
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View layout = inflater.inflate(R.layout.toast_layout, null);

			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(android.R.drawable.ic_media_pause);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setTextSize(16);
			text.setText("Paused");

			Toast toast = new Toast(mContext);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			
			toast.setDuration(3000000);
			toast.setView(layout);
			toast.show();
		}
	};

	private Handler showEndGameScores = new Handler() {
		public void handleMessage(Message msg) {
			Toast toast = null;

			if (mHighScore - mScores > 0) {
				toast = Toast.makeText(mContext, "High Score: " + mHighScore
						+ "\n\nYour Score: " + mScores, Toast.LENGTH_LONG);
			} else {
				toast = Toast.makeText(mContext,
						"New High Score\n\nYour Score: " + mScores
								+ "\n\nPrevious High Score: " + mHighScore,
						Toast.LENGTH_LONG);
			}

			toast.show();
		}
	};	

		
	// handles user input
	public boolean doKeyDown(int keyCode, KeyEvent event) {
		synchronized (mSurfaceHolder) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				moveLeft();
				return true;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				moveRight();
				return true;
			case KeyEvent.KEYCODE_DPAD_UP:
				rotate();
				return true;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				rotate();
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				drop();
				return true;
			}
		}

		return false;
	}

	/**
	 * This method defines the touch screen actions
	 * @param MotionEvent event 
	 * @return boolean
	 */
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (mSurfaceHolder){
	        int eventaction = event.getAction();        
	        int X = (int)event.getX(); 
	        int Y = (int)event.getY();
	        if(mTouchType==1){
		        switch (eventaction ) {
			        case MotionEvent.ACTION_DOWN: 
			        	mX=X;
			        	mXDown=X;
			        	mYDown=Y;
			             break;             
			        
			        case MotionEvent.ACTION_UP: // user actions
			   
			        	if( mXDown <(mCanvasWidth/2)-mBlockSize*5 ) //coordinates of the left of the board. Seem very close, good in landscape aswell
			        		moveLeft();
			        	else if ( mXDown > (mCanvasWidth/2) + mBlockSize*5 ) // //coordinates of the right of the board. Seem very close, good in landscape aswell
			        		moveRight();
			        	else if ( mYDown > mCanvasHeight-mBlockSize*6 ) //used to drop block,
			        		drop();
			        	else
			        		rotate();			        	
			              break; 
		        }
	        }
	        else{
	        	switch (eventaction ) {
		            case MotionEvent.ACTION_DOWN: 
		            	mX=X;
		            	mXDown=X;
		            	mYDown=Y;
		                break;
		                 
		            case MotionEvent.ACTION_MOVE:
		            	if(X<mXDown-5||X>mXDown+5||Y>mYDown+5||Y<mYDown-5){
		    	        	if(mCounter==mTouchDelay){
		    		        	if(X<mX)
		    		        		moveLeft();
		    		        	if(X>mX)
		    		        		moveRight();
		    		        	mX=X;
		    		        	mCounter=0;
		    	        	}
		    	        	else {
		    	        		mX=X;
		    		        	mCounter++;
		    	        	}
		            	}
		                break;
		                
		            case MotionEvent.ACTION_UP: 
		            	if(X>=mXDown-5&&X<=mXDown+5&&Y<=mYDown+5&&Y>=mYDown-5)        	
		            	{
		            		if(X>=mDropButton.left&&X<=mDropButton.right
		            			&&Y>=mDropButton.top&&Y<=mDropButton.bottom)
		            			drop();
		            		else
		            			rotate();
		            	}		            		
		                break; 
	        	}
	        }
	        return true; 
		}
    }

	//handles scores and total lines
	private void updateScores(int lines) {
		mTotalLines = mTotalLines + lines;

		// checks to see if level needs to be incremented
		if (mTotalLines >= (mLevel * LEVEL_UP_LINES)) {
			mLevel++;
			showLevelUp.sendEmptyMessage(0);
		}

		if (lines == 1) {
			mScores = mScores + 10 * lines;
		} else if (lines == 2) {
			mScores = mScores + 10 * lines + 5;
		} else if (lines == 3) {
			mScores = mScores + 10 * lines + 10;
		} else if (lines == 4) {
			mScores = mScores + 10 * lines + 15;
		}
	}

	public String getSaveGameString(){
		StringBuilder stringGameSettings = new StringBuilder(250);
		//board piece piecex piecey nextpiece scores totallines level
		
		stringGameSettings.append(mBoard.getBoardForSave());//0
		stringGameSettings.append(" ");
		stringGameSettings.append(mPiece.getPieceIdForSave());//1
		stringGameSettings.append(" ");
		stringGameSettings.append(mPiece.getPieceRotationForSave());//2
		stringGameSettings.append(" ");
		stringGameSettings.append(mPiecePos.x);//3
		stringGameSettings.append(" ");
		stringGameSettings.append(mPiecePos.y);//4
		stringGameSettings.append(" ");
		stringGameSettings.append(mNextPiece.getPieceIdForSave());//5
		stringGameSettings.append(" ");
		stringGameSettings.append(mNextPiece.getPieceRotationForSave());//6
		stringGameSettings.append(" ");
		stringGameSettings.append(mScores);//7
		stringGameSettings.append(" ");
		stringGameSettings.append(mTotalLines);//8
		stringGameSettings.append(" ");
		stringGameSettings.append(mLevel);//9
		stringGameSettings.append(" ");
	
		// need to save level + speed;
		return stringGameSettings.toString();

	}	
} //End of Class
