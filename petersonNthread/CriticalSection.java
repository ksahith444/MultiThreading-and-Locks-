package petersonNthread;

public class CriticalSection implements Runnable{

	    private volatile Counter c;
	    private int id;
	    private peterson_Nthread lockpNthread;
	   
	    public CriticalSection(Counter c, int id, peterson_Nthread lock) {
	        this.c = c;
	        this.id = id;
	        lockpNthread = lock;
	    }
	      
	    public void run() {
	            lockpNthread.lock(id);
            		c.getAndIncrement();	
	            lockpNthread.unlock(id);
	    }
	}


class Counter {
    private volatile int value;
    public Counter(int c)   {
        value = c;
    }
    public int get()
    {
        return value;
    }
    public int getAndIncrement()  { 
        return value++;
    }
}