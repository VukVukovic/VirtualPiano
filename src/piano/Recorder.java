package piano;

import java.util.ArrayList;
import java.util.Comparator;

public class Recorder implements PianoEventListener {	
	private boolean recording = false;
	private ArrayList<Tripple> buffer = new ArrayList<Tripple>();
	private Composition composition;
	private Mapping map;
	
	public Recorder(Mapping map) {
		this.map = map;
	}
	
	public void startRecording() {
		recording = true;
	}
	
	public boolean isRecording() {
		return recording;
	}
	
	public Composition stopRecording() {
		recording = false;
		
		buildComposition();
		
		buffer.clear();
		Composition ret = composition;
		composition = null;
		return ret;
	}

	private void buildComposition() {
		composition = new Composition(map);
		
		buffer.sort(new Comparator<Tripple>() {
			@Override
			public int compare(Tripple o1, Tripple o2) {
				return (int)(o1.timePressed()-o2.timePressed());
		}});
		
		int i = 0;
		
		long time = 0;
		Note note=null;
		while (i < buffer.size()) {
			Tripple t = buffer.get(i);
			Pitch p = t.pitch();
			long timePressed = t.timePressed();
			long durationPressed = t.duration();
			
			boolean shorter = Math.abs(durationPressed - Piano.BEAT_18) < Math.abs(durationPressed - Piano.BEAT_14);
						
			// Add pauses
			while (time < timePressed) {
				if(time==0) {time=timePressed; break;}
				
				if (time + (shorter?Piano.BEAT_14:Piano.BEAT_18) < timePressed && note!=null) {
					if (note.size()>1 && note.duration().equals(Duration.d1_8)) 
						note.setDuration(Duration.d1_4);
					composition.add(note);
					time+=note.duration().equals(Duration.d1_4)?Piano.BEAT_14:Piano.BEAT_18;
					note=null;
				}
				
				if (time + Piano.BEAT_14 <= timePressed) {
					composition.add(new Pause(Duration.d1_4));
					time+=Piano.BEAT_14;
				}
				else if(time + Piano.BEAT_18 <= timePressed) {
					composition.add(new Pause(Duration.d1_8));
					time+=Piano.BEAT_18;
				} else break;
			}
			
			if (note == null) note = new Note(shorter?Duration.d1_8:Duration.d1_4);
			note.addPitch(p);
			
			i++;
		}
		
		if (note != null) {
			if (note.size()>1 && note.duration().equals(Duration.d1_8)) 
				note.setDuration(Duration.d1_4);
			composition.add(note);
		}
	}

	@Override
	public void onButtonPressed(Pitch p, boolean showing) {
	}

	@Override
	public void onButtonReleased(Pitch p, boolean showing, long clickedTime, long duration) {
		if (!showing)
			buffer.add(new Tripple(p, clickedTime, duration));
	}
	
}
