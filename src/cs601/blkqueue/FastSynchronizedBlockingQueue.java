package cs601.blkqueue;

import java.util.ArrayList;

public class FastSynchronizedBlockingQueue<T> implements MessageQueue<T> {

    private int size;
    private ArrayList<T> arrayList;

	public FastSynchronizedBlockingQueue(int size) {
        this.size = size;
        arrayList = new ArrayList<T>(size);
	}

	@Override
	public synchronized void put(T o) throws InterruptedException {
        while ( arrayList.size() == size ){
            wait();
        }
        if ( arrayList.size() == 0 ){
            notifyAll();
        }
        arrayList.add(o);
	}

	@Override
	public synchronized T take() throws InterruptedException {
        while ( arrayList.size() == 0 ){
            wait();
        }
        if ( arrayList.size() == size ){
            notifyAll();
        }
        T tmp = arrayList.get(0);
        arrayList.remove(0);
		return tmp;
	}
}
