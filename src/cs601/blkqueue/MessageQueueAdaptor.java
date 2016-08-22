package cs601.blkqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class MessageQueueAdaptor<T> implements MessageQueue<T> {

    private int size;
	private ArrayBlockingQueue<T> abq;

    MessageQueueAdaptor(int size) {
        this.size = size;
        abq = new ArrayBlockingQueue<T>(size);
    }

	@Override
	public void put(T o) throws InterruptedException {
        abq.put(o);
	}

	@Override
	public T take() throws InterruptedException {
        return abq.take();
	}
}
