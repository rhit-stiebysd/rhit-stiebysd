import java.lang.reflect.Array;

public class BinaryHeap<T extends Comparable<? super T>> {
	private T[] array;
	private Class<T> type;
	private int capacity;
	public BinaryHeap() {
		this.array = (T[]) Array.newInstance(this.type, INITIAL_CAPACITY);

	}
	
//	public BinaryHeap() {
//		
//	}
}
