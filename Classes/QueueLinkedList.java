package Classes;

import Interfaces.ILinkedBased;
import Interfaces.IQueue;

public class QueueLinkedList implements IQueue, ILinkedBased {
	
	SinglyLinkedList Queue = new SinglyLinkedList();
	
	// Enqueue element in the queue by adding an item to the end of the linkedlist
	@Override
	public void enqueue(Object item) {
		Queue.add(item);
	}

	// Dequeue element from the queue by removing the first element in the linkedlist
	@Override
	public Object dequeue() {
		if (Queue.isEmpty()) {
			throw new IllegalStateException();
		}
		Object temp = Queue.get(0);
		Queue.remove(0);
		return temp;
	}
	
	// The queue is empty if the linkedlist is empty
	@Override
	public boolean isEmpty() {
		return Queue.isEmpty();
	}

	// The size of the queue is number of elements stored in the linkedlist
	@Override
	public int size() {
		return Queue.size();
	}

}
