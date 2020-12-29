package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final ActionListener actionListener;
	private final Runnable disposeRunnable;
	
	private static final int DOT_SIZE = 10;
	private static final int SCORE_HEIGHT = 18;
	private static final int BOARD_WIDTH = 40 * DOT_SIZE;
	private static final int BOARD_HEIGHT = 40 * DOT_SIZE;
	private static final int RAND_POS_X = BOARD_WIDTH / DOT_SIZE - 1;
	private static final int RAND_POS_Y = BOARD_HEIGHT / DOT_SIZE - 1;
	private static final int DELAY = 140;
	
	private final Timer timer;
	private final Image head;
	private final Image apple;
	private final Image dot;
	
	private Fifo fifo;
	private final GameState gameState = new GameState();
	
	private Point applePos;
	
	private Direction direction;

	
	public Board(Runnable disposeRunnable)
	{
		actionListener = constructActionListener();
		this.disposeRunnable = disposeRunnable;
		addKeyListener(constructKeyAdapter());
		setBackground(Color.black);
		setFocusable(true);
		
		setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT + SCORE_HEIGHT));
		
		ImageIcon iih = new ImageIcon("resources/head.png");
		head = iih.getImage();
		ImageIcon iid = new ImageIcon("resources/dot.png");
		dot = iid.getImage();
		ImageIcon iia = new ImageIcon("resources/apple.png");
		apple = iia.getImage();
		
		timer = new Timer(DELAY, actionListener);
		timer.start();
	}
	
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		doDrawing(g);
	}
	
	
	void doDrawing(Graphics g)
	{
		if(gameState.isGameCompleted())
		{
			gameCompletedScreen(g);
		}
		else if(gameState.isInGame()) 
		{	
			Iterator<Point> iter = fifo.getIterator();
			Point p = iter.next();
			drawOnBoard(g, applePos, apple);
			drawOnBoard(g, p, head);
			while(iter.hasNext())
			{
				p = iter.next();
				drawOnBoard(g, p, dot);
			}
		}
		else 
		{ 
			if(gameState.isGameOver()) gameOverScreen(g);
			else 
			{
				gameStartScreen(g);
			}
		}
		g.setColor(Color.white);
		g.fillRect(0, 0, BOARD_WIDTH, SCORE_HEIGHT);
		String msg = "Length " + gameState.getSnakeLength() + "/" + gameState.getFullSnakeLength() + ", Eats " + gameState.getEats() 
				+ "/" + gameState.getRequiredEats() + ", Level " + gameState.getLevel() + "/" + gameState.getNumOfLevels() 
				+ ", Cycle " + gameState.getCycle() + "/" + gameState.getMaxCycles();
		g.setColor(Color.black);
		Font font = new Font("Hellvetica", Font.BOLD, 14);
		g.setFont(font);
		g.drawString(msg, 2, 14);
	}
	
	
	void drawOnBoard(Graphics g, Point p, Image image)
	{
		g.drawImage(image, p.getX(), p.getY() + SCORE_HEIGHT, this);
	}
	
	
	void gameOverScreen(Graphics g)
	{
		String msg = "Game Over";
		gameMessage(msg, g);
	}
	
	
	void gameCompletedScreen(Graphics g)
	{		
		String msg = "Congratulations: Game Completed";
		gameMessage(msg, g);
	}
	
	
	void gameStartScreen(Graphics g)
	{
		String msg = "Press spacebar to start";
		gameMessage(msg, g);
	}
	
	
	void gameMessage(String message, Graphics g)
	{
		Font font = new Font("Hellvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(font);
		
		g.setColor(Color.white);
		g.setFont(font);
		g.drawString(message, (BOARD_WIDTH - metr.stringWidth(message)) / 2, BOARD_HEIGHT / 2);		
	}
	
	
	Point newAppleLocation()
	{
		int appleX = ((int) (Math.random() * RAND_POS_X)) * DOT_SIZE;
		int appleY = ((int) (Math.random() * RAND_POS_Y)) * DOT_SIZE;
		Point apple = new Point(appleX, appleY);
		
		if(fifo.isInList(apple)) return newAppleLocation();
		return apple;
	}
	
	
	void putInGame()
	{
		fifo = new Fifo(gameState.getFullSnakeLength());
		fifo.add(newHeadLocation());
		applePos = newAppleLocation();
		direction = Direction.RIGHT;
		gameState.putInGame();
	}
	
	Point newHeadLocation()
	{
		int headX = 2 * DOT_SIZE;
		int headY = 2 * DOT_SIZE;
		
		return new Point(headX, headY);
	}
	
	
	/**
	 * Called once every time a snake dies
	 */
	void snakeDied()
	{
		gameState.snakeDied();
	}
	
	
	KeyAdapter constructKeyAdapter()
	{
		return new KeyAdapter() 
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				int key = e.getKeyCode();
				
				if((key == KeyEvent.VK_LEFT) && !direction.isRight()) direction = Direction.LEFT;
				if((key == KeyEvent.VK_RIGHT) && !direction.isLeft()) direction = Direction.RIGHT;
				if((key == KeyEvent.VK_UP) && !direction.isDown()) direction = Direction.UP;
				if((key == KeyEvent.VK_DOWN) && !direction.isUp()) direction = Direction.DOWN;
				if((key == KeyEvent.VK_ESCAPE)) disposeRunnable.run();
				if((key == KeyEvent.VK_SPACE))
				{
					if(!gameState.isInGame())
					{
						putInGame();
					}
				}
			}
		};
	}
	
	
	ActionListener constructActionListener()
	{
		return e -> 
		{
			if(gameState.isInGame())
			{
				int headX = fifo.getFirst().getX();
				int headY = fifo.getFirst().getY();
				switch(direction)
				{
					case RIGHT:
						headX += DOT_SIZE;
						break;
					case LEFT:
						headX -= DOT_SIZE;
						break;
					case UP:
						headY -= DOT_SIZE;
						break;
					case DOWN:
						headY += DOT_SIZE;
						break;
				}
				var point = new Point(headX, headY);
				if(point.equals(applePos)) 
				{
					if(gameState.isGameOneOffComplete())
					{
						gameState.eat();
						gameState.levelUp();
					}
					else fifo.grow();
				}
				if(!gameState.isGameCompleted())
				{
					boolean isCollision = fifo.add(point);
					if(fifo.getFirst().equals(applePos)) 
					{
						applePos = newAppleLocation();
						gameState.eat();
						if(gameState.isLevelComplete())
						{
							gameState.levelUp();
						}
					}
					else if(headX >= BOARD_WIDTH || headX < 0 || headY >= BOARD_HEIGHT || headY < 0 || isCollision)
					{
						snakeDied();
					}
				}
				repaint();
			}
			else // not in game
			{
				if(!gameState.isGameCompleted())
				{
					
				}
			}
					
		};
	}
}
