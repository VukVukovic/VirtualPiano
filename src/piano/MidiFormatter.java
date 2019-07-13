package piano;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

public class MidiFormatter extends Formatter {
	private Mapping map;
	private Sequence seq;
	private Track t;
	private long actionTime;
	
	public MidiFormatter(Mapping map) {
		this.map = map;
	}
	
	@Override
	protected void exportMusicSymbol(MusicSymbol ms) {
		ms.export(this);
	}

	@Override
	protected void prepareFile(File file) {
		try {
			actionTime = 0;
			seq = new Sequence(javax.sound.midi.Sequence.PPQ,24);
			t = seq.createTrack();

			byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
			SysexMessage sm = new SysexMessage();
			sm.setMessage(b, 6);
			MidiEvent me = new MidiEvent(sm,actionTime);
			t.add(me);

			MetaMessage mt = new MetaMessage();
	        byte[] bt = {0x02, (byte)0x00, 0x00};
			mt.setMessage(0x51 ,bt, 3);
			me = new MidiEvent(mt,actionTime);
			t.add(me);

			mt = new MetaMessage();
			String TrackName = new String("Midifile export");
			mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
			me = new MidiEvent(mt,actionTime);
			t.add(me);

			ShortMessage mm = new ShortMessage();
			mm.setMessage(0xB0, 0x7D,0x00);
			me = new MidiEvent(mm,actionTime);
			t.add(me);

			mm = new ShortMessage();
			mm.setMessage(0xB0, 0x7F,0x00);
			me = new MidiEvent(mm,actionTime);
			t.add(me);

			mm = new ShortMessage();
			mm.setMessage(0xC0, 0x00, 0x00);
			me = new MidiEvent(mm,actionTime);
			t.add(me);

			actionTime++;
		} catch (InvalidMidiDataException e) {}
	}

	@Override
	protected void finallizeFile(File file) {
		try {
			actionTime+=19;
			MetaMessage mt = new MetaMessage();
	        byte[] bet = {}; // empty array
			mt.setMessage(0x2F,bet,0);
			MidiEvent me = new MidiEvent(mt, actionTime);
			t.add(me);

			MidiSystem.write(seq,1,file);
		} 
		catch (InvalidMidiDataException e) {}
		catch (IOException e) {}
	}

	@Override
	protected void export(Note n) {
		try {
			for (Pitch p : n) {
				ShortMessage mm = new ShortMessage();
				mm.setMessage(0x90,map.getMidi(p),0x60);
				MidiEvent me = new MidiEvent(mm,actionTime);
				t.add(me);
			}
			
			actionTime+=20;
			if (n.duration().equals(Duration.d1_4)) actionTime+=20;
			
			for (Pitch p : n) {
				ShortMessage mm = new ShortMessage();
				mm.setMessage(0x80,map.getMidi(p),0x40);
				MidiEvent me = new MidiEvent(mm,actionTime);
				t.add(me);
			}
		} catch (InvalidMidiDataException e) {}
	}

	@Override
	protected void export(Pause p) {
		actionTime+=20;
		if (p.duration().equals(Duration.d1_4)) actionTime+=20;
	}

}
