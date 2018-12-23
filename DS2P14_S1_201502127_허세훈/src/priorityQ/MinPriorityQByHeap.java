package priorityQ;

public class MinPriorityQByHeap<T extends Comparable<T>> extends MinPriorityQ<T> {

	private static final int DEFAULT_CAPACITY = 100;
	private static final int HEAP_ROOT = 1;
		
	private int _size;
	private int _capacity;
	private T[] _heap;
	
	public int size() {
		return this._size;
	}
	private void setSize(int newSize) {
		this._size = newSize;
	}
	public int capacity() {
		return this._capacity;
	}
	private void setCapacity(int newCapacity) {
		this._capacity = newCapacity;
	}
	private T[] heap() {
		return this._heap;
	}
	private void setHeap(T[] newHeap) {
		this._heap = newHeap;
	}
	
	public MinPriorityQByHeap() {
		this(MinPriorityQByHeap.DEFAULT_CAPACITY);
	}
	
	@SuppressWarnings("unchecked")
	public MinPriorityQByHeap(int givenCapacity) {
		this.setCapacity(givenCapacity);
		this.setHeap((T[]) new Comparable[givenCapacity + 1]);
		this.setSize(0);
	}
	
	@Override
	public boolean isEmpty() {
		return (this.size() == 0);
	}

	@Override
	public boolean isFull() {
		return (this.size() == this.capacity());
	}

	@Override
	public boolean add(T anElement) {
		if(this.isFull()) {
			return false;
		}
		else {
			int positionForAdd = this.size() + 1;
			this.setSize(positionForAdd);
			while( (positionForAdd) > 1 &&
					(anElement.compareTo(this.heap()[positionForAdd / 2])< 0) )
			{ //부모노드보다 작다면~ + 배열의 size가 2 이상이라면~
				this.heap()[positionForAdd] = this.heap()[positionForAdd / 2];
				//부모노드를 내린다.
				positionForAdd = positionForAdd / 2;
			}
			this.heap()[positionForAdd] = anElement;
			return true;
		}
	}

	@Override
	public T min() {
		if(this.isEmpty()) {
			return null;
		}
		else {
			return this.heap()[MinPriorityQByHeap.HEAP_ROOT];
		}
	}

	@Override
	public T removeMin() {
		if(this.isEmpty()) {
			return null;
		}
		else {
			T rootElement = this.heap()[MinPriorityQByHeap.HEAP_ROOT];
			this.setSize(this.size() - 1);
			if(this.size() > 0) {
				T lastElement = this.heap()[this.size() + 1];
				int parent = MinPriorityQByHeap.HEAP_ROOT;
				while( (parent * 2) <= this.size() ) {
					int smallerChild = parent * 2;
					if( (smallerChild < this.size()) &&
							(this.heap()[smallerChild].compareTo(this.heap()[smallerChild + 1]) > 0) )
					{
						smallerChild++;
					}
					if( lastElement.compareTo(this.heap()[smallerChild]) <= 0) {
						break;
					}
					this.heap()[parent] = this.heap()[smallerChild];
					parent = smallerChild;
				}
				this.heap()[parent] = lastElement;
			}
			return rootElement;
		}
	}
	
}
