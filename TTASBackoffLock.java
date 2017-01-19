
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

class Backoff {
	final int minDelay, maxDelay;
	int limit;
	final Random random;

	public Backoff(int min, int max) {
		minDelay = min;
		maxDelay = max;
		limit = minDelay;
		random = new Random();
	}

	public void backoff() throws InterruptedException {

		int delay = random.nextInt(limit);
		limit = Math.min(maxDelay, 2 * limit);
		Thread.sleep(delay);
	}

}

public class TTASBackoffLock {
	private AtomicBoolean state = new AtomicBoolean(false);
	private static int MIN_DELAY;
	private static int MAX_DELAY;
	static volatile double delay = 0;

	public TTASBackoffLock(int MIN_DELAY, int MAX_DELAY) {
		TTASBackoffLock.MIN_DELAY = MIN_DELAY;
		TTASBackoffLock.MAX_DELAY = MAX_DELAY;
	}

	public void lock() {
		Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);
		while (true) {
			while (state.get()) {
			}
			if (!state.getAndSet(true))
				return;
			else
				try {
					backoff.backoff();
				} catch (InterruptedException e) {
				}
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
				TTASBackoffLock lockTTASBackoff = new TTASBackoffLock(2,18);

				// System.out.println("Starting....");
				long start = System.currentTimeMillis();

				for (int k = 0; k < 1000; k++) {
					for (int i = 0; i < t.length; ++i) {
						t[i] = new Thread(new TTASBackoffCriticalSection(c, lockTTASBackoff));
						t[i].start();
						delay = lockTTASBackoff.exp_rv(r, mean);
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

				// System.out.println("ending....");
				long end = System.currentTimeMillis();

				System.out.println("BackoffLock Time taken for " + numberofthreads + " threads with mean " + mean
						+ " is :" + (end - start));
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

class TTASBackoffCriticalSection implements Runnable {

	private volatile Counter c;
	private TTASBackoffLock lockTTASBackoff;

	public TTASBackoffCriticalSection(Counter c, TTASBackoffLock lock) {
		this.c = c;
		lockTTASBackoff = lock;
	}

	public void run() {
		lockTTASBackoff.lock();
		c.getAndIncrement();
		lockTTASBackoff.unlock();
	}
}
