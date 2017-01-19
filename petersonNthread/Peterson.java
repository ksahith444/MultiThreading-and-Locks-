package petersonNthread;

public class Peterson {
	 
	 private boolean[] flag = new boolean[2];
	 private int victim;
	 
	 public void lock() {
		// System.out.println("threadid " + Thread.currentThread().getId());
	 int i = (int) Thread.currentThread().getId()%2;
	
	 int j = 1 - i;
	 flag[i] = true;
	 victim = i;
	while (flag[j] && victim == i) {}; // wait
	 }
	 
	 public void unlock() {
	 int i = (int) Thread.currentThread().getId()%2;
	 flag[i] = false;
	 }

	 }
