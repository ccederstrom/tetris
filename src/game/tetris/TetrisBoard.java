package game.tetris;
import java.util.Random;

public class TetrisBoard {
	// width and height of board
	public static final int WIDTH = 10;
	public static final int HEIGHT = 24;
	public static final int DISPLAYED_HEIGHT = 20;

	private int mBoard[][] = new int[HEIGHT][WIDTH];

	/***
	 * Default constructor, creates a 2D array of all 0's
	 */
	public TetrisBoard() {
		for (int i = 0; i < HEIGHT; i++)
			for (int j = 0; j < WIDTH; j++)
				mBoard[i][j] = 0;
	}
	
	/***
	 * 
	 * @param tempBoard - String
	 */
	public TetrisBoard(String tempBoard){
		int j = 0;
		for( int i = 0 ; i < HEIGHT; i++)
			for( int x = 0 ; x < WIDTH ; x++,j++)
				mBoard[i][x] = Character.getNumericValue(tempBoard.charAt(j));
	}

	/***
	 * Returns Integer value of the x,y coordinate. The returned value 
	 * determines empty space, color of block 
	 * @param x - Integer: Coordinate for the X value
	 * @param y - Integer: Coordinate for the Y value.
	 * @return int - 0 represents empty space, 1 represents... need to find out correct colors
	 */
	public int getBlock(int x, int y) {
		return mBoard[y][x];
	}

	
	/***
	 * returns true if the piece is on top of any blocks in the board
	 * or the piece is out of bounds
	 * @param Coord[]
	 * @return boolean
	 */
	public boolean checkCollision(Coord[] coords) {
		for (int i = 0; i < 4; i++)
			if (coords[i].x < 0 || coords[i].x >= TetrisBoard.WIDTH
			 || coords[i].y < 0 || coords[i].y >= TetrisBoard.HEIGHT
			 || mBoard[coords[i].y][coords[i].x] != 0)
				return true;
		return false;
	}

	 
	/***
	 * merge a piece into the board. Coord[] represents the 4 coordinates 
	 * of an entire piece
	 * @param shapeId - int
	 * @param coords - Coord[]
	 */
	public void mergePiece(int shapeId, Coord[] coords) {
		if(TetrisThread.mSoundEffect)TetrisThread.mSoundPool.play(TetrisThread.mSoundId, 1, 1, 0, 0, 1);
		for (int i = 0; i < 4; i++)
			mBoard[coords[i].y][coords[i].x] = shapeId;
	}

	/***
	 * check if any lines are filled up and if so delete them
	 * @param filledLines - boolean[]
	 * @return int
	 */
	public int checkFilledLines(boolean[] filledLines) {
		int lines = 0; // number of filled lines

		// loop through all the lines
		for (int i = 0; i < HEIGHT; i++) {
			boolean emptyBlock = false;

			// check if there are any empty blocks in a line
			for (int j = 0; j < WIDTH; j++)
				if (mBoard[i][j] == 0) {
					emptyBlock = true;
					break;
				}

			// mark the line as filled or unfilled
			if (emptyBlock)
				filledLines[i] = false;
			else {
				lines++;
				filledLines[i] = true;
			}
		}

		return lines;
	}

	/***
	 * 
	 * @param lines - boolean[]
	 */
	public void deleteLines(boolean[] lines) {
		for (int i = HEIGHT - 1; i >= 0; i--)
			if (lines[i])
				deleteLine(i);
	}

	/***
	 * remove a line from the board
	 * @param line
	 */
	private void deleteLine(int line) {
		// shift down all the lines above the one to be deleted
		for (int i = line; i < HEIGHT - 1; i++) {
			for (int j = 0; j < WIDTH; j++)
				mBoard[i][j] = mBoard[i+1][j];
		}
		// clear the top line
		for (int j = 0; j < WIDTH; j++)
			mBoard[HEIGHT - 1][j] = 0;
	}

	/***
	 * Used to randomly generate filled rows and its density
	 * @param numRows - int
	 * @param rowDensity - int
	 */
	public void fillRows(int numRows, int rowDensity) {
		Random random = new Random();

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < rowDensity; j++) {
				int r = random.nextInt(WIDTH - j);
				int col = 0;
				int k = 0;
				for (;;) {
					if (mBoard[i][col] == 0) {
						if (k == r)
							break;
						else
							k++;
					}
					col++;
				}
				mBoard[i][col] = random.nextInt(7) + 1;
			}
		}
	}

	/***
	 * returns a string with board settings to caller in order to save 
	 * @return String
	 */
	public String getBoardForSave() {
		StringBuilder tempString = new StringBuilder(HEIGHT*WIDTH+1);
		
		for( int i = 0 ; i < HEIGHT; i++)
			for( int x = 0 ; x < WIDTH ; x++)
				tempString.append(mBoard[i][x]);
		
		return tempString.toString();
	}
}
