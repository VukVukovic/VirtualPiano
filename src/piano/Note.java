package piano;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

public class Note extends MusicSymbol implements Iterable<Pitch> {
	private LinkedList<Pitch> pitches = new LinkedList<Pitch>();
	
	public Note(Duration d) {
		super(d);
	}
	
	public Note addPitch(Pitch p) {
		pitches.add(p);
		return this;
	}
	
	public void setDuration(Duration d) {
		this.d = d;
	}
	
	protected String print() {
		StringBuilder sb = new StringBuilder();
		for (Pitch p : pitches)
			sb.append(p);
		return (d.equals(Duration.d1_8))?sb.toString().toLowerCase() : sb.toString();
	}

	@Override
	protected void play(Player player, Mapping map) throws InterruptedException {
		for (Pitch p : pitches)
			player.press(map.getMidi(p));
		if (d.equals(Duration.d1_4)) Thread.sleep(Piano.BEAT_14);
		else Thread.sleep(Piano.BEAT_18);
		for (Pitch p : pitches)
			player.release(map.getMidi(p));
	}

	@Override
	protected int draw(Graphics g, Color clr18, Color clr14, Mapping map, boolean symbol, int width1_4, int height) {
		int width = width1_4/2;
		Color clr = clr18;
		if (d.equals(Duration.d1_4)) {
			clr=clr14;
			width=width1_4;
		}
		
		g.setColor(clr);
		int n = pitches.size();
		int yStart = -(n/2)*height;
		
		g.fillRect(0, yStart, width, height*n);
		
		g.setColor(Color.BLACK);
		int fontSize = (int)(0.8*height);
		Font font = new Font("Arial", Font.BOLD, fontSize);
		FontMetrics metrics = g.getFontMetrics(font);
		g.setFont(font);
		
		for (Pitch p : pitches) {
			String label = (symbol)?map.getSymbol(p):p.toString();
			g.drawString(label, width/2 - metrics.stringWidth(label)/2, yStart+((height - metrics.getHeight()) / 2) + metrics.getAscent());
			yStart += height;
		}
		return width;
	}

	@Override
	protected void showKeyPress(GraphicalPiano graphicalPiano) {
		for (Pitch p : pitches)
			graphicalPiano.pressed(p, true);
	}

	@Override
	protected void showKeyRelease(GraphicalPiano graphicalPiano) {
		for (Pitch p : pitches)
			graphicalPiano.released(p, true);
	}

	@Override
	protected void export(Formatter f) {
		f.export(this);
	}
	
	@Override
	public Iterator<Pitch> iterator() {
		return pitches.iterator();
	}
	
	public int size() {
		return pitches.size();
	}

	@Override
	protected boolean checkPlayed(LinkedList<Pitch> buffer) {
		
		for (Pitch p1 : pitches) {
			boolean found=false;
			for (Pitch p2 : buffer)
				if (p1.equals(p2)) { found=true; break; }
			if (!found) return false;
		}
		return true;
	}
}
