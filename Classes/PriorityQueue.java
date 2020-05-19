package Classes;

import Interfaces.IPriorityQueue;

public class PriorityQueue implements IPriorityQueue {

	private class Node

	{
		Object value;
		Object key;
		Node next;

		public Object getValue() {
			return value;

		}

		public Object getKey() {
			return key;
		}

		public void setKey(Object key) {
			this.key = key;
		}

		public Node getNext() {
			return next;

		}

		public void setValue(Object value) {

			this.value = value;
		}

		public void setNext(Node next) {
			this.next = next;

		}
	}

	Node front, rear;
	int size = 0;

	public PriorityQueue() {
		front = rear = null;
	}

	@Override
	public void insert(Object item, int key) {
		// creating a new node
		Node newElement = new Node();
		// checking if the queue is empty or not
		if (isEmpty()) {
			newElement.setValue(item);
			newElement.setKey(key);
			front = rear = newElement;
			size++;
		} else {
			// if the queue is not empty assign item values to newElement node
			// pointer points on front of the queue
			// if the key of newElement is smaller than that of pointer
			// make the newElement as first node and let it be front
			newElement.setValue(item);
			newElement.setKey(key);
			Node pointer = front;
			Node prev;

			if ((int) newElement.getKey() < (int) pointer.getKey()) {
				newElement.setNext(pointer);
				front = newElement;
				size++;
			} else {

				// if not so i will iterate through the remaining of the queue to see the
				// correct position
				// prev is pointing on the front and pointer point to the next node and their
				// motion will remain the same
				// pointer leads prev

				prev = pointer;
				pointer = pointer.getNext();
				while (pointer != null) {
					if ((int) newElement.getKey() < (int) pointer.getKey()) {
						break;
					}
					prev = prev.getNext();
					pointer = pointer.getNext();
				}
				prev.setNext(newElement);
				newElement.setNext(pointer);
				size++;
			}

		}
		// if the newElement inserted after rear , it will be rear
		if (rear.getNext() == newElement) {
			rear = newElement;
		}
	}

	@Override
	public Object removeMin() {

		if (isEmpty()) {
			throw new IllegalStateException();
		}

		// creating a pointer that points to the node wanted to be deleted in order not
		// to lose it
		// front will move to the next node
		// setting the next of the pointer equal null in order to be deleted
		
		Node pointer = front;
		front = front.getNext();
		Object min = pointer.getValue();
		pointer.setNext(null);
		size--;
		return min;
	}

	@Override
	public Object min() {
		// return the front of the queue that has highest priority
		return front.getValue();
	}

	@Override
	public boolean isEmpty() {
		// if size == zero it will return true else it will return false
		return size == 0;
	}

	@Override
	public int size() {
		// return member variable size
		return size;
	}

}
