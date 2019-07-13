package piano;

public interface PianoEventListener {
	void onButtonPressed(Pitch p, boolean showing);
	void onButtonReleased(Pitch p, boolean showing, long clickedTime, long duration);
}
