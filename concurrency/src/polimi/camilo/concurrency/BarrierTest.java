package polimi.camilo.concurrency;

public class BarrierTest {

	public static void main(String[] args) throws InterruptedException {		
		Barrier barrier = new Barrier(3);
		System.out.println("Await test");
		(new Runner(barrier)).start();
		(new Runner(barrier)).start();
		Thread.sleep(1000);
		barrier.await();
		Thread.sleep(1000);
		System.out.println("Interrupt test");
		(new Runner(barrier)).start();
		(new Runner(barrier)).start();
		barrier.interrupt();
	}

}

class Runner extends Thread {
	Barrier _barrier;
	
	public Runner (Barrier barrier) {
		_barrier = barrier;
	}
	
    public void run() {
    	try {
			_barrier.await();
		} catch (InterruptedException e) {
			System.out.println("interrupted");
		}
    }
}

class Barrier {
	private int _n;
	private int _count;
	private boolean _isInterrupt;
	
	public Barrier (int n) {
		_n = n;
	}
	
	public synchronized void await() throws InterruptedException {
		_count++;
		System.out.println("wait #" + _count);		
		if (_count < _n) {
			this.wait();
		}
		System.out.println("resume #" + _count);
		_count--;
		if (_isInterrupt) {
			if (_count == 0) {
				_isInterrupt = false;
			}
			throw new InterruptedException();
		} else {
			this.notifyAll();
		}
	}
	
	public synchronized void interrupt() {
		System.out.println("interrupt");
		_isInterrupt = true;
		this.notifyAll();
	}
}
