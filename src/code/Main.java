package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

import javax.print.DocFlavor.URL;

public class Main {
	
	
	
	
	public static final int SUPPORT = 100;
	
	static Hashtable<String, Integer>  ids = new Hashtable<String, Integer>();
	static Hashtable<String, Integer> freqIds = new Hashtable<String, Integer>();
	static Hashtable<Integer, String> reverseFreqIds = new Hashtable<Integer, String>();
	static Hashtable<String, Integer> freqPairIds = new Hashtable<String, Integer>();
	static Hashtable<Integer, String> reversefreqPairIds = new Hashtable<Integer, String>();
	static Hashtable<String, Integer> freqTriples = new Hashtable<String, Integer>();
	static Hashtable<Integer, String> reverseFreqTriples = new Hashtable<Integer, String>();
	static Hashtable<String, Integer> triplesCount = new Hashtable<String, Integer>();
	static ArrayList<String> lines = new ArrayList<String>();
	static Hashtable<String, Double> confidenceOne = new Hashtable<String, Double>();
	static Hashtable<String, Double> confidenceTwo = new Hashtable<String, Double>();
	
	public static void main(String[] args) throws IOException {
	
		System.out.println("Processing data: Please Wait ...");
		processData();
		filter();
		int size = freqIds.size();
		int[][] list = new int[size][size];
		ArrayList<String> finalOne = new ArrayList<String>();
		ArrayList<String> finalTwo = new ArrayList<String>();

		countPairs(list);
		int index = 0;
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < list.length; j++) {
				if (!freqPairIds.contains(reverseFreqIds.get(i) + "," + reverseFreqIds.get(j))) {
					
					if (list[i][j] >= SUPPORT) {
						freqPairIds.put(reverseFreqIds.get(i) + "," + reverseFreqIds.get(j), list[i][j]);
						reversefreqPairIds.put(list[i][j], reverseFreqIds.get(i) + "," + reverseFreqIds.get(j));
						index++;
					}					
				}
			}
		}
		
		confidencePairs();
		getAllLines();
		createTriples(list);
		countTriples();
		confidenceTriples();
				
		sortValue(confidenceOne, finalOne);
		sortValue(confidenceTwo, finalTwo);
		
		Collections.sort(finalTwo);
		
		PrintWriter writer = new PrintWriter("./output.txt", "UTF-8");
		
		writer.println("OUTPUT A");
		for (int j = 0; j < 5; j++) {
			writer.println(finalOne.get(j));
		}
		writer.println("OUTPUT B");
		for (int j = 0; j < 5; j++) {
			writer.println(finalTwo.get(j));
		}
		writer.close();
		
	}
	
	public static void sortValue(Hashtable<?, Double> t, ArrayList<String> finalResult) {

		   
		   String temp;
		   String[] total;
	       ArrayList<Map.Entry<?, Double>> l = new ArrayList(t.entrySet());
	       Collections.sort(l, new Comparator<Map.Entry<?, Double>>() {

	         public int compare(Map.Entry<?, Double> o1, Map.Entry<?, Double> o2) {
	            return o2.getValue().compareTo(o1.getValue());
	        }});

	       for (int i = 0; i < 15; i++) {
	    	   temp = l.get(i).toString();
	    	   total = temp.split(",|\\=");
	    	   if (total.length == 3) {
	    		   finalResult.add(total[0] + " " + total[1] + " " + total[2]);
	    		   //System.out.println(total[0] + " " + total[1] + " " + total[2]);
	    	   } else {
	    		   //System.out.println(total[0] + " " + total[1] + " " + total[2] + " " + total[3]);
	    		   finalResult.add(total[0] + " " + total[1] + " " + total[2] + " " + total[3]);
	    	   }  
	       }
	    	       
	}
	
	public static void getAllLines() throws IOException {
		File file = new File("./browsingdata.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(file));
	    String line = null;
	    String[] browseIds;
	    while ((line = br.readLine()) != null) {
	    	lines.add(line);
	    }
	    br.close();
	}
	
	public static void countTriples() {

		 String[] browseIds;
		 String temp;
        for(String key : freqTriples.keySet()) {
        	browseIds = key.split(",");  	
        	for (String line : lines) {
            	if (line.contains(browseIds[0]) && line.contains(browseIds[1]) && line.contains(browseIds[2])) {
            		Arrays.sort(browseIds);
            		temp = browseIds[0] + "," + browseIds[1] + "," + browseIds[2];
            		
            		if (triplesCount.get(temp) == null) {
            			triplesCount.put(temp, 1);
            		} else {
            			triplesCount.put(temp, triplesCount.get(temp) + 1);
            		}
            	}
        	}
        }
		
	}
	
	
	public static void createTriples(int[][] list) throws IOException {
		int index = 0;
		String temp;
		for (int k = 0; k < freqIds.size(); k++) {
			for (int i = k + 1; i < freqIds.size(); i++) {
				for (int j = i + 1; j < freqIds.size(); j++) {
					
					if (list[freqIds.get(reverseFreqIds.get(k))][freqIds.get(reverseFreqIds.get(i))] >= SUPPORT || list[freqIds.get(reverseFreqIds.get(i))][freqIds.get(reverseFreqIds.get(k))] >= SUPPORT) {
						if (list[freqIds.get(reverseFreqIds.get(k))][freqIds.get(reverseFreqIds.get(j))] >= SUPPORT || list[freqIds.get(reverseFreqIds.get(j))][freqIds.get(reverseFreqIds.get(k))] >= SUPPORT) {
							if (list[freqIds.get(reverseFreqIds.get(i))][freqIds.get(reverseFreqIds.get(j))] >= SUPPORT || list[freqIds.get(reverseFreqIds.get(j))][freqIds.get(reverseFreqIds.get(i))] >= SUPPORT) {
								temp = reverseFreqIds.get(k) + "," + reverseFreqIds.get(i) + ","  + reverseFreqIds.get(j);
								freqTriples.put(temp, index);
								reverseFreqTriples.put(index, temp);
								index++;
							}
						}
					}
				}
			}
		}
	}
	
	
	
	public static void countPairs(int[][] list) throws IOException {

		File file = new File("./browsingdata.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(file));
	    String line = null;
	    String[] browseIds;
	    while ((line = br.readLine()) != null) {
	    	browseIds = line.split(" ");
	    	for (int i = 0; i < browseIds.length; i++) {
	    		for (int j = i + 1; j < browseIds.length; j++) {	    			
	    			if (freqIds.get(browseIds[i]) != null && freqIds.get(browseIds[j]) != null) {
    					
	    				if (freqIds.get(browseIds[i]) > freqIds.get(browseIds[j])) {
	    					list[freqIds.get(browseIds[j])][freqIds.get(browseIds[i])] += 1;
	    				} else {
	    					list[freqIds.get(browseIds[i])][freqIds.get(browseIds[j])] += 1;
	    				}
    				}
	    		}
	    	}
	    }
	    br.close();
	}
	
	
	public static void processData() throws IOException {
		
		File file = new File("./browsingdata.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(file));
	    String line = null;
	    String[] browseIds;
	    while ((line = br.readLine()) != null) {
	    	browseIds = line.split(" ");
	    	for (int i = 0; i < browseIds.length; i++) {
	    		
	    		if (!ids.containsKey(browseIds[i])) {
	    			ids.put(browseIds[i], 1);
	    		} else {
	    			ids.put(browseIds[i], ids.get(browseIds[i]) + 1);
	    		}	
	    	}
	    }
	    br.close();
		
	}
	
	public static void filter() {
		int i = 0;
        for(String key : ids.keySet()){
        	if (ids.get(key) >= SUPPORT) {
        		freqIds.put(key, i);
        		reverseFreqIds.put(i, key);
        		i++;
        	}
        }
	}
	
	public static void confidencePairs() {
		
		String[] p;
		for (String id : freqIds.keySet()) {
			
			for (String pair : freqPairIds.keySet()) {
				if (pair.contains(id)) {
					p = pair.split(",");
					if (p[0].equals(id)) {
						confidenceOne.put(pair, (freqPairIds.get(pair) / (double)ids.get(id)));
					} else {
						confidenceOne.put(p[1] + "," + p[0], (freqPairIds.get(pair) / (double)ids.get(id)));
					}
				}
			}
			
		}	
	}
	
	public static void confidenceTriples() {
		String pairOne, pairTwo, pairThree;
		String[] trip;
		for (String triple : triplesCount.keySet()) {
			if (triplesCount.get(triple) >= SUPPORT) {
				trip = triple.split(",");
				pairOne = trip[0] + "," + trip[1];
				pairTwo = trip[0] + "," + trip[2];
				pairThree = trip[1] + "," + trip[2];
				
				if (freqPairIds.get(trip[0] + "," + trip[1]) != null) {
					confidenceTwo.put(trip[0] + "," + trip[1] + "," + trip[2], (triplesCount.get(triple) / (double)freqPairIds.get(trip[0] + "," + trip[1])));
				} else if (freqPairIds.get(trip[1] + "," + trip[0]) != null) {
					confidenceTwo.put(trip[1] + "," + trip[0] + "," + trip[2], (triplesCount.get(triple) / (double)freqPairIds.get(trip[1] + "," + trip[0])));
				}

				if (freqPairIds.get(trip[0] + "," + trip[2]) != null) {
					confidenceTwo.put(trip[0] + "," + trip[2] + "," + trip[1], (triplesCount.get(triple) / (double)freqPairIds.get(trip[0] + "," + trip[2])));
				} else if (freqPairIds.get(trip[2] + "," + trip[0]) != null) {
					confidenceTwo.put(trip[2] + "," + trip[0] + "," + trip[1], (triplesCount.get(triple) / (double)freqPairIds.get(trip[2] + "," + trip[0])));
				}
				
				if (freqPairIds.get(trip[1] + "," + trip[2]) != null) {
					confidenceTwo.put(trip[1] + "," + trip[2] + "," + trip[0], (triplesCount.get(triple) / (double)freqPairIds.get(trip[1] + "," + trip[2])));
				} else if (freqPairIds.get(trip[2] + "," + trip[1]) != null) {
					confidenceTwo.put(trip[2] + "," + trip[1] + "," + trip[0], (triplesCount.get(triple) / (double)freqPairIds.get(trip[2] + "," + trip[1])));
				}
				
			}
			
			
		}
	}
	
	
}
