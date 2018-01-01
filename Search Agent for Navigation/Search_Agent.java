import java.io.*;
import java.util.*;

final class Node
{
	LinkedHashMap<Integer, Integer> adjacencyList;//Child, cost
	boolean visited = false;
	int number = -1;
	String name;
	int parent = -1;
	int pathCost = -1;
	int heuristic = -1;

	Node(int num, String name)
	{
		this.number = num;
		this.name = name;
		this.adjacencyList = new LinkedHashMap<Integer, Integer>();
		this.visited = false;
	}
}

final class pQueueNode
{
	Integer srcNode = -1;
	Integer edgeCost = -1;
	Integer heuristics = -1;
	
	public pQueueNode(Integer sNode, Integer eCost) 
	{
		this.srcNode = sNode;
		this.edgeCost = eCost;
	}
	
	public pQueueNode(Integer sNode, Integer eCost, Integer heuristic) 
	{
		this.srcNode = sNode;
		this.edgeCost = eCost;
		this.heuristics = heuristic;
	}
}

final class PriorityQueueComparator implements Comparator<pQueueNode>
{
    @Override
    public int compare(pQueueNode X, pQueueNode Y)
    {
        if(X.edgeCost > Y.edgeCost)
        	return 1;
        else if(X.edgeCost < Y.edgeCost)
        	return -1;
        else
        {
        	if(X.srcNode > Y.srcNode)
        		return 1;
        	else if(X.srcNode < Y.srcNode)
        		return -1;
        	else
        		return 0;
        }
        //return 0;
    }
}


final class PriorityQueueHeuristicComparator implements Comparator<pQueueNode>
{
    @Override
    public int compare(pQueueNode X, pQueueNode Y)
    {
        if(X.edgeCost + X.heuristics > Y.edgeCost + Y.heuristics)
        	return 1;
        else if(X.edgeCost + X.heuristics < Y.edgeCost + Y.heuristics)
        	return -1;
        else
        {
        	if(X.srcNode > Y.srcNode)
        		return 1;
        	else if(X.srcNode < Y.srcNode)
        		return -1;
        	else
        		return 0;
        }
        
    }
}

public class Search_Agent
{
	public static BufferedWriter outputWriter;
	public static void WriteResult(ArrayList<Node> inputGraph, Integer goalState)
	{
		try
		{
		if(inputGraph.get(goalState).parent != -1)
			WriteResult(inputGraph, inputGraph.get(goalState).parent);
		outputWriter.write(inputGraph.get(goalState).name + " " + inputGraph.get(goalState).pathCost);
		outputWriter.newLine();
		}
		catch (Exception e)
		{
			
		}
		
	}
	public static void main(String[] args)
	{
		try
		{		

//			for (int x = 0; x < 400; x++)
//			{
//					String inputfile = String.format("./MoreCases/input%1$d.txt",x);
//					String outputfile = String.format("./newResults/output%1$d.txt",x);
			String inputfile = "input.txt";
			String outputfile = "output.txt";

					//BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
					BufferedReader inputReader = new BufferedReader(new FileReader(inputfile));
					outputWriter = new BufferedWriter(new FileWriter(outputfile));
//			BufferedReader inputReader = new BufferedReader(new FileReader("input.txt"));
//			BufferedWriter outputWriter = new BufferedWriter(new FileWriter("output.txt"));

			String line = new String();

			String Algo = inputReader.readLine();
			String startState = inputReader.readLine();
			String goalState = inputReader.readLine();
			Integer noOfLines = Integer.parseInt(inputReader.readLine());

			HashMap<String, Integer> Nodes = new HashMap<String, Integer>();
			ArrayList<Node> graphNodes = new ArrayList<Node>();

			for(Integer i = 0; i < noOfLines; i++)
			{
				line = inputReader.readLine();
				String[] words = line.split(" ");

				if (!Nodes.containsKey(words[0]))
				{		
					graphNodes.add(new Node(Nodes.size(),words[0]));
					Nodes.put(words[0], Nodes.size());
				}
				if (!Nodes.containsKey(words[1]))
				{
					graphNodes.add(new Node(Nodes.size(),words[1]));
					Nodes.put(words[1], Nodes.size());
				}

				if(Algo.equals("BFS") || Algo.equals("DFS"))
					words[2] = "1";

				graphNodes.get(Nodes.get(words[0]).intValue()).adjacencyList.put(Nodes.get(words[1]).intValue(), Integer.parseInt(words[2]));

			}


			for(int i = 0; i< graphNodes.size(); i++)
			{
				Node tmpNode = graphNodes.get(i);


				Set keys = tmpNode.adjacencyList.keySet();

				for (Iterator j = keys.iterator(); j.hasNext(); ) {
					Integer key = (Integer)j.next();
					Integer value = tmpNode.adjacencyList.get(key);

				}
			}		

			Integer noOfHeuristicsLines = Integer.parseInt(inputReader.readLine());

			for(Integer i = 0; i < noOfHeuristicsLines; i++)
			{
				line = inputReader.readLine();
				String[] words = line.split(" ");

				graphNodes.get(Nodes.get(words[0]).intValue()).heuristic = Integer.parseInt(words[1]);
			}

			Integer startSt = Nodes.get(startState).intValue();
			Integer goalSt= Nodes.get(goalState).intValue();
			Deque<Integer> outputList = new LinkedList<Integer>();
			boolean result = false;

			switch(Algo)
			{
			case "BFS" : result = runBFS(graphNodes, startSt, goalSt);
			break;
			case "DFS" : result = runDFS(graphNodes, startSt, goalSt);
			break;
			case "UCS" : result = runUCS(graphNodes, startSt, goalSt);
			break;
			case "A*" : result = runAStar(graphNodes, startSt, goalSt);
			break;
			default: result = false;
			break;

			}

			if(result)
			{
				WriteResult(graphNodes, goalSt);

//				for(int i = goalSt; i != -1; )
//				{
//
//					outputList.addLast(i);
//					i = graphNodes.get(i).parent;
//				}
//				while(!outputList.isEmpty())
//				{
//					Integer i = outputList.removeLast();
//
//					outputWriter.write(graphNodes.get(i).name + " " + graphNodes.get(i).pathCost);
//					outputWriter.newLine();
//				}
			}
			else
			{
				System.out.println("FAIL!?!?!?!?!!?!?!");
			}

			inputReader.close();
			outputWriter.close();
			//}
		}
		catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static boolean runBFS(ArrayList<Node> inputGraph, Integer startState, Integer goalState)
	{
		Queue<Integer> expansionQueue = new LinkedList<Integer>();
		inputGraph.get(startState).pathCost = 0;
		expansionQueue.add(inputGraph.get(startState).number);

		while(!expansionQueue.isEmpty())
		{
			Node currentNode = inputGraph.get(expansionQueue.remove());
			currentNode.visited = true;

			if(currentNode.number == goalState)
				return true;
			for (Map.Entry<Integer,Integer> entry : currentNode.adjacencyList.entrySet()) 
			{
				Integer key = entry.getKey();
				Integer value = entry.getValue();

				if(!inputGraph.get(key).visited)
				{
					expansionQueue.add(key);

					inputGraph.get(key).parent = currentNode.number;
					inputGraph.get(key).pathCost = currentNode.pathCost + value;
					
					inputGraph.get(key).visited = true; 
				}
			}
		}
		return false;
	}
	
	public static boolean runDFS(ArrayList<Node> inputGraph, Integer startState, Integer goalState)
	{
		Deque<Integer> expansionStack = new LinkedList<Integer>();
		inputGraph.get(startState).pathCost = 0;
		expansionStack.addLast(inputGraph.get(startState).number);

		while(!expansionStack.isEmpty())
		{
			Node currentNode = inputGraph.get(expansionStack.removeLast());
			currentNode.visited = true;

			if(currentNode.number == goalState)
				return true;
			Deque<Integer> tmpStack = new LinkedList<Integer>();
			for (Map.Entry<Integer,Integer> entry : currentNode.adjacencyList.entrySet()) 
			{
				Integer key = entry.getKey();
				Integer value = entry.getValue();

				if(!inputGraph.get(key).visited)
				{
					tmpStack.addLast(key);

					inputGraph.get(key).parent = currentNode.number;
					inputGraph.get(key).pathCost = currentNode.pathCost + value;
					
					inputGraph.get(key).visited = true;
				}
			}
			for(Iterator i = tmpStack.iterator(); !tmpStack.isEmpty();)
			{
				expansionStack.addLast(tmpStack.removeLast());
			}
		}
		return false;
	}
	
	public static boolean runUCS(ArrayList<Node> inputGraph, Integer startState, Integer goalState)
	{
		Comparator<pQueueNode> pQueueComparator = new PriorityQueueComparator();
		PriorityQueue<pQueueNode> expansionQueue = new PriorityQueue<pQueueNode>(pQueueComparator);
		inputGraph.get(startState).pathCost = 0;
		expansionQueue.add(new pQueueNode(inputGraph.get(startState).number, inputGraph.get(startState).pathCost));

		while(!expansionQueue.isEmpty())
		{
			Node currentNode = inputGraph.get(expansionQueue.remove().srcNode);
			currentNode.visited = true;

			if(currentNode.number == goalState) 
				return true;
			for (Map.Entry<Integer,Integer> entry : currentNode.adjacencyList.entrySet()) 
			{
				Integer key = entry.getKey();
				Integer value = entry.getValue();
				
				Node childNode = inputGraph.get(key);

				if(!childNode.visited || childNode.pathCost > currentNode.pathCost + value) 
				{
					childNode.parent = currentNode.number;
					childNode.pathCost = currentNode.pathCost + value;
					
					if(!childNode.visited)
					{
						expansionQueue.add(new pQueueNode(key, childNode.pathCost));						
					}
					else
					{
						expansionQueue.remove(childNode);
						expansionQueue.add(new pQueueNode(key, childNode.pathCost));
					}					
					childNode.visited = true;
				}
			}
		}
		return false;
	}
	
	public static boolean runAStar(ArrayList<Node> inputGraph, Integer startState, Integer goalState)
	{
		Comparator<pQueueNode> pQueueComparator = new PriorityQueueHeuristicComparator();
		PriorityQueue<pQueueNode> expansionQueue = new PriorityQueue<pQueueNode>(pQueueComparator);
		inputGraph.get(startState).pathCost = 0;
		expansionQueue.add(new pQueueNode(inputGraph.get(startState).number, inputGraph.get(startState).pathCost, inputGraph.get(startState).heuristic));

		while(!expansionQueue.isEmpty())
		{
			Node currentNode = inputGraph.get(expansionQueue.remove().srcNode);
			currentNode.visited = true;

			if(currentNode.number == goalState) 
				return true;
			for (Map.Entry<Integer,Integer> entry : currentNode.adjacencyList.entrySet()) 
			{
				Integer key = entry.getKey();
				Integer value = entry.getValue();
				
				Node childNode = inputGraph.get(key);

				if(!childNode.visited || childNode.pathCost > currentNode.pathCost + value )
				{
					childNode.parent = currentNode.number;
					childNode.pathCost = currentNode.pathCost + value;
					
					if(!childNode.visited)
					{
						expansionQueue.add(new pQueueNode(key, childNode.pathCost,childNode.heuristic));

					}
					else
					{
						expansionQueue.remove(childNode);
						expansionQueue.add(new pQueueNode(key, childNode.pathCost, childNode.heuristic));
					}					
						
					childNode.visited = true;
				}
			}
		}
		return false;
	}

}
