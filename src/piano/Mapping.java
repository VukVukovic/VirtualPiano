package piano;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Mapping {
	Map<String,Pitch> pitchMap=new HashMap<String,Pitch>();
	Map<Pitch,String> symbolMap = new HashMap<Pitch,String>();
	Map<Pitch,Integer> midiMap = new HashMap<Pitch,Integer>();
	
	
	public Mapping(File file) throws FileNotFoundException {
		load(file);
	}
	
	private void load(File file) throws FileNotFoundException {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			Stream<String> stream = bufferedReader.lines();
			
			Pattern regex = Pattern.compile("([^,]+),([A-G])(#?)([2-6]+),([0-9]+)");
			stream.forEach(s->{
				Matcher m = regex.matcher(s);
				if(m.matches()) {
					String symbol = m.group(1);
					Step step = Step.chrToStep(m.group(2).charAt(0));
					boolean sharp = m.group(3).equals("#");
					int octave = Integer.parseInt(m.group(4));
					int midi = Integer.parseInt(m.group(5));
					
					Pitch pitch = new Pitch(step, sharp, octave);
					pitchMap.put(symbol, pitch);
					midiMap.put(pitch, midi);
					symbolMap.put(pitch, symbol);
				}
			});
	}
	
	public Pitch getPitch(String symbol) {
		return pitchMap.getOrDefault(symbol, null);
	}
	
	public int getMidi(Pitch p) {
		return midiMap.getOrDefault(p, 0);
	}
	
	public String getSymbol(Pitch p) {
		return symbolMap.getOrDefault(p, "");
	}
}
