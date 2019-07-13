package piano;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Pause extends MusicSymbol {
	public Pause(Duration d) {
		super(d);
	}
	
	protected String print() {
		return (d.equals(Duration.d1_8))?"p":"P";
	}

	@Override
	protected void play(Player p, Mapping map) throws InterruptedException {
		if (d.equals(Duration.d1_4)) Thread.sleep(Piano.BEAT_14);
		else Thread.sleep(Piano.BEAT_18);
	}

	@Override
	protected int draw(Graphics g, Color clr18, Color clr14, Mapping map, boolean symbol, int width1_4, int height) {
		int width = width1_4/2;
		Color clr = clr18;
		if (d.equals(Duration.d1_4)) {
			clr=clr14;
			width=width1_4;
		}
		Color darker = new Color((int)(0.8*clr.getRed()), (int)(0.8*clr.getGreen()), (int)(0.8*clr.getBlue()));
		g.setColor(darker);
		g.fillRect(0, 0, width, height);
		return width;
	}

	@Override
	protected void showKeyPress(GraphicalPiano graphicalPiano) {
	}

	@Override
	protected void showKeyRelease(GraphicalPiano graphicalPiano) {
	}

	@Override
	protected void export(Formatter f) {
		f.export(this);
	}

	@Override
	protected boolean checkPlayed(LinkedList<Pitch> buffer) {
		if (buffer.size()==0) return true;
		return false;
	}
}
