package Classes;

import java.util.Iterator;

import Interfaces.ILinkedList;

public class SinglyLinkedList implements ILinkedList{
	private class listNode {
		
		private listNode next;
		private Object element;
		
		public listNode(listNode node, Object element)
		{
			this.next = node;
			this.setElement(element);
		}
		
		public listNode getNext() {
			return next;
		}
		
		public void setNext(listNode next) {
			this.next = next;
		}

		public Object getElement() {
			return element;
		}

		public void setElement(Object element) {
			this.element = element;
		}
		
	}
	
	private listNode head;
	private listNode tail;
	private int size;
	
	public SinglyLinkedList() {
		size = 0;
	}
	
	public SinglyLinkedList(listNode node) {
		head = node;
		tail = node;
		size = 1;
	}
	@Override
	public void add(int index, Object element) {
		if(index < 0 || index > this.size()) { throw new IndexOutOfBoundsException();}
		listNode newNode = new listNode(null, element);
		if (index == 0) {
			newNode.setNext(this.head);
			this.head = newNode;
			size++;
			if (tail == null) {
				this.tail = this.head;
			}
			
		}
		else if (index >= size) { add(element); }
		else { 
			listNode current = head;
			 for (int i = 1; i < index; i++) {
				 current = current.getNext();
			 }
			 newNode.setNext(current.getNext());
			 current.setNext(newNode);
			 size++;
		}
	}

	@Override
	public void add(Object element) {
		listNode newNode = new listNode(null, element);
		if(this.tail == null) {
			head = tail = newNode;
		}
		else {
			tail.setNext(newNode);
			this.tail = newNode;
		}
		size++;
	}

	@Override
	public Object get(int index) {
		if(index < 0 || index >= this.size()) { return null;}
		// Index starts from 0
		if (getHead() == null || index < 0)
		{
			return null;
		}
		listNode fetcher = getHead();
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
			return fetcher.getElement();
		}
		return null;
	}

	@Override
	public void set(int index, Object element) {
		if(index < 0 || index >= this.size()) { throw new IndexOutOfBoundsException();}
		if (getHead() == null || index < 0) {
			return;
		}
		listNode fetcher = getHead();
		int counter = 0;
		while(fetcher != null && counter < index) {
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
			listNode temp = this.head;
			this.head = this.head.getNext();
			temp.setNext(null);
			size --;
		}
		else {
		listNode prev = this.head;
		for (int i = 1; i < index; i++) {
			prev = prev.getNext();
		}
		if (index == size - 1) {
			this.tail = prev;
			prev.setNext(null);
		}
		else {
			listNode temp = prev.getNext();
			prev.setNext(prev.getNext().getNext());
			temp.setNext(null);
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
		if(fromIndex < 0 || fromIndex > toIndex) { throw new IndexOutOfBoundsException();}
		SinglyLinkedList sub  = new SinglyLinkedList();
		if (getHead() == null || fromIndex > toIndex)
		{
			return null;
		}
		listNode fetcher = getHead();
		int counter = 0;
		// Iterate to find the fromNode
		while(fetcher != null && counter < fromIndex)
		{
			if(fetcher == this.tail) {
				fetcher = null;
			}
			else {
				fetcher = fetcher.getNext();
			}
			counter++;
		}
		int size = 1;
		// If index not found return null
		if(fetcher != null)
		{
			sub.setHead(fetcher);
			while(fetcher != this.tail && counter < toIndex-1)
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
		listNode fetcher = getHead();
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
	private void setSize(int size) {
		this.size = size;
	}

	public listNode getHead() {
		return head;
	}

	private void setHead(listNode head) {
		this.head = head;
	}

	public listNode getTail() {
		return tail;
	}

	private void setTail(listNode tail) {
		this.tail = tail;
	}
	
	/*
	 * Forward Iterator for the list
	 * */
	private class ForwardListIterator implements Iterator<Object> {

		private listNode fetcher;
		
		ForwardListIterator (listNode node)
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
	
	public Iterator<Object> iterator() {
    	return new ForwardListIterator(this.head);
    }
}
