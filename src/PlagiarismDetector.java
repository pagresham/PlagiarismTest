

import java.io.File;
import java.nio.file.Paths;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

/*
 * SD2x Homework #11
 * Improve the efficiency of the code below according to the guidelines in the assignment description.
 * Please be sure not to change the signature of the detectPlagiarism method!
 * However, you may modify the signatures of any of the other methods as needed.
 */

// 1. Creates an array of the file names contained in the 'corpus' dir.
// 2. Creates a HashMap<String, Int> to hold a mapping of file-file names to number of matches between those files
// 3. 


public class PlagiarismDetector {

	public static Map<String, Integer> detectPlagiarism(String dirName, int windowSize, int threshold) {

		// .list() gets a list of all the dirs inside the given directory //
		String[] files = new File(dirName).list();
		// get length in var so it doesnt have to be calculated each time

		
		
		// What if instead, I get run createPhrase on every file once. Store those in a HashMap. 
		// Then do the comparison of each file to each file. 
		Map<String, Set<String>> allPhrases = new HashMap<>();
		// For each file in the list, get a set of phrases 
		for ( int i = 0 ;i < files.length; i++) {
			Set<String> phrases = createPhrases(dirName + "/" + files[i], windowSize);
			if(phrases == null) 
				return null;
			// Fills the Map with phrases for each file //
			allPhrases.put(files[i], phrases);
		}
//		System.out.println("allPhrases contains: " + allPhrases.size() + " elements" );
		Map<String, Integer> numberOfMatches = new HashMap<String, Integer>();
		for (int i = 0; i < files.length; i++ ) {
			String file1 = files[i];
			Set<String> outsidePhrases = allPhrases.get(files[i]);
			for (int j = 0; j < files.length; j++ ) {
				String file2 = files[j];
				if(file1.equals(file2) || numberOfMatches.containsKey(file2 + "-" + file1)) {
					continue;
				} else {
					Set<String> insidePhrases = allPhrases.get(files[j]);
					List<String> matches = findMatches(outsidePhrases, insidePhrases);
					if (matches == null ) return null;
					if (matches.size() > threshold) {
						String key = files[i] + "-" + files[j];
						 numberOfMatches.put(key, matches.size());
					}	
				}
			}
		}
		return sortResults(numberOfMatches);
	}

	
	/*
	 * This method reads the given file and then converts it into a Collection of Strings.
	 * It does not include punctuation and converts all words in the file to uppercase.
	 */
	protected static List<String> readFile(String filename) {
		if (filename == null) return null;
		// Changed the type of List to an ArrayList, since the contents are being accessed by their indexes //
		List<String> words = new ArrayList<String>();
		
		try {
			Scanner in = new Scanner(new File(filename));
			while (in.hasNext()) {
				words.add(in.next().replaceAll("[^a-zA-Z]", "").toUpperCase());
			}
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return words;
	}

	
	/*
	 * This method reads a file and converts it into a Set/List of distinct phrases,
	 * each of size "window". The Strings in each phrase are whitespace-separated.
	 */
	protected static Set<String> createPhrases(String filename, int window) {
		if (filename == null || window < 1) return null;
		
		// This now returns an arrayList //
		List<String> words = readFile(filename);
		
		Set<String> phrases = new HashSet<String>();
		
		// goes through all the words, and gets a phrase of words of a particular size (window)
		// Gets a phrase starting on every word in words. 
		// Builds a phrase , starting at each word in the list //
		
		// Stops before one window size of words, because it would run out of words if it ran clear to the end. //
		
		for (int i = 0; i < words.size() - window + 1; i++) {
			String phrase = "";
			for (int j = 0; j < window; j++) {
				phrase += words.get(i+j) + " ";
			}

			phrases.add(phrase);

		}
		
		return phrases;		
	}

	

	
	/*
	 * Returns a Set of Strings that occur in both of the Set parameters.
	 * However, the comparison is case-insensitive.
	 */
//	protected static Set<String> findMatches(Set<String> myPhrases, Set<String> yourPhrases) {
	// Here I changed the return type from Set to ArrayList, 
//	protected static List<String> findMatches(Set<String> myPhrases, Set<String> yourPhrases) {
	protected static List<String> findMatches(Set<String> myPhrases, Set<String> yourPhrases) {	
		if(myPhrases == null || yourPhrases == null) {
			return null;
		}
		else {
//			Set<String> matches = new HashSet<String>();
			List<String> matches = new ArrayList<>();
			for (String mine : myPhrases) {
				
				for (String yours : yourPhrases) {
					
					if (mine.equalsIgnoreCase(yours)) {
						matches.add(mine);
					}
				}
			}
			return matches;
		}
		
		
	}
	
	
	
	

	/*
	 * Returns a LinkedHashMap in which the elements of the Map parameter
	 * are sorted according to the value of the Integer, in non-ascending order.
	 */
	protected static HashMap<String, Integer> sortResults(Map<String, Integer> possibleMatches) {
		
		
		// write an embedded class that holds the values in the sorted result below. 
		// make it implement compareable, and then use that to sort it. 
		class SortedResult implements Comparable {
			protected String key;
			protected int matches;
			public SortedResult(String key, int matches) {
				this.key = key;
				this.matches = matches;
			}
			@Override
			public int compareTo(Object o) {
				
				int ans = this.matches - ((SortedResult) o).getMatches();
				return ans;
			}
			public int getMatches() {
				return matches;
			}
		}
	
		
		
		// take possibleMatches, put them into an array of sortResult objects. Then use compareTo() to sort them //
//		List<SortedResult> results = new ArrayList<>();
//		for (Entry<String, Integer> m : possibleMatches.entrySet()) {
//			SortedResult s = new SortedResult(m.getKey(), m.getValue());
//			results.add(s);
//		}
//		Collections.sort(results);
//		System.out.println(results.toString());

		
		
		
		
		
		// Because this approach modifies the Map as a side effect of printing 
		// the results, it is necessary to make a copy of the original Map
		
		// Makes a copy of the original arg hashmap.
//		Map<String, Integer> copy = new HashMap<String, Integer>(possibleMatches);
		
		// creates a new hashmap that will get returned 
		HashMap<String, Integer> list = new LinkedHashMap<String, Integer>();
		// what is another structure that would serve this purpose, and be smaller? 
		// Must be a mapping, and must be in order
		
		
		
		for (int i = 0; i < possibleMatches.size(); i++) {
			int maxValue = 0;
			String maxKey = null;
			for (String key : possibleMatches.keySet()) {
				if (possibleMatches.get(key) > maxValue) {
					maxValue = possibleMatches.get(key);
					maxKey = key;
				}
			}
			// puts the current max key and value into the result Map
			list.put(maxKey, maxValue);
			// sets the values for the max key to be -1, so it is not considered the next time through. 
			possibleMatches.put(maxKey, -1);
		}

		return list;
	}
	
	/*
	 * This method is here to help you measure the execution time and get the output of the program.
	 * You do not need to consider it for improving the efficiency of the detectPlagiarism method.
	 */
    public static void main(String[] args) {
    	if (args.length == 0) {
    		System.out.println("Please specify the name of the directory containing the corpus.");
    		System.exit(0);
    	}
    	String directory = args[0];
    	long start = System.currentTimeMillis();
    	Map<String, Integer> map = PlagiarismDetector.detectPlagiarism(directory, 4, 5);
    	long end = System.currentTimeMillis();
    	double timeInSeconds = (end - start) / (double)1000;
    	System.out.println("Execution time (wall clock): " + timeInSeconds + " seconds");
    	Set<Map.Entry<String, Integer>> entries = map.entrySet();
    	for (Map.Entry<String, Integer> entry : entries) {
    		System.out.println(entry.getKey() + ": " + entry.getValue());
    	}
    }

}
