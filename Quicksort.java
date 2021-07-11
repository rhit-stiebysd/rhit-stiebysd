import java.util.Random;

public class Quicksort {
	public static void sort(int[] array) {
		quickSortRecurse(array, 0, array.length - 1);
	}

	private static void quickSortRecurse(int[] a, int first, int last) {
		if (first < last + 1) {
			int part = partition(a, first, last);
			quickSortRecurse(a, first, part - 1);
			quickSortRecurse(a, part + 1, last);
		}
	}

	public static int partition(int[] a, int first, int last) {
		swap(a, first, generatePivot(first, last));
		int bound = first + 1;
		for (int i = bound; i <= last; i++) {
			if (a[i] < a[first]) {
				swap(a, i, bound++);
			}
		}
		swap(a, first, bound - 1);
		return bound - 1;
	}


	public static int generatePivot(int first, int last) { // Random pivot guarantees O(NlogN)
		Random r = new Random();
		return r.nextInt((last - first) + 1) + first;
	}
	static private void swap(int[] a, int first, int second) {
		int temp = a[first];
		a[first] = a[second];
		a[second] = temp;
	}
}
