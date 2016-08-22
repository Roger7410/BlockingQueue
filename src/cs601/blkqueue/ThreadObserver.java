package cs601.blkqueue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

/** A runnable class that attaches to another thread and wakes up
 *  at regular intervals to determine that thread's state. The goal
 *  is to figure out how much time that thread is blocked, waiting,
 *  or sleeping.
 */
class ThreadObserver implements Runnable {
	protected final Map<String, Long> histogram = new HashMap<String, Long>();
	protected int numEvents = 0;
	protected int blocked = 0;
	protected int waiting = 0;
	protected int sleeping = 0;

    protected boolean done = false;
    protected StackTraceElement[] ste;

    protected final Thread threadToMonitor;
    protected final long periodInNanoSeconds;

	public ThreadObserver(Thread threadToMonitor, long periodInNanoSeconds) {
        this.threadToMonitor = threadToMonitor;
        this.periodInNanoSeconds = periodInNanoSeconds;
	}

	@Override
	public void run() {
        while(!done){
            numEvents++;
            switch (threadToMonitor.getState()){
                case BLOCKED: blocked++; break;
                case WAITING: waiting++; break;
                case TIMED_WAITING: sleeping++; break;
            }

            ste = threadToMonitor.getStackTrace();
            if(ste.length != 0){
                if(!histogram.containsKey(ste[0].getClassName()+"."+ste[0].getMethodName())){
                    histogram.put(ste[0].getClassName()+"."+ste[0].getMethodName(),1L);
                }
                else{
                    histogram.put(ste[0].getClassName()+"."+ste[0].getMethodName(),histogram.get(ste[0].getClassName()+"."+ste[0].getMethodName())+1);
                }
            }
            LockSupport.parkNanos(periodInNanoSeconds);
        }

	}

	public Map<String, Long> getMethodSamples() { return histogram; }

	public void terminate() { done = true; }

	public String toString() {
		return String.format("(%d blocked + %d waiting + %d sleeping) / %d samples = %1.2f%% wasted",
							 blocked,
							 waiting,
							 sleeping,
							 numEvents,
							 100.0*(blocked + waiting + sleeping)/numEvents);
	}
}
