package game.tetris;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import android.graphics.Color;

public class TetrisPiece {
	private static final int[][] lightColorsRGB = {
		//{0,   255, 255}, // cyan
		{4,   206, 220},  // cyan
		{0,     0, 255}, // blue
		{255, 213,   4},// orange
		{255, 255,   0}, // yellow
		{92,   223,   59}, // green
		{232,   120, 192}, // purple
		{255,   104,   65}  // red
	};
	private static final int[][] mediumColorsRGB = new int[7][3];
	private static final int[][] darkColorsRGB = new int[7][3];

	public static final int[] lightColors = new int[7];
	public static final int[] mediumColors = new int[7];
	public static final int[] darkColors = new int[7];

	private static int[] shapes = {
		 0,1, 0,0, 0,-1,  0,-2, // I shape
		 1,0, 0,0, -1,0,  -2,0,
		0,-1, 0,0,  0,1,   0,2,
		-1,0, 0,0,  1,0,   2,0,

		0,0,  0,1, 0,-1, -1,-1, // J shape
		0,0,  1,0, -1,0,  -1,1,
		0,0, 0,-1,  0,1,   1,1,
		0,0, -1,0,  1,0,  1,-1,

		0,0, 0,-1,  1,-1,  0,1, // L shape
		0,0, -1,0, -1,-1,  1,0,
		0,0, -1,1,   0,1, 0,-1,
		0,0, -1,0,   1,0,  1,1,

		0,0, -1,0, 0,-1, -1,-1, // O shape
		0,0, -1,0, 0,-1, -1,-1,
		0,0, -1,0, 0,-1, -1,-1,
		0,0, -1,0, 0,-1, -1,-1,

		0,0,  1,0, 0,-1, -1,-1, // S shape
		0,0, 0,-1, -1,0,  -1,1,
		0,0, -1,0,  0,1,   1,1,
		0,0,  0,1,  1,0,  1,-1,

		0,0,  1,0, -1,0,  0,-1, // T shape
		0,0,  0,1, 0,-1,  -1,0,
		0,0,  0,1, -1,0,   1,0,
		0,0,  1,0, 0,-1,   0,1,

		0,0, -1,0, 0,-1,  1,-1, // Z shape
		0,0,  0,1, -1,0, -1,-1,
		0,0,  1,0,  0,1,  -1,1,
		0,0, 0,-1,  1,0,   1,1
	};

	//private Random mRandom = new Random();
	private int mShapeId;
	private int mRotation;
	

	// initialize the colors used in the block bitmaps
	static {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 3; j++) {
				mediumColorsRGB[i][j] = (lightColorsRGB[i][j] * 8) / 10;
				darkColorsRGB[i][j] = (lightColorsRGB[i][j] * 5) / 10;
			}

			lightColors[i] = Color.rgb(lightColorsRGB[i][0],
			                           lightColorsRGB[i][1],
			                           lightColorsRGB[i][2]);
			mediumColors[i] = Color.rgb(mediumColorsRGB[i][0],
			                            mediumColorsRGB[i][1],
			                            mediumColorsRGB[i][2]);
			darkColors[i] = Color.rgb(darkColorsRGB[i][0],
			                          darkColorsRGB[i][1],
			                          darkColorsRGB[i][2]);
		}
	}
	

	/***
	 * Sets piece ID and Rotation
	 * @param shapeID
	 * @param shapeRotation
	 */
	public void set(String shapeID, String shapeRotation) {
		mShapeId  = Integer.parseInt(shapeID);
		mRotation =  Integer.parseInt(shapeRotation);
	}
	
	/***
	 * Returns an int of the corresponding ID.
	 * @return int
	 */
	public int getShapeId() {
		return mShapeId;
	}

	/***
	 * 
	 * @param rotation
	 */
	public void setRotation(int rotation){
		mRotation = rotation;
	}

	// randomizes the the shape and rotation of the piece (for when a new
	// piece is spawned)
	public void randomize() {
		Random rand;

		try {
			rand = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			rand = new Random();
		} 

		mShapeId = rand.nextInt(7) + 1;
		mRotation = rand.nextInt(4);
	}

	// returns the coordinates the piece occupies given the position of the
	// center of the piece
	public Coord[] getCoords(Coord pos) {
		int shapeIndex = ((mShapeId - 1) * 32) + (mRotation * 8);
		Coord[] coords = new Coord[4];

		for (int i = 0; i < 4; i++)
			coords[i] = new Coord(pos.x + shapes[shapeIndex + (2 * i)],
			                      pos.y + shapes[shapeIndex + (2 * i) + 1]);
		return coords;
	}

	public void rotateClockwise() {
		if (mRotation == 3)
			mRotation = 0;
		else
			mRotation++;
	}

	public void rotateCounterclockwise() {
		if (mRotation == 0)
			mRotation = 3;
		else
			mRotation--;
	}
	
	//returns string with  mShapeId mRotation  for saving
	public int getPieceIdForSave(){
		return mShapeId;
	}
	
	public int getPieceRotationForSave(){
		return mRotation;
	}
}
