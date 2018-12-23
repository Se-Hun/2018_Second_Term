package priorityQ;

public abstract class MinPriorityQ<T> {


	//private static final int DEFAULT_CAPACITY = 100;
	//private static final int HEAP_ROOT = 1;
	
	//private int _size;
	//private int _capacity;
	//private T[] _heap;
	
	public abstract int size();
	public abstract int capacity();
	
	public abstract boolean isEmpty();
	public abstract boolean isFull();
	
	public abstract boolean add(T anElement);
	
	public abstract T min();
	
	public abstract T removeMin();
	
	/*public int size() {
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
	
	public MinPriorityQ() {
		this(MinPriorityQ.DEFAULT_CAPACITY);
	}
	
	@SuppressWarnings("unchecked")
	public MinPriorityQ(int givenCapacity) {
		this.setCapacity(givenCapacity);
		this.setHeap((T[]) new Comparable[givenCapacity + 1]);
		this.setSize(0);
	}
	
	public boolean isEmpty() {
		return (this.size() == 0);
	}
	public boolean isFull() {
		return (this.size() == this.capacity());
	}
	
	public boolean add(T anElement) {
		if(this.isFull()) {
			return false;
		}
		else {
			int positionForAdd = this.size() + 1;
			this.setSize(positionForAdd);
			while( (positionForAdd) > 1 &&
					(anElement.compareTo(this.heap()[positionForAdd / 2])< 0) )
			{ //�θ��庸�� �۴ٸ�~ + �迭�� size�� 2 �̻��̶��~
				this.heap()[positionForAdd] = this.heap()[positionForAdd / 2];
				//�θ��带 ������.
				positionForAdd = positionForAdd / 2;
			}
			this.heap()[positionForAdd] = anElement;
			return true;
		}
		return true;
	}
	
	public T min() {
		if(this.isEmpty()) {
			return null;
		}
		else {
			return this.heap()[MinPriorityQ.HEAP_ROOT];
		}
	}
	
	public T removeMin() {
		if(this.isEmpty()) {
			return null;
		}
		else {
			T rootElement = this.heap()[MinPriorityQ.HEAP_ROOT];
			this.setSize(this.size() - 1);
			if(this.size() > 0) {
				T lastElement = this.heap()[this.size() + 1];
				int parent = MinPriorityQ.HEAP_ROOT;
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
	*/
}
