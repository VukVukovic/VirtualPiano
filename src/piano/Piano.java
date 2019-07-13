package piano;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.sound.midi.MidiUnavailableException;

public class Piano extends Frame {
	public static final int BEAT_18 = 150, BEAT_14 = 300;
	
	private Mapping map;
	private GraphicalPiano graphicalPiano;
	private Notebook notebook;
	private Player player;
	private Composition composition;
	private Recorder recorder;
	
	private Checkbox rbSymbols, rbNotes, cbShowLabels;
	private boolean saved=true;
	
	private Button btnLoad, btnPlay, btnReset, btnRecord, btnSaveMidi, btnSaveTxt;
	Panel notebookOptions, buttons, options;
	
	public Piano() {
		super("Piano");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { exit(); }
		});
		
		try {
			map = new Mapping(new File("map.csv"));
		} catch (FileNotFoundException e1) {
			System.out.println("Mapping file was not found!");
			exit();
		}
		
		notebook = new Notebook(map);
		graphicalPiano = new GraphicalPiano(map);
		recorder = new Recorder(map);
		initPlayer(notebook, graphicalPiano);
		
		player.addPlayerEventListener(new PlayerEventListener() {
			@Override
			public void playingStarted() {
				notebook.pauseChecking();
			}
			@Override
			public void playingFinished() {
				notebook.continueChecking();
			}
		});
		
		player.addPlayerEventListener(new PlayerEventListener() {

			@Override
			public void playingStarted() {
				btnPlay.setLabel("Pause");
			}

			@Override
			public void playingFinished() {
				btnPlay.setLabel("Play");
				if (!player.hasComposition())
					notebook.clear();
			}
			
		});
		
		initFrame();
		initComponents();
		initKeyListener();
		
		graphicalPiano.addPianoEventListener(new PianoEventListener() {
			@Override
			public void onButtonPressed(Pitch p, boolean showing) {
				player.press(map.getMidi(p));
			}

			@Override
			public void onButtonReleased(Pitch p, boolean showing, long clickedTime, long duration) {
				player.release(map.getMidi(p));
			}
		});
		graphicalPiano.addPianoEventListener(notebook);
		graphicalPiano.addPianoEventListener(recorder);
		
		setVisible(true);
	}
	
	private void initComponents() {
		options = new Panel();
		options.setLayout(new FlowLayout());
		CheckboxGroup cbg = new CheckboxGroup();
		rbNotes = new Checkbox("Notes", cbg, true);
		rbSymbols = new Checkbox("Text", cbg, false);
		options.add(rbNotes); options.add(rbSymbols);
		
		ItemListener displayOption = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				notebook.setMode(rbSymbols.getState());
			}
		};
		rbNotes.addItemListener(displayOption); rbSymbols.addItemListener(displayOption);
		
		cbShowLabels = new Checkbox("Show labels", true);
		cbShowLabels.addItemListener(new ItemListener() {public void itemStateChanged(ItemEvent e) {
				graphicalPiano.setLabels(cbShowLabels.getState());
		}});
		options.add(cbShowLabels);
		
		buttons = new Panel();
		buttons.setLayout(new GridLayout(0,1,0,5));
		
		btnLoad = new Button("Load composition");
		btnLoad.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { loadFile(); } });
		btnPlay = new Button("Play");
		btnPlay.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { playButtonClicked(); } });
		btnReset = new Button("Reset");
		btnReset.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { resetButtonClicked(); }});
		btnRecord = new Button("Record");
		btnRecord.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { recordButtonClicked(); }});
		
		btnSaveMidi = new Button("Save Midi");
		btnSaveMidi.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { saveMidi(); }});
		
		btnSaveTxt = new Button("Save Txt");
		btnSaveTxt.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { saveTxt(); }});
		
		buttons.add(btnLoad);
		buttons.add(btnPlay);
		buttons.add(btnReset);
		
		buttons.add(new Label());
		
		buttons.add(btnRecord);
		
		Panel savePanel = new Panel();
		savePanel.add(btnSaveTxt); savePanel.add(btnSaveMidi);
		buttons.add(savePanel);
		
		notebookOptions = new Panel();
		notebookOptions.setLayout(new BorderLayout());
		notebookOptions.add(notebook, BorderLayout.CENTER);
		notebookOptions.add(options, BorderLayout.NORTH);
		notebookOptions.add(buttons, BorderLayout.EAST);
		
		setLayout(new GridLayout(2,1));
		add(notebookOptions);
		add(graphicalPiano);
	}

	private void initKeyListener() {
		KeyAdapter key = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Pitch p = map.getPitch(e.getKeyChar()+"");
				if (p != null)
					graphicalPiano.pressed(p, false);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				Pitch p = map.getPitch(e.getKeyChar()+"");
				if (p != null) {
					graphicalPiano.released(p, false);
				}
		}};
		addKeyListener(key);
		notebookOptions.addKeyListener(key); options.addKeyListener(key);
		
		btnLoad.addKeyListener(key); btnPlay.addKeyListener(key); btnReset.addKeyListener(key);
		btnRecord.addKeyListener(key); btnSaveTxt.addKeyListener(key); btnSaveMidi.addKeyListener(key);
		
		notebook.addKeyListener(key); graphicalPiano.addKeyListener(key);
		rbSymbols.addKeyListener(key); rbNotes.addKeyListener(key); cbShowLabels.addKeyListener(key);
	}

	private void initFrame() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int)(0.9*screenSize.getWidth());
		int height = (int)(0.6*screenSize.getHeight());
		setSize(width, height);
		setLocationRelativeTo(null);
	}

	private void initPlayer(Notebook notebook, GraphicalPiano graphicalPiano) {
		try {
			player = new Player(notebook, graphicalPiano);
		} catch (MidiUnavailableException e) {
		}
	}
	
	private void loadFile() {
		FileDialog fd = new FileDialog(this, "Choose a composition file", FileDialog.LOAD);
		fd.setDirectory(System.getProperty("user.dir"));
		fd.setFile("*.txt");
		fd.setVisible(true);
		String dir = fd.getDirectory();
		String filename = fd.getFile();
		if (filename != null) {
			composition = new Composition(map);
			composition.parseComposition(new File(dir + filename));
			notebook.setComposition(composition, 0);
			player.reset();
		}
	}
	
	private void saveTxt() {
		if (composition!=null) {
			FileDialog fd = new FileDialog(this, "Choose save file", FileDialog.SAVE);
			fd.setDirectory(System.getProperty("user.dir"));
			fd.setFile("*.txt");
			fd.setVisible(true);
			String dir = fd.getDirectory();
			String filename = fd.getFile();
			if (filename != null) {
				TxtFormatter txtF = new TxtFormatter(map);
				txtF.export(new File(dir+filename), composition);
				saved = true;
			}
		}
	}
	
	private void saveMidi() {
		if (composition!=null) {
			FileDialog fd = new FileDialog(this, "Choose save file", FileDialog.SAVE);
			fd.setDirectory(System.getProperty("user.dir"));
			fd.setFile("*.midi");
			fd.setVisible(true);
			String dir = fd.getDirectory();
			String filename = fd.getFile();
			if (filename != null) {
				MidiFormatter txtF = new MidiFormatter(map);
				txtF.export(new File(dir+filename), composition);
				saved = true;
			}
		}
	}
	
	private void playButtonClicked() {
		if (composition==null) return;
		
		if (player.hasComposition()) {
			if (player.playing())
				player.pausePlaying();
			else
				player.continuePlaying();
		} else {
			player.setComposition(composition);
			player.play();
		}
			
	}
	
	private void resetButtonClicked() {
		notebook.setComposition(composition, 0);
		player.setComposition(composition);
		btnPlay.setLabel("Play");
	}
	
	private void recordButtonClicked() {
		if (recorder.isRecording()) {
			composition = recorder.stopRecording();
			notebook.setComposition(composition, 0);
		}
		else {
			player.reset();
			recorder.startRecording();
		}
		
		btnRecord.setLabel((recorder.isRecording())?"Stop recording":"Record");
	}
	
	private void checkSaved() {
		if (!saved) {
			
		}
	}
	
	private void exit() {
		checkSaved();
		if (player!=null)
			player.interrupt();
		if (notebook!=null)
			notebook.interrupt();
		dispose();
	}

	public static void main(String args[]) {
		new Piano();
	}
}
