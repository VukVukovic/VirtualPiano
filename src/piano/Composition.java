package piano;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Composition implements Iterable<MusicSymbol> {
	private ArrayList<MusicSymbol> symbols = new ArrayList<MusicSymbol>();
	private Mapping map;
	
	public Composition(Mapping map) {
		this.map = map;
	}
	
	public Mapping getMapping() {
		return map;
	}
	
	public int size() { return symbols.size(); }
	public MusicSymbol get(int i) { if (i>=0 && i<size()) return symbols.get(i); return null;}
	public void add(MusicSymbol ms) { symbols.add(ms); }
	public void clear() { symbols.clear(); }
	
	public void parseComposition(File file) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			Stream<String> stream = bufferedReader.lines();
			StringBuilder sb = new StringBuilder();
			stream.forEach(line->sb.append(line));
			
			String compositionString = sb.toString();
			
			Pattern regex = Pattern.compile("\\[[^\\[\\]]+\\]|[^\\[\\]]");
			Pattern regexInside = Pattern.compile("[^ \\[\\]]");
			
			Matcher matcher = regex.matcher(compositionString);
			while (matcher.find()) {
				String currentMatch = matcher.group();
				if (currentMatch.length()>1) {
					boolean chord = !currentMatch.contains(" ");
					Note chordNote = new Note(Duration.d1_4);
					
					Matcher matcherInside = regexInside.matcher(currentMatch);
					while (matcherInside.find()) {
						String currentMatchInside = matcherInside.group();
						if (chord) 
							chordNote.addPitch(map.getPitch(currentMatchInside));
						else
							symbols.add(new Note(Duration.d1_8).addPitch(map.getPitch(currentMatchInside)));
					}
					
					if (chord) symbols.add(chordNote);
				} else {
					MusicSymbol ms;
					if (currentMatch.equals(" ")) ms = new Pause(Duration.d1_8);
					else if (currentMatch.equals("|")) ms = new Pause(Duration.d1_4);
					else ms = new Note(Duration.d1_4).addPitch(map.getPitch(currentMatch));
					symbols.add(ms);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Composition file not found!");
		}
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (MusicSymbol ms : symbols)
			sb.append(ms).append(" ");
		
		return sb.toString();
	}

	@Override
	public Iterator<MusicSymbol> iterator() {
		return symbols.iterator();
	}
}
