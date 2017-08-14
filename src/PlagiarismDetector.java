

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/*
 * SD2x Homework #11
 * Improve the efficiency of the code below according to the guidelines in the assignment description.
 * Please be sure not to change the signature of the detectPlagiarism method!
 * However, you may modify the signatures of any of the other methods as needed.
 */

public class PlagiarismDetector {

	public static Map<String, Integer> detectPlagiarism(String dirName, int windowSize, int threshold) {
		File dirFile = new File(dirName);
		// .list() gets a list of all the dirs inside the given directory //
		String[] files = dirFile.list();
		
		Map<String, Integer> numberOfMatches = new HashMap<String, Integer>();
		// go through list of files //
		for (int i = 0; i < files.length; i++) {
			// get each file //
			String file1 = files[i];
			// Get the phrases from file 1
			// instead of doing this for file1 each time, only create the file1 Set once
			Set<String> file1Phrases = createPhrases(dirName + "/" + file1, windowSize); 
			if ( file1Phrases == null ) {
				return null;
			}
			// compare each file to every other file //
			for (int j = 0; j < files.length; j++) { 
				String file2 = files[j];
				
				// get the set of phrases from file2  //
				Set<String> file2Phrases = createPhrases(dirName + "/" + file2, windowSize); 
				
				if (file2Phrases == null)
					return null;
				
				Set<String> matches = findMatches(file1Phrases, file2Phrases);
				
				if (matches == null)
					return null;
				
				// checks if number of matches is above the given threshold value //
				if (matches.size() > threshold) {
					// creates the string for the key //
					String key = file1 + "-" + file2;
					// checks if string is already in numberOfMathces //
					// checks if theey are not the same file //
					if (!numberOfMatches.containsKey(file2 + "-" + file1) && !file1.equals(file2)) {
						numberOfMatches.put(key,matches.size());
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
		
		List<String> words = new LinkedList<String>();
		
		try {
			Scanner in = new Scanner(new File(filename));
			while (in.hasNext()) {
				words.add(in.next().replaceAll("[^a-zA-Z]", "").toUpperCase());
			}
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
	protected static Set<String> findMatches(Set<String> myPhrases, Set<String> yourPhrases) {
	
		Set<String> matches = new HashSet<String>();
		
		if (myPhrases != null && yourPhrases != null) {
		
			for (String mine : myPhrases) {
				for (String yours : yourPhrases) {
					if (mine.equalsIgnoreCase(yours)) {
						matches.add(mine);
					}
				}
			}
		}
		return matches;
	}
	
	/*
	 * Returns a LinkedHashMap in which the elements of the Map parameter
	 * are sorted according to the value of the Integer, in non-ascending order.
	 */
	protected static LinkedHashMap<String, Integer> sortResults(Map<String, Integer> possibleMatches) {
		
		// Because this approach modifies the Map as a side effect of printing 
		// the results, it is necessary to make a copy of the original Map
		
		Map<String, Integer> copy = new HashMap<String, Integer>(possibleMatches);
		// 1. I removed the code that was copying the structure. Instead used the API!
	
		
		LinkedHashMap<String, Integer> list = new LinkedHashMap<String, Integer>();
		// what is another structure that would serve this purpose, and be smaller? 
		// Must be a mapping, and must be in order
		
		
		for (int i = 0; i < copy.size(); i++) {
			int maxValue = 0;
			String maxKey = null;
			for (String key : copy.keySet()) {
				if (copy.get(key) > maxValue) {
					maxValue = copy.get(key);
					maxKey = key;
				}
			}
			// puts the current max key and value into the result Map
			list.put(maxKey, maxValue);
			// sets the values for the max key to be -1, so it is not considered the next time through. 
			copy.put(maxKey, -1);
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
