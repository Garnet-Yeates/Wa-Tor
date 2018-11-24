package edu.wit.yeatesg;

import static edu.wit.yeatesg.EntityType.FISH;
import static edu.wit.yeatesg.EntityType.SHARK;
import static edu.wit.yeatesg.EntityType.SPACE;

import java.util.ArrayList;
import java.util.Random;

public class Map
{
	private static final Random R = new Random();

	private WaTor container;

	private int numTiles;

	public String[][] array;

	private String[][] nextArray;
	
	private int maxIndex;
	private int minIndex;
	
	public Map(int numTilesPerSide, WaTor container)
	{
		numTiles = numTilesPerSide;
		this.container = container;
			
		array = new String[numTilesPerSide][numTilesPerSide];
		clear(array);
		maxIndex = array.length - 1;
		minIndex = 0;

		nextArray = new String[numTilesPerSide][numTilesPerSide];	
		clear(nextArray);
	}
	
	public void nextChronon()
	{	
		for (int y = 0; y < array.length; y++)
		{
			for (int x = 0; x < array.length; x++)
			{				
				ArrayPoint loc = new ArrayPoint(y, x);
				String entity = entityAt(loc);
				EntityType type = EntityType.fromString(entity);
				
				switch (type)
				{
				case SHARK:
					doSharkChronon(loc);
					break;
				case FISH:
					doFishChronon(loc);
					break;
				default:
					break;
				}
			}
		}
		
		array = cloneArray(nextArray);
		clear(nextArray);
		container.repaint();

	}
	
	private String[][] cloneArray(String[][] arr)
	{
		String[][] clone = new String[arr.length][arr.length];
		for (int y = 0; y < arr.length; y++)
		{
			for (int x = 0; x < arr[y].length; x++)
			{
				clone[y][x] = arr[y][x];
			}
		}
		return clone;
	}

	private void doFishChronon(ArrayPoint loc)
	{
		boolean hasAdjSpace = hasAdjacentEntity(loc, EntityType.SPACE);

		if (hasAdjSpace)
		{
			ArrayPoint spaceLoc = randomAdjacentEntityLocation(loc, EntityType.SPACE);
			move(loc, spaceLoc);	
		}
		else
		{
			move(loc, loc);
		}
	}

	private void doSharkChronon(ArrayPoint loc)
	{		
		boolean hasAdjFish = hasAdjacentEntity(loc, EntityType.FISH);
		boolean hasAdjSpace = hasAdjacentEntity(loc, EntityType.SPACE);
		
		if (hasAdjFish || hasAdjSpace)
		{
			if (hasAdjFish)
			{
				ArrayPoint fishLoc = randomAdjacentEntityLocation(loc, EntityType.FISH);
				move(loc, fishLoc);
			}
			else
			{
				ArrayPoint spaceLoc = randomAdjacentEntityLocation(loc, EntityType.SPACE);
				move(loc, spaceLoc);
			}
		}
		else
		{
			move(loc, loc);
		}
	}
	
	public void move(ArrayPoint startLoc, ArrayPoint endLoc)
	{
		String entity = entityAt(startLoc);
		EntityType type = EntityType.fromString(entity);
		String s1 = entity.split(",")[0];
		String s2 = (entity.split(",").length > 1) ? entity.split(",")[1] : "-1";
		
		int energy = (type == SHARK) ? Integer.parseInt(s2) : 0;
		int survived = Integer.parseInt(s1.substring(1, s1.length()));
		survived++;
		energy--;
		
		if (startLoc.equals(endLoc)) // Entity didn't move
		{
			if (type == FISH)
			{
				nextArray[startLoc.getY()][startLoc.getX()] = "F" + survived;
				if (survived == container.getChuronsTillFishReproduce())
				{
					nextArray[startLoc.getY()][startLoc.getX()] = "F" + 0;
					reproduce(FISH, startLoc);
				}
			}
			else if (type == SHARK)
			{
				nextArray[startLoc.getY()][startLoc.getX()] = "S" + survived + "," + energy;
				if (energy < 1)
				{
					nextArray[startLoc.getY()][startLoc.getX()] = " ";
				}
				else if (survived == container.getChuronsTillSharkReproduce())
				{
					nextArray[startLoc.getY()][startLoc.getX()] = "S" + 0 + "," + energy;
					reproduce(SHARK, startLoc);
				}
			}
		}
		else // Entity moved
		{
			String entity2 = entityAt(endLoc);
			EntityType type2 = EntityType.fromString(entity2);
			
			if (type2 == EntityType.SPACE)
			{
				if (type == SHARK) // Shark moved to space
				{
					nextArray[endLoc.getY()][endLoc.getX()] = "S" + survived + "," + energy;
					if (energy < 1)
					{
						nextArray[endLoc.getY()][endLoc.getX()] = " ";
					}
					else if (survived == container.getChuronsTillSharkReproduce())
					{
						nextArray[endLoc.getY()][endLoc.getX()] = "S" + 0 + "," + energy;
						reproduce(SHARK, endLoc);
					}
				}
				else if (type == FISH) // Fish moved to space
				{
					nextArray[endLoc.getY()][endLoc.getX()] = "F" + survived;
					if (survived == container.getChuronsTillFishReproduce())
					{
						nextArray[endLoc.getY()][endLoc.getX()] = "F" + 0;
						reproduce(FISH, endLoc);
					}
				}
			}
			
			if (type == SHARK && type2 == FISH) // Shark moved to fish
			{
				energy += container.getFishEnergyWorth();
				
				nextArray[endLoc.getY()][endLoc.getX()] = "S" + survived + "," + energy;				
				if (energy < 1)
				{
					nextArray[endLoc.getY()][endLoc.getX()] = " ";
				}
				else if (survived == container.getChuronsTillSharkReproduce())
				{
					nextArray[endLoc.getY()][endLoc.getX()] = "S" + 0 + "," + energy;
					reproduce(SHARK, endLoc);
				}
			}
		}
	}
	
	private void reproduce(EntityType type, ArrayPoint startLoc)
	{
		if (type != SPACE)
		{
			if (hasAdjacentEntity(startLoc, SPACE))
			{
				ArrayPoint endLoc = randomAdjacentEntityLocation(startLoc, SPACE);

				String entity = nextArray[startLoc.getY()][startLoc.getX()];
				System.out.println(entity);
				String s1 = entity.split(",")[0];
				String s2 = (entity.split(",").length > 1) ? entity.split(",")[1] : "-1";
				
				int energy = (type == SHARK) ? Integer.parseInt(s2) : 0;
				int survived = Integer.parseInt(s1.substring(1, s1.length()));
				survived = 0;
				
				if (type == SHARK)
				{
					nextArray[endLoc.getY()][endLoc.getX()] = "S" + survived + "," + energy;
					nextArray[startLoc.getY()][startLoc.getX()] = "S" + 0 + "," + container.getBaseSharkEnergy();
				}
				else
				{
					nextArray[endLoc.getY()][endLoc.getX()] = "F" + survived;
					nextArray[startLoc.getY()][startLoc.getX()] = "F" + 0;	
				}
			}
		}
	}

	public void randomFill(double fishWeight, double sharkWeight)
	{
		System.out.println("Random Filling");

		assert (sharkWeight + fishWeight == 1);

		clear(array);
		System.out.println("cleared");

		int maxCells = (int) (numTiles * numTiles * 0.45);
		int minCells = (int) (numTiles * numTiles * 0.25);

		int totalEntitiesToAdd = R.nextInt(maxCells - minCells) + minCells;
		int numSharksToAdd = (int) (totalEntitiesToAdd * sharkWeight);
		int numFishToAdd = (int) (totalEntitiesToAdd * fishWeight);
		
		spawnMultiple(SHARK, numSharksToAdd);
		spawnMultiple(FISH, numFishToAdd);

		System.out.println("Random Filled");
		container.repaint();
	}
	
	public void spawnMultiple(EntityType type, int amount)
	{
		ArrayList<ArrayPoint> emptyTileLocs = new ArrayList<ArrayPoint>();
		for (int y = 0; y < array.length; y++)
		{
			for (int x = 0; x < array.length; x++)
			{
				if (array[y][x].equalsIgnoreCase(" "))
				{
					emptyTileLocs.add(new ArrayPoint(y, x));
				}
			}
		}
		
		if (!emptyTileLocs.isEmpty())
		{	
			if (amount > emptyTileLocs.size())
			{
				amount = emptyTileLocs.size();
			}

			for (int i = 0; i < amount; i++)
			{
				i: while (true)
				{
					int randIndex = R.nextInt(emptyTileLocs.size());

					ArrayPoint space = emptyTileLocs.get(randIndex);
					if (type == SHARK)
					{
						array[space.getY()][space.getX()] = type.asString + "0," + container.getBaseSharkEnergy();
					}
					else if (type == FISH)
					{
						array[space.getY()][space.getX()] = type.asString + "0" + ",X";
					}
					
					break i;
				}
			}
		}
	}

	public void spawn(EntityType type)
	{
		if (hasEmptyTiles())
		{
			assert (type == FISH || type == SHARK);
			
			ArrayPoint space = findEmptyTile();
		
			if (type == SHARK)
			{
				array[space.getY()][space.getX()] = type.asString + "0," + container.getBaseSharkEnergy();
			}
			else if (type == FISH)
			{
				array[space.getY()][space.getX()] = type.asString + "0";
			}
		}
	}
	
	public void spawn(ArrayPoint p, EntityType type)
	{
		if (type == SHARK)
		{
			array[p.getY()][p.getX()] = type.asString + "0," + container.getBaseSharkEnergy();
		}
		else if (type == FISH)
		{
			array[p.getY()][p.getX()] = type.asString + "0";
		}	
	}
	
	public boolean hasEmptyTiles()
	{
		return findEmptyTile() != null ? true : false;
	}

	public ArrayPoint findEmptyTile()
	{
		ArrayList<ArrayPoint> emptyTileLocs = new ArrayList<ArrayPoint>();
		for (int y = 0; y < array.length; y++)
		{
			for (int x = 0; x < array.length; x++)
			{
				if (array[y][x].equalsIgnoreCase(" "))
				{
					emptyTileLocs.add(new ArrayPoint(y, x));
				}
			}
		}
		
		if (!emptyTileLocs.isEmpty())
		{
			return (emptyTileLocs.get(R.nextInt(emptyTileLocs.size())));
		}
		else
		{
			return null;
		}
	}

	public void clear(String[][] clearing)
	{
		for (int y = 0; y < clearing.length; y++)
		{
			for (int x = 0; x < clearing.length; x++)
			{
				clearing[y][x] = " ";
			}
		}
		container.repaint();
	}

	public String entityAt(ArrayPoint p)
	{
		return array[p.getY()][p.getX()];
	}
	
	public ArrayList<ArrayPoint> adjacentEntityList(ArrayPoint adjacentTo)
	{
		int x = adjacentTo.getX();
		int y = adjacentTo.getY();

		ArrayList<ArrayPoint> nearby = new ArrayList<ArrayPoint>();

		if (x < maxIndex)
		{
			nearby.add(new ArrayPoint(y, x + 1));
		}
		if (x > minIndex)
		{
			nearby.add(new ArrayPoint(y, x - 1));
		}
		if (y < maxIndex)
		{
			nearby.add(new ArrayPoint(y + 1, x));
		}
		if (y > minIndex)
		{
			nearby.add(new ArrayPoint(y - 1, x));
		}
		
		return nearby;
	}
	
	public boolean hasAdjacentEntity(ArrayPoint adjacentTo, EntityType type)
	{
		if (adjacentEntityList(adjacentTo, type).size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public ArrayList<ArrayPoint> adjacentEntityList(ArrayPoint adjacentTo, EntityType lookingFor)
	{
		ArrayList<ArrayPoint> nearby = new ArrayList<ArrayPoint>();

		for (ArrayPoint adj : adjacentEntityList(adjacentTo))
		{
			String entityFound = entityAt(adj);
			EntityType foundType = EntityType.fromString(entityFound);
			if (foundType == lookingFor)
			{
				nearby.add(adj);
			}
		}

		return nearby;
	}
	
	public ArrayPoint randomAdjacentEntityLocation(ArrayPoint adjacentTo, EntityType lookingFor)
	{
		ArrayList<ArrayPoint> adjList = adjacentEntityList(adjacentTo, lookingFor);
		if (!adjList.isEmpty())
		{
			return (adjList.get(R.nextInt(adjList.size())));	
		}
		else
		{
			return null;
		}
	}

	public void print()
	{
		for (int y = 0; y < array.length; y++)
		{
			for (int x = 0; x < array.length; x++)
			{	
				if (array[y][x].contains("S"))
				{
					System.out.print("S ");
//					System.out.print(array[y][x] + " ");
				}
				else if (array[y][x].contains("F"))
				{
					System.out.print("F ");
//					System.out.print(array[y][x] + ",0 ");
				}
				else
				{
					System.out.print("+ ");
				}
			}
			System.out.println();
		}
		
		System.out.println("\n\n");
	}

}