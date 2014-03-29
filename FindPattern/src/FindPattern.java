import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class FindPattern {

	private static Scanner s;
	private static String pathname = "/Users/jenny/Desktop/output/"; 
	private static String singlePatternPath = "/Users/jenny/Desktop/singlePatternRecord/";
	private static String newSinglePatternPath = "/Users/jenny/Desktop/newSinglePatternRecord/";
	private static File[] listOfFiles; 
	static String filename;
	static int MIN = 2;  //minimum size of a pattern
	static int MAX = 25;  //maximum size of a pattern
	static HashMap<Integer, HashSet<SequencePair> > allPattern; //key = pattern size; value = all the patterns of a fixed size
	static HashMap<Integer, HashSet<NewSequencePair> > allPatternTime;
	static int threshold = 5;
	static int total = 0;
	static ArrayList<Pair> myList;
	static ArrayList<NewPair> myNewList;
	
	
	public static void main(String[] args) throws IOException {
		
		//initialize the allPattern
		allPattern = new HashMap<Integer, HashSet<SequencePair> >();
		allPatternTime = new HashMap<Integer, HashSet<NewSequencePair> >();
		
		ArrayList<Pair> myList = new ArrayList<Pair>();
		ArrayList<Pair> myNewList = new ArrayList<Pair>();
	
		//read from a directory
		File folder = new File(pathname);
		listOfFiles = folder.listFiles(); 
		
		//test
		//getPatterns(2);
		
		//test
		for(int i = 0; i < listOfFiles.length;++i) {
			System.out.println(listOfFiles[i].getName());
		}
		
		//initialize allPattern
		for(int i = MIN; i <=MAX; ++i) {
			allPattern.put(new Integer(i), new HashSet<SequencePair>());
			allPatternTime.put(new Integer(i), new HashSet<NewSequencePair>());
		}

		
		for(int i = 0; i < listOfFiles.length;++i) {
			if(listOfFiles[i].isFile()) {
				//System.out.println(listOfFiles[i].getName());
				
				filename = listOfFiles[i].getName();
				System.out.println("Generating for file " + filename);
				//if(filename == ".DS_Store") break;
				
				for(int j =MIN; j <= MAX; ++j) {
					HashSet<SequencePair> hash =  getPatterns(j);
					HashSet<NewSequencePair> newHash = getNewPatterns(j);
					
					Iterator iter = hash.iterator();
					while(iter.hasNext()) {
						allPattern.get(new Integer(j)).add((SequencePair) iter.next());
					}
					
					iter = newHash.iterator();
					while(iter.hasNext()) {
						allPatternTime.get(new Integer(j)).add((NewSequencePair) iter.next());
					}
					
					
					
				}
				
			}
			else if(listOfFiles[i].isDirectory()) {
				System.out.println("Error: this is a directory");
			}
		}
		
		//keep the record of of all patterns in a file
		BufferedWriter out = new BufferedWriter(new FileWriter(singlePatternPath+"allPattern.txt"));
		HashSet<SequencePair> temp = new HashSet<SequencePair>();
		
		for(int i = MIN; i <= MAX;++i) {
			temp = allPattern.get(i);
			int count= 0 ;
			out.write("size:" + i + "\n");
			Iterator it = temp.iterator();
			
			while(it.hasNext()) {
				SequencePair current = (SequencePair) it.next();
				ArrayList<String> lrc = current.lrcSeq;
				ArrayList<String> melo = current.meloSeq;
				out.write("{");
				for(int j = 0; j <lrc.size();++j) {
					out.write("{" + lrc.get(j) +",");
					out.write(melo.get(j) + "}");
				}
				out.write("}");
				out.newLine();
				count++;
			}
			out.write("! there are totally " +  count + " frequent patterns of size " + i +  " when threshold is set to " + threshold);
			out.newLine();
			total += count;
			
		}
		out.write("! there are in sum " + total + " patterns");
		if(out!=null)
			out.close();
		
		//duration pattern
		out = new BufferedWriter(new FileWriter(newSinglePatternPath+"allPatternDuration.txt"));
		HashSet<NewSequencePair> newTemp = new HashSet<NewSequencePair>();
		
		for(int i = MIN; i <= MAX;++i) {
			newTemp = allPatternTime.get(i);
			int count= 0 ;
			out.write("size:" + i + "\n");
			Iterator it = newTemp.iterator();
			
			while(it.hasNext()) {
				NewSequencePair current = (NewSequencePair) it.next();
				ArrayList<String> melo = current.meloSeq;
				ArrayList<String> dur = current.durSeq;
				out.write("{");
				for(int j = 0; j <melo.size();++j) {
					out.write("{" + melo.get(j) +",");
					out.write(dur.get(j) + "}");
				}
				out.write("}");
				out.newLine();
				count++;
			}
			out.write("! there are totally " +  count + " frequent patterns of size " + i +  " when threshold is set to " + threshold);
			out.newLine();
			total += count;
			
		}
		out.write("! there are in sum " + total + " patterns");
		if(out!=null)
			out.close();
		
	
	}
	
	
	private static  HashSet<NewSequencePair> getNewPatterns(int size) throws IOException {
		
		HashSet<NewSequencePair> mySet = new HashSet<NewSequencePair>();
		
		generateSequence();
		myNewList = (ArrayList<NewPair>) generateRelatveNewSequence(myNewList);
		
		//generate a Map(key = sequence pair of size n , the number of the pair across the song)
		Map<NewSequencePair, Integer> newSequenceCount = new HashMap<NewSequencePair, Integer>();
		NewSequencePair tempNewSequence;
		ArrayList<String> first;
		ArrayList<String> second;
		NewPair newCurrent = null;
		
		for(int i = 0 ; i  < myNewList.size() - size; ++i) {
			
			first = new ArrayList();
			second = new ArrayList();
	
			
			for(int j = i; j < i + size; ++j) {
				
				newCurrent = myNewList.get(i);
				
				first.add(newCurrent.melo);
				second.add(newCurrent.dur);
				//System.out.println(newCurrent.melo + " : " + newCurrent.dur);
			}
			tempNewSequence = new NewSequencePair(first,second);
			
			//printSequencePair(tempSequence);
			
			
			Integer newCount = newSequenceCount.get(tempNewSequence);
			newSequenceCount.put(tempNewSequence, newCount == null? 1: newCount+1);
			
		}
	
		BufferedWriter out = new BufferedWriter(new FileWriter(newSinglePatternPath + filename));
		out.write("size: " + size );
		out.newLine();

		int count = 0;
		Iterator iter = newSequenceCount.keySet().iterator();
		while(iter.hasNext()) {
		    NewSequencePair key = (NewSequencePair)iter.next();
		    Integer val = (Integer)newSequenceCount.get(key);
		    
		    if(val >= threshold) {
		    	
		       mySet.add(key);
			   count ++;
			   //printSequencePair(System.out, key);
			   //printSequencePair(out,key);
			   //System.out.println("," + val);
			   
			  //keep a record of frequent patterns for a specific song in a file
			   out.write("{");
			   ArrayList<String> melo = key.meloSeq;
			   ArrayList<String> dur = key.durSeq;
			   if(melo.size()!=melo.size()) {
					System.out.println("Writing to File Error");
					return null;
				}
			   
				for(int i = 0 ; i < melo.size(); ++i) {
					System.out.println("{" + melo.get(i) +":" + dur.get(i) + "}");
					out.write("{" + melo.get(i) +"," + dur.get(i) + "}");
				}
				
				out.write("}");
				out.write(":" + val);
				out.newLine();
				
			   
		   }
		    
		}
		out.write("! there are totally " +  count + " frequent patterns of size " + size +  " when threshold is set to " + threshold);
		out.newLine();
		if(out!=null) {
	    	out.close();
	    }
		
		
		//System.out.println("there are totally " +  count + " frequent patterns of size " + size +  " when threshold is set to " + threshold);

		return mySet;
	}
	
	
	private static  HashSet<SequencePair> getPatterns(int size) throws IOException {
	
		HashSet<SequencePair> mySet = new HashSet<SequencePair>();
		
		generateSequence();
		myList = (ArrayList<Pair>) generateRelatveSequence(myList);
		
		//generate a Map(key = sequence pair of size n , the number of the pair across the song)
		Map<SequencePair, Integer> sequenceCount = new HashMap<SequencePair, Integer>();
		SequencePair tempSequence;
		ArrayList<String> first;
		ArrayList<String> second;
	
		Pair current = null;
		NewPair newCurrent = null;
		
		for(int i = 0 ; i  < myList.size() - size; ++i) {
			
			first = new ArrayList();
			second = new ArrayList();
		
			
			for(int j = i; j < i + size; ++j) {
				current  = myList.get(j);
				
				first.add(current.lrc);
				second.add(current.melo);
			
				
			}
			tempSequence = new  SequencePair(first,second);
		
			
			//printSequencePair(tempSequence);
			
			Integer count = sequenceCount.get(tempSequence);
			sequenceCount.put(tempSequence, count == null? 1: count+1);
			
		}
	
		BufferedWriter out = new BufferedWriter(new FileWriter(singlePatternPath + filename, true));
		out.write("size: " + size );
		out.newLine();

		int count = 0;
		Iterator iter = sequenceCount.keySet().iterator();
		while(iter.hasNext()) {
		    SequencePair key = (SequencePair)iter.next();
		    Integer val = (Integer)sequenceCount.get(key);
		    
		    if(val >= threshold) {
		    	
		       mySet.add(key);
			   count ++;
			   printSequencePair(System.out, key);
			   //printSequencePair(out,key);
			   System.out.println("," + val);
			   
			  //keep a record of frequent patterns for a specific song in a file
			   out.write("{");
			   ArrayList<String> lrc = key.lrcSeq;
			   ArrayList<String> melo = key.meloSeq;
			   if(lrc.size()!=melo.size()) {
					System.out.println("Writing to File Error");
					return null;
				}
			   
				for(int i = 0 ; i < lrc.size(); ++i) {
					out.write("{" + lrc.get(i) +"," + melo.get(i) + "}");
				}
				
				out.write("}");
				out.write(":" + val);
				out.newLine();
				
			   
		   }
		    
		}
		out.write("! there are totally " +  count + " frequent patterns of size " + size +  " when threshold is set to " + threshold);
		out.newLine();
		if(out!=null) {
	    	out.close();
	    }
		
		System.out.println("there are totally " +  count + " frequent patterns of size " + size +  " when threshold is set to " + threshold);

		return mySet;
	}
	
	public static void printSequencePair(OutputStream out, SequencePair current) {
		
		System.out.print("[");
		ArrayList<String> lrc = current.lrcSeq;
		ArrayList<String> melo = current.meloSeq;
		if(lrc.size()!=melo.size()) {
			System.out.println("Error");
			return;
		}
		
		for(int i = 0 ; i < lrc.size(); ++i) {
			System.out.print("{" + lrc.get(i) +"," + melo.get(i) + "}");
		}
		
		System.out.print("]");
		
	}


	private static List<Pair> generateRelatveSequence(List<Pair> myList) {
		// TODO Auto-generated method stub
		List<Pair> relList = new ArrayList<Pair>();
		String lrc,melo;
		for (int i = 1; i < myList.size();++i) {
			
		    lrc = Integer.toString(Integer.parseInt(myList.get(i).lrc) - Integer.parseInt(myList.get(i-1).lrc));
		    melo =  Integer.toString(Integer.parseInt(myList.get(i).melo) - Integer.parseInt(myList.get(i-1).melo));
			relList.add(new Pair(lrc, melo));
		}
		
		return relList;
	}
	
	private static List<NewPair> generateRelatveNewSequence(ArrayList<NewPair> myList2) {
		// TODO Auto-generated method stub
		List<NewPair> relList = new ArrayList<NewPair>();
		String melo,dur;
		for (int i = 1; i < myList2.size();++i) {
			
		    melo = Integer.toString(Integer.parseInt(myList2.get(i).melo) - Integer.parseInt(myList2.get(i-1).melo));
		    //System.out.println("banabn" + myList2.get(i).dur+ " " + myList2.get(i-1).dur);
		    //System.out.println("apple " + Double.parseDouble(myList2.get(i).dur)/Double.parseDouble(myList2.get(i-1).dur));
		    dur =  Double.toString(Double.parseDouble(myList2.get(i).dur)/Double.parseDouble(myList2.get(i-1).dur));
		    
			relList.add(new NewPair(melo, dur));
		}
		
		return relList;
	}

	private static void generateSequence() throws FileNotFoundException {
		// TODO Auto-generated method stub
		
		s = new Scanner(new BufferedReader(new FileReader(pathname + filename)));
		
		//an arrayList that keep the sequence of melody and lrc
		myList = new ArrayList<Pair>();
		myNewList = new ArrayList<NewPair>();
		
		String line = null;
		int wordStress;
		int pitch;
		int melStress;
		int stress;
		int duration;
		
		while(s.hasNextLine()) {
			line = s.nextLine();
			String[] temp = line.split(",");
			
			
			/*for(int i = 1; i <temp.length;++i) {
				System.out.print("[" + temp[i] + "]");
			
			}
			System.out.println();*/
			
			wordStress = Integer.parseInt(temp[1]);
			pitch = Integer.parseInt(temp[2]);
			melStress = Integer.parseInt(temp[3]);
			duration = Integer.parseInt(temp[4]);

			
			//combine word level stress and sentence level stress
			stress = wordStress * 3 + melStress;
			if(stress < 0 || stress > 9) {
				System.out.println("Stress range error");
			}
		
			myList.add(new Pair(Integer.toString(stress) ,Integer.toString(pitch)));
			myNewList.add(new NewPair(Integer.toString(pitch) ,Integer.toString(duration)));
			
		}
		
	    /*for(int i = 0; i < myList.size();++i) { 
			System.out.println(myList.get(i).lrc + " "  + myList.get(i).melo);
		}*/
	
	}
}
