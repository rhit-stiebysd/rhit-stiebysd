import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * 
 * Implementation of most of the Set interface operations using a Binary Search
 * Tree
 *
 * @author Matt Boutell and <<< Sam Stieby >>>.
 * @param <T>
 */

public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {
	private Node root;
	private boolean changed;

	//NULL_NODE to help with edge cases
	private final Node NULL_NODE = new Node();

	public BinarySearchTree() {
		root = NULL_NODE;
		changed = false;
	}

	public boolean insert(T i) throws IllegalArgumentException {
		// Catch exceptions
		if (i == null) {
			throw new IllegalArgumentException();
		}
		BoolCont bc = new BoolCont(true);
		root = root.insert(i, bc);
		changed = true;
		return bc.check;
	}

	// Recursive call into Node class
	public boolean contains(T i) {
		return root.contains(i);
	}

	public boolean remove(T i) throws IllegalArgumentException {
		// Catch exceptions
		if (i == null) {
			throw new IllegalArgumentException();
		}
		BoolCont bc = new BoolCont(true);
		root = root.remove(i, bc);
		changed = true;
		return bc.check;
	}

	// Recursive call into Node class
	public int size() {
		return root.size();
	}

	// Recursive call into Node class
	public int height() {
		return root.height();
	}

	// For manual tests only
	void setRoot(Node n) {
		this.root = n;
	}

	// Recursive call into Node class
	public void printPreOrder() {
		root.printPreOrder();
	}

	// Recursive call into Node class
	public void printInOrder() {
		root.printInOrder();
	}

	// Recursive call into Node class
	public void printPostOrder() {
		root.printPostOrder();
	}

	// Recursive call into Node class
	public boolean isEmpty() {
		return root.isEmpty();
	}

	// Recursive call into Node class
	public boolean containsNonBST(int i) {
		return root.containsNonBST(i);
	}

	// Recursive call into Node class
	public ArrayList<T> toArrayList() {
		ArrayList<T> list = new ArrayList<>();
		root.toArrayList(list);
		return list;
	}

	// Recursive call into Node class
	public Object[] toArray() {
		ArrayList<T> list = new ArrayList<T>();
		root.toArrayList(list);
		return list.toArray();
	}

	// Recursive call into Node class
	public Iterator<T> preOrderIterator() {
		return root.preOrderIterator();
	}

	// Recursive call into Node class
	@Override
	public Iterator<T> iterator() {
		return root.inOrderIterator();
	}

	// Recursive call into Node class
	public Iterator<T> inefficientIterator() {
		return root.inefficientIterator();
	}

	// Recursive call into Node class
	public String toString() {
		ArrayList<T> list = new ArrayList<T>();
		root.toArrayList(list);
		return list.toString();
	}

	// Node class, used to handle most functions used by BST
	// Not private, since we need access for manual testing.
	class Node {
		private T data;
		private Node left;
		private Node right;

		public Node() {
			this.data = null;
			this.left = null;
			this.right = null;
		}

		// Attempts to remove specified element, returns true if successful, false if
		// otherwise
		public Node remove(T i, BoolCont bc) {
			Node newNode;
			// Base case: if desired element isn't in the tree, there is nothing to remove
			if (this == NULL_NODE) {
				bc.check = false;
				return this;
			}

			// Traverse the left subtree if desired element is less than current element
			if (i.compareTo(this.data) < 0) {
				this.left = this.left.remove(i, bc);
			}

			// Traverse the right subtree if desired element is larger than current element
			if (i.compareTo(this.data) > 0) {
				this.right = this.right.remove(i, bc);
			}

			// Handles removal if the node to be removed has a right child but no left child
			if (i.compareTo(this.data) == 0 && this.right != NULL_NODE && this.left == NULL_NODE) {
				bc.check = true;
				return this.right;
			}

			// Handles removal if the node to be removed has a left child but no right child
			if (i.compareTo(this.data) == 0 && this.right == NULL_NODE && this.left != NULL_NODE) {
				bc.check = true;
				return this.left;
			}

			// Handles removal if the node to be removed has no children
			if (i.compareTo(this.data) == 0 && this.right == NULL_NODE && this.left == NULL_NODE) {
				bc.check = true;
				return NULL_NODE;
			}

			// Handles removal if the node to be removed has a left and a right child
			if (i.compareTo(this.data) == 0 && this.right != NULL_NODE && this.left != NULL_NODE) {
				newNode = this.left;
				while (newNode.right != NULL_NODE) {
					newNode = newNode.right;
				}
				this.data = newNode.data;
				this.left = this.left.remove(newNode.data, bc);
				bc.check = true;
			}
			return this;
		}

		// Checks if the BST contains the specified element
		public boolean contains(T i) {
			// Base case: if the entire tree has been searched and nothing is found, the
			// tree doesn't contain the element
			if (this == NULL_NODE) {
				return false;
			}
			// If the data of this node is the desired element, the tree contains it
			if (this.data == i) {
				return true;
			}
			// Traverse the right tree if desired element is larger than element at this
			// location
			if (i.compareTo(this.data) > 0) {
				return this.right.contains(i);
			}
			// Traverse the left tree if desired element is less than element at this
			// location
			if (i.compareTo(this.data) < 0) {
				return this.left.contains(i);
			}
			return false;
		}

		// Inserts a node in the BST, in the location it should be in order to preserve
		// the search property
		public Node insert(T i, BoolCont bc) {
			// Base case: once reached the end of the tree, add the new node with the
			// specified element
			if (this == NULL_NODE) {
				bc.check = true;
				return new Node(i);
			}

			// If the intended element is less than this node, go through the left subtree
			if (i.compareTo(this.data) < 0) {
				this.left = this.left.insert(i, bc);
			}
			// If the intended element is greater than this node, go through the right
			// subtree
			if (i.compareTo(this.data) > 0) {
				this.right = this.right.insert(i, bc);
			}
			// If the intended element is already in the tree, nothing is inserted
			if (i.compareTo(this.data) == 0) {
				bc.check = false;
			}
			return this;
		}

		// Prints the BST in preorder
		public void printPreOrder() {
			if (this == NULL_NODE)
				return;
			System.out.println(this.data.toString());
			left.printPreOrder();
			right.printPreOrder();
		}

		// Prints the BST via inorder traversal
		public void printInOrder() {
			if (this == NULL_NODE)
				return;
			left.printPreOrder();
			System.out.println(this.data.toString());
			right.printPreOrder();
		}

		// Prints the BST in postorder
		public void printPostOrder() {
			if (this == NULL_NODE)
				return;
			left.printPreOrder();
			right.printPreOrder();
			System.out.println(this.data.toString());
		}

		// Calculates the size of the BST
		public int size() {
			if (this == NULL_NODE) {
				return 0;
			}
			return (1 + left.size() + right.size());
		}

		// Calculates the height of the BST
		public int height() {
			if (this == NULL_NODE) {
				return -1;
			}
			return (1 + Math.max(left.height(), right.height()));
		}

		// Checks to see if the BST contains a specific integer
		public boolean containsNonBST(int i) {
			if (this == NULL_NODE) {
				return false;
			}
			if (this.data.equals(i)) {
				return true;
			}
			return (this.left.containsNonBST(i) || this.right.containsNonBST(i));
		}

		// Converts the entire tree into an ArrayList
		public void toArrayList(ArrayList<T> list) {
			if (this == NULL_NODE) {
				return;
			}
			left.toArrayList(list);
			list.add(this.data);
			right.toArrayList(list);
			return;
		}

		// Creates an iterator using an ArrayList
		public Iterator<T> inefficientIterator() {
			ArrayList<T> list = new ArrayList<T>();
			root.toArrayList(list);
			return new InefficientIterator(list);
		}

		// Creates an inorder iterator
		public Iterator<T> inOrderIterator() {
			return new InOrderIterator();
		}

		// Creates a preorder iterator
		public Iterator<T> preOrderIterator() {
			return new PreOrderIterator();
		}

		public Node(T element) {
			this.data = element;
			this.left = NULL_NODE;
			this.right = NULL_NODE;
		}

		public T getData() {
			return this.data;
		}

		public Node getLeft() {
			return this.left;
		}

		public Node getRight() {
			return this.right;
		}

		// For manual testing
		public void setLeft(Node left) {
			this.left = left;
		}

		public void setRight(Node right) {
			this.right = right;
		}

		// Checks if a tree is empty
		public boolean isEmpty() {
			return (this == NULL_NODE);
		}

	}

	// Iterator using ArrayList
	public class InefficientIterator implements Iterator<T> {
		public ArrayList<T> list;
		private int index;

		public InefficientIterator(ArrayList<T> list) {
			this.list = list;
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			return (this.index < this.list.size());
		}

		@Override
		public T next() throws NoSuchElementException {
			// Catch exceptions
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			T temp = list.get(index);
			index++;
			return temp;
		}

	}

	public class InOrderIterator implements Iterator<T> {
		private boolean removed;
		private int nextCalled;
		private T last;
		private Node temp;
		Stack<Node> s;

		public InOrderIterator() {
			s = new Stack<Node>();
			changed = false;
			last = null;
			removed = false;
			nextCalled = 0;
			temp = null;
			if (root != NULL_NODE) {
				s.push(root);
				addLefts(root);
			}
		}

		@Override
		public boolean hasNext() {
			return !s.isEmpty();
		}

		// Returns the next element in the tree found via inorder traversal
		@Override
		public T next() throws NoSuchElementException, ConcurrentModificationException {
			// Catch exceptions
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			if (changed) {
				throw new ConcurrentModificationException();
			}
			nextCalled++;
			temp = s.pop();
			if (temp.right != NULL_NODE) {
				s.push(temp.right);
				addLefts(temp.right);
				removed = false;
				last = temp.data;
				return temp.data;
			}
			removed = false;
			last = temp.data;
			return temp.data;
		}

		// Helper method to add all the left nodes of the passed in node to the
		// iterator's stack
		private void addLefts(Node n) {
			while (n.left != NULL_NODE) {
				s.push(n.left);
				n = n.left;
			}
		}

		// Removes the last item returned by next()
		public void remove() throws IllegalStateException {
			if (s.isEmpty() || removed || nextCalled < 1) {
				throw new IllegalStateException();
			}
			BinarySearchTree.this.remove(last);
			removed = true;
			nextCalled--;
		}
	}

	public class PreOrderIterator implements Iterator<T> {

		private Stack<Node> s;
		private int nextCalled;
		private boolean removed;
		private T last;

		public PreOrderIterator() {
			changed = false;
			nextCalled = 0;
			removed = false;
			last = null;
			s = new Stack<Node>();
			if (root != NULL_NODE) {
				s.push(root);
			}
		}

		@Override
		public boolean hasNext() {
			return (!s.isEmpty());
		}

		// Returns the next element in the tree found via preorder traversal
		@Override
		public T next() throws NoSuchElementException, ConcurrentModificationException {
			// Catch exceptions
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			if (changed) {
				throw new ConcurrentModificationException();
			}
			nextCalled++;
			Node temp = s.pop();
			if (temp.right != NULL_NODE)
				s.push(temp.right);
			if (temp.left != NULL_NODE)
				s.push(temp.left);
			last = temp.data;
			return temp.data;
		}

		// Removes the last item returned by next()
		public void remove() throws IllegalStateException {
			// Catch exceptions
			if (s.isEmpty() || removed || nextCalled < 1) {
				throw new IllegalStateException();
			}
			BinarySearchTree.this.remove(last);
			removed = true;
		}

	}

	// Inner class to record changes to a boolean through generations of recursive
	// methods
	public class BoolCont {
		private boolean check;

		public BoolCont(boolean b) {
			this.check = b;
		}
	}

}
