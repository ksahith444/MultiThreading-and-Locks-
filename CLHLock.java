

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class CLHLock {
	AtomicReference<QNode> tail;
	ThreadLocal<QNode> myPred;
	ThreadLocal<QNode> myNode;
	static volatile double delay = 0;

	public CLHLock() {
		tail = new AtomicReference<QNode>(new QNode());
		myNode = new ThreadLocal<QNode>() {
			protected QNode initialValue() {
				return new QNode();
			}
		};
		myPred = new ThreadLocal<QNode>() {
			protected QNode initialValue() {
				return null;
			}
		};
	}

	public void lock() {
		QNode qnode = myNode.get();
		qnode.locked = true;
		QNode pred = tail.getAndSet(qnode);
		myPred.set(pred);
		while (pred.locked) {
		}
	}

	public void unlock() {
		QNode qnode = myNode.get();
		qnode.locked = false;
		myNode.set(myPred.get());
	}

	public double exp_rv(Random r, double mean) {
		return -(Math.log(1 - r.nextDouble()) * mean);
	}

	public static void main(String[] args) {
        
                int numberofthreads =Integer.parseInt(args[0]);
                int mean = Integer.parseInt(args[1]);
				Counter c = new Counter(0);
				Thread[] t = new Thread[numberofthreads];
				Random r = new Random();
				CLHLock lockCLH = new CLHLock();

			//	System.out.println("Starting....");
				long start = System.currentTimeMillis();

				for (int k = 0; k < 1000; k++) {
					for (int i = 0; i < t.length; ++i) {
						t[i] = new Thread(new CLHCriticalSection(c, lockCLH));
						t[i].start();
						delay = lockCLH.exp_rv(r, mean);
						try {
							Thread.sleep((long) (delay * 0.001));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				for (int k = 0; k < 1000; k++) {
					for (int i = 0; i < t.length; ++i) {
						try {
							t[i].join();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}

			//	System.out.println("ending....");
				long end = System.currentTimeMillis();

				System.out.println("CLHLock Time taken for " +numberofthreads +" threads with mean "+ mean + " is :" + (end - start));
				System.out.println(c.get());
			}
		
	
}

class Counter {
	private volatile int value;

	public Counter(int c) {
		value = c;
	}

	public int get() {
		return value;
	}

	public int getAndIncrement() {
		return value++;
	}
}

class CLHCriticalSection implements Runnable {

	private volatile Counter c;
	// private int id;
	private CLHLock lockCLH;

	public CLHCriticalSection(Counter c, CLHLock lock) {
		this.c = c;
		//this.id = id;
		lockCLH = lock;
	}

	public void run() {
		lockCLH.lock();
		//System.out.println(id);
		c.getAndIncrement();
		lockCLH.unlock();
	}
}

class QNode {
	volatile boolean locked = false;
}
