package FindPattern;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class FindPattern {

	private static String inputPath = "/Users/jenny/Desktop/output/"; 
	private static String singlePatternPath = "/Users/jenny/Desktop/singlePatternRecord/";
	private static String newSinglePatternPath = "/Users/jenny/Desktop/newSinglePatternRecord/";
	private static File[] listOfFiles; 
	private static String filename;  //the file that currently dealing with
	private static HashMap<Integer, HashSet<SequencePair> > allPatternOne; //[key patternSize : value pattern
	private static HashMap<Integer, HashSet<SequencePair> > allPatternTwo;
	private static HashMap<SequencePair, Integer> countOne;
	private static HashMap<SequencePair, Integer> countTwo;
	private static HashMap<SequencePair,Integer> hashOne; //current generated patterns for a file of size n pattern;
    private static HashMap<SequencePair,Integer> hashTwo; //current generated patterns for a file of size n pattern;
	private static ArrayList<Integer> lrcSeq;//lrc sequence for current file
	private static ArrayList<Integer> meloSeq;//melo sequence for current file 
	private static ArrayList<Double> durSeq;//duration sequence for current file
	
	
	static int MIN = 2;
	static int MAX = 20;
	//static int threshold = 2;
	
	public static void main(String[] arsg) throws IOException {
		
		allPatternOne = new HashMap<Integer, HashSet<SequencePair> >();
		allPatternTwo = new HashMap<Integer, HashSet<SequencePair> >();
		countOne = new HashMap<SequencePair, Integer>();
		countTwo = new HashMap<SequencePair, Integer>();
		
		File folder = new File(inputPath);
		listOfFiles = folder.listFiles();
		
		/*for(int i = 0; i < listOfFiles.length;++i) {
			if(listOfFiles[i].getName().equals(".DS_Store")) continue;
			System.out.println(listOfFiles[i].getName());
		}*/
		
		/*for(int i = 0; i < listOfFiles.length;++i) {
			filename = listOfFiles[i].getName();
			if(filename.equals(".DS_Store")) continue;
			
		}*/
		
		
		//initialize allPattern
		for(int i = MIN; i <=MAX; ++i) {
			allPatternOne.put(new Integer(i), new HashSet<SequencePair>());
			allPatternTwo.put(new Integer(i), new HashSet<SequencePair>());
		}

		
		
		//test
		/*filename = "50_cent-in_da_club.txt";
		generateSequence();
		for(int i = 0; i < lrcSeq.size();++i) {
			System.out.println(lrcSeq.get(i) + ":" + meloSeq.get(i) + ":" + durSeq.get(i));
		}
		getPatterns(3);*/
	
		
		
		
		for(int i = 0; i < listOfFiles.length;++i) {
			filename = listOfFiles[i].getName();
			System.out.println(filename);
			if(filename.equals(".DS_Store")) continue;
			

			int temp = countOne.size();
			
			for(int j =MIN; j <= MAX; ++j) {
				
				//generate current hashOne and hashTwo
				generateSequence();
				getPatterns(j);
				//System.out.println("size " + j + " " + hashOne.size());
				

				Iterator iter = hashOne.entrySet().iterator();
				while(iter.hasNext()) {
					Map.Entry entry = (Entry) iter.next();
					SequencePair key = (SequencePair) entry.getKey();
					Integer value = (Integer) entry.getValue();
					
					if(countOne.containsKey(key)) {
						countOne.put(key, countOne.get(key) + value);
					}
					else
						countOne.put(key, value);
				}
				
				iter = hashTwo.entrySet().iterator();
				while(iter.hasNext()) {
					Map.Entry entry = (Entry) iter.next();
					SequencePair key = (SequencePair) entry.getKey();
					Integer value = (Integer) entry.getValue();
					
					if(countTwo.containsKey(key)) {
						countTwo.put(key, countTwo.get(key) + value);
					}
					else countTwo.put(key, value);
				}
				
				
			}
			temp = countOne.size()-temp;
			//System.out.println("add patterns" +  temp);
			
			//log 
			generateForSingleFile();
		}
		
		/*System.out.println("Count one size:  " + countOne.size());//792
		Iterator itr = countOne.entrySet().iterator();
		while(itr.hasNext()) {
			 Map.Entry entry = (Map.Entry) itr.next();
			 SequencePair key = (SequencePair)entry.getKey();
			 Integer value = (Integer)entry.getValue();
			 System.out.println("[" + key.firstSeq +":"+key.secondSeq +"]" + " ;  "+ value);
		}
		System.out.println(countOne.size());*/
		
		//if given one sequence of lrc, choose the melo sequence that appears the most time
		HashMap<ArrayList<Integer>, Integer> mostPattern = new HashMap<ArrayList<Integer>, Integer>();
		
		Iterator iter = countOne.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Entry) iter.next();
			Integer value = (Integer) entry.getValue();
			SequencePair key = (SequencePair) entry.getKey();//System.out.println("current" + "[" + key.firstSeq +":"+key.secondSeq +"]" + " ;  "+ value);
			
			if(mostPattern.containsKey(key.firstSeq)) {
				if(mostPattern.get(key.firstSeq) < value) {
					mostPattern.put(key.firstSeq, value);
				}
			}
			else 
				mostPattern.put(key.firstSeq, value);
		}
		
		//TEST
		/*System.out.println("dddd: " + hashOne.size() + " " + mostPattern.size());
		iter = mostPattern.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Entry) iter.next();
			ArrayList<Integer> key = (ArrayList<Integer>) entry.getKey();
			Integer value = (Integer) entry.getValue();
			System.out.println(key+":" +value);
		}*/
		
		
		HashMap<SequencePair, Integer>  countOnetemp = new HashMap<SequencePair, Integer>();
		iter = countOne.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Entry) iter.next();
			SequencePair key = (SequencePair) entry.getKey();
			Integer value = (Integer) entry.getValue();
			if(value == mostPattern.get(key.firstSeq)) {
				countOnetemp.put(key, value);
				//System.out.println("current" + "[" + key.firstSeq +":"+key.secondSeq +"]" + " ;  "+ value);
			}
				
		}
		countOne = countOnetemp;
		
        HashMap<ArrayList<Integer>, Integer> mostPatternTwo = new HashMap<ArrayList<Integer>, Integer>();
		
		iter = countTwo.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Entry) iter.next();
			Integer value = (Integer) entry.getValue();
			SequencePair key = (SequencePair) entry.getKey();//System.out.println("current" + "[" + key.firstSeq +":"+key.secondSeq +"]" + " ;  "+ value);
			
			if(mostPatternTwo.containsKey(key.firstSeq)) {
				if(mostPatternTwo.get(key.firstSeq) < value) {
					mostPatternTwo.put(key.firstSeq, value);
				}
			}
			else 
				mostPatternTwo.put(key.firstSeq, value);
		}
		
		HashMap<SequencePair, Integer>  countTwotemp = new HashMap<SequencePair, Integer>();
		iter = countTwo.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Entry) iter.next();
			SequencePair key = (SequencePair) entry.getKey();
			Integer value = (Integer) entry.getValue();
			if(value == mostPattern.get(key.firstSeq))
				countTwotemp.put(key, value);
		}
		countTwo = countTwotemp;
		
		
		
		
		//overall data
		int sum1 = 0;
		int sum2 = 0;
		Iterator entries = countOne.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();
		    SequencePair key = (SequencePair)entry.getKey();
		    Integer value = (Integer)entry.getValue();
		    //System.out.println("Key = " + key + ", Value = " + value);
		    //if(key.firstSeq.size())
		    sum1++;
	    	allPatternOne.get(key.firstSeq.size()).add(key);
		    /*if(value >= threshold) {
		    	sum1++;
		    	allPatternOne.get(key.firstSeq.size()).add(key);
		    }*/
		}
		
		entries = countTwo.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();
		    SequencePair key = (SequencePair)entry.getKey();
		    Integer value = (Integer)entry.getValue();
		    //System.out.println("Key = " + key + ", Value = " + value);
		    //if(key.firstSeq.size())
		    sum2++;
	    	allPatternTwo.get(key.firstSeq.size()).add(key);
		    /*if(value > threshold) {
		    	sum2++;
		    	allPatternTwo.get(key.firstSeq.size()).add(key);
		    }*/
		}
		System.out.println("sum1 " + sum1);
		System.out.println("sum2 " + sum2);
		
		
		//test 
		/*int count = 0;
		entries = allPatternOne.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();
		    Integer value = (Integer)entry.getKey();
		    HashSet<SequencePair> set = (HashSet<SequencePair>) entry.getValue();
		    count += set.size();
		}
		System.out.println("result"+ count);*/
		
		//record the all pattern informations in log files
		LogOverall();
		
		
		
		
		
		
	}

	private static void generateSequence() throws FileNotFoundException {
		// TODO Auto-generated method stub
        Scanner s = new Scanner(new BufferedReader(new FileReader(inputPath + filename)));
		lrcSeq = new ArrayList<Integer>();
		meloSeq = new ArrayList<Integer>();
		durSeq = new ArrayList<Double>();
		
		String line = null;
		int wordStress;
		int pitch;
		int melStress;
		int stress;
		double duration;
		
		while(s.hasNextLine()) {
			line = s.nextLine();
			String[] temp = line.split(",");
			
			wordStress = Integer.parseInt(temp[1]);
			pitch = Integer.parseInt(temp[2]);
			melStress = Integer.parseInt(temp[3]);
			duration = Double.parseDouble(temp[4]);
			

			//combine word level stress and sentence level stress
			//stress = wordStress * 3 + melStress;
			/*if(stress < 0 || stress > 9) {
				System.out.println("Stress range error");
			}*/
			lrcSeq.add(wordStress);
			meloSeq.add(pitch);
			durSeq.add(duration);
		}
		
		//calculate relative value
		for(int i = 0;i < lrcSeq.size() -1;++i) {
			lrcSeq.set(i, lrcSeq.get(i+1)-lrcSeq.get(i));
			meloSeq.set(i, meloSeq.get(i+1) - meloSeq.get(i));
			durSeq.set(i, durSeq.get(i+1) / durSeq.get(i));
		}
		lrcSeq.remove(lrcSeq.size()-1);
		meloSeq.remove(meloSeq.size()-1);
		durSeq.remove(durSeq.size()-1);
		
	}

	private static void LogOverall() throws IOException {
		
	
		// TODO Auto-generated method stub
		BufferedWriter out;
		Iterator entries = allPatternOne.entrySet().iterator();
		
		 out = new BufferedWriter(new FileWriter(singlePatternPath+"allPatternOneMore.txt"));
		    int count1 = 0;
		    entries = allPatternOne.entrySet().iterator();
			while (entries.hasNext()) {
			    Map.Entry entry = (Map.Entry) entries.next();
			    Integer value = (Integer)entry.getKey();
			    HashSet<SequencePair> set = (HashSet<SequencePair>) entry.getValue();
			    count1+=set.size();
			    out.write("!size " + value + " " + set.size() + "\n");
			    Iterator itr = set.iterator();
			    while(itr.hasNext()) {
			    	SequencePair current = (SequencePair) itr.next();
			        out.write("[" + current.firstSeq +":"+current.secondSeq +"]"+"\n");
			    }
			}
			out.write("! total " + count1);
			//System.out.println(count2);
			if(out!=null)
				out.close();
		
	
		
		
	    out = new BufferedWriter(new FileWriter(newSinglePatternPath+"allPatternTwoMore.txt"));
	    int count2 = 0;
	    entries = allPatternTwo.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();
		    Integer value = (Integer)entry.getKey();
		    HashSet<SequencePair> set = (HashSet<SequencePair>) entry.getValue();
		    count2+=set.size();
		    out.write("!size " + value + " " + set.size() + "\n");
		    Iterator itr = set.iterator();
		    while(itr.hasNext()) {
		    	SequencePair current = (SequencePair) itr.next();
		        out.write("[" + current.firstSeq +":"+current.secondSeq +"]"+"\n");
		    }
		}
		out.write("! total " + count2);
		//System.out.println(count2);
		if(out!=null)
			out.close();
		
		
	}

	private static void generateForSingleFile() {
		// TODO Auto-generated method stub
		
	}
	
	//given a file, generate all sequence of  size 
	private static void getPatterns(int size) {
		// TODO Auto-generated method stub
		hashOne = new HashMap<SequencePair,Integer>();
		hashTwo = new HashMap<SequencePair,Integer>();
		ArrayList<Integer> first;
		ArrayList<Integer> second;
		ArrayList<Double> third;
		
		//System.out.println(lrcSeq.size());
		
		for(int i = 0; i < lrcSeq.size() - size ;++i) {
			first = new ArrayList<Integer>();
			second = new ArrayList<Integer>();
			third = new ArrayList<Double>();
			
			for(int j = i; j < i+ size ;++j) {
				first.add(lrcSeq.get(j));
				second.add(meloSeq.get(j));
				third.add(durSeq.get(j));
			}
			//System.out.println(first);
			//System.out.println(second);
			//System.out.println(third);
			SequencePair cur= new SequencePair(first, second);
		
			int count = hashOne.containsKey(cur)?hashOne.get(cur):0;
			hashOne.put(cur, count+1);
			
			cur = new SequencePair(second, third);
			count = hashTwo.containsKey(cur)?hashTwo.get(cur):0;
			hashTwo.put(cur, count+1);
		
		}
		
		//test
		/*System.out.println("original" + lrcSeq.size());
		int count = 0;
		Iterator itr = hashOne.entrySet().iterator();
		while(itr.hasNext()) {
			Map.Entry entry = (Entry) itr.next();
			Integer value = (Integer) entry.getValue();
			SequencePair key = (SequencePair) entry.getKey();
			System.out.println("[" + key.firstSeq +":"+key.secondSeq +"]" + " ;  "+ value);
			
			
			count+=value;
		}
		System.out.println("recombine" + count + "hashOne size: " + hashOne.entrySet().size());*/
	
	}
	
}
