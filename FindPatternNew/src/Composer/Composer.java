package Composer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import FindPattern.FindPattern;
import FindPattern.SequencePair;
import Phonetic.WordStruct;

public class Composer {
	
	public  static HashMap<Integer, HashSet<SequencePair> > allPatternOne; //[key patternSize : value pattern
	public  static HashMap<Integer, HashSet<SequencePair> > allPatternTwo;
	static ArrayList <Map<ArrayList<Integer>, ArrayList<Integer> > > patternPoolsOne; 
	static ArrayList <Map<ArrayList<Integer>, ArrayList<Integer> > > patternPoolsTwo; 
	static ArrayList <Map<ArrayList<Integer>, ArrayList<Integer> > > patternPools; 
	static ArrayList <Map<ArrayList<Integer>, ArrayList<Integer> > > patternPoolsDuration; 


	private static String str;
	private static HTree hashtable;
	public static String jasonResult;
	private static  HashMap<String, Integer> beatsMap;
	private static ArrayList<Integer> lrc;
	private static ArrayList<Integer> melo;
	private static ArrayList<Integer> dur;
	private ArrayList<Integer> beatArray;
	static String beatFirst;
	static String beatSecond;
	static int MIN = 2;
	static int MAX = 10;
	

	public static void main(String[] args) throws IOException {
		String input  = "In this Map example, we will learn how to check if HashMap is empty in Java. There are two ways to find out if Map is empty, one is using size() method, if size is zero means Map is empty. Another way to check if HashMap is empty is using more readable isEmpty() method which returns true if Map is empty. Here is code example:";
	    //String input = "This is ! ? good.";
		new Composer(input,4,4);
		
	}


	Composer(String input, int first, int second) throws IOException {
		
		//ini
		melo = new ArrayList<Integer>();
		dur = new ArrayList<Integer>();
		
		this.str = input;
		beatFirst = Integer.toString(first);
		beatSecond = Integer.toString(second);
		beatMap();
		beatArray = new ArrayList<Integer>();
		
		for(int i = 1; i <= Integer.parseInt(beatSecond); ++i) {
			if(beatsMap.get(beatFirst+"+"+beatSecond+"+"+i)==null) 
				System.out.println("does not exist this type");
			else 
				beatArray.add(beatsMap.get(beatFirst+"+"+beatSecond+"+"+i));
			
		}
		
		
		
		FindPattern myPattern = new FindPattern();
		allPatternOne = myPattern.allPatternOne;
		allPatternTwo = myPattern.allPatternTwo;
		
		//ini patternPool
		patternPools = new ArrayList <Map<ArrayList<Integer>, ArrayList<Integer> > >();
		for(int i = 0; i <= MAX;++i) {
			patternPools.add(new HashMap<ArrayList<Integer>, ArrayList<Integer> >());
		}
		
		Iterator itr = allPatternOne.entrySet().iterator();
		while(itr.hasNext()) {
			
			 Map.Entry entry = (Map.Entry) itr.next();
			 Integer value = (Integer) entry.getKey();
			 HashSet key = (HashSet)entry.getValue();
			 System.out.println("size: " + value);
			 Iterator iter2 = key.iterator();
			 while(iter2.hasNext()) {
				 SequencePair cur = (SequencePair) iter2.next();
				 patternPools.get(value).put(cur.firstSeq, cur.secondSeq);
				 System.out.println(cur.firstSeq + " ? " + cur.secondSeq);

			 }
		}
		
		patternPoolsDuration = new ArrayList <Map<ArrayList<Integer>, ArrayList<Integer> > >();
		for(int i = 0; i <= MAX;++i) {
			patternPoolsDuration.add(new HashMap<ArrayList<Integer>, ArrayList<Integer> >());
		}
		
		itr = allPatternTwo.entrySet().iterator();
		while(itr.hasNext()) {
			
			 Map.Entry entry = (Map.Entry) itr.next();
			 Integer value = (Integer) entry.getKey();
			 HashSet key = (HashSet)entry.getValue();
			 System.out.println("size: " + value);
			 Iterator iter2 = key.iterator();
			 while(iter2.hasNext()) {
				 SequencePair cur = (SequencePair) iter2.next();
				 patternPoolsDuration.get(value).put(cur.firstSeq, cur.secondSeq);
				 System.out.println(cur.firstSeq + " & " + cur.secondSeq);

			 }
		}

		
		
		lrc = parseLrc();
		//System.out.println(lrc);
		//generate Relative Array
		for(int i = 0; i < lrc.size() -1 ;++i) {
			if(lrc.get(i+1) == 99) 
				lrc.set(i+1, lrc.get(i));
			lrc.set(i, lrc.get(i+1)-lrc.get(i));
		}
		lrc.remove(lrc.size()-1);
		
		//add beat information
		int position = 0;
		for(int i = 0; i < lrc.size(); ++i) {
			lrc.set(i, lrc.get(i) + beatArray.get(position));
			//System.out.println("add " + beatArray.get(position));
			position = (position+1) % beatArray.size();
		}
		
		
		System.out.println(lrc);
		
		//generate melo array
		int successLen = 0;
		int startPos = 0;
		while(startPos < lrc.size() - MIN) {
			boolean find = false;
			
			for(int i = MAX; i>MIN;i--) {
				ArrayList<Integer> searchResult = new ArrayList<Integer>();
				
				ArrayList<Integer> current = new ArrayList<Integer>();
				for(int j = startPos; j < startPos + i && j < lrc.size(); ++j) {
					current.add(lrc.get(j));	
				}
				
				if(patternPools.get(i).get(current)!=null) {
					successLen+=i;
					searchResult = patternPools.get(i).get(current);
					startPos+=i;
					find = true;
					melo.addAll(searchResult);
					break;
				}
				else {
					System.out.println("can not find for size "+ i);
				}
					
			}
			if(find == false) {
				System.out.println("cannnot find for any size");
				startPos++;
				melo.add(new Integer(0));
			}
			
		}
		
		while(startPos!= lrc.size()) {
			melo.add(new Integer(0));
			startPos++;
		}
		System.out.println("lrc size" + lrc.size());
		System.out.println("melo size" + melo.size());
		System.out.println("melo array is " + melo);
		System.out.println("success length" + successLen);
		
		
		//generate duration according to melo
		successLen = 0;
		startPos = 0;
		while(startPos < melo.size() - MIN) {
			boolean find = false;
			
			for(int i = MAX; i>MIN;i--) {
				ArrayList<Integer> searchResult = new ArrayList<Integer>();
				
				ArrayList<Integer> current = new ArrayList<Integer>();
				for(int j = startPos; j < startPos + i && j < melo.size(); ++j) {
					current.add(melo.get(j));	
				}
				
				if(patternPoolsDuration.get(i).get(current)!=null) {
					searchResult = patternPoolsDuration.get(i).get(current);
					startPos+=i;
					find = true;
					successLen+=i;
					dur.addAll(searchResult);
					break;
				}
				else {
					System.out.println("can not find for size "+ i);
				}
					
			}
			if(find == false) {
				System.out.println("cannnot find for any size");
				startPos++;
				dur.add(new Integer(0));
			}
			
		}
		while(startPos!=melo.size()) {
			dur.add(new Integer(1));
			startPos++;
		}
		System.out.println("melo size" + melo.size());
		System.out.println("dur size" + dur.size());
		System.out.println("dur array is " + dur);
		System.out.println("success length" + successLen);
		getJason();
		
		
	}
	
	
	private boolean checkArray(ArrayList<Integer> first, ArrayList<Integer> second) {
		// TODO Auto-generated method stub
		if(first.size() != second.size())		return false;
		for(int i = 0; i < first.size();++i) {
			if(first.get(i)!=second.get(i)) {
				return false;
			}
		}
		return true;
	}


	private static ArrayList<Integer> parseLrc() throws IOException {
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		str = str.replace("[^a-zA-Z]", "");
		str = str.replace("?", "");
		str = str.replace("!", "");
		str = str.replace(".", "");
		str = str.replace(",", "");
		str = str.replace(":", "");
		str = str.replace("()","");
		str = str.trim().replaceAll(" +", " ");
		
		System.out.println(str);
		RecordManager recman = RecordManagerFactory.createRecordManager("Dictionary");
		long recid = recman.getNamedObject("Dictionary");
		if(recid!=0) {
          	hashtable  = HTree.load(recman, recid);
          	//System.out.println("can open dictionary");
          }
          else {
          	hashtable = HTree.createInstance(recman);
          	recman.setNamedObject("Dictionary", hashtable.getRecid());
          }
		
		String[] strArray = str.split(" ");   //to be modified here
		
		
		for(String current: strArray) {
			WordStruct ws = (WordStruct) hashtable.get(current.toUpperCase());
			if(ws==null) {
				System.out.println("no: " + current);
				result.add(new Integer(99));
			}
				
			else {
				String stress = ws.stress;
				for(int i = 0;i < stress.length();++i) {
					result.add(Integer.parseInt(stress.substring(i, i+1)));
				}
			}
		}
		
		
		return result;
		
	}
	static void getJason() {
		  
		
		jasonResult = "[";
		//int first = startOne;
		//int two = startTwo;
		  
	       
	       for(int i = 0; i < lrc.size(); ++i) {
	    	   /*first+=lrc.get(i);
	    	   //System.out.println(lrc);
	    	   if(dur.get(i) > 0) {
	    		   two = two * dur.get(i);
	    	   }
	    	   else 
	    		   two = two / dur.get(i) * (-1);
	    	   if(two > 64) {
	    		   two =64;
	    	   }
	    	   if(two< 1)
	    		   two = 1;*/
	    	   
	    	   jasonResult = jasonResult +  "{keys:" + melo.get(i) +"," +"duration:" + dur.get(i) +"},";
	 
	       }
	       jasonResult = jasonResult.substring(0, jasonResult.length()-1);
	       jasonResult+="]";
	       System.out.println(jasonResult);
	       
		
	}
	
	
	static void beatMap() {
		beatsMap = new HashMap<String,Integer>();
		beatsMap.put("2+2+1", 3);
		beatsMap.put("2+2+2", 3);
		
		beatsMap.put("4+1+1", 3);
		
		beatsMap.put("4+2+1", 3);
		beatsMap.put("4+2+2", 1);
		
		beatsMap.put("4+3+1", 3);
		beatsMap.put("4+3+2", 1);
		beatsMap.put("4+3+3", 1);
		
		beatsMap.put("4+4+1", 3);
		beatsMap.put("4+4+2", 1);
		beatsMap.put("4+4+3", 2);
		beatsMap.put("4+4+4", 1);
		
		beatsMap.put("4+5+1", 3);
		beatsMap.put("4+5+2", 1);
		beatsMap.put("4+5+3", 3);
		beatsMap.put("4+5+4", 1);
		beatsMap.put("4+5+5", 1);
		
		beatsMap.put("4+6+1", 3);
		beatsMap.put("4+6+2", 1);
		beatsMap.put("4+6+3", 1);
		beatsMap.put("4+6+4", 2);
		beatsMap.put("4+6+5", 1);
		beatsMap.put("4+6+6", 1);
		
		beatsMap.put("8+3+1", 3);
		beatsMap.put("8+3+2", 1);
		beatsMap.put("8+3+3", 1);
		
		beatsMap.put("8+4+1", 3);
		beatsMap.put("8+4+2", 1);
		beatsMap.put("8+4+3", 2);
		beatsMap.put("8+4+4", 1);
		
		beatsMap.put("8+6+1", 3);
		beatsMap.put("8+6+2", 1);
		beatsMap.put("8+6+3", 1);
		beatsMap.put("8+6+4", 2);
		beatsMap.put("8+6+5", 1);
		beatsMap.put("8+6+6", 1);
		
		beatsMap.put("8+12+1", 3);
		beatsMap.put("8+12+2", 1);
		beatsMap.put("8+12+3", 1);
		beatsMap.put("8+12+4", 2);
		beatsMap.put("8+12+5", 1);
		beatsMap.put("8+12+6", 1);
		beatsMap.put("8+12+7", 2);
		beatsMap.put("8+12+8", 1);
		beatsMap.put("8+12+9", 1);
		beatsMap.put("8+12+10", 2);
		beatsMap.put("8+12+11", 1);
		beatsMap.put("8+12+12", 1);
	}
	
}
