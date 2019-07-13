package piano;

import java.io.File;

public abstract class Formatter {
	
	public void export(File file, Composition composition) {
		prepareFile(file);
		
		for (MusicSymbol ms : composition)
			exportMusicSymbol(ms);
		
		finallizeFile(file);
	}
	
	protected abstract void exportMusicSymbol(MusicSymbol ms);
	protected abstract void prepareFile(File file);
	protected abstract void finallizeFile(File file);
	
	protected abstract void export(Note n);
	protected abstract void export(Pause p);
}
