package code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HNCWordSim {
	
	public double getWordSim(String hnc1, String hnc2)
	{
		
		double result = 0.0;
		
		if(!hnc1.matches(".*[\\(\\#\\$\\&\\|\\/\\+\\,\\;\\‖].*") && !hnc2.matches(".*[\\(\\#\\$\\&\\|\\/\\+\\,\\;\\‖].*"))
		{
			if(hnc1.matches("^!.*"))
			{
				result = 0.65 * sim(hnc1, hnc2);
			}
			else if (hnc1.matches("^\\^.*"))
			{
				result = 0.95 * sim(hnc1, hnc2);
			}
			else
			{
				result = sim(hnc1, hnc2);
			}
			
			System.out.println("calculate hnc1:" + hnc1 + " & hnc2:" + hnc2 + " result is:" + result);
		}
		else if(hnc1.matches(".*\\,l[0-9]+\\,.*"))
		{
			Pattern pattern = Pattern.compile("^\\((.*)\\,l[0-9]+\\,(.*)\\)$");
			
			Matcher matcher = pattern.matcher(hnc1);
			
			matcher.find();
			
			result = (getWordSim(matcher.group(1), hnc2) + getWordSim(matcher.group(2), hnc2))/(1+1.0);
			
		}
		
		else if(hnc1.matches(".*[\\(\\#\\$\\&\\|\\/\\+\\,\\;\\‖].*")) 
		{
			
			HashMap<String, String> divideResult = divideHNC(hnc1);
			
			System.out.println("now operator:" + divideResult.get("operator"));
			
			if(divideResult.get("operator").equals("#"))
			{
				result = (0.7*getWordSim(divideResult.get("hnc1"), hnc2) + getWordSim(divideResult.get("hnc2"), hnc2))/(0.7+1);
			}
			else if(divideResult.get("operator").equals("$"))
			{
				result = (0.7*getWordSim(divideResult.get("hnc1"), hnc2) + getWordSim(divideResult.get("hnc2"), hnc2))/(0.7+1);
			}
			else if(divideResult.get("operator").equals("&"))
			{
				result = (0.6*getWordSim(divideResult.get("hnc1"), hnc2) + getWordSim(divideResult.get("hnc2"), hnc2))/(0.6+1);
			}
			else if(divideResult.get("operator").equals("|"))
			{
				result = (0.6*getWordSim(divideResult.get("hnc1"), hnc2) + getWordSim(divideResult.get("hnc2"), hnc2))/(0.6+1);
			}
			else if(divideResult.get("operator").equals("/"))
			{
				result = (0.2*getWordSim(divideResult.get("hnc1"), hnc2) + getWordSim(divideResult.get("hnc2"), hnc2))/(0.2+1);
			}
			else if(divideResult.get("operator").equals("‖"))
			{
				result = (0.8*getWordSim(divideResult.get("hnc1"), hnc2) + getWordSim(divideResult.get("hnc2"), hnc2))/(0.8+1);
			}
			else if(divideResult.get("operator").equals("+"))
			{
				result = (0.5*getWordSim(divideResult.get("hnc1"), hnc2) + 0.5*getWordSim(divideResult.get("hnc2"), hnc2))/(0.5+0.5);
			}
			else if(divideResult.get("operator").equals(";") || divideResult.get("operator").equals(","))
			{
				result = (getWordSim(divideResult.get("hnc1"), hnc2)+ getWordSim(divideResult.get("hnc2"), hnc2)) / (1+1);
			}
			
		}
		else if(!hnc1.matches(".*[\\(\\#\\$\\&\\|\\/\\+\\,\\;‖].*")&&hnc2.matches(".*[\\(\\#\\$\\&\\|\\/\\+\\,\\;‖].*")) 
		{
			result = getWordSim(hnc2, hnc1);
		}
		
		return result;
		
	}
	
	
	private double sim(String hnc1, String hnc2)
	{
//		System.out.println(hnc1 + "," + hnc2);
		double simcp = 0.0;
		double simcc = 0.0;
		double simep = 0.0;
		
		HashMap<String, String> hnc1Map = new HashMap<String, String>();
		HashMap<String, String> hnc2Map = new HashMap<String, String>();
		
		String cp1 = "";
		String cp2 = "";
		
		String cc1 = "";
		String cc2 = "";
		
		String ep1 = "";
		String ep2 = "";
		
		Pattern pattern = Pattern.compile("^[f]{0,2}(jl|jw|pe|gw|[lshqxpw]){0,1}([vguzr]*)(.*)");
		
		Matcher matcher = pattern.matcher(hnc1);
		
		while(matcher.find())
		{
			hnc1Map.put("cp", matcher.group(3));
			hnc1Map.put("cc", matcher.group(1));
			hnc1Map.put("ep", matcher.group(2));
		}
		
		matcher = pattern.matcher(hnc2);
		
		while(matcher.find())
		{
			hnc2Map.put("cp", matcher.group(3));
			hnc2Map.put("cc", matcher.group(1));
			hnc2Map.put("ep", matcher.group(2));
		}
		
//		System.out.println("hnc1 cp: " + hnc1Map.get("cp") + " hnc1 cc: " + hnc1Map.get("cc") + " hnc1 ep: " + hnc1Map.get("ep"));
//		System.out.println("hnc2 cp: " + hnc2Map.get("cp") + " hnc2 cc: " + hnc2Map.get("cc") + " hnc2 ep: " + hnc2Map.get("ep"));
		
		//cp value.
		cp1 = hnc1Map.get("cp");
		cp2 = hnc2Map.get("cp");
		
		if(null ==cp1 || null == cp2 || cp1.isEmpty() || cp2.isEmpty())
		{
			simcp = 0.0;
		}
		else
		{
			int nowPosition = 0;
			
			int dept;
			int dist;
			double hc;
			
			for(int i = 0; i < cp1.length() && i < cp2.length(); i++)
			{
				if(cp1.substring(0, i+1).equals(cp2.substring(0, i+1)))
				{
					nowPosition = i;
				}
			}
			
			
			dept = nowPosition+1;
			dist = (cp1.length()-dept) + (cp2.length()-dept);
			hc = Math.abs(cp1.length() - cp2.length()) > 0.5 ? Math.abs(cp1.length() - cp2.length()) : 0.5;
			
//			System.out.println("dept:" + dept + " , dist:" + dist + " , " + " hc:" + hc);
			
			simcp = (2*dept + 1)/(dist + 2*dept + 0.5*hc);
			
//			System.out.println("simcp:" + simcp);
		}
		
		//cc value
		
		cc1 = hnc1Map.get("cc");
		cc2 = hnc2Map.get("cc");
		
		if(null == cc1 || null == cc2 || cc1.isEmpty() || cc2.isEmpty())
		{
			simcc = 0.5;
		}
		else
		{
			if((cc1.equals("p") && cc2.equals("p")) || (cc1.equals("jw") && cc2.equals("jw")) || (cc1.equals("w") && cc2.equals("w")))
			{
				simcc = 1;
			}
			
			else if((cc1.matches("p|jw|w") && cc2.matches("p|jw|w")) || (!cc1.matches("p|jw|w") && !cc2.matches("p|jw|w")))
			{
				simcc = 0.5;
			}
			else
			{
				simcc = 0.0;
			}
		}
		
//		System.out.println("simcc:" + simcc);
		
		//ep value
		
		ep1 = hnc1Map.get("ep");
		ep2 = hnc2Map.get("ep");
		
		if(null == ep1 || null == ep2 || ep1.isEmpty() || ep2.isEmpty())
		{
			simep = 0.0;
		}
		else
		{
			for(String i : ep1.split(""))
			{
				 if(ep2.contains(i))
				 {
					 simep = 1;
				 }
			}
		}
//		System.out.println("simep:" + simep);
		
		System.out.println("simcp:" + simcp + " simcc:" + simcc + " simep:" + simep);
		
		return simcp*(4.0/7.0) + simcc*(2.0/7.0) + simep*(1.0/7.0);
		
	}
	
	private HashMap<String, String> divideHNC(String hnc)
	{
		String split1 = "";
		String split2 = "";
		
		ArrayList<String> split = new ArrayList<String>();
		
		HashMap<String, String> divideResult = new HashMap<String, String>();
		
		split = Split.split(hnc);
		
		String nowOperator = "";
		int  nowSplit = -1;
		
		for(int i = 1; i < split.size(); i+=2)
		{
			if(nowOperator.equals(""))
			{
				nowOperator = split.get(i);
				nowSplit = i;
			}
			else if(",;".contains(split.get(i)) && !",;".contains(split.get(i)))
			{
				nowOperator = split.get(i);
				nowSplit = i;
			}
			else if("+".contains(split.get(i)) && !",;".contains(nowOperator) && !"+".contains(nowOperator))
			{
				nowOperator = split.get(i);
				nowSplit = i;
			}
			else if("#$&|/‖".contains(split.get(i)) && !nowOperator.equals("+") && !",;".contains(nowOperator) && "#$&|/‖".contains(nowOperator))
			{
				nowOperator = split.get(i);
				nowSplit = i;
			}
		}
		
		for(int i = 0; i < nowSplit; i++)
		{
			
			if(split.get(i).matches(".*[\\(\\#\\$\\&\\|\\/\\+\\,\\;‖].*") && split.get(i).length() > 1)
			{
				split1 = split1 + "(" + split.get(i) + ")";
			}
			else
			{
				split1 += split.get(i);
			}
		}
		
		for(int i = nowSplit+1; i < split.size(); i++)
		{
			if(split.get(i).matches(".*[\\(\\#\\$\\&\\|\\/\\+\\,\\;‖].*") && split.get(i).length() > 1)
			{
				split2 = split2 + "(" + split.get(i) + ")";
			}
			else
			{
				split2 += split.get(i);
			}
		}
		
		divideResult.put("hnc1", split1);
		divideResult.put("hnc2", split2);
		divideResult.put("operator", nowOperator);
		
		return divideResult;
	}
}
