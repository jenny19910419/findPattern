package Clustsering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import FindPattern.SequencePair;

public class Similarity {
	
	

	private static String inputPath = "/Users/jenny/Desktop/singlePatternRecord/"; 
	private static File[] listOfFiles; 
	private static int fileNum;
	private static double matrix[][];//the result matrix
	
	public static void main(String[] args) throws IOException {
		
		File folder = new File(inputPath);
		listOfFiles = folder.listFiles();
		int outer = 0;
		int inner = 0;
		fileNum = listOfFiles.length;
		//fileNum=5;
		matrix = new  double[fileNum][fileNum];
		
		String filenameOne;
		
		for(int i = 0 ; i < fileNum;++i) {
			//int inner = 0;
			filenameOne = listOfFiles[i].getName();
			//System.out.println(filename);
			if(filenameOne.equals(".DS_Store")) continue;
			outer++;
			
			String filenameTwo;
			for(int j = 0; j < fileNum;++j) {
				filenameTwo = listOfFiles[j].getName();
				if(filenameTwo.equals(".DS_Store")) continue;
				inner++;
				//System.out.println(filenameOne + "  " + filenameTwo);
				matrix[i][j] = CompareTwoFile(filenameOne,filenameTwo);
			}
			//System.out.println(inner);
			
		}
		/*if(outer != fileNum) {
			System.out.println("wrong dimension");
		}*/
		//System.out.println(outer * outer + " : " + inner);
		logTheMatrix();
		genreateWekaFile();

	}

	private static void genreateWekaFile() throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter out = new BufferedWriter(new FileWriter("/Users/jenny/Desktop/music.arff"));

		out.write("@relation music"+"\n\n");
		
		File folder = new File(inputPath);
		listOfFiles = folder.listFiles();
		String filename;
		for(int i = 0; i < fileNum;++i) {
			filename = listOfFiles[i].getName();
			
			if(filename.equals(".DS_Store")) continue;
			//System.out.println(filename);
			out.write("@attribute "+ i + " numeric" +"\n");
		}
		out.write("\n");
		
		out.write("@data" +"\n");
		
		for(int i = 1; i < fileNum;++i) {
			out.write(Double.toString(matrix[i][1]));
			for(int j = 2; j < fileNum;++j) {
				out.write("," + Double.toString(matrix[i][j]));
			}
			out.write("\n");
		}
		if(out!=null)
			out.close();
	
		
	}

	private static void logTheMatrix() throws IOException {
		// TODO Auto-generated method stub
		
		BufferedWriter out = new BufferedWriter(new FileWriter("/Users/jenny/Desktop/Matrix.txt"));

		for(int i = 1; i < fileNum;++i) {
			for(int j = 1; j < fileNum;++j) {
				out.write(matrix[i][j]+ " ");
			}
			out.write("\n");
		}
		if(null!=out)
			out.close();
		
	}

	private static double CompareTwoFile(String one, String two) throws FileNotFoundException {
		// TODO Auto-generated method stub
		double result = -1;
		HashSet<SequencePair> first = readInPatterns(one);
		HashSet<SequencePair> second = readInPatterns(two);
		
		HashSet<SequencePair> union = getUnion(first,second);
		HashSet<SequencePair> inter = getIntersection(first,second);
		System.out.println(one + "  " + two);
		System.out.println("union: " + union.size() + "    inter: " + inter.size());
		//System.out.println("first file  " + one+ " pattern size:" + first.size());
		//System.out.println("second file  " + two + " pattern size:" + second.size());
		result = inter.size() * 1.0/union.size();
		return result;
		
	}
	
	
	private static HashSet getIntersection(HashSet<SequencePair> first,
			HashSet<SequencePair> second) {
		// TODO Auto-generated method stub
		HashSet<SequencePair> result= new HashSet<SequencePair>();
		 for (SequencePair current : first) {
	            if(second.contains(current)) {
	                result.add(current);
	            }
	        }
		
		return result;
	}

	private static HashSet getUnion(HashSet<SequencePair> first,
			HashSet<SequencePair> second) {
		// TODO Auto-generated method stub
		HashSet<SequencePair> result= new HashSet<SequencePair>();
		result.addAll(first);
		result.addAll(second);
		
		return result;
	}

	private static HashSet<SequencePair> readInPatterns(String filename) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner s = new Scanner(new File(inputPath + filename));
		String line;
		ArrayList<Integer> lrc = new ArrayList<Integer>();
		ArrayList<Integer> melo = new ArrayList<Integer>();
		HashSet<SequencePair> result = new HashSet<SequencePair>();
	
		int size  = - 1;
		int count = 0;

		while(s.hasNextLine()) {
			line  = s.nextLine();
			int p = line.lastIndexOf(":");
			//System.out.println("test: " + line + " : " + p);
			line = line.substring(0,p);
			
			
			lrc = new ArrayList<Integer>();
			melo = new ArrayList<Integer>();
			line = line.substring(1,line.length()-1);
			int pos =line.indexOf(":");
			String first = line.substring(1,pos-1);
			String second = line.substring(pos+2,line.length()-1);
			//System.out.println("["+ first + "]=[" + second + "]");
			String[] one = first.split(",");
			String[] two = second.split(",");
			for(int i = 0; i < one.length;++i) {
				one[i] = one[i].trim();
			    two[i] = two[i].trim();
				//System.out.println("[" + one[i] + "]");
				lrc.add(Integer.parseInt(one[i]));
				melo.add(Integer.parseInt(two[i]));
			}
			result.add(new SequencePair(lrc,melo));
			
		}	
		if(s!=null) 
			s.close();
		return result;
	}
	
}
