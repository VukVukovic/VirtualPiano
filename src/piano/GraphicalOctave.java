package piano;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.awt.event.MouseAdapter;

public class GraphicalOctave extends Canvas {
	private LinkedList<PianoEventListener> listeners = new LinkedList<PianoEventListener>();
	
	private Mapping mapping;
	private boolean visibleLabels=true;
	
	private static final int WHITE_NUM=7, BLACK_NUM=5, SKIP_BLACK=1;
	
	private Button whites[], blacks[];
	private int prevw=-1, prevh=-1;
	
	private static final Color WHITE_COLOR = new Color(0.99607843f,0.99607843f,0.99607843f);
	private static final Color WHITE_SHADOW = new Color(0.94921875f, 0.94921875f, 0.94921875f);
	private static final Color BLACK_COLOR = new Color(0.15686274509f,0.15686274509f,0.15686274509f);
	private static final Color BLACK_SHADOW = new Color(0.21960784313f,0.21960784313f,0.21960784313f);
	private static final Color WHITE_PRESSED = new Color(0.84705882352f, 0.84705882352f, 0.84705882352f);
	private static final Color BLACK_PRESSED = new Color(0.09803921568f,0.09803921568f,0.09803921568f);
	private static final Color RED_SHOW = new Color(0.78431372549f, 0f, 0f);
	
	private class Button {
		private final Pitch pitch;
		private boolean pressed, showing, black, first;
		private long timeClicked, startTime;
		
		private int x, y, w, h;
		
		public Button(Pitch pitch, boolean black, boolean first) {
			this.pitch = pitch;
			this.black = black;
			this.first = first;
			this.startTime = System.currentTimeMillis();
		}
		
		public void setLocation(int x, int y) { this.x=x; this.y=y; }
		public void setSize(int w, int h) { this.w=w; this.h=h; }
		
		public Pitch pitch() { return pitch; }
		
		public void press(boolean showing) {
			pressed = true;
			this.showing = showing;
			timeClicked = System.currentTimeMillis()-startTime;
			for (PianoEventListener lis : listeners)
				lis.onButtonPressed(pitch, showing);
		}
		
		public void release() {
			for (PianoEventListener lis : listeners)
				lis.onButtonReleased(pitch, showing, timeClicked, System.currentTimeMillis()-timeClicked);
			pressed = false;
			showing = false;
		}
		
		public void draw(Graphics g) {
			// Base color
			g.setColor(black?BLACK_COLOR:WHITE_COLOR);
			g.fillRect(x, y, w, h);
			
			// Shadow
			int shadowHeight = (int)((black?0.1:0.05)*h);
			g.setColor(black?BLACK_SHADOW:WHITE_SHADOW);
			g.fillRect(x,y+h-shadowHeight, w, shadowHeight);
			
			if (showing) g.setColor(RED_SHOW);
			else if (pressed) g.setColor(black?BLACK_PRESSED:WHITE_PRESSED);
			if (pressed) g.fillRect(x, y, w, h);
			
			if (!black && !first) {
				g.setColor(Color.BLACK);
				g.fillRect(x, y, 2, h);
			}
			
			if (visibleLabels) {
				g.setColor(black?Color.WHITE:Color.BLACK);
				int fontSize = (int)((black?0.6:0.5)*w);
				Font font = new Font("Arial", Font.BOLD, fontSize);
				FontMetrics metrics = g.getFontMetrics(font);
				g.setFont(font);
				
				String label = mapping.getSymbol(pitch);
				int x0 = x + w/2 - metrics.stringWidth(label)/2;
				int y0 = (int)((black?0.7:0.9)*h);
				g.drawString(label, x0, y0);
			}
		}
	}
	
	public GraphicalOctave(Mapping mapping, boolean first, int octave) {
		this.mapping = mapping;
		
		whites = new Button[WHITE_NUM];
		for (int i=0;i<WHITE_NUM;i++)
			whites[i] = new Button(new Pitch(Step.intToStep(i), false, octave), false, first&&i==0);
		
		blacks = new Button[BLACK_NUM];
		for (int i=0;i<BLACK_NUM;i++) {
			Step s = Step.intToStep(i > SKIP_BLACK ? i+1 : i);
			blacks[i] = new Button(new Pitch(s, true, octave), true, false);
		}
		
		updateLocationSize();
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				clicked(e.getPoint().x, e.getPoint().y, false);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				clicked(e.getPoint().x, e.getPoint().y, true);
			}
		});
	}
	
	private void clicked(int x, int y, boolean released) {
		int w = getWidth(), h = getHeight();
		
		if (x < 0 || y<0) return;
		
		int whiteIndex = (int)(x/(w/WHITE_NUM));
		
		int blackHeight = (int)(0.6*h);
		int blackWidth = (int)(w/(2*WHITE_NUM));
		
		int blackIndex = -1;
		
		if (y < blackHeight) {
			int blckInd = (x- w/(2*WHITE_NUM))/(w/WHITE_NUM);
			if (Math.abs(x - (blckInd+1)*w/WHITE_NUM) <= blackWidth/2) {
				if (blckInd > SKIP_BLACK)
					blckInd--;
				if (blckInd < BLACK_NUM) blackIndex = blckInd;
			}
		}
		
		if (blackIndex >= 0) {
			if (blackIndex >= BLACK_NUM) return;
			if (!released) press(blacks[blackIndex].pitch(), false);
			else release(blacks[blackIndex].pitch(), false);
		}
		
		else {
			if (whiteIndex >= WHITE_NUM) return;
			if (!released) press(whites[whiteIndex].pitch(), false);
			else release(whites[whiteIndex].pitch(), false);
		} 
		
		repaint();
	}
	
	public synchronized void press(Pitch p, boolean show) {
		boolean sharp = p.sharp();
		Step s = p.step();
		
		if (sharp) {
			int i = s.value();
			if (i>SKIP_BLACK) i--;
			blacks[i].press(show);
				
		} else 
			whites[s.value()].press(show);
		repaint();
	}
	
	public synchronized void release(Pitch p, boolean show) {
		boolean sharp = p.sharp();
		Step s = p.step();
		if (sharp) {
			int i = s.value();
			if (i>SKIP_BLACK) i--;
			blacks[i].release();
				
		} else 
			whites[s.value()].release();
		repaint();
	}
	
	private void updateLocationSize() {
		int w = getWidth(), h=getHeight();
		if (prevw == w && prevh == h) return;
		prevw=w; prevh=h;
		
		for (int i=0;i<WHITE_NUM;i++) {
			whites[i].setLocation(i*w/WHITE_NUM, 0);
			whites[i].setSize(w/WHITE_NUM, h);
		}
		
		int blackWidth = (int)(w/(2*WHITE_NUM));
		int blackHeight = (int)(0.6*h);
		
		for (int i=0;i<BLACK_NUM;i++) {
			int j = i+1;
			if (i > SKIP_BLACK) j++;
					
			int x0 = j*w/WHITE_NUM - blackWidth/2;
			int y0 = 0;
			
			blacks[i].setLocation(x0, y0);
			blacks[i].setSize(blackWidth, blackHeight);
		}
	}
	
	public synchronized void paint(Graphics g) {
		int w = getWidth(), h = getHeight();
		g.clearRect(0, 0, w, h);
		
		updateLocationSize();
		
		for (int i=0;i<WHITE_NUM;i++)
			whites[i].draw(g);
		
		for (int i=0;i<BLACK_NUM;i++)
			blacks[i].draw(g);
	}
	
	public synchronized void setLabels(boolean visible) {
		this.visibleLabels = visible;
		repaint();
	}
	
	public void addPianoEventListener(PianoEventListener gol) {
		listeners.add(gol);
	}
}
