package Classes;

import java.util.Comparator;
import java.util.Iterator;

import Interfaces.ILinkedList;

public class DoublyLinkedList implements ILinkedList{
	private class dListNode {
		private dListNode next;
		private dListNode prev;
		private Object element;
		
		public dListNode(dListNode next, dListNode prev, Object element)
		{
			this.next = next;
			this.prev = prev;
			this.element = element;
		}
		
		public dListNode getNext() {
			return next;
		}
		
		public void setNext(dListNode next) {
			this.next = next;
		}
		
		public dListNode getPrev() {
			return prev;
		}
		
		public void setPrev(dListNode prev) {
			this.prev = prev;
		}

		public Object getElement() {
			return element;
		}

		public void setElement(Object element) {
			this.element = element;
		}
	}

	private dListNode head;
	private dListNode tail;
	private int size;
	
	public DoublyLinkedList()
	{
		size = 0;
	}
	
	DoublyLinkedList(dListNode node) {
		setHead(node);
		setTail(node);
		size = 0;
	}

	@Override
	public void add(int index, Object element) {
		dListNode newNode = new dListNode(null, null, element);
		if(index < 0 || index > this.size()) { throw new IndexOutOfBoundsException();}
		if (index == 0) {
			newNode.setNext(this.head);
			if (tail == null) {
				this.tail = this.head = newNode;
			}
			else {
				this.head.setPrev(newNode);
				this.head = newNode;
			}
			size++;
		}
		else if (index >= size) { add(element); }
		else { 
			dListNode current = this.head;
			for (int i = 1; i < index; i++) {
				 current = current.getNext();
			 }
			 newNode.setNext(current.getNext());
			 current.getNext().setPrev(newNode);
			 current.setNext(newNode);
			 size++;
		}
	}

	@Override
	public void add(Object element) {
		dListNode newNode = new dListNode(null, null, element);
		if(this.tail == null) {
			head = tail = newNode;
		}
		else {
			this.tail.setNext(newNode);
			newNode.setPrev(this.tail);
			this.tail = newNode;
		}
		size++;
	}

	@Override
	  public Object get(int index) {
	    if (index < 0 || index >= this.size()) {
	      return null;
	    }
	    // Index starts from 0 excluding
	    if (getHead() == null) {
	      return null;
	    }
	    dListNode fetcher;
	    if (index < this.size / 2) {
	      fetcher = getHead();
	      int counter = 0;
	      while (counter < index) {
	        fetcher = fetcher.getNext();
	        counter++;
	      }
	    } else {
	      fetcher = getTail();
	      int counter = this.size - 1;
	      while (counter > index) {
	        fetcher = fetcher.prev;
	        --counter;
	      }
	    }
	    if (fetcher != null) {
	      return fetcher.getElement();
	    }
	    return null;
	  }

	@Override
	public void set(int index, Object element) {
		if(index < 0 || index >= this.size()) { throw new IndexOutOfBoundsException();}
		if (getHead() == null)
		{
			return;
		}
		dListNode fetcher = getHead();
		int counter = 0;
		while(fetcher != null && counter < index)
		{
			if(fetcher == this.tail) {
				fetcher = null;
			}
			else {
				fetcher = fetcher.getNext();
			}
			counter++;
		}
		if(fetcher != null)
		{
			fetcher.setElement((int[])element);
		}
	}

	@Override
	public void clear() {
		this.head = this.tail = null;
		size = 0;
	}

	@Override
	public boolean isEmpty() {
		if(this.size < 1) {
			return true;
		}
		return false;
	}

	@Override
	public void remove(int index) {
		if (index < 0 || index >= size || size == 0) { throw new IndexOutOfBoundsException(); }
		else if (size == 1)	{
			this.clear();
		}
		else if (index == 0) {
			dListNode temp = this.head;
			this.head = this.head.getNext();
			this.head.setPrev(null);
			temp.setNext(null);
			size --;
		}
		else {
			dListNode prev = this.head;
			for (int i = 1; i < index; i++) {
				prev = prev.getNext();
			}
			if (index == size - 1) {
				this.tail.setPrev(null);
				this.tail = prev;
				prev.setNext(null);
			}
			else {
				dListNode temp = prev.getNext();
				prev.getNext().getNext().setPrev(prev);
				prev.setNext(prev.getNext().getNext());
				temp.setNext(null);
				temp.setPrev(null);
			}
			size--;
		}
	}

	@Override
	public int size() {
		
		return this.size;
	}

	@Override
	public ILinkedList sublist(int fromIndex, int toIndex) {
		DoublyLinkedList sub  = new DoublyLinkedList();
		if(fromIndex < 0 || fromIndex > toIndex) { throw new IndexOutOfBoundsException();}
		if (getHead() == null || fromIndex > toIndex)
		{
			return null;
		}
		dListNode fetcher = getHead();
		int counter = 0;
		// Iterate to find the fromNode
		while(fetcher != null && counter < fromIndex)
		{
			fetcher = fetcher.getNext();
			counter++;
		}
		int size = 1;
		// If index not found return null
		if(fetcher != null)
		{
			sub.setHead(fetcher);
			while(fetcher != this.getTail() && counter < toIndex-1)
			{
				fetcher = fetcher.getNext();
				counter++;
				size++;
			}
			sub.setTail(fetcher);
			sub.setSize(size);
			return sub;
		}
		
		return null;
	}

	@Override
	public boolean contains(Object o) {
		int[] element = (int[])o;
		if (getHead() == null)
		{
			return false;
		}
		dListNode fetcher = getHead();
		while(fetcher != null)
		{
			if (((int[])fetcher.getElement())[0] == element[0] && 
					((int[])fetcher.getElement())[1] == element[1])
			{
				return true;
			}
			if(fetcher == this.tail) {
				fetcher = null;
			}
			else {
				fetcher = fetcher.getNext();
			}
		}
		return false;
	}
	
	void printTitle() {
		dListNode fetcher = this.head;
		while (fetcher != null) {
			Mail m = (Mail)fetcher.getElement();
			System.out.println(m.getTitle());
			fetcher = fetcher.getNext();
		}
	}
	
	/*
	 * Iterative implementation of quicksort sorting algorithm using stack 
	 *  */ 
	public void Qsort(Comparator<Object> cmp) { 
		
		Stack stack = new Stack(); 
		// Push the head and tail of the list means we are going to sort all the list
		stack.push(this.head); 
		stack.push(this.tail);
		// While stack is not empty means there is indices to be sorted
		while (!stack.isEmpty()) { 
			// Get the end and start index from stack to arrange the list between them
			dListNode end = (dListNode)stack.pop();	
			dListNode start = (dListNode)stack.pop();
			// If the start and end indices are the same 
			// Or end crossed start
			// Or the indices crossed the limits of the list
			// Then no sort to be performed on this indices
			if (start == end || start == null || end == null || start.getPrev() == end) { continue; } 
			// Select the median between the two indices as the pivote
			dListNode p = getListCenter(start, end);	
			// Call partitioning function
			p = partition(p, start, end, cmp);
			// Sort list after the pivote
			stack.push(p.getNext());
			stack.push(end);
			// Sort list before the pivote
			stack.push(start);
			stack.push(p.getPrev());
			
		} 
	}
	
	/*
	 * This function get the  median between two indices in the list
	 * */
	private dListNode getListCenter(dListNode start, dListNode end)
	{
		 if (start == null) { 
	            return start; 
		 }
		 	dListNode fetcher1 = start;
		 	dListNode fetcher2 = start; 
	  
	        while (fetcher2 != null && fetcher2 != end && fetcher2.getNext() != end) { 
	        	fetcher1 = fetcher1.getNext(); 
	            fetcher2 = fetcher2.getNext().getNext(); 
	        } 
	        return fetcher1; 
	 }
	
	/*
	 * Separate the list into partitions where the part before the pivote is of values less than it
	 * And the after it is of values more than it
	 * */
	private dListNode partition (dListNode position, dListNode start, dListNode end, Comparator<Object> cmp) {
		
		// Pointer from the start of the list
		dListNode l = start; 
		// Pointer from the last element of the list
		dListNode h = end;
		// Object to be compared to be placed in its right position
		Object piv = position.getElement(); 
		// Swap the pivote and end index to put the pivote at the end of the list
		swap(position, end); 
		// We will separate the list to part before the pivote less than it 
		// and other after the pivote more than it 
		while (l != h && l.getPrev() != h) { 
			// Advance left pointer till find key more than that of the pivote 
			if (cmp.compare(l.getElement(), piv) < 0) { 
				l = l.getNext(); 
			} 
			// Fall back right pointer till find key less than that of the pivote
			else if (cmp.compare(h.getElement(), piv) >= 0) { 
				h = h.getPrev(); 
			} 
			// When both left and right pointer stop swap them
			else { 
				swap(l, h);
			} 
		}
		
		dListNode idx = h;
		
		if (cmp.compare(h.getElement(), piv) < 0) { 
			idx = idx.getNext(); 
		} 
		// Return the pivote to its place between the less part and more part
		swap(end, idx); 
		// Return the pivote
		return idx; 
	}
	
	/*
	 * Swap the elements of two nodes
	 * */
	private void swap(dListNode i, dListNode j) { 
		Object temp = i.getElement(); 
		i.setElement(j.getElement()); 
		j.setElement(temp);
	}
	
	/*
	 * Forward Iterator for the list
	 * */
	private class ForwardListIterator implements Iterator<Object> {

		private dListNode fetcher;
		
		ForwardListIterator (dListNode node)
		{
			fetcher = node;
		}
		
		@Override
		public boolean hasNext() {
			if (fetcher != null) { return true;}
			return false;
		}

		@Override
		public Object next() {
			if (!this.hasNext()) { throw new IllegalArgumentException();}
			Object temp = fetcher.getElement();
			fetcher = fetcher.getNext();
			return temp;
		}
		
	}
	/*
	 * Backward Iterator for the list
	 * */
	private class BackwardListIterator implements Iterator<Object> {

		private dListNode fetcher;
		
		BackwardListIterator (dListNode node)
		{
			fetcher = node;
		}
		
		@Override
		public boolean hasNext() {
			if (fetcher != null) { return true;}
			return false;
		}

		@Override
		public Object next() {
			if (!this.hasNext()) { throw new IllegalArgumentException();}
			Object temp = fetcher.getElement();
			fetcher = fetcher.getPrev();
			return temp;
		}
		
	}
	/*
	 * Returns Iterator view for the list
	 * */
	public Iterator<Object> iterator(boolean forward) {
	    	if (forward) {
	    		return new ForwardListIterator(this.head);
	    	}
	    	return new BackwardListIterator(this.tail);
	    }
	
	/*
	 * Returns a list carrying same objects in the current list 
	 * */
	public DoublyLinkedList copyView() {
		DoublyLinkedList list = new DoublyLinkedList();
		Iterator<Object> it = this.iterator(true);
		while(it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}
	
	private void setSize(int size) {
		this.size = size;
	}

	public dListNode getHead() {
		return head;
	}

	public void setHead(dListNode head) {
		this.head = head;
	}

	public dListNode getTail() {
		return tail;
	}

	public void setTail(dListNode tail) {
		this.tail = tail;
	}
	
	public DoublyLinkedList searchStack(Comparator<Object> comp, Object value) {
		this.Qsort(comp);
		Stack stack = new Stack();
		DoublyLinkedList back = new DoublyLinkedList();
		stack.push(this.head);
		stack.push(this.tail);
		while (!stack.isEmpty()) {
			dListNode end = (dListNode) stack.pop();
			dListNode start = (dListNode) stack.pop();
			if(end == null || start == null || end.getNext() == start) {
				break;
			}
			dListNode node = getListCenter(start, end);

			if (comp.compare(node.getElement(), value) == 0) {
				back.add(node.getElement());
				dListNode pointer;
				pointer = node.getNext();
				while (pointer != null) {
					if (comp.compare(pointer.getElement(), value) == 0) {
						back.add(pointer.getElement());
					}
				pointer = pointer.getNext();
				}
				pointer = node.getPrev();
				while (pointer != null) {
					if (comp.compare(pointer.getElement(), value) == 0) {
						back.add(pointer.getElement());
					}
					pointer = pointer.getPrev();
				}
			//searching right side
			} else if (comp.compare(node.getElement(), value) < 0) {
				stack.push(node.getNext());
				stack.push(end);
			//searching left side
			} else {
				stack.push(start);
				stack.push(node.getPrev());
			}

		}
		return back;
	}
}
