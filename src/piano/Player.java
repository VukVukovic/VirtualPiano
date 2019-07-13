package piano;

import java.util.LinkedList;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class Player extends Thread {
	private LinkedList<PlayerEventListener> listeners = new LinkedList<PlayerEventListener>();
	
	private boolean playing = false;
	
	private Composition composition;
	
	private Notebook notebook;
	private GraphicalPiano graphicalPiano;
	
	private int currentMusicSymbol = 0;
	
	private static final int DEFAULT_INSTRUMENT = 1;
	private MidiChannel channel;
	
	
	public Player(Notebook notebook, GraphicalPiano graphicalPiano) throws MidiUnavailableException { 
		this.channel = getChannel(DEFAULT_INSTRUMENT);
		this.notebook = notebook;
		this.graphicalPiano = graphicalPiano;
		start();
	}
	
	private static MidiChannel getChannel(int instrument) throws MidiUnavailableException {
		Synthesizer synthesizer = MidiSystem.getSynthesizer();
		synthesizer.open();
		return synthesizer.getChannels()[instrument];
	}
	
	private void notifyListenersStarted() {
		for (PlayerEventListener l : listeners) l.playingStarted();
	}
	
	private void notifyListenersFinished() {
		for (PlayerEventListener l : listeners) l.playingFinished();
	}
	
	public void addPlayerEventListener(PlayerEventListener listener) {
		listeners.add(listener);
	}
	
	public synchronized void play() {
		playing = true;
		notebook.setComposition(composition, 0);
		notify();
	}
	
	public synchronized void setComposition(Composition composition) {
		this.composition = composition;
		this.currentMusicSymbol = 0;
		this.playing=false;
	}
	
	public synchronized void toStart() {
		playing = false;
		currentMusicSymbol = 0;
	}
	
	public synchronized void reset() {
		composition = null;
		playing = false;
		currentMusicSymbol = 0;
	}
	
	public synchronized void continuePlaying() {
		if (composition!=null) {
			playing = true;
			notebook.setComposition(composition, currentMusicSymbol);
			notify();
		}
	}
	
	public synchronized void pausePlaying() {
		if (composition!=null) {
			playing = false;
		}
	}
	
	public synchronized boolean playing() {
		return playing;
	}

	public synchronized boolean hasComposition() {
		return composition!=null;
	}
	public synchronized void press(final int note) {
		channel.noteOn(note, 50);
	}
	
	public synchronized void release(final int note) {
		channel.noteOff(note, 50);
	}
	
	@Override
	public void run() {
		try {
			while (!interrupted()) {
				synchronized (this) {
					while (!playing)
						wait();
				}
				notifyListenersStarted();
					while (currentMusicSymbol < composition.size()) {
						MusicSymbol ms = composition.get(currentMusicSymbol);
						
						ms.showKeyPress(graphicalPiano);
						ms.play(this, composition.getMapping());
						ms.showKeyRelease(graphicalPiano);
						
						notebook.move();
						
						synchronized (this) {
							if (!playing) break;
							currentMusicSymbol++;
						}
					}
					if (currentMusicSymbol==composition.size())
						reset();
					notifyListenersFinished();
				}
			
		} catch (InterruptedException e) {
			notifyListenersFinished();
		}
	}
}
