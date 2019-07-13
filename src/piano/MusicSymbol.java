package piano;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public abstract class MusicSymbol {
	protected Duration d;
	
	public MusicSymbol(Duration d) {
		this.d = d;
	}
	
	public Duration duration() {
		return d;
	}
	
	protected abstract String print();
	
	protected abstract void play(Player p, Mapping map) throws InterruptedException;
	
	protected abstract void showKeyPress(GraphicalPiano graphicalPiano);
	
	protected abstract void showKeyRelease(GraphicalPiano graphicalPiano);
	protected abstract int draw(Graphics g, Color clr18, Color clr14, Mapping map, boolean symbol, int width1_4, int height);
	
	protected abstract void export(Formatter f);
	
	protected abstract boolean checkPlayed(LinkedList<Pitch> buffer);
	
	@Override
	public String toString() {
		return print();
	}
}