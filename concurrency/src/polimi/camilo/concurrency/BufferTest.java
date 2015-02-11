package polimi.camilo.concurrency;

import java.util.*;
import java.lang.*;
import java.io.*;

/* Name of the class has to be "Main" only if the class is public. */
public class BufferTest
{
	public static void main (String[] args) throws java.lang.Exception
	{
		Buffer buffer = new Buffer();
		(new Getter(buffer,1)).start();
		(new Reader(buffer,1)).start();
		(new Getter(buffer,2)).start();
		buffer.write(2,2);
		Thread.sleep(1000);
		buffer.write(1,1);
		System.out.println("Write");
		System.out.println(buffer.read(1));
	}
}

class Reader extends Thread {
	Buffer _buffer;
	int _pos;
	
	public Reader(Buffer buffer, int pos) {
		_buffer = buffer;
		_pos = pos;
	}
	
    public void run() {
    	System.out.println("Start reading...");
		try {
			System.out.println(_buffer.read(_pos));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

class Getter extends Thread {
	Buffer _buffer;
	int _pos;
	
	public Getter(Buffer buffer, int pos) {
		_buffer = buffer;
		_pos = pos;
	}
	
    public void run() {
    	System.out.println("Start getting...");
		try {
			System.out.println(_buffer.get(_pos));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

class Buffer {
	List<LockableInt> _data;
	
	public Buffer() {
		_data = new ArrayList<LockableInt>();
		for (int i = 0; i < 10; i++) {
			_data.add(i, new LockableInt());
		}
	}
	
	public int read(int pos) throws InterruptedException {
		return _data.get(pos).read();
	}
	
	public void write(int value, int pos) throws InterruptedException {
		_data.get(pos).write(value);
	}
	
	public int get(int pos) throws InterruptedException {
		return _data.get(pos).get();
	}
}

class LockableInt {
	private int _data = -1;
	private int _nReadWaiting = 0;
	
	public synchronized int read() throws InterruptedException {
		_nReadWaiting++;
		while(_data == -1) {
			this.wait();
		}
		_nReadWaiting--;
		System.out.println("read");
		this.notifyAll();
		return _data;
	}
	
	public synchronized void write(int value) throws InterruptedException {
		while(_data != -1) {
			this.wait();
		}
		_data = value;
		this.notifyAll();
	}
	
	public synchronized int get() throws InterruptedException {
		while(_data == -1 || _nReadWaiting > 0) {
			this.wait();
		}
		int value = _data;
		_data = -1;
		System.out.println("get");
		this.notifyAll();
		return value;
	}
}
