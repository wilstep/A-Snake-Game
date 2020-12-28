package snake;


/**
 *	Class to provide the data and rudimentary business logic that particular to
 *  which level and cycle the game is in. 
 */
public class GameState 
{
	private boolean isInGame;
	private boolean isGameCompleted = false;
	private int level = 0;
	private int cycle = 0;
	private int lives = 5;
	private int eats = 0;
	private static final int MAX_CYCLES = 2;
	private static final int[] numOfLevels = {2, 3};
	private static final int[] requiredEats = {5, 7};
	private static final int[] fullSnakeLength = {4, 5};

	
	/**
	 * default constructor
	 */
	public GameState() 
	{
		
	}
	
	
	/**
	 * To be called prior to first life being put in game,
	 * and then after each death
	 */
	public void useALife()
	{
		--lives;
	}
	
	
	/**
	 * @return true if the game is still alive
	 */
	public boolean isAlive()
	{
		return lives > 0;
	}
	
	
	/**
	 * @return true if game play is active
	 */
	public boolean isInGame()
	{
		return isInGame;
	}
	
	
	/**
	 * @return The number of apples which must be eaten
	 * to complete this level
	 */
	public int getRequiredEats()
	{
		return requiredEats[cycle];
	}
	
	
	/**
	 * @return The maximum snake length for this cycle
	 */
	public int getFullSnakeLength()
	{
		return fullSnakeLength[cycle];
	}
	
	
	/**
	 * @return number of levels in current cycle
	 */
	public int getNumOfLevels()
	{
		return numOfLevels[cycle];
	}
	
	
	public int getLevel()
	{
		return level + 1;
	}
	
	
	public int getCycle()
	{
		return cycle + 1;
	}
	
	public int getEats()
	{
		return eats;
	}
	
	
	public void eat()
	{
		++eats;
	}
	
	
	public void snakeDied()
	{
		--lives;
		isInGame = false;
	}
	
	public int getMaxCycles()
	{
		return MAX_CYCLES;
	}
	
	
	public boolean isGameCompleted()
	{
		return isGameCompleted;
	}
	
	
	public void levelUp()
	{
		++level;
		if(level == numOfLevels[cycle]) 
		{
			++cycle;
			level = 0;
		}
		if(cycle == MAX_CYCLES)
		{
			isInGame = false;
			isGameCompleted = true;
			level = numOfLevels[--cycle] - 1;
		}
	}
	
	
	public boolean isLevelComplete()
	{
		return eats == requiredEats[cycle];
	}
	
	
	public void putInGame()
	{
		eats = 0;
		isInGame = true;
	}
}
