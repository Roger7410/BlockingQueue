package cs601.blkqueue;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

public class RingBuffer<T> implements MessageQueue<T> {
	private final AtomicLong w = new AtomicLong(-1);	// just wrote location
	private final AtomicLong r = new AtomicLong(0);		// about to read location

    private int size;
    private T[] array;

	public RingBuffer(int n) {
        if(isPowerOfTwo(n)){
            this.size = n;
        }
        else{
            throw new IllegalArgumentException("Size should be power of 2");
        }
        array = (T[]) new Object[size];
	}

	// http://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
	static boolean isPowerOfTwo(int v) {
		if (v<0) return false;
		v = v - ((v >> 1) & 0x55555555);                    // reuse input as temporary
		v = (v & 0x33333333) + ((v >> 2) & 0x33333333);     // temp
		int onbits = ((v + (v >> 4) & 0xF0F0F0F) * 0x1010101) >> 24; // count
		// if number of on bits is 1, it's power of two, except for sign bit
		return onbits==1;
	}

	@Override
	public void put(T v) throws InterruptedException {
        while (w.intValue()-r.intValue()+1 == size){//mark
            LockSupport.parkNanos(1);
        }
        array[(w.intValue()+1)&(size-1)] = v;
        w.getAndIncrement();
	}

	@Override
	public T take() throws InterruptedException {
        while( r.intValue() > w.intValue() ){
            LockSupport.parkNanos(1);
        }
        T tmp = array[r.intValue()&(size-1)];
        r.getAndIncrement();
		return tmp;
	}
}
