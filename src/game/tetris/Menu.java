package game.tetris;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class Menu extends Activity implements OnClickListener {
	private Button playGame;
	private Button viewHighScore;
	private Button options;
	private Button about;
	private Button exit;
	private int mDisplayHeight;
	private int mDisplayWidth;
	// filename for savegamesettigns
	public static final String PREFS_NAME = "TetrisGameSettings";
	private Bitmap[] mBlockBitmaps;
	private boolean mPaused;
	private Animation mButtonFlickerAnimation;
	private Animation mFadeOutAnimation;
	private Animation mAlternateFadeOutAnimation;
	private Animation mFadeInAnimation;
	private View mBackground;
    private View mStartButton;
    private View mOptionsButton;
    private View mScoresButton;
   // private View mContinueButton;
    private View mAboutButton;
    private View mExitButton;
    
	//for bitmap menu
	private Bitmap mBitmapBackground;
	private Canvas mCanvasMenu;


	/**
	 * Invoked when the Activity is created.
	 * 
	 * @param savedInstanceState
	 *            a Bundle containing state saved from a previous execution, or
	 *            null if this is a new execution
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
     
		requestWindowFeature(Window.FEATURE_NO_TITLE); 	// turn off the window's title bar

		// get screen size
		Display display = (Display) getWindowManager().getDefaultDisplay();
		mDisplayHeight = display.getHeight();
		mDisplayWidth = display.getWidth();

		//background 
		chooseBlockSize(mDisplayWidth, mDisplayHeight);
		mBlockBitmaps = generateBlockBitmaps(mBlockSize);
		generateBackground(mDisplayWidth, mDisplayHeight );
		BitmapDrawable mDrawableBackground = new BitmapDrawable(mBitmapBackground);
		setContentView(R.layout.menu); //set view

        mPaused = true; 
        mStartButton = findViewById(R.id.PlayGame);
        mOptionsButton = findViewById(R.id.Options);
        mScoresButton = findViewById(R.id.ViewHighScores);
        mAboutButton = findViewById(R.id.About);
        mExitButton = findViewById(R.id.Exit);
       // mContinueButton = findViewById(R.id.Continue);
        
        
		mBackground = findViewById(R.id.background);
		mBackground.setBackgroundDrawable(mDrawableBackground); // set the generated background
        
		//menu onClickListeners
        if (mStartButton != null) {
            mStartButton.setOnClickListener(sStartButtonListener);
        }
        if (mScoresButton != null) {
            mScoresButton.setOnClickListener(sScoresButtonListener);
        }
        
        if (mAboutButton != null) {
            mAboutButton.setOnClickListener(sAboutButtonListener);
        }
        
        if (mOptionsButton != null) {
            mOptionsButton.setOnClickListener(sOptionButtonListener);
        }
		
        if (mExitButton != null) {
            mExitButton.setOnClickListener(sExitButtonListener);
        }
        
        
        mButtonFlickerAnimation = AnimationUtils.loadAnimation(this, R.anim.button_flicker);
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mAlternateFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		
		// click listeners for buttons
	//	playGame = (Button) this.findViewById(R.id.PlayGame);
	//	playGame.setOnClickListener(this);
		//viewHighScore = (Button) this.findViewById(R.id.ViewHighScores);
		//viewHighScore.setOnClickListener(this);
	//	options = (Button) this.findViewById(R.id.Options);
//		options.setOnClickListener(this);
		//about = (Button) this.findViewById(R.id.About);
		//about.setOnClickListener(this);
		//exit = (Button) this.findViewById(R.id.Exit);
		//exit.setOnClickListener(this);
		Music.play(this, R.raw.tatrisa); 		// get some tunes!

	    //tetris1.setPadding(0, 0, 0,mDisplayHeight/500 );
	}
		
	

	
    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener sStartButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mPaused) {
            	if (checkForSavedGame())
            		continueGameShowDialog();
            	else
            	{
            		Intent i = new Intent(getBaseContext(), Tetris.class);
            		v.startAnimation(mButtonFlickerAnimation);
            		mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
            		mBackground.startAnimation(mFadeOutAnimation);
            		mStartButton.startAnimation(mAlternateFadeOutAnimation);
            		mPaused = true; 
            	}
            }
        }
    };
    
    private View.OnClickListener sOptionButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mPaused) {
                Intent i = new Intent(getBaseContext(), Settings.class);

                v.startAnimation(mButtonFlickerAnimation);
                mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                mBackground.startAnimation(mFadeOutAnimation);
                mStartButton.startAnimation(mAlternateFadeOutAnimation);
                mPaused = true;
            }
        }
    };
    
    
    private View.OnClickListener sScoresButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mPaused) {
                Intent i = new Intent(getBaseContext(), HighScore.class);
                
                v.startAnimation(mButtonFlickerAnimation);
                mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                mBackground.startAnimation(mFadeOutAnimation);
                mStartButton.startAnimation(mAlternateFadeOutAnimation);
                mPaused = true;
            }
        }
    };
      
    
    
    private View.OnClickListener sAboutButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mPaused) {
                Intent i = new Intent(getBaseContext(), About.class);
                v.startAnimation(mButtonFlickerAnimation);
                mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                mBackground.startAnimation(mFadeOutAnimation);
                mStartButton.startAnimation(mAlternateFadeOutAnimation);
                mPaused = true;
            }
        }
    };
    
    
    private View.OnClickListener sExitButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mPaused) {
                //Intent i = new Intent(getBaseContext(), Settings.class);
            	finish();
                v.startAnimation(mButtonFlickerAnimation);
                //mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                mBackground.startAnimation(mFadeOutAnimation);
                mStartButton.startAnimation(mAlternateFadeOutAnimation);
                mPaused = true;
            }
        }
    };	

    
    

    
    
    
	void generateBackground(int width, int height){
		mBitmapBackground = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mCanvasMenu = new Canvas(mBitmapBackground); //draw into the bitmap
		Rect r = new Rect();
		Paint paint = new Paint();
		
		/*//clear screen, used for testing
		paint.setColor(Color.RED);
		paint.setStyle(Style.FILL);
		r.bottom = height;
		r.right = width;
		r.left = 0;
		r.top = 0;
		mCanvasMenu.drawRect(r, paint); // end of test
		*/
		
		Random random = new Random();
		int rows = (int) Math.ceil(height/mBlockSize);
		int columns = (int) Math.ceil(width/mBlockSize);
		// draw background
		int left, top;
		for( int i = 0; i<=rows ; i++) {
			for( int j=0; j<=columns;j++){
				//draw block
				//r.left =  j*mBlockSize;
				left=j*mBlockSize;
				//r.right =  j*mBlockSize + mBlockSize;
				//r.bottom = i*mBlockSize + mBlockSize;
				//r.top = i*mBlockSize;	
				top=i*mBlockSize;
				//mCanvasMenu.drawBitmap(mBlockBitmaps[random.nextInt(7)], null, r, null);
				mCanvasMenu.drawBitmap(mBlockBitmaps[random.nextInt(7)], left, top, null);
			}
		}
	}

	/**
	 * Restart music
	 */
	@Override
	protected void onResume() {
		super.onResume();
        mPaused = false;
		Music.play(this, R.raw.tatrisa);
		
        if (mStartButton != null) {
        	mStartButton.setVisibility(View.VISIBLE);
        	mStartButton.clearAnimation();
            mStartButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_slide));
        }
        
        if (mOptionsButton != null) {
        	mOptionsButton.setVisibility(View.VISIBLE);
        	mOptionsButton.clearAnimation();
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_slide);
            anim.setStartOffset(200L);
            mOptionsButton.startAnimation(anim);
        }
        
        if (mBackground != null) {
        	mBackground.clearAnimation();
        }
        
        
        
        if (mScoresButton!= null) {
        	mScoresButton.setVisibility(View.VISIBLE);
        	mScoresButton.clearAnimation();
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_slide);
            anim.setStartOffset(200L);
            mScoresButton.startAnimation(anim);
        }
        
       // if(mContinueButton!=null){
       // 	mContinueButton.setVisibility(View.VISIBLE);
       // 	mContinueButton.clearAnimation();
       //     Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_slide);
       //     anim.setStartOffset(200L);
       //    mContinueButton.startAnimation(anim);
      //  }
        
        if(mAboutButton != null ) {
        	mAboutButton.setVisibility(View.VISIBLE);
        	mAboutButton.clearAnimation();
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_slide);
            anim.setStartOffset(200L);
            mAboutButton.startAnimation(anim);
        }
        
        if(mExitButton != null){
        	mExitButton.setVisibility(View.VISIBLE);
        	mExitButton.clearAnimation();
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_slide);
            anim.setStartOffset(200L);
            mExitButton.startAnimation(anim);
        }
              
	}

	/**
	 * Pause music
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mPaused=true;
		Music.stop(this);
	}

	/**
	 * Switch case menu for executing methods
	 */

/*	public void onClick(View v) {
		switch (v.getId()) {
		//case R.id.PlayGame:
		//	if (checkForSavedGame())
		//		continueGameShowDialog();
		//	else
		//		startGame();
		//	break;
		case R.id.ViewHighScores:
			Intent hs = new Intent(this, HighScore.class);
			startActivity(hs);
			break;
		//case R.id.Options:
		//	Intent opt = new Intent(this, Settings.class);
		//	startActivity(opt);
		//	break;
		case R.id.About:
			Intent a = new Intent(this, About.class);
			startActivity(a);
			break;
		case R.id.Exit:
			finish();
			break;
		default: 
			break;
		}
	}
	*/

	/** Start a new game */
	private void startGame() {
		startActivity(new Intent(getBaseContext(), Tetris.class));
	}

	// if game was found this dialog box will come up and alert user
	private boolean continueGameShowDialog() {
		new AlertDialog.Builder(this).setIcon(
				android.R.drawable.ic_dialog_alert).setTitle(
				R.string.saved_game_title).setMessage(R.string.saved_game_msg)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								startGame();

							}
						}).setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// if user wants a new game delete old game
								// settings and
								// start a new game
								deleteSavedGave();
								startGame();
							}
						}).show();

		return true;
	}

	// checks for saved game by opening the preferences and checking if
	// gameSaved exist
	// if it exist it checks if its set to true;
	private boolean checkForSavedGame() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean("gameSaved", false);
	}

	// deletes all contents of the game preferences file (deletes all saved game
	// settings)
	private void deleteSavedGave() {
		SharedPreferences GameSettings = getSharedPreferences(PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = GameSettings.edit();
		editor.clear();
		editor.commit();
	}
	
	
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
						colors[colorIndex] = mediumColor;
					else if ((x + y) < (blockSize - 1))
						colors[colorIndex] = lightColor;
					else
						colors[colorIndex] = darkColor;
				}

			// create the bitmap from the color array
			blockBitmaps[i] = Bitmap.createBitmap(colors,
			                                       blockSize,
			                                       blockSize,
			                                       Bitmap.Config.ARGB_8888);
		}

		return blockBitmaps;
	}
	
	
	private int mBlockSize;
	/***
	 * Generates block size according to the screen resolution.
	 */
	private void chooseBlockSize(int width, int height) {
		// get the largest block size that will fit the screen
		mBlockSize = Math.min(height / TetrisBoard.DISPLAYED_HEIGHT,
		                      (width*4/7) / TetrisBoard.WIDTH);
		// make it an even number
		mBlockSize &= ~1;
	}

	
	protected class StartActivityAfterAnimation implements Animation.AnimationListener {
        private Intent mIntent;
        
        StartActivityAfterAnimation(Intent intent) {
            mIntent = intent;
        }
            

        public void onAnimationEnd(Animation animation) {
        	mStartButton.setVisibility(View.INVISIBLE);
        	mStartButton.clearAnimation();
        	mOptionsButton.setVisibility(View.INVISIBLE);
        	mOptionsButton.clearAnimation();
        	startActivity(mIntent);   
	        	
        	
        }

        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
            
        }

        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
            
        }
        
    }


	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
