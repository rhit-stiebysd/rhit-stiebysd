import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

/**
 * This program runs various sorts and gathers timing information on them.
 *
 * @author <<Sam Stieby>> Created May 7, 2013.
 */
public class SortRunner {
	private static Random rand = new Random(); // uses a fixed seed for debugging. Remove the parameter later.

	/**
	 * Starts here.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// array size must be an int. You will need to use something much larger
		int size = 1000000;

		// Each integer will have the range from [0, maxValue). If this is significantly
		// higher than size, you
		// will have small likelihood of getting duplicates.
		int maxValue = Integer.MAX_VALUE;

		// Test 1: Array of random values.
		System.out.println("Random Arrays");
		int[] randomIntArray = getRandomIntArray(size, maxValue);
		runAllSortsForOneArray(randomIntArray);

		// TODO: Tests 2-4
		// Generate the three other types of arrays (shuffled, almost sorted, almost
		// reverse sorted)
		// and run the sorts on those as well.
		System.out.println("Shuffled Arrays");
		int[] shuffledIntArray = getUniqueElementArray(size);
		runAllSortsForOneArray(shuffledIntArray);
		System.out.println("Almost Sorted Arrays");
		int[] almostSortedArray = getAlmostSortedArray(size);
		runAllSortsForOneArray(almostSortedArray);
		System.out.println("Almost Reverse Sorted Arrays");
		int[] almostReverseSortedArray = getAlmostReverseSortedArray(size);
		runAllSortsForOneArray(almostReverseSortedArray);

	}


	/**
	 * 
	 * Runs all the specified sorts on the given array and outputs timing results on
	 * each.
	 *
	 * @param array
	 */
	private static void runAllSortsForOneArray(int[] array) {
		long startTime, elapsedTime;
		boolean isSorted = false;

		// TODO: Read this.
		// We prepare the arrays. This can take as long as needed to shuffle items,
		// convert
		// back and forth from ints to Integers and vice-versa, since you aren't timing
		// this
		// part. You are just timing the sort itself.

		int[] sortedIntsUsingDefaultSort = array.clone();
		Integer[] sortedIntegersUsingDefaultSort = copyToIntegerArray(array);
		Integer[] sortedIntegersUsingHeapSort = sortedIntegersUsingDefaultSort.clone();
		Integer[] sortedIntegersUsingTreeSort = sortedIntegersUsingDefaultSort.clone();
		int[] sortedIntsUsingQuickSort = array.clone();
		

		int size = array.length;

		// What is the default sort for ints? Read the javadoc.
		// Mergesort
		startTime = System.currentTimeMillis();
		Arrays.sort(sortedIntsUsingDefaultSort);
		elapsedTime = (System.currentTimeMillis() - startTime);
		isSorted = verifySort(sortedIntsUsingDefaultSort);
		displayResults("int", "the default sort", elapsedTime, size, isSorted);

		// What is the default sort for Integers (which are objects that wrap ints)?
		// Timsort, a different type of Mergesort
		startTime = System.currentTimeMillis();
		Arrays.sort(sortedIntegersUsingDefaultSort);
		elapsedTime = (System.currentTimeMillis() - startTime);
		isSorted = verifySort(sortedIntegersUsingDefaultSort);
		displayResults("Integer", "the default sort", elapsedTime, size, isSorted);

		// TreeSet Sorting

		TreeSet<Integer> set = new TreeSet<Integer>();
		ArrayList<Integer> dupList = new ArrayList<Integer>();
		startTime = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			if (!set.add(sortedIntegersUsingTreeSort[i])) {
				dupList.add(sortedIntegersUsingTreeSort[i]);
			}
		}
		elapsedTime = (System.currentTimeMillis() - startTime);
		int inc = 0;
		Iterator<Integer> it = set.iterator();
		while (it.hasNext()) {
			Integer n = it.next();
			sortedIntegersUsingTreeSort[inc] = n;
			inc++;
			while (dupList.contains(n)) {
				dupList.remove(n);
				sortedIntegersUsingTreeSort[inc] = n;
				inc++;
			}
		}
		isSorted = verifySort(sortedIntegersUsingTreeSort);
		displayResults("TreeSet", "the tree sort", elapsedTime, size, isSorted);

		// Quicksort sorting

		startTime = System.currentTimeMillis();
		Quicksort.sort(sortedIntsUsingQuickSort);
		elapsedTime = (System.currentTimeMillis() - startTime);
		isSorted = verifySort(sortedIntsUsingQuickSort);
		displayResults("QuickSort", "quicksort", elapsedTime, size, isSorted);

		// BinaryHeap sorting

		startTime = System.currentTimeMillis();
		BinaryHeap.sort(sortedIntegersUsingHeapSort, Integer.class);
		elapsedTime = (System.currentTimeMillis() - startTime);
		isSorted = verifySort(sortedIntegersUsingHeapSort);
		displayResults("BinaryHeap", "the heapsort", elapsedTime, size, isSorted);

	}

	private static void displayResults(String typeName, String sortName, long elapsedTime, int size, boolean isSorted) {
		if (isSorted) {
			System.out.printf("Sorted %.1e %ss using %s in %d milliseconds\n", (double) size, typeName, sortName,
					elapsedTime);
		} else {
			System.out.println("ARRAY NOT SORTED");
		}
	}

	/**
	 * Checks in O(n) time if this array is sorted.
	 *
	 * @param a An array to check to see if it is sorted.
	 */
	private static boolean verifySort(int[] a) {
		for (int i = 0; i < a.length; i++) {
			if (i + 1 == a.length) {
				return true;
			}
			if (a[i] > a[i + 1]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks in O(n) time if this array is sorted.
	 *
	 * @param a An array to check to see if it is sorted.
	 */
	private static boolean verifySort(Integer[] a) {
		for (int i = 1; i < a.length; i++) {
			if (a[i - 1] > a[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Copies from an int array to an Integer array.
	 *
	 * @param randomIntArray
	 * @return A clone of the primitive int array, but with Integer objects.
	 */
	private static Integer[] copyToIntegerArray(int[] ints) {
		Integer[] integers = new Integer[ints.length];
		for (int i = 0; i < ints.length; i++) {
			integers[i] = ints[i];
		}
		return integers;
	}

	/**
	 * Creates and returns an array of random ints of the given size.
	 *
	 * @return An array of random ints.
	 */
	private static int[] getRandomIntArray(int size, int maxValue) {
		int[] a = new int[size];
		for (int i = 0; i < size; i++) {
			a[i] = rand.nextInt(maxValue);
		}
		return a;
	}

	/**
	 * Creates a shuffled random array.
	 *
	 * @param size
	 * @return An array of the ints from 0 to size-1, all shuffled
	 */
	private static int[] getUniqueElementArray(int size) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			a.add(i);
		}
		Collections.shuffle(a);
		int[] toReturn = new int[size];
		for (int j = 0; j < a.size() - 1; j++) {
			toReturn[j] = a.get(j);
		}
		return toReturn;
	}
	
	private static int[] getAlmostReverseSortedArray(int size) {
		int[] toReturn = new int[size];
		for (int i = 0; i < size - 1; i++) {
			toReturn[i] = i;
		}
		Quicksort.sort(toReturn);
		for (int j = 0; j < (int) (size*0.01); j++) {
			toReturn[j] = rand.nextInt(size - 1);
		}
		return toReturn;
	}
	
	private static int[] getAlmostSortedArray(int size) {
		int[] toReturn = new int[size];
		for (int i = 0; i < size - 1; i++) {
			toReturn[i] = i;
		}
		Quicksort.sort(toReturn);
		for (int j = 0; j < (int) (size*0.01); j++) {
			toReturn[size - j - 1] = rand.nextInt(size - 1);
		}
		return toReturn;
	}

}
