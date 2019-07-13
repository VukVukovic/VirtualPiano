package piano;

public class Pitch {
	private Step step;
	private boolean sharp;
	private int octave;
	
	public Pitch(Step step, boolean sharp, int octave) {
		this.step = step;
		this.sharp = sharp;
		this.octave = octave;
	}
	
	public Step step() { return step; }
	public boolean sharp() { return sharp; }
	public int octave() { return octave; }
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Pitch) {
			Pitch p = (Pitch)o;
			return p.step==this.step && p.sharp==this.sharp && p.octave==this.octave;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return octave*2*GraphicalPiano.OCTAVE_NUM + step.value() + (sharp?GraphicalPiano.OCTAVE_NUM:0);
    }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(step);
		if (sharp) sb.append('#');
		sb.append(octave);
		return sb.toString();
	}
}
