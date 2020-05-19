package Tests;

import org.junit.jupiter.api.Test;

import Classes.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class PriorityQueueTest {
	PriorityQueue queue;

    @Test
    void test() {
        queue = new PriorityQueue();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        queue.insert(2, 1);
        assertFalse(queue.isEmpty());
		assertEquals(1, queue.size());
		queue.insert(3, 2);
		queue.insert(5, 3);
		queue.insert(8, 4);
		queue.insert(4, 3);
		queue.insert(10, 3);
		assertEquals(6, queue.size());
		queue.removeMin();
		queue.removeMin();
		queue.removeMin();
		assertEquals(3, queue.size());
		assertEquals(4, queue.min());
		queue.insert(2, 1);
		assertEquals(4, queue.size());
		assertEquals(2, queue.min());
	}

	@Test
	void test2() {
		queue = new PriorityQueue();
		assertTrue(queue.isEmpty());
		assertEquals(0, queue.size());
		queue.insert(2, 1);
		assertFalse(queue.isEmpty());
		assertEquals(1, queue.size());
		queue.insert(3, 1);
		queue.insert(5, 1);
		queue.insert(8, 1);
		queue.insert(4, 1);
		queue.insert(10, 1);
		assertEquals(6, queue.size());
		queue.removeMin();
		assertEquals(3, queue.min());
		queue.removeMin();
		queue.removeMin();
		assertEquals(3, queue.size());
		assertEquals(8, queue.min());
		queue.insert(2, 1);
		assertEquals(4, queue.size());
		assertEquals(8, queue.min());
	}
	

}