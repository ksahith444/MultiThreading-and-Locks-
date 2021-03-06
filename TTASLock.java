

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class TTASLock {
	AtomicBoolean state = new AtomicBoolean(false);
	static volatile double delay = 0;

	public void lock() {
		while (true) {
			while (state.get()) {
			}
			if (!state.getAndSet(true))
				return;
		}
	}

	public void unlock() {
		state.set(false);
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
				TTASLock lockTTAS = new TTASLock();

				//System.out.println("Starting TTASLock threads....");
				long start = System.currentTimeMillis();

				for (int k = 0; k < 1000; k++) {
					for (int i = 0; i < t.length; ++i) {
						t[i] = new Thread(new TTASCriticalSection(c, lockTTAS));
						t[i].start();
						delay = lockTTAS.exp_rv(r, mean);
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

				//System.out.println("ending....");
				long end = System.currentTimeMillis();

				System.out.println("TTASLock Time taken for " +numberofthreads +" threads with mean "+ mean + " is :" + (end - start));
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

class TTASCriticalSection implements Runnable {

	private volatile Counter c;
	// private int id;
	private TTASLock lockTTAS;

	public TTASCriticalSection(Counter c, TTASLock lock) {
		this.c = c;
		// this.id = id;
		lockTTAS = lock;
	}

	public void run() {
		lockTTAS.lock();
		c.getAndIncrement();
		lockTTAS.unlock();
	}
}
