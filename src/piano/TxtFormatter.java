package piano;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TxtFormatter extends Formatter {
	private boolean sequence1_8 = false;
	private StringBuilder sb = new StringBuilder();
	private boolean error;
	private Mapping map;
	
	public TxtFormatter(Mapping map) {
		this.map = map;
	}
	
	@Override
	protected void exportMusicSymbol(MusicSymbol ms) {
		ms.export(this);
	}

	@Override
	protected void prepareFile(File file) {
		sequence1_8 = false;
		error = false;
		sb = new StringBuilder();
	}

	@Override
	protected void finallizeFile(File file) {
		if (sequence1_8) sb.setCharAt(sb.length()-1, ']');
		try {
			PrintWriter out = new PrintWriter(file.getPath());
			out.println(sb.toString());
			out.close();
		} catch (FileNotFoundException e) {
			error = true;
		}

	}

	@Override
	protected void export(Note n) {
		if (n.duration().equals(Duration.d1_4)) {
			if (sequence1_8) sb.setCharAt(sb.length()-1, ']');
			sequence1_8 = false;
			
			boolean chord = n.size()>1;
			if (chord) sb.append('[');
			for (Pitch p : n)
				sb.append(map.getSymbol(p));
			if (chord) sb.append(']');
		} else {
			if (!sequence1_8) {
				sequence1_8 = true;
				sb.append('[');
			}
			
			// only one always
			for (Pitch p : n) {
				sb.append(map.getSymbol(p));
				sb.append(' ');
			}
		}
	}

	@Override
	protected void export(Pause p) {
		if (sequence1_8) sb.setCharAt(sb.length()-1, ']');
		sequence1_8 = false;
		
		if (p.duration().equals(Duration.d1_4)) sb.append('|');
		else sb.append(' ');
	}

}
