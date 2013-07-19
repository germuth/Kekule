package gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import shared.Cell;
import shared.InputParser;
 
public class ProgressBarDemo extends JPanel
                             implements ActionListener, 
                                        PropertyChangeListener {
 
    private JProgressBar progressBar;
    private JButton startButton;
    //private JTextArea taskOutput;
    private Task task;
 
    public class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
        	readClassification();
        	setProgress(0);
        	return null;
        }
        
        public void setProgress2(int n){
        	setProgress(n);
        }
        
        public void startIt(Cell c){
        	//geneticAlgorithm.GeneticAlgorithm.task = this;
        	//geneticAlgorithm.GeneticAlgorithm.main(c);
        }
        
        public void readClassification() {
    		// reading classification
    		File f = new File("FullClassificationRank5.txt");
    		Scanner s = null;
    		try {
    			s = new Scanner(f);
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		int rank = 6;
    		s.nextLine();
    		s.nextLine();
    		s.nextLine();

    		final ArrayList<Cell> classifications = new ArrayList<Cell>();

    		Cell input = null;
    		try {
    			input = InputParser.readCell2(s, rank);
    		} catch (Exception e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}

    		while (input != null) {
    			classifications.add(input);

    			try {
    				input = InputParser.readCell2(s, rank);
    			} catch (Exception e) {
    				input = null;
    			}
    		}
    		
    		for(int i = 0; i < classifications.size(); i++){
    			final JButton j = new JButton("K" + (i+1));
    			ProgressBarDemo.this.add(j);
    			
    			j.addActionListener( new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						String name = j.getText();
						name = name.substring(1);
						int number = Integer.parseInt(name);
						Task.this.startIt( classifications.get(number - 1) );
					}
    				
    			});
    		}
    		ProgressBarDemo.this.repaint();
    	}
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            //taskOutput.append("Done!\n");
        }
    }
 
    public ProgressBarDemo() {
        super(new BorderLayout());
 
        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
 
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
 
        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);
 
        add(panel, BorderLayout.PAGE_START);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
 
    }
 
    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }
 
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        } 
    }
 
 
    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ProgressBarDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new ProgressBarDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}