import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.String;
import java.sql.Statement;


//TODO:
/* 
 * Handle Multiple Implication
 * Unification of variables
 * Handle Distribution properly
 * Split Sentences on )&(
 * 
 * 
 * 
 * 
 * 
 */


final class Predicate
{
	int number;
	String name;
	int noOfParams;
	HashMap<Integer,String> params = new HashMap<Integer, String>();
	HashMap<Integer,String> substitution = new HashMap<Integer, String>();
}

final class Sentence
{
	ArrayList<Predicate> PredicateSet = new ArrayList<>();
	ArrayList<Boolean> complement = new ArrayList<>();
}

public class homework {

	String[] operators = new String[]{"&", "|", "~", "=>"};
	public static BufferedWriter outputWriter;
	public static ArrayList<Predicate> Predicates = new ArrayList<Predicate>();
	public static HashMap<String, Integer> PredicateMap = new HashMap<String,Integer>();
	public static HashMap<String, ArrayList<String>> PredicateStatementMap = new HashMap<String,ArrayList<String>>();
	public static ArrayList<Sentence> KB = new ArrayList<Sentence>();
	public static String[] parsedQueries;
	public static ArrayList<Predicate> Queries = new ArrayList<Predicate>();
	public static HashMap<String, Integer> QueriesMap = new HashMap<String,Integer>();
	public static ArrayList<String> UnifiedStatements = new ArrayList<String>();
	
	public static void main(String[] args)
	{
		try
		{
			String inputfile = "input.txt";
			String outputfile = "output.txt";

			BufferedReader inputReader = new BufferedReader(new FileReader(inputfile));
			outputWriter = new BufferedWriter(new FileWriter(outputfile));

			Integer noOfQueries = Integer.parseInt(inputReader.readLine());
			String[] inputQueries = new String[noOfQueries];
			for (int i = 0; i < noOfQueries; i++) 
			{
				inputQueries[i] = inputReader.readLine();
			}
			
			Integer noOfStatements = Integer.parseInt(inputReader.readLine());
			ArrayList<String> inputStatements = new ArrayList<String>();
			for (int i = 0; i < noOfStatements; i++) 
			{
				String statement = parseStatement(inputReader.readLine(),i,noOfStatements);
				inputStatements.add(cleanUp(statement));
				System.out.println(inputStatements.get(i));
			}
			
			ConstructPredicateStatementMap(inputStatements);
			
			ParseQueries(inputQueries);
			
			PerformResolution();
						
			inputReader.close();
			outputWriter.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
	}
	
	public static String cleanUp(String stmt)
	{
		stmt = stmt.replaceAll("\\(", "");
		stmt = stmt.replaceAll("\\)", "");
		return stmt;
	}
	
	public static String parseStatement(String statement, int i, int num)
	{
		
		statement = statement.replaceAll("\\s+","");
		String newStatement = parsePredicates(statement,i,num);
//		newStatement = parseImplication("((A=>B)=>C)");
		newStatement = parseImplication(newStatement);		
		newStatement = parseNegation(newStatement);
		newStatement = distributeOR(newStatement);
		
		AddSentencetoKB(newStatement,i);
		
		return newStatement;
	}
	
	public static String parsePredicates(String statement, int lineNum, int totalNum)
	{
		String subString = "";
		for (int i = 0; i < statement.length(); i++) 
		{
			if(Character.isUpperCase(statement.charAt(i)))
			{
				int index = statement.indexOf(')',i);
				subString = statement.substring(i, index);
				Integer PredRep = CreatePredicate(subString, lineNum, totalNum);
				statement = statement.replace(subString+")", PredRep.toString());
//				i += index;
			}
		}
		return statement;
	}
	
	public static int CreatePredicate(String Pred, int i, int num)
	{
		String[] predSplit = Pred.split("\\(");
		if(!PredicateMap.containsKey(predSplit[0]))
		{
			Predicate newPredicate = new Predicate();
			newPredicate.name = predSplit[0];
			newPredicate.number = Predicates.size();
			String argsStr = predSplit[1];
			String[] args = argsStr.split(",");
			String param = "";
			for (int j = 0; j < args.length; j++) 
			{
				if(Character.isUpperCase(args[j].charAt(0)))
				{					
					param+= args[j] + ",";
				}
				else
				{
					param+= args[j] + i + ",";					
				}
			}
			param = param.substring(0,param.length()-1);
			newPredicate.params.put(i, param);
						
			newPredicate.noOfParams = predSplit[1].split(",").length;
			Predicates.add(newPredicate);
			PredicateMap.put(newPredicate.name, newPredicate.number);
			return newPredicate.number;
		}
		else
		{
			Predicate tmpPredicate = Predicates.get(PredicateMap.get(predSplit[0]));
			String argsStr = predSplit[1];
			String[] args = argsStr.split(",");
			String param = "", sub = "";
			
			for (int j = 0; j < args.length; j++) 
			{
				if(Character.isUpperCase(args[j].charAt(0)))
				{					
					param+= args[j] + ",";
				}
				else
				{
					param+= args[j] + i + ",";					
				}
			}
			param = param.substring(0,param.length()-1);
			if(tmpPredicate.params.containsKey(i))
			{				
				tmpPredicate.params.put(i, tmpPredicate.params.get(i) + "|" + param);
			}
			else
			{
				tmpPredicate.params.put(i, param);				
			}
		}
		
		return PredicateMap.get(predSplit[0]);
	}
		
	//TODO: Write this properly!!
	public static String parseImplication(String statement)
	{
		while(statement.contains("=>"))
		{
//			String[] implySplit = statement.split("=>");
////			System.out.println(implySplit[0]);
////			System.out.println(implySplit[1]);
//			String newStatement = "~" + implySplit[0].substring(1) + "|" + implySplit[1].substring(0,implySplit[1].length()-1);
//			System.out.println(newStatement);
//			return newStatement;
			
			String newStatement = statement.replace("=>","|");
			newStatement = "~" + newStatement.substring(1,newStatement.length()-1);
			//System.out.println(newStatement);
			return newStatement;
					
			
			//Handle multiple =>  
//			
//			int index = statement.indexOf('=');
//			int indexOp = statement.lastIndexOf('(',index);
//			int indexCl = statement.indexOf(')',index);
//			//String newStatement = statement.replace("=>","|");
//			statement = statement.substring(0,indexOp+1) + "~" + statement.substring(indexOp+1,index) + "|" + statement.substring(index+2);
//			System.out.println(statement);
//			//return newStatement;
		}
		return statement;
	}
	
	public static String parseNegation(String statement)
	{
		while(statement.contains("~("))
		{
			int index = statement.indexOf("~(");
			int endIndex = statement.indexOf(')',index);
			String tmpStr = statement.substring(index+2, endIndex);
			
			if(tmpStr.contains("&"))
			{
				String[] andSplit = tmpStr.split("&");
				tmpStr = negateStr(andSplit[0]) + "|" + negateStr(andSplit[1]); 
			}
			else if(tmpStr.contains("|"))
			{
				String[] orSplit = tmpStr.split("|");
				tmpStr = negateStr(orSplit[0]) + "&" + negateStr(orSplit[1]); 
			}
			else
			{
				tmpStr = negateStr(tmpStr);
			}
			statement = statement.replace(statement.substring(index, endIndex+1), "(" + tmpStr +")");
		}
		return statement;
	}
	
	public static String negateStr(String clause) 
	{
		if(clause.charAt(0) == '~')
			return clause.substring(1);
		else
			return "~"+clause;
	}
	
	public static String distributeOR(String statement) 
	{
		while(statement.contains(")|") || statement.contains("|("))
		{
			if(statement.contains(")|"))
			{
				int index = statement.indexOf(")|");
				int startIndex = statement.lastIndexOf('(',index);
				String tmpStr = statement.substring(startIndex+1, index);

				if(tmpStr.contains("&"))
				{
					String[] andSplit = tmpStr.split("&");
					statement = statement.substring(0,startIndex+1) + andSplit[0] + "|" + statement.substring(index + 2) + ")&(" + andSplit[1] + "|" + statement.substring(index + 2) + ")";
				}
				else
				{
					statement = statement.substring(0,startIndex) + tmpStr + statement.substring(index+1);
				}
			}
			if(statement.contains("|("))
			{
				int index = statement.indexOf("|(");
				int endIndex = statement.indexOf(')',index);
				String tmpStr = statement.substring(index+2, endIndex);

				if(tmpStr.contains("&"))
				{
					String[] andSplit = tmpStr.split("&");
					statement = "(" + statement.substring(0,index) + "|" + andSplit[0] + ")&(" + statement.substring(0,index) + "|" +andSplit[1] + ")";
				}
				else
				{
					statement = statement.substring(0,index + 1) + tmpStr + statement.substring(endIndex+1);
				}
			}
		}
		return statement;
	}
	
	public static void ConstructPredicateStatementMap(ArrayList<String> inputStatements)
	{
		for (int i = 0; i < inputStatements.size(); i++) 
		{
			String[] predSplit = inputStatements.get(i).split("\\|");
			for (int j = 0; j < predSplit.length; j++) 
			{
				ArrayList<String> stmnt;
				if(PredicateStatementMap.containsKey(predSplit[j]))
				{
					stmnt = PredicateStatementMap.get(predSplit[j]);
					stmnt.add(i+"-"+inputStatements.get(i));
				}
				else
				{
					stmnt = new ArrayList<String>();
					stmnt.add(i+"-"+inputStatements.get(i));
				}
				PredicateStatementMap.put(predSplit[j], stmnt);
			}
		}
	}
	
	public static void ParseQueries(String[] inputQueries)
	{
		parsedQueries = new String[inputQueries.length];
		for (int i = 0; i < inputQueries.length; i++) 
		{
			String stmt = inputQueries[i].substring(0, inputQueries[i].length()-1);
			boolean notFlag = false;
			if(stmt.contains("~"))
			{
				notFlag = true;
				stmt = stmt.replace("~", "");
			}
			String[] predSplit = stmt.split("\\(");
			Predicate newPredicate = new Predicate();
			newPredicate.name = predSplit[0];
			newPredicate.number = PredicateMap.get(predSplit[0]);
			String argsStr = predSplit[1];
			String[] args = argsStr.split(",");
			String param = "";//, sub = "";
			for (int j = 0; j < args.length; j++) 
			{
				param+= args[j] + ",";				
			}
			
			newPredicate.params.put(i, param);
			
			newPredicate.noOfParams = predSplit[1].split(",").length;
			Queries.add(newPredicate);
			QueriesMap.put(newPredicate.name, newPredicate.number);
			String query = "";
			if(notFlag)
				query+="~";
			query+=newPredicate.number;
			parsedQueries[i] = query;
			
			System.out.println(parsedQueries[i]);
		}
	}
	
	public static void PerformResolution() 
	{
		for (int i = 0; i < parsedQueries.length; i++) 
		{
			ArrayList<String> Statements = PredicateStatementMap.get(parsedQueries[i]);
			
			for (int j = 0; j < Statements.size(); j++) 
			{
				String stmt = Statements.get(j);
				boolean result = CheckContradiction(Statements.get(j), i);
				if (result)
					System.out.println(result);
			}
		}
	}
	
	public static boolean CheckContradiction(String Statement, int index) 
	{
		int StmtIndex = Integer.valueOf((Statement.split("-")[0]));
		String[] split = Statement.split("-")[1].split("\\|");
		if(split.length<2)
		{			
			Predicate tmpPred = Predicates.get(Integer.valueOf(split[0].replace("~", "")));
			Predicate queryPred = Queries.get(index);
			
			if(!tmpPred.name.equals(queryPred.name))
				return false;
			
			int noOfParams = tmpPred.noOfParams;
			
			String Params = tmpPred.params.get(StmtIndex);		
			String[] paramList = Params.split(",");
						
			String qParams = queryPred.params.get(index);				
			String[] qParamsList = qParams.split(",");
			
			for (int i = 0; i < noOfParams; i++) 
			{
				if(Character.isLowerCase(paramList[i].charAt(0))||paramList[i].equals(qParamsList[i]))
					continue;
				else
					return false;
			}
			return true;
		}
		else
		{
			int i;
			for (i = 0; i < split.length; i++) 
			{
				if (split[i].equals(parsedQueries[index]))
					break;
			}
			Predicate tmpPred = Predicates.get(Integer.valueOf(split[i].replace("~", "")));
			Predicate queryPred = Queries.get(index);
			
			if(!tmpPred.name.equals(queryPred.name))
				return false;
			
			int noOfParams = tmpPred.noOfParams;
			
			String Params = tmpPred.params.get(StmtIndex);		
			String[] paramList = Params.split(",");
						
			String qParams = queryPred.params.get(index);				
			String[] qParamsList = qParams.split(",");
			
			for (int j = 0; j < noOfParams; j++) 
			{
				if(Character.isLowerCase(paramList[j].charAt(0))||paramList[j].equals(qParamsList[j]))
					continue;
				else
					return false;
			}
			Unify(Statement,parsedQueries[index],index);
			System.out.println("Unify " + Statement + " and " + parsedQueries[index]);
			return true;
		}
		//return false;
	}
	
	public static void Unify(String stmnt1, String stmnt2,int index)
	{
		UnifiedStatements.add(stmnt1.replace(stmnt2, ""));
		while(!UnifiedStatements.isEmpty())
		{
			CheckContradiction(UnifiedStatements.get(0), index);
		}
	}
	
	public static void AddSentencetoKB(String newStatement,int num)
	{
		String[] Statements = splitAnd(newStatement);
		
		for (int i = 0; i < Statements.length; i++) 
		{
			String[] sentPredicates = Statements[i].split("\\|");
			ArrayList<Predicate> predList = new ArrayList<Predicate>();
			ArrayList<Boolean> negList = new ArrayList<Boolean>();
			for (int j = 0; j < sentPredicates.length; j++) 
			{
				if(sentPredicates[i].contains("~"))
					negList.add(true);
				else
					negList.add(false);
				Predicate newPred = new Predicate();
				Predicate tmpPred = Predicates.get(Integer.valueOf(sentPredicates[i].replace("~", "")));
				
				newPred.name = tmpPred.name;
				newPred.number = tmpPred.number;
				newPred.noOfParams = tmpPred.noOfParams;
				
				newPred.params.put(0,getParams(tmpPred,num));
				predList.add(newPred);
			}
			Sentence newSent = new Sentence();
			newSent.PredicateSet = predList;
			newSent.complement = negList;
			KB.add(newSent);
		}
	}
	
	public static String getParams(Predicate tmpPred, int line)
	{
		String Params = tmpPred.params.get(line);
		String[] pSplit = Params.split("\\|");
		String args = "",pArgs = "";
		
		for (int i = 0; i < pSplit.length; i++) 
		{
			if(!pSplit[i].equals("-"))
			{
				args = pSplit[i];
				pSplit[i] = "-";
			}
		}
		pArgs = pSplit[0];
		for (int i = 1; i < pSplit.length; i++) 
		{
			pArgs += "|" + pSplit[i];
		}
		
		tmpPred.params.put(line,pArgs);
		return args;
	}
	
	public static String[] splitAnd(String Statement)
	{
		String[] sentences = Statement.split("\\)&\\(");
		for (int i = 0; i < sentences.length; i++) 
		{
			sentences[i] = sentences[i].replace("(", "").replace(")", "");
		}
		return sentences;
	}
}
