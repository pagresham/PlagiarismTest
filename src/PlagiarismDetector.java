import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set; 

/**
 * Reads a body of works into memory. 
 * Gets a Set of phrases from each file windowSize long - starting at every word in the file
 * @author piercegresham
 */
public class PlagiarismDetector {

	public static Map<String, Integer> detectPlagiarism(String dirName, int windowSize, int threshold) {

		// get a list of all the files inside the given directory 
		String[] files = new File(dirName).list();

		// Run createPhrase on every file once. Store those in a HashMap. 
		// Then do the comparison of each file to each file. 
		Map<String, Set<String>> allPhrases = new HashMap<>();
		
		// For each file in the list, get a set of phrases 
		// Fills the Map with phrases for each file
		for ( int i = 0 ;i < files.length; i++) {
			Set<String> phrases = createPhrases(dirName + "/" + files[i], windowSize);
			if(phrases == null) 
				return null;
			
			allPhrases.put(files[i], phrases);
		}
		
		// Compare each set of phrases to every other set of phrases 
		// Looks for matching phrases, and counts them.
		// If the count exceeds threshold, make entry in numberOfMatches Map
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
	protected static List<String> findMatches(Set<String> myPhrases, Set<String> yourPhrases) {	
		if(myPhrases == null || yourPhrases == null) {
			return null;
		}
		else {
			//			Set<String> matches = new HashSet<String>();
			List<String> matches = new ArrayList<>();
			for (String mine : myPhrases) {

				for (String yours : yourPhrases) {

					if (mine.equals(yours)) {
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


		// Creates a new hashmap that will get returned 
		HashMap<String, Integer> list = new LinkedHashMap<String, Integer>();
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
	 * Main method runs program, as well as runs a wallClock timer to get process time
	 * Measure the execution time and get the output of the program.
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please specify the name of the directory containing the body of files.");
			System.exit(0);
		}
		
		String directory = args[0];
		// Start timer //
		long start = System.currentTimeMillis();
		// Run method //
		Map<String, Integer> map = PlagiarismDetector.detectPlagiarism(directory, 4, 5);
		long end = System.currentTimeMillis();
		// Calculate elapsed time //
		double timeInSeconds = (end - start) / (double)1000;
		System.out.println("Execution time (wall clock): " + timeInSeconds + " seconds");
		// Display results
		Set<Map.Entry<String, Integer>> entries = map.entrySet();
		for (Map.Entry<String, Integer> entry : entries) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}

}
