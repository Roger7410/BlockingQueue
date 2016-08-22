package cs601.blkqueue;

import java.util.ArrayList;

public class SynchronizedBlockingQueue<T> implements MessageQueue<T> {

    private int size;
    private ArrayList<T> arrayList;

	public SynchronizedBlockingQueue(int size) {
        this.size = size;
        arrayList = new ArrayList<T>(size);
	}

	@Override
	public synchronized void put(T o) throws InterruptedException {
        while ( arrayList.size() == this.size){
            wait();
        }
        arrayList.add(o);
        notifyAll();
	}

	@Override
	public synchronized T take() throws InterruptedException {
		while ( arrayList.size() == 0 ){
            wait();
        }
        T tmp = arrayList.get(0);
        arrayList.remove(0);
        notifyAll();
        return tmp;
	}
}
