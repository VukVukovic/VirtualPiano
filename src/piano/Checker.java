package piano;

import java.util.LinkedList;

public class Checker extends Thread {
	private int currentMusicSymbol = 0;
	private Notebook notebook;
	private Composition composition=null;
	private LinkedList<Pitch> buffer = new LinkedList<Pitch>();
	private boolean paused = false;
	
	private long startTime;
	
	public Checker(Notebook notebook) {
		this.notebook = notebook;
		this.startTime = System.currentTimeMillis();
		start();
	}
	
	public synchronized void setComposition(Composition composition) {
		this.composition = composition;
		currentMusicSymbol=0;
		notify();
	}
	
	public synchronized void add(Pitch p) {
		long currentTime = System.currentTimeMillis() - startTime;
		buffer.add(p);
	}
	
	public synchronized void pauseChecking() {
		paused = true;
	}
	
	public synchronized void continueChecking() {
		paused = false;
		notify();
	}
	
	@Override
	public void run() {
		try {
			while (!interrupted()) {
				MusicSymbol ms;
				
				synchronized(this) {
					while (paused || composition==null || currentMusicSymbol==composition.size())
						wait();
					
					ms = composition.get(currentMusicSymbol);
					buffer.clear();
				}
				
				if (ms.duration().equals(Duration.d1_4)) sleep(300);
				else sleep(150);
				
				synchronized (this) {
					if (paused) continue;
					if (ms.checkPlayed(buffer)) { 
						notebook.move(); 
						currentMusicSymbol++; 
					}
				}
			}
		} catch (InterruptedException e) {}
	}
}
