package piano;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.KeyListener;

public class GraphicalPiano extends Panel {
	public static final int OCTAVE_NUM = 5, FIRST_OCTAVE = 2;
	
	private GraphicalOctave octaves[] = new GraphicalOctave[OCTAVE_NUM];
	
	public GraphicalPiano(Mapping map) {
		setLayout(new GridLayout(1, OCTAVE_NUM));
		
		for (int i = 0; i < OCTAVE_NUM; i++) {
			octaves[i] = new GraphicalOctave(map, i==0, i+FIRST_OCTAVE);
			add(octaves[i]);
		}
	}
	
	public void addPianoEventListener(PianoEventListener listener) {
		for (int i = 0; i < octaves.length; i++)
			octaves[i].addPianoEventListener(listener);
	}
	
	public synchronized void pressed(Pitch p, boolean show) {
		int i = p.octave()-FIRST_OCTAVE;
		octaves[i].press(p, show);
	}
	
	public synchronized void released(Pitch p, boolean show) {
		int i = p.octave()-FIRST_OCTAVE;
		octaves[i].release(p, show);
	}
	
	public synchronized void setLabels(boolean visible) {
		for (int i=0;i<octaves.length;i++)
			octaves[i].setLabels(visible);
	}
	
	@Override
	public void addKeyListener(KeyListener keyListener) {
		for (int i=0;i<octaves.length;i++)
			octaves[i].addKeyListener(keyListener);
		super.addKeyListener(keyListener);
	}
}
