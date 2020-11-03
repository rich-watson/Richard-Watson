package hw4;

import java.util.LinkedList;
							//Richard Watson

public class TwoThreeIntSet {

	private class Node {
		int[] items; // the keys/items in the node
		Node[] subtrees; // the children of this node
		int nodeType; // the node type (2, 3, or 4)

		/**
		 * Create a new node with item as the key and two subtrees
		 * 
		 * @param item  the key/item for the node
		 * @param left  the root of the subtree less than the item
		 * @param right the root of the subtree greater than the item
		 */
		public Node(int item, Node left, Node right) {
			items = new int[3];
			subtrees = new Node[4];
			subtrees[0] = left;
			subtrees[1] = right;
			nodeType = 2;
			items[0] = item;
		}

		/**
		 * Creates a new leaf with item as the key.
		 * 
		 * @param item the key/item for the leaf.
		 */
		public Node(int item) {
			this(item, null, null);
		}

		/**
		 * Returns true if this node is a leaf
		 * 
		 * @return true if this node is a leaf.
		 */
		public boolean isLeaf() {
			return subtrees[0] == null;
		}
	}

	private Node root;

	public TwoThreeIntSet() {
	}

	// DO NOT MODIFY ANYTHING ABOVE THIS LINE

	public boolean contains(int item) {
		return contains(root, item);
	}

	private boolean contains(Node n, int item) {

		// The base case here is empty tree. The item is not in the empty tree.
		if (n == null)
			return false;

		// If the node is a 2-node, use normal BST search.
		if (n.nodeType == 2) {
			if (item < n.items[0])
				return contains(n.subtrees[0], item);
			if (item > n.items[0])
				return contains(n.subtrees[1], item);
			return true;
		}

		else if (n.nodeType == 3) {
			
			// If the node is a 3-node, we must check both items in this node. If neither is
			// the item we are looking for, we must decide which of the three subtrees to
			// search next.
			if (item < n.items[0])
				return contains(n.subtrees[0], item);
			if (item > n.items[0] && item < n.items[1])
				return contains(n.subtrees[1], item);
			if (item > n.items[1])
				return contains(n.subtrees[2], item);
			return true;
			
			
		}

		// If this node is not a 2-node or a 3-node, this is an error
		else
			throw new RuntimeException("ERROR: " + n.nodeType + "-node found while searching");
	}

	/**
	 * Inserts item into the 2-3 tree.
	 * 
	 * @param item the number to be inserted into the tree.
	 */
	public void put(int item) {
		/*
		 * If the tree is empty, create a new leaf node for the item. It is also the
		 * root of the tree.
		 */
		if (root == null)
			root = new Node(item);

		/*
		 * Otherwise, we will be inserting the item in a pre-existing leaf using a
		 * recursive helper function.
		 */
		else {
			root = put(root, item);
			/*
			 * The recursive helper function returns a 2-3 tree where the root might be a
			 * 4-node. Check for this and split if necessary.
			 */
			if (root.nodeType == 4) {
				Node left = new Node(root.items[0], root.subtrees[0], root.subtrees[1]);
				Node right = new Node(root.items[2], root.subtrees[2], root.subtrees[3]);
				root = new Node(root.items[1], left, right);
			}
		}
	}

	/**
	 * A recursive helper function.
	 * 
	 * @param n    the root of a 2-3 tree into which we will insert a number
	 * @param item the number to be inserted.
	 * @return the root of new tree with item inserted. Note that this root might
	 *         now be a 4 node.
	 */
	private Node put(Node n, int item) {
		/*
		 * First check if n contains item. If it does, there is nothing to do and we can
		 * return the root of this subtree unchanged
		 */
		int itemCount = n.nodeType - 1;
		for (int i = 0; i < itemCount; i++) {
			if (n.items[i] == item)
				return n;
		}

		/*
		 * Because we always insert into a pre-existing leaf, the base case here is a
		 * tree with one node (the root is a leaf).
		 */
		if (n.isLeaf()) {

			// If the node is a 2-node, insert the new item to its left or right.
			if (n.nodeType == 2) {
				if (item < n.items[0]) {
					n.items[1] = n.items[0];
					n.items[0] = item;
				} else {
					n.items[1] = item;
				}
				n.nodeType = 3;
			}

			else if (n.nodeType == 3) {
				// Turn this into a 4-node and return it since you are allowed
				// to temporarily have a 4-node as a the root.
				
				if (item < n.items[0]) {
					n.items[2] = n.items[1];
					n.items[1] = n.items[0];
					n.items[0] = item;
					n.nodeType = 4;
					return n;
				}
				
				if (item > n.items[0] && item < n.items[1]) {
					n.items[2] = n.items[1];
					n.items[1] = item;
					n.nodeType = 4;
					return n;
				}
				
				else
					n.items[2] = item;
				n.nodeType = 4;
			}

			else
				throw new RuntimeException("ERROR: " + n.nodeType + "-node found while inserting");

			return n;
		}

		// If not a leaf node, we will need to insert in one of our subtrees.
		else {
			if (n.nodeType == 2) {
				if (item < n.items[0]) {
					Node result = put(n.subtrees[0], item);
					// if the resulting root is not a 4-node, we can insert it as our new left.
					if (result.nodeType != 4) {
						n.subtrees[0] = result;
					}
					// otherwise, we need to fix it by splitting it
					else {
						Node newLeft = new Node(result.items[0], result.subtrees[0], result.subtrees[1]);
						Node newMiddle = new Node(result.items[2], result.subtrees[2], result.subtrees[3]);
						n.items[1] = n.items[0];
						n.items[0] = result.items[1];
						n.subtrees[2] = n.subtrees[1];
						n.subtrees[1] = newMiddle;
						n.subtrees[0] = newLeft;
						n.nodeType = 3;
					}
				} else {
					Node result = put(n.subtrees[1], item);
					// if the resulting root is not a 4-node, we can insert it as our new right.
					if (result.nodeType != 4) {
						n.subtrees[1] = result;
					}
					// otherwise, we need to fix it by splitting it
					else {
						Node newMiddle = new Node(result.items[0], result.subtrees[0], result.subtrees[1]);
						Node newRight = new Node(result.items[2], result.subtrees[2], result.subtrees[3]);
						n.items[1] = result.items[1];
						n.subtrees[2] = newRight;
						n.subtrees[1] = newMiddle;
						n.nodeType = 3;
					}
				}
				return n;

			} else if (n.nodeType == 3) {
				if (item < n.items[0]) {
					Node result = put(n.subtrees[0], item);
					if (result.nodeType != 4)
						n.subtrees[0] = result;
					else {
						Node newLeft = new Node(result.items[0], result.subtrees[0], result.subtrees[1]);
						Node newMiddle = new Node(result.items[2], result.subtrees[2], result.subtrees[3]);
						n.items[2] = n.items[1];
						n.items[1] = n.items[0];
						n.items[0] = result.items[1];
						n.subtrees[3] = n.subtrees[2];
						n.subtrees[2] = n.subtrees[1];
						n.subtrees[0] = newLeft;
						n.subtrees[1] = newMiddle;
						n.nodeType = 4;
					}
				}
				else if (item > n.items[0] && item < n.items[1]) {
					Node result = put(n.subtrees[1], item);
					if (result.nodeType != 4) {
						n.subtrees[1] = result;
					}
					else {
						Node midLeft = new Node(result.items[0], result.subtrees[0], result.subtrees[1]);
						Node midRight = new Node(result.items[2], result.subtrees[2], result.subtrees[3]);
						n.items[2] = n.items[1];
						n.items[1] = result.items[1];
						n.subtrees[1] = midLeft;
						n.subtrees[3] = n.subtrees[2];
						n.subtrees[2] = midRight;
						n.nodeType = 4;
					}
					
				}
				else if (item > n.items[1]) {
					Node result = put(n.subtrees[2], item);
					if (result.nodeType != 4)
						n.subtrees[2] = result;
					else {
						Node newRight = new Node(result.items[2], result.subtrees[2], result.subtrees[3]);
						Node newMiddle = new Node(result.items[0], result.subtrees[0], result.subtrees[1]);
						n.items[2] = result.items[1];
						n.subtrees[2] = newMiddle;
						n.subtrees[3] = newRight;
						n.nodeType = 4;
					}
				}
				return n;
			} else
				throw new RuntimeException("ERROR: " + n.nodeType + "-node found while inserting");

		}
	}
	

	// DO NOT MODIFY ANYTHING BEOW THIS LINE.
	// THE METHODS BELOW WILL BE USED FOR TESTING.

	/*
	 * Returns the height of the tree.
	 * 
	 * @return the height of the tree.
	 */
	public int getHeight() {
		return getHeight(root);
	}

	private int getHeight(Node n) {
		if (n == null)
			return -1;
		int subtreeHeight = getHeight(n.subtrees[0]);
		if (subtreeHeight == -2)
			return -2;
		for (int i = 1; i < n.nodeType; i++)
			if (getHeight(n.subtrees[i]) != subtreeHeight)
				return -2;
		return subtreeHeight + 1;
	}

	/*
	 * Returns the level order of the two three tree as a String. The string is a
	 * comma separated list of nodes where each node is a | separated list of keys
	 * inside of parentheses. For example, after inserting 1, 2, 3, 4, and 0 into an
	 * an empty 2-3 tree, the level order returned would be the String
	 * "(2),(0|1),(3|4)"
	 */
	public String levelOrder() {
		LinkedList<Node> q = new LinkedList<Node>();
		StringBuilder result = new StringBuilder();
		boolean firstNode = true;
		if (root != null)
			q.addLast(root);
		while (!q.isEmpty()) {
			if (firstNode)
				firstNode = false;
			else
				result.append(',');
			Node current = q.removeFirst();
			result.append('(');
			result.append(current.items[0]);
			int itemCount = current.nodeType - 1;
			for (int i = 1; i < itemCount; i++) {
				result.append('|');
				result.append(current.items[i]);
			}
			result.append(')');
			if (!current.isLeaf()) {
				for (int i = 0; i <= itemCount; i++)
					q.addLast(current.subtrees[i]);
			}
		}
		return result.toString();
	}

	// A tiny main that inserts 10-19 into an emtpy TwoThreeIntSet
	// And prints out the level order after each insertion.
	public static void main(String[] args) {
		TwoThreeIntSet t = new TwoThreeIntSet();
		for (int i = 10; i < 20; i++) {
			t.put(i);
			System.out.println(t.levelOrder());
		}
	}
}
