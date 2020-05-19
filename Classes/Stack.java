package Classes;

import Interfaces.IStack;

/**
 * This class implements a stack as a linkedlist
 * defining the stack main operations 
 * @author Mo'men
 *
 */
/**
 * @author Mo'men
 *
 */
public class Stack implements IStack {

	private class Node { 
        private Object data; 
        private Node next;
        
        public Node getNext() {
			return next;
		}
		
		public void setNext(Node next) {
			this.next = next;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}
    }
	
	/**
	 * A variable carries the top node of the stack
	 */
	Node top;
	/**
	 * A variable carries the number of elements in the stack 
	 */
	int size;
	
	
	/**
	 * Empty constructor initializes a the top node with null
	 * and the size with 0
	 */
	public Stack ()
	{
		top = null;
		size = 0;
	}
	
	/**
	 * @param node the first element in the stack
	 */
	public Stack (Node node)
	{
		top = node;
		size = 1;
	}
	
	
	/**
	 * The function removes the first element in the stack and return its value
	 * @return the value of the element removed from the stack
	 */
	@Override
	public Object pop() {
		// Check if the stack is empty
		if (isEmpty()) {
			throw new IllegalStateException();
		}
		// Store the top node
		Node temp = top;
		// Advances the top pointer to leave the top node
		top = top.getNext();
		// Remove the node pointer to the list
		temp.setNext(null);
		size --;
		return temp.getData();
	}

	/**
	 * This function get the top element in the stack
	 * @return the top element of the stack
	 */
	@Override
	public Object peek() {
		// Check if the stack is empty
		if (isEmpty()) {
			return null;
		}
		return top.getData();
	}

	/**
	 * This function inserts element at the top of the stack
	 * @param element to be inserted at the top of the stack
	 */
	@Override
	public void push(Object element) {
		// Create a new node
		Node newNode = new Node();
		// Assign the passed parameter to its element
		newNode.setData(element);
		// Set its reference to top
		newNode.setNext(top);
		// Advances the top pointer to store the new node
		top = newNode;
		size ++;
	}

	/**
	 * Checks if the stack is empty
	 * @return boolean value indicating whether the stack is empty or not
	 */
	@Override
	public boolean isEmpty() {
		// Check if size equals zero then the stack is empty
		if(size == 0) {
			return true;
		}
		return false;
	}

	/**
	 * @return returns the  number of elements in the stack 
	 */
	@Override
	public int size() {
		// Return the size member
		return this.size;
	}

	
	/**
	 * Prints the elements of the stack
	 */
	public void print() {
		Node fetcher = top;
		String stack = "";
		if(size == 0) { System.out.println("Empty");}
		else {
			while (fetcher.getNext() != null) {
				stack += fetcher.getData().toString() + " , ";
				fetcher = fetcher.getNext();
			}
			stack += fetcher.getData().toString() + "]";
			System.out.println(stack);
		}
	}
}
