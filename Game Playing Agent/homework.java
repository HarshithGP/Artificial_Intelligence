import java.io.*;
import java.util.*;


public class homework {
	public static BufferedWriter outputWriter;
	public static void main(String[] args)
	{
		try
		{		
			long startTime = System.currentTimeMillis();
			int[] skip = new int[]{449, 821, 1371, 2767, 5000};
			int y = 0;
			for (int x = 2886; x < 2887; x++)
			{
				if (x == skip[y])
				{
					y++;
					continue;
				}
			System.out.println("Test: " + String.valueOf(x+1));
//			String inputfile = String.format("./newTestCases/input%1$d.txt",x);
//			String outputfile = String.format("./myNewOutputs/output%1$d.txt",x);
				String inputfile = String.format("./INPUT/%1$d.in",x);
				String outputfile = String.format("./myOUTPUT/%1$d.out",x);
//			String inputfile = String.format("./testcases/%1$d.in",x);
//			String outputfile = String.format("./myOutputs/%1$d.out",x);
//			String inputfile = "input.txt";
//			String outputfile = "output.txt";

					//BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
					BufferedReader inputReader = new BufferedReader(new FileReader(inputfile));
					outputWriter = new BufferedWriter(new FileWriter(outputfile));
//			BufferedReader inputReader = new BufferedReader(new FileReader("input.txt"));
//			BufferedWriter outputWriter = new BufferedWriter(new FileWriter("output.txt"));

			String line = new String();

			Integer boardSize = Integer.parseInt(inputReader.readLine());
			String mode = inputReader.readLine();
			String myPlay = inputReader.readLine();
			String oppPlay = "X";
			if(myPlay.equals("X"))
				oppPlay = "O";
			Integer depth = Integer.parseInt(inputReader.readLine());
			
			int[][] boardValues = new int[boardSize][boardSize];
			int[][] boardPositions = new int[boardSize][boardSize];
			
			for(int i = 0; i < boardSize; i++)
			{
				line = inputReader.readLine();
				String[] words = line.split(" ");
				
				for(int j = 0; j < words.length; j++)
				{
					boardValues[i][j] = Integer.parseInt(words[j]);
				}
			}
			
			
			for(int i = 0; i < boardSize; i++)
			{
				line = inputReader.readLine();
								
				for(int j = 0; j < line.length(); j++)
				{
					int value = 0;
					if(line.charAt(j) == myPlay.charAt(0))
						value = 1;
					else if (line.charAt(j) == oppPlay.charAt(0))
						value = -1;
					boardPositions[i][j] = value;
				}
					
					//boardPositions[i][j] = Character.getNumericValue(line.charAt(j));
			}
					
			
			//String moveMade = MinimaxDecision(boardValues, boardPositions, 1, depth);	
			String moveMade = new String();
			switch(mode)
			{
				case "MINIMAX" : moveMade = MinimaxDecision(boardValues, boardPositions, 1, depth);
								break;
				case "ALPHABETA" : moveMade = AlphaBetaDecision(boardValues, boardPositions, 1, depth);
								break;
				default:					
			}
			//String moveMade = AlphaBetaDecision(boardValues, boardPositions, 1, depth);	
			String newboard = MakeMove(boardPositions, moveMade, myPlay, oppPlay);
			//System.out.println(newboard);
			
			outputWriter.write(moveMade);
			outputWriter.newLine();
			
			outputWriter.write(newboard);
			outputWriter.newLine();
			
			inputReader.close();
			outputWriter.close();
			
		}
			long endTime = System.currentTimeMillis();
			
			System.out.println("Execution Time = " + String.valueOf(endTime - startTime) + "ms");
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static boolean IsBoardComplete(int[][] boardPositions)
	{
		for(int i = 0; i < boardPositions.length; i++)
		{
			for(int j = 0; j < boardPositions.length; j++)
			{
				if(boardPositions[i][j] == 0)
					return false;
			}
		}
		return true;
	}
	
	public static String MinimaxDecision(int[][] boardValues, int[][] boardPositions, int player, int depth)
	{
		//PrintBoard(boardValues, boardPositions);
		String Decision = MaxValue(boardValues, boardPositions, player, depth);
		String[] Move = Decision.split(" ");
		
		int posX = 1 + Integer.parseInt(Move[1]);
		System.out.println(Decision);
		char posY = (char)((int)'A' + Integer.parseInt(Move[2]));
		
		//System.out.println(String.format("%c%d %s",posY,posX, Move[3]));
		return String.format("%c%d %s",posY,posX, Move[3]);
	}
	
	public static String AlphaBetaDecision(int[][] boardValues, int[][] boardPositions, int player, int depth)
	{
		//System.out.println("ALPHABETA");
		//PrintBoard(boardValues, boardPositions);
		String Decision = MaxValue(boardValues, boardPositions, player, depth, Integer.MIN_VALUE,Integer.MAX_VALUE);
		String[] Move = Decision.split(" ");
		
		int posX = 1 + Integer.parseInt(Move[1]);
		//System.out.println(Decision);
		char posY = (char)((int)'A' + Integer.parseInt(Move[2]));
		
		//System.out.println(String.format("%c%d %s",posY,posX, Move[3]));
		return String.format("%c%d %s",posY,posX, Move[3]);
	}	
	
	public static String MaxValue(int[][] boardValues, int[][] boardPositions, int player, int depth)
	{
		//System.out.println("MAX");
		if(depth==0)//write the other terminal test when no more moves can me made
			return EvaluateBoard(boardValues, boardPositions, player);
		if(IsBoardComplete(boardPositions))
			return EvaluateBoard(boardValues, boardPositions, player);
		String newValue = new String();
		//Stake
		String move = "Stake";
		//System.out.println("Stake");
		Integer maxValue = Integer.MIN_VALUE, pos_x = -1, pos_y = -1;
		for(int i = 0; i < boardPositions.length;i++)
		{
			for (int j = 0; j < boardPositions.length; j++) 
			{
				//System.out.println(String.format("MAX: i = %d j = %d", i, j));
				if(boardPositions[i][j] == 0)
				{
					boardPositions[i][j] = player;//Need to take care of handling Stake and Raid
					newValue = MinValue(boardValues, boardPositions, -1 * player, depth - 1);
					if(Integer.parseInt(newValue.split(" ")[0]) > maxValue)
					{
						maxValue = Integer.parseInt(newValue.split(" ")[0]);
						//System.out.println(String.format("New MAX: %d", maxValue));
						pos_x = i;
						pos_y = j;
					}
					
					boardPositions[i][j] = 0;
				}
			}
		}
		
		//Raid
		//System.out.println("Raid");
		for(int i = 0; i < boardPositions.length;i++)
		{
			for (int j = 0; j < boardPositions.length; j++) 
			{
				//System.out.println(String.format("MAX: i = %d j = %d", i, j));
				if(boardPositions[i][j] == 0)
				{
					if(CanRaid(boardPositions, i, j, player))
					{
						
						int[][] raidedBoard = DoRaid(boardPositions, i, j, player);
						newValue = MinValue(boardValues, raidedBoard, -1 * player, depth - 1);
						if(Integer.parseInt(newValue.split(" ")[0]) > maxValue)
						{
							move = "Raid";
							maxValue = Integer.parseInt(newValue.split(" ")[0]);
							//System.out.println(String.format("New MAX: %d", maxValue));
							pos_x = i;
							pos_y = j;
						}
					}
				}
			}
		}
		return String.format("%d %d %d %s", maxValue, pos_x, pos_y, move);
	}
			
	public static String MinValue(int[][] boardValues, int[][] boardPositions, int player, int depth)
	{
		//System.out.println("MIN");
		if(depth==0)
			return EvaluateBoard(boardValues, boardPositions, player);
		if(IsBoardComplete(boardPositions))
			return EvaluateBoard(boardValues, boardPositions, player);
		String newValue = new String();
		//Stake
		String move = "Stake";
		Integer minValue = Integer.MAX_VALUE, pos_x = -1, pos_y = -1;
		for(int i = 0; i < boardPositions.length;i++)
		{
			for (int j = 0; j < boardPositions.length; j++) 
			{
				//System.out.println(String.format("MIN: i = %d j = %d", i, j));
				if(boardPositions[i][j] == 0)
				{
					boardPositions[i][j] = player;//Need to take care of handling Stake and Raid
					newValue = MaxValue(boardValues, boardPositions, -1 * player, depth - 1);
					
					if(Integer.parseInt(newValue.split(" ")[0]) < minValue)
					{						
						minValue = Integer.parseInt(newValue.split(" ")[0]);
						//System.out.println(String.format("New MIN: %d", minValue));
						pos_x = i;
						pos_y = j;
					}
					
					boardPositions[i][j] = 0;						
				}
			}
		}
		
		//Raid
		for(int i = 0; i < boardPositions.length;i++)
		{
			for (int j = 0; j < boardPositions.length; j++) 
			{
				//System.out.println(String.format("MAX: i = %d j = %d", i, j));
				if(boardPositions[i][j] == 0)
				{
					if(CanRaid(boardPositions, i, j, player))
					{
						int[][] raidedBoard = DoRaid(boardPositions, i, j, player);
						newValue = MaxValue(boardValues, raidedBoard, -1 * player, depth - 1);
						if(Integer.parseInt(newValue.split(" ")[0]) < minValue)
						{
							move = "Raid";
							minValue = Integer.parseInt(newValue.split(" ")[0]);
							//System.out.println(String.format("New MAX: %d", minValue));
							pos_x = i;
							pos_y = j;
						}
					}
				}
			}
		}
				
		return String.format("%d %d %d %s", minValue, pos_x, pos_y, move);
	}
	
	public static String MaxValue(int[][] boardValues, int[][] boardPositions, int player, int depth, int Alpha, int Beta)
	{
		//System.out.println("MAX");
		if(depth==0)//write the other terminal test when no more moves can me made
			return EvaluateBoard(boardValues, boardPositions, player);
		if(IsBoardComplete(boardPositions))
			return EvaluateBoard(boardValues, boardPositions, player);
		
		String newValue = new String();
		//Stake
		String move = "Stake";
		//System.out.println("Stake");
		Integer maxValue = Integer.MIN_VALUE, pos_x = -1, pos_y = -1;
		for(int i = 0; i < boardPositions.length;i++)
		{
			for (int j = 0; j < boardPositions.length; j++) 
			{
				//System.out.println(String.format("MAX: i = %d j = %d", i, j));
				if(boardPositions[i][j] == 0)
				{
					boardPositions[i][j] = player;//Need to take care of handling Stake and Raid
					newValue = MinValue(boardValues, boardPositions, -1 * player, depth - 1, Alpha, Beta);
					if(Integer.parseInt(newValue.split(" ")[0]) > maxValue)
					{
						maxValue = Integer.parseInt(newValue.split(" ")[0]);
						//System.out.println(String.format("New MAX: %d", maxValue));
						pos_x = i;
						pos_y = j;
					}
					
					boardPositions[i][j] = 0;
					if(maxValue>=Beta)
					{
						return String.format("%d %d %d %s", maxValue, pos_x, pos_y, move);
					}
					Alpha=Math.max(Alpha, maxValue);
				}
			}
		}
		
		//Raid
		//System.out.println("Raid");
		for(int i = 0; i < boardPositions.length;i++)
		{
			for (int j = 0; j < boardPositions.length; j++) 
			{
				//System.out.println(String.format("MAX: i = %d j = %d", i, j));
				if(boardPositions[i][j] == 0)
				{
					if(CanRaid(boardPositions, i, j, player))
					{
						
						int[][] raidedBoard = DoRaid(boardPositions, i, j, player);
						newValue = MinValue(boardValues, raidedBoard, -1 * player, depth - 1, Alpha, Beta);
						if(Integer.parseInt(newValue.split(" ")[0]) > maxValue)
						{
							move = "Raid";
							maxValue = Integer.parseInt(newValue.split(" ")[0]);
							//System.out.println(String.format("New MAX: %d", maxValue));
							pos_x = i;
							pos_y = j;
						}
						if(maxValue>=Beta)
						{
							return String.format("%d %d %d %s", maxValue, pos_x, pos_y, move);
						}
						Alpha=Math.max(Alpha, maxValue);
					}
				}
			}
		}
		return String.format("%d %d %d %s", maxValue, pos_x, pos_y, move);
	}
	
	public static String MinValue(int[][] boardValues, int[][] boardPositions, int player, int depth, int Alpha, int Beta)
	{
		//System.out.println("MAX");
		if(depth==0)//write the other terminal test when no more moves can me made
			return EvaluateBoard(boardValues, boardPositions, player);
		if(IsBoardComplete(boardPositions))
			return EvaluateBoard(boardValues, boardPositions, player);
		
		String newValue = new String();
		//Stake
		String move = "Stake";
		Integer minValue = Integer.MAX_VALUE, pos_x = -1, pos_y = -1;
		for(int i = 0; i < boardPositions.length;i++)
		{
			for (int j = 0; j < boardPositions.length; j++) 
			{
				//System.out.println(String.format("MIN: i = %d j = %d", i, j));
				if(boardPositions[i][j] == 0)
				{
					boardPositions[i][j] = player;//Need to take care of handling Stake and Raid
					newValue = MaxValue(boardValues, boardPositions, -1 * player, depth - 1, Alpha, Beta);
					
					if(Integer.parseInt(newValue.split(" ")[0]) < minValue)
					{						
						minValue = Integer.parseInt(newValue.split(" ")[0]);
						//System.out.println(String.format("New MIN: %d", minValue));
						pos_x = i;
						pos_y = j;
					}
					
					boardPositions[i][j] = 0;
					if(minValue <= Alpha)
					{
						return String.format("%d %d %d %s", minValue, pos_x, pos_y, move);
					}
					Beta=Math.min(Beta, minValue);
				}
			}
		}
		
		//Raid
		for(int i = 0; i < boardPositions.length;i++)
		{
			for (int j = 0; j < boardPositions.length; j++) 
			{
				//System.out.println(String.format("MAX: i = %d j = %d", i, j));
				if(boardPositions[i][j] == 0)
				{
					if(CanRaid(boardPositions, i, j, player))
					{
						int[][] raidedBoard = DoRaid(boardPositions, i, j, player);
						newValue = MaxValue(boardValues, raidedBoard, -1 * player, depth - 1, Alpha, Beta);
						if(Integer.parseInt(newValue.split(" ")[0]) < minValue)
						{
							move = "Raid";
							minValue = Integer.parseInt(newValue.split(" ")[0]);
							//System.out.println(String.format("New MAX: %d", minValue));
							pos_x = i;
							pos_y = j;
						}
						if(minValue <= Alpha)
						{
							return String.format("%d %d %d %s", minValue, pos_x, pos_y, move);
						}
						Beta=Math.min(Beta, minValue);
					}
				}
			}
		}
				
		return String.format("%d %d %d %s", minValue, pos_x, pos_y, move);
	}
	

	public static String EvaluateBoard(int[][] boardValues, int[][] boardPositions, int player)
	{
		int value = 0;
		for(int i = 0; i < boardPositions.length;i++)
		{
			for (int j = 0; j < boardPositions.length; j++) 
			{
				value += boardValues[i][j] * boardPositions[i][j];
			}
		}
		
		//value *= player;
		//PrintBoard(boardValues, boardPositions);
		System.out.println(value);
		return String.valueOf(value);
	}
	
	public static void PrintBoard(int[][] boardValues, int[][] boardPositions)
	{
		for(int i = 0; i < boardValues.length; i++)
		{
			String printLine = new String();
			for(int j = 0; j < boardValues.length; j++)
			{
				printLine +=  String.format("%d\t", boardValues[i][j]);
			}
			System.out.println(printLine);
		}
		
		for(int i = 0; i < boardPositions.length; i++)
		{
			String printLine = new String();
			for(int j = 0; j < boardPositions.length; j++)
			{
				printLine +=  String.format("%d\t", boardPositions[i][j]);
			}
			System.out.println(printLine);
		}
	}
	
	public static boolean CanRaid(int[][] boardPositions, int x, int y, int player)
	{
		/*for(int i = 0; i < boardPositions.length; i++)
		{
			for(int j = 0; j < boardPositions.length; j++)
			{
				
			}			
		}*/
		boolean friendlyNeighbor = false;
		boolean hostileNeighbor = false;
		
		boardPositions[x][y] = player;
		
		if(x!=0)
		{
			if(boardPositions[x][y] == boardPositions[x-1][y])
				friendlyNeighbor = true;
			if(boardPositions[x][y] == -1 * boardPositions[x-1][y])
				hostileNeighbor = true;			
		}
		if(y != 0)
		{
			if(boardPositions[x][y] == boardPositions[x][y-1])
				friendlyNeighbor = true;
			if(boardPositions[x][y] == -1 * boardPositions[x][y-1])
				hostileNeighbor = true;
		}
		if(x!=boardPositions.length - 1)
		{
			if(boardPositions[x][y] == boardPositions[x+1][y])
				friendlyNeighbor = true;
			if(boardPositions[x][y] == -1 * boardPositions[x+1][y])
				hostileNeighbor = true;
		}
		if(y!=boardPositions.length - 1)
		{
			if(boardPositions[x][y] == boardPositions[x][y+1])
				friendlyNeighbor = true;
			if(boardPositions[x][y] == -1 * boardPositions[x][y+1])
				hostileNeighbor = true;
		}
		
		boardPositions[x][y] = 0;
		
		return (friendlyNeighbor && hostileNeighbor);
	}
	
	public static int[][] DoRaid(int[][] boardPositions, int x, int y, int player)
	{
		
		int[][] tempBoard = new int [boardPositions.length][boardPositions.length];
		
		for(int i = 0; i < boardPositions.length;i++)
		{
			tempBoard[i] = Arrays.copyOf(boardPositions[i], boardPositions[i].length);
		}
		
		tempBoard[x][y] = player;
		
		if(x!=0)
		{			
			if(tempBoard[x][y] == -1 * tempBoard[x-1][y])
				tempBoard[x-1][y] = player;						
		}
		if(y != 0)
		{			
			if(tempBoard[x][y] == -1 * tempBoard[x][y-1])
				tempBoard[x][y-1] = player;
		}
		if(x!=tempBoard.length - 1)
		{			
			if(tempBoard[x][y] == -1 * tempBoard[x+1][y])
				tempBoard[x+1][y] = player;
		}
		if(y!=tempBoard.length - 1)
		{			
			if(tempBoard[x][y] == -1 * tempBoard[x][y+1])
				tempBoard[x][y+1] = player;
		}
		return tempBoard;
	}
	
	public static String MakeMove(int[][] boardPositions, String move, String myPlay, String oppPlay)
	{
		int row = 0;
		for(int i = 1; i < move.split(" ")[0].length(); i++)
		{
			row = 10*row + Character.getNumericValue(move.split(" ")[0].charAt(i));
		}			
		
		row -= 1;
		int col = (int)(move.split(" ")[0].charAt(0) - 'A');
		if(move.split(" ")[1].equals("Stake"))
		{			
			boardPositions[row][col] = 1;
		}
		else
		{
			boardPositions = DoRaid(boardPositions, row, col, 1);
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < boardPositions.length; i++)
		{
			for(int j = 0; j < boardPositions.length; j++)
			{
				if(boardPositions[i][j] == 1)
					sb.append(myPlay);
				else if(boardPositions[i][j] == -1)
					sb.append(oppPlay);
				else 
					sb.append(".");
			}
			sb.append("\n");
		}
		return sb.toString().trim();
	}
}
