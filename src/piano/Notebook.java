package piano;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Notebook extends Canvas implements Runnable, PianoEventListener {
	private static final int MARKER_NUM = 20;
	private static final double PADDING = 0.1;
	private static final Color COLOR_RED = new Color(0.82352941176f,0f,0f);
	private static final Color COLOR_GREEN = new Color(0.06666666666f, 0.82352941176f, 0f);
	
	private boolean paused = false;
	
	private LinkedList<Pitch> buffer = new LinkedList<Pitch>();
	
	private Thread thr = new Thread(this);
	
	private boolean symbolMode = false;
	private Mapping map;
	
	private Composition composition=null;
	private int symbolInd = 0;
	
	public Notebook(Mapping map) {
		this.map = map;
		thr.start();
	}
	
	public synchronized void setComposition(Composition composition, int symbolInd) {
		this.composition = composition;
		this.symbolInd = symbolInd;
		repaint();
		notify();
	}
	
	public synchronized void move() {
		if (composition == null) return;
		
		if (symbolInd < composition.size())
			symbolInd++;
		
		repaint();
	}
	
	public synchronized void setMode(boolean symbolMode) {
		this.symbolMode = symbolMode;
		repaint();
	}
	
	public synchronized void pauseChecking() {
		paused = true;
	}
	
	public synchronized void continueChecking() {
		paused = false;
		notify();
	}
	
	public synchronized void clear() {
		symbolInd = 0;
		repaint();
		notify();
	}

	public void paint(Graphics g) {
		int w = getWidth(), h = getHeight();
		g.clearRect(0, 0, w, h);
		
		// Draw markers
		int markerWidth = (int)(w*(1-2*PADDING)/(MARKER_NUM-1));
		
		g.setColor(Color.BLACK);
		int x0 = (int)(PADDING*w);
		int y0 = (int)(0.2*h);
		int markerHeight = (int)(0.8*(h-y0));
		for (int i = 0; i < MARKER_NUM;i++) {
			g.fillRect(x0, y0, 2, markerHeight);
			x0 += markerWidth;
		}
		
		if (composition==null) return;
		
		x0 = (int)(PADDING*w);
		y0 = (int)(0.4*h);
		g.translate(x0, y0);
		
		int takenWidth = 0, i = symbolInd;
		while (takenWidth + markerWidth<=(MARKER_NUM-1)*markerWidth && i < composition.size()) {
			MusicSymbol ms = composition.get(i++);
			int dx = ms.draw(g, COLOR_GREEN, COLOR_RED, map, symbolMode, markerWidth, (int)(0.1*h));
			g.translate(dx, 0);
			takenWidth += dx;
		}
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				MusicSymbol ms;
				
				synchronized(this) {
					while (paused || composition==null || symbolInd==composition.size())
						wait();
					
					ms = composition.get(symbolInd);
					buffer.clear();
				}
								
				if (ms.duration().equals(Duration.d1_4)) Thread.sleep(Piano.BEAT_14);
				else Thread.sleep(Piano.BEAT_18);
				
				synchronized (this) {
					if (paused) continue;
					if (ms.checkPlayed(buffer)) 
						move();
				}
			}
		} catch (InterruptedException e) {}
	}
	
	public void interrupt() {
		thr.interrupt();
	}

	@Override
	public synchronized void onButtonPressed(Pitch p, boolean showing) {
		buffer.add(p);
	}

	@Override
	public synchronized void onButtonReleased(Pitch p, boolean showing, long clickedTime, long duration) {
	}
}
