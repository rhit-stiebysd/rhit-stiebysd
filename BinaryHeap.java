import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;

public class BinaryHeap<T extends Comparable<? super T>> {

	private T[] array;
	private Class<T> type;
	public int size;
	Comparator<T> comp;
	private boolean reduce;

	@SuppressWarnings("unchecked")
	public BinaryHeap(Class<T> type) {
		this.type = type;
		this.reduce = true;
		comp = new Comparator<T>() {
			@Override
			public int compare(T t1, T t2) {
				return t1.compareTo(t2);
			}
		};

		this.array = (T[]) Array.newInstance(this.type, 1);
		this.size = 0;
	}

	public BinaryHeap(T[] a) {
		this.reduce = false;
		this.array = a;
		this.size = a.length - 1;
		this.comp = new Comparator<T>() {
			@Override
			public int compare(T t1, T t2) {
				return -t1.compareTo(t2);
			}
		};
		int minIndex = 0;
		T min = this.array[0];
		for (int i = 0; i < this.size + 1; i++) {
			if (min.compareTo(this.array[i]) > 0) {
				minIndex = i;
				min = array[i];
			}
		}
		this.array[minIndex] = this.array[0];
		this.array[0] = min;
		buildHeap();
	}

	public void insert(T element) {
		expand();
		this.size++;
		this.array[this.size] = element;
		percUp(this.size);
	}

	public void buildHeap() {
		for (int i = size / 2; i > 0; i--) {
			percDown(i);
		}

	}

	public boolean shouldPerc(int i) {
		if (this.size < 2 * i) {
			return false;
		}
		T left = this.array[2 * i];
		T e = this.array[i];

		if (this.size < (2 * i + 1)) {
			if (this.comp.compare(left, e) < 0) {
				return true;
			} else {
				return false;
			}
		}

		T right = this.array[2 * i + 1];
		if (this.comp.compare(left, right) > 0) {
			if (this.comp.compare(right, e) < 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if (this.comp.compare(left, e) < 0) {
				return true;
			} else {
				return false;
			}
		}

	}

	public T deleteMin() {
		if (this.size == 0)
			return null;
		else {
			T e = this.array[1];
			T lastElem = this.array[this.size];
			this.array[1] = lastElem;
			percDown(1);
			this.array[this.size] = e;
			this.size--;
			if (this.reduce) {
				shrink();
			}
			percUp(this.size);
			return e;
		}

	}

	public String toString() {
		return Arrays.toString(this.array);
	}

	public static <T extends Comparable<? super T>> void sort(T[] array, Class<T> type) {
		BinaryHeap<T> h = new BinaryHeap<T>(array);
		int len = h.size;
		for (int i = 0; i < len; i++) {
			h.deleteMin();
		}
		array = h.array;

	}

	public void percDown(int i) {
		T e = this.array[i];

		if (this.size < 2 * i) {
			return;
		}

		T left = this.array[i * 2];

		if (this.size < 2 * i + 1) {
			if (shouldPerc(i)) {
				this.array[i] = left;
				this.array[i * 2] = e;
				percDown(i * 2);
			}
			return;

		}

		T right = this.array[i * 2 + 1];

		if (this.comp.compare(left, right) <= 0) {
			if (shouldPerc(i)) {
				this.array[i] = left;
				this.array[i * 2] = e;
				percDown(i * 2);
				return;
			}
		} else {
			if (shouldPerc(i)) {
				this.array[i] = right;
				this.array[i * 2 + 1] = e;
				percDown(i * 2 + 1);
			}
			return;
		}
	}

	public void percUp(int i) {
		if (i <= 1)
			return;
		T e = this.array[i];
		int pInd = i / 2;
		T p = this.array[pInd];
		if (this.comp.compare(p, e) <= 0) {
			return;
		} else {
			this.array[pInd] = e;
			this.array[i] = p;
			percUp(pInd);
		}

	}

	@SuppressWarnings("unchecked")
	public void expand() {
		T[] newArray = (T[]) Array.newInstance(this.type, this.array.length + 1);
		for (int i = 0; i < this.size + 1; i++) {
			newArray[i] = this.array[i];
		}
		this.array = newArray;
	}

	@SuppressWarnings("unchecked")
	public void shrink() {
		T[] newArray = (T[]) Array.newInstance(this.type, this.array.length - 1);
		for (int i = 0; i <= this.size; i++) {
			newArray[i] = this.array[i];
		}
		this.array = newArray;
	}
}
