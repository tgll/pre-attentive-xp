package fr.m1m2.advancedEval;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.lri.swingstates.canvas.CRectangle;
import fr.lri.swingstates.canvas.Canvas;

enum Status {
	INSTRUCTION, ACTIVE, CONFIRMATION, END
}

public class Experiment {

	public static Font FONT = new Font("Helvetica", Font.PLAIN, 30);

	protected String participant = "";
	protected int block = -1;
	protected int trial = -1;

	protected File designFile = null;

	protected ArrayList<Trial> allTrials = new ArrayList<Trial>();
	protected int currentTrial = 0;

	protected Canvas canvas;

	JFrame jFrame;

	Status status;

	long startTime, endTime;
	long perceptionTime;

	boolean wasSuccess;

	public Experiment() {
	}

	public void start() {
		// 1. parse the experiment design file for feeding allTrials with the list of
		// trials that should be run for that participant
		loadTrials();

		// 2. init the log file
		initLog();

		// 3. init the graphical scene
		initScene();

		// 4. start the first trial
		nextTrial();
	}

	public void initScene() {
		jFrame = new JFrame("Experiment -- preattention");
		canvas = new Canvas(1400, 1200);
		jFrame.getContentPane().add(canvas);
		jFrame.pack();
		jFrame.setVisible(true);
	}

	public void nextTrial() {

		showInstruction();

		jFrame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (status == Status.INSTRUCTION) {
						activate();
					}
				}

				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (status == Status.ACTIVE) {
						confirm();
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

		jFrame.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {

				if (status == Status.CONFIRMATION) {
					CRectangle target = allTrials.get(currentTrial).getTarget();

					if (target.getMinX() < e.getX() && e.getX() < target.getMaxX() && e.getY() > target.getMinY()
							&& e.getY() < target.getMaxY()) {

						wasSuccess = true;
						writeToFile();

						if (currentTrial == allTrials.size() - 1) {
							allTrials.get(currentTrial).removePlaceholders();
							allTrials.get(currentTrial).displayEnd();
							status = Status.END;
						} else {
							currentTrial++;
							nextTrial();
						}

					} else {
						wasSuccess = false;
						allTrials.get(currentTrial).errors++;
						nextTrial();
					}

				}

			}
		});

	}

	public void showInstruction() {
		if (wasSuccess) {
			if (currentTrial > 0) {
				allTrials.get(currentTrial - 1).removePlaceholders();
			}
		} else {
			allTrials.get(currentTrial).removePlaceholders();
		}
		allTrials.get(currentTrial).displayInstructions();
		status = Status.INSTRUCTION;
	}

	public void activate() {
		allTrials.get(currentTrial).removeInstructions();
		allTrials.get(currentTrial).addShapes();

		status = Status.ACTIVE;
		startTime = System.currentTimeMillis();
	}

	public void confirm() {

		endTime = System.currentTimeMillis();
		perceptionTime = endTime - startTime;

		allTrials.get(currentTrial).removeShapes();

		status = Status.CONFIRMATION;

		allTrials.get(currentTrial).addPlaceholdres();
	}

	public void loadTrials() {

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("experimentDesign.csv")));
			String line = br.readLine();
			String[] parts = line.split(",");
			line = br.readLine();
			while (line != null) {
				parts = line.split(",");
				// check that these are the right data for the current participant
				if (parts[0].equals(participant)) {
					boolean practice = parts[1].equals("TRUE");
					int b = Integer.parseInt(parts[2]); // block
					int t = Integer.parseInt(parts[3]); // trial
					String objectCount = parts[4];
					String vv = parts[5];
					Trial tl = new Trial(this, practice, b, t, vv, objectCount);
					allTrials.add(tl);
				}
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("General exception");
			e.printStackTrace();
		}

	}

	public void initLog() {
		File log = new File("experimentResults.csv");

		try {
			PrintWriter printWriter = new PrintWriter(log);
			String header = "Time,ParticipantID,Practice,Block,Trial,VisualVariable,ObjectCount,PerceptionTime,Errors"
					+ "\n";
			printWriter.print(header);
			printWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void writeToFile() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = simpleDateFormat.format(cal.getTime());

		try (FileWriter fw = new FileWriter("experimentResults.csv", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw);) {

			Trial trial = allTrials.get(currentTrial);

			String dataPoint = date + "," + getParticipant() + "," + trial.practice + "," + getBlock() + ","
					+ trial.trial + "," + trial.visualVariable + "," + trial.objectCount + "," + perceptionTime + ","
					+ trial.errors + "\n";

			out.print(dataPoint);
			out.flush();

		} catch (IOException e) {
			System.out.println("Could not open log file.");
		}
	}

	/*******************************/
	/****** GETTERS AND SETTERS ******/
	/*******************************/

	public Canvas getCanvas() {
		return canvas;
	}

	public String getParticipant() {
		return participant;
	}

	public void setParticipant(String participant) {
		this.participant = participant;
	}

	public int getBlock() {
		return block;
	}

	public void setBlock(int block) {
		this.block = block;
	}

	public int getTrial() {
		return trial;
	}

	public void setTrial(int trial) {
		this.trial = trial;
	}

	public File getDesignFile() {
		return designFile;
	}

	public void setDesignFile(File designFile) {
		this.designFile = designFile;
	}

	/*********************************************/
	/****** METHODS TO START AT A GIVEN POINT ******/
	/*********************************************/

	/**
	 * @param participantsHeader
	 *            the name of the column where the participant ID can be found
	 * @return the list of participants found in the experiment file
	 */
	public ArrayList<String> participantsList(String participantsHeader) {
		ArrayList<String> participants = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(designFile));
			String line = br.readLine();
			String[] parts = line.split(",");
			int participantsIndex = 0;
			for (int i = 0; i < parts.length; i++) {
				if (parts[i].compareTo(participantsHeader) == 0) {
					participantsIndex = i;
				}
			}
			line = br.readLine();
			String currentParticipant = "";
			while (line != null) {
				parts = line.split(",");
				String p = parts[participantsIndex];
				if (p.compareTo(currentParticipant) != 0) {
					currentParticipant = p;
					participants.add(p);
				}
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return participants;
	}

	/**
	 * @param blockHeader
	 *            the name of the column where the block number can be found
	 * @param trialHeader
	 *            the name of the column where the trial number ID can be found
	 * @return an array of size 2 containing the number of blocks in its first cell
	 *         and the maximum number of trials per block in its second cell
	 */
	public int[] trialCounter(String blockHeader, String trialHeader) {
		int[] res = new int[2];
		res[0] = -1;
		res[1] = -1;
		try {
			BufferedReader br = new BufferedReader(new FileReader(designFile));
			String line = br.readLine();
			String[] parts = line.split(",");
			int blockIndex = 0;
			int trialIndex = 0;
			for (int i = 0; i < parts.length; i++) {
				if (parts[i].compareTo(blockHeader) == 0) {
					blockIndex = i;
				} else if (parts[i].compareTo(trialHeader) == 0) {
					trialIndex = i;
				}
			}
			line = br.readLine();
			while (line != null) {
				parts = line.split(",");
				int b = Integer.parseInt(parts[blockIndex]);
				int t = Integer.parseInt(parts[trialIndex]);
				res[0] = Math.max(res[0], b);
				res[1] = Math.max(res[1], t);
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	/*******************************/
	/************* MAIN **************/
	/*******************************/

	public static void main(String[] args) {
		final Experiment experiment = new Experiment();

		final JFrame starterFrame = new JFrame("Experiment starter");
		starterFrame.getContentPane().setLayout(new GridLayout(4, 2));

		File experimentFile = new File("experimentDesign.csv");
		experiment.setDesignFile(experimentFile);

		ArrayList<String> participantsList = experiment.participantsList("Participant");
		String[] participantsArray = new String[participantsList.size()];
		int i = 0;
		for (Iterator<String> iterator = participantsList.iterator(); iterator.hasNext();) {
			String s = iterator.next();
			participantsArray[i] = s;
			i++;
		}
		JComboBox<String> comboParticipants = new JComboBox<String>(participantsArray);
		starterFrame.getContentPane().add(new JLabel("Participant ID:"));
		starterFrame.getContentPane().add(comboParticipants);
		comboParticipants.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String p = (String) cb.getSelectedItem();
				experiment.setParticipant(p);
			}
		});
		experiment.setParticipant(participantsArray[0]);
		experiment.setBlock(0);
		experiment.setTrial(0);

		int[] trialCount = experiment.trialCounter("Block", "Trial");
		System.out.println("trial count " + trialCount[0] + " block, containing " + trialCount[1] + " trials each");
		starterFrame.getContentPane().add(new JLabel("Block:"));
		JSpinner spinnerBlock = new JSpinner();
		spinnerBlock.setModel(new SpinnerNumberModel(1, 1, trialCount[0], 1));
		starterFrame.getContentPane().add(spinnerBlock);
		spinnerBlock.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer b = (Integer) (((JSpinner) e.getSource()).getModel().getValue());
				experiment.setBlock(b);
			}
		});

		starterFrame.getContentPane().add(new JLabel("Trial:"));
		JSpinner spinnerTrial = new JSpinner();
		spinnerTrial.setModel(new SpinnerNumberModel(1, 1, trialCount[1], 1));
		starterFrame.getContentPane().add(spinnerTrial);
		spinnerTrial.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Integer t = (Integer) (((JSpinner) e.getSource()).getModel().getValue());
				experiment.setTrial(t);
			}
		});

		starterFrame.getContentPane().add(new JLabel(""));
		JButton goButton = new JButton("OK");
		starterFrame.getContentPane().add(goButton);
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				experiment.start();
				starterFrame.setVisible(false);
			}
		});

		starterFrame.pack();
		starterFrame.setVisible(true);
		starterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
