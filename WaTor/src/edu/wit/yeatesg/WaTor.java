package edu.wit.yeatesg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class WaTor extends JPanel
{
	private static final long serialVersionUID = 1777168099445808538L;
	
	private static final int PREF_XY_BOUND = 1000;
	
	private int tileSize;
	
	private int gridlineSize;
	private int numGridlines;
	
	private int preferredLength;
	
	private Map map;
	
	public static void main(String[] args)
	{
		openDisplayGUI(500, 0);
	}
	
	private static void openDisplayGUI(int width, int gridThickness)
	{
		JFrame gameFrame = new JFrame();

		WaTor gamePanel = new WaTor(width, gridThickness);
		gamePanel.setPreferredSize(new Dimension(gamePanel.preferredLength, gamePanel.preferredLength));
		gameFrame.getContentPane().add(gamePanel);
		gameFrame.pack();
		gameFrame.getContentPane().setBackground(Color.LIGHT_GRAY);
		gameFrame.setResizable(false);
		gameFrame.setVisible(true);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.add(gamePanel);		
	}
	
	/**
	 * Create the panel.
	 */
	public WaTor(int numTiles, int gridlineSize)
	{
		setSpatialFields(numTiles, gridlineSize);
		
		map = new Map(numTiles, this);
		map.randomFill(0.95, 0.05);
		
		timer.setInitialDelay(0);
		timer.start();
	}
	
	class Clock implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			tick();
		}
	}
	
	private int delay = 10;
	private Timer timer = new Timer(delay, new Clock());
	
	private int tickNum = 0;
	
	public void tick()
	{
		tickNum++;
		if (tickNum > 250)
		{
			act();
		}
	}
	
	private int baseSharkEnergy = 5;
	private int fishEnergyWorth = 20;
	private int churonsTillSharkReproduce = 5;
	private int churonsTillFishReproduce = 50;
	
	public void act()
	{
		map.nextChronon();
	}
	
	private void setSpatialFields(int numTiles, int gridlineSize)
	{
		
		this.numGridlines = numTiles + 1;
		this.gridlineSize = gridlineSize;
		
		this.tileSize = (PREF_XY_BOUND - numGridlines*gridlineSize) / numTiles;
		
		this.preferredLength = numTiles*tileSize + numGridlines*gridlineSize - 10;		
	}
	
	public int getFishEnergyWorth()
	{
		return fishEnergyWorth;
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(preferredLength, preferredLength);
	}

	public int getChuronsTillFishReproduce()
	{
		return churonsTillFishReproduce;
	}

	public int getChuronsTillSharkReproduce()
	{
		return churonsTillSharkReproduce;
	}

	public int getBaseSharkEnergy()
	{
		return baseSharkEnergy;
	}
	
	private Color gridlineColor = Color.DARK_GRAY;
	private Color fishColor = Color.GREEN;
	private Color sharkColor = Color.BLUE;
	private Color spaceColor = Color.BLACK;
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(gridlineColor);
		g.fillRect(0, 0, preferredLength, preferredLength);

		int yPos = gridlineSize;
		for (int y = 0; y < map.array.length; y++)
		{
			int xPos = gridlineSize;
			for (int x = 0; x < map.array.length; x++)
			{
				EntityType type = EntityType.fromString(map.array[y][x]);

				switch (type)
				{
				case SPACE:
					g.setColor(spaceColor);
					g.fillRect(xPos, yPos, tileSize, tileSize);
					break;
				case SHARK:
					g.setColor(sharkColor);
					g.fillRect(xPos, yPos, tileSize, tileSize);
					break;
				case FISH:
					g.setColor(fishColor);
					g.fillRect(xPos, yPos, tileSize, tileSize);
					break;
				}
				
				xPos += gridlineSize + tileSize;
			}
			
			yPos += gridlineSize + tileSize;

		}
	}
	
	

}