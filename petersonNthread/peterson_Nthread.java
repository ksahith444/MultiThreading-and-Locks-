package petersonNthread;

import java.util.Random;

public class peterson_Nthread {

	int instances;
	int levels = 0;
	int N;
	static int cnt = 0;
	static volatile double delay = 0;

	volatile Peterson[] PeterInstances;
	volatile int[] IncomingThreadIDs;
	int[] threadIDS;
	Tree t;

	peterson_Nthread(int n) {
		N = n;
		int temp = n;
		instances = n - 1;

		PeterInstances = new Peterson[instances]; // creating peterson instances
		for (int i = 0; i < instances; i++) {
			PeterInstances[i] = new Peterson();
		}

		threadIDS = new int[n];
		for (int i = 0; i < n; i++) {
			threadIDS[i] = i;
		}

		while (temp != 1) // no. of levels in binary tree
		{
			temp /= 2;
			levels++;
		}

		IncomingThreadIDs = new int[n];
		for (int i = 0; i < n; i += 2) {
			IncomingThreadIDs[i] = cnt;
			IncomingThreadIDs[i + 1] = cnt;
			cnt += 2;
		}

		t = new Tree();
		t.buildBinaryTree(0, threadIDS.length - 1, threadIDS);
	}

	public static void main(String[] args) {

		int numberofthreads = 0;
		int mean = 0;
		for (int p = 2; p < 5; p = p * 2) {
			numberofthreads = p;
			for (int q = 10; q < 101; q = q + 10) {
				mean = q;

				Counter c = new Counter(0);
				Thread[] t = new Thread[numberofthreads];
				peterson_Nthread l = new peterson_Nthread(numberofthreads);
				Random r = new Random();

				System.out.println("Starting....");
				long start = System.currentTimeMillis();

				for (int k = 0; k < 500; k++) {
					for (int i = 0; i < t.length; ++i) {
						t[i] = new Thread(new CriticalSection(c, i, l));

						t[i].start();
						delay = l.exp_rv(r, mean);
						try {
							Thread.sleep((long) (delay * 0.001));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				for (int k = 0; k < 500; k++) {
					for (int i = 0; i < t.length; ++i) {
						try {
							t[i].join();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();// don't expect
																// it but good
																// practice to
																// handle anyway
						}
					}
				}

				System.out.println("ending....");
				long end = System.currentTimeMillis();

				System.out.println("Time taken: " + (end - start));
				System.out.println(c.get());
			}
		}
	}

	public double exp_rv(Random r, double mean) {
		return -(Math.log(1 - r.nextDouble()) * mean);
	}

	public void lock(int id) {

		int[] pathtoroot = new int[levels];
		pathtoroot[0] = IncomingThreadIDs[id];

		for (int k = 1; k < levels; k++) {
			pathtoroot[k] = t.findParent(pathtoroot[k - 1]);
		}

		for (int i = 0; i < levels; i++) {
			PeterInstances[pathtoroot[i]].lock();

		}

	}

	public void unlock(int id) {
		int[] path = new int[levels];
		path[0] = IncomingThreadIDs[id];

		for (int k = 1; k < levels; k++) {
			path[k] = t.findParent(path[k - 1]);
		}

		for (int i = levels - 1; i >= 0; i--) {
			PeterInstances[path[i]].unlock();
		}
	}

}
