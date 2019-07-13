package piano;

public class Tripple {
	private Pitch p;
	private long timePressed;
	private long duration;
	
	public Tripple(Pitch p, long timePressed, long duration) {
		this.p = p;
		this.timePressed = timePressed;
		this.duration = duration;
	}
	
	public Pitch pitch() { return p; }
	public long timePressed() { return timePressed; }
	public long duration() { return duration; }
}
