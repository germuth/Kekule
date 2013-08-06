package gui;

import geneticAlgorithm.GeneticAlgorithmTask;
import gui.parameterWindow.GAWindow;
import gui.parameterWindow.MutationWindow;
import gui.parameterWindow.PopulationWindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


/**
 * MainFrame
 * 
 * This class represents the JFrame which contains the entire 
 * graphical user interface. Was made using an eclipse attachment.
 * 
 * @author Aaron
 *
 */
public class MainFrame extends JFrame{
	/**
	 * The main JPanel of which everything is added to
	 */
	private JPanel contentPane;
	/**
	 * The center piece of the grahpical user interface. It is the JPanel
	 * which displays the molecular structure
	 */
	private StructureDisplayer structureDisplayer; 
	/**
	 * The JTextField used to input the rank
	 */
	private JTextField rank;
	/**
	 * The JTextField used to input a classification
	 */
	private JTextField classification;
	/**
	 * The AddToLibrary button, currently not used
	 */
	private JButton addToLib;
	/**
	 * The Previous button, to look through all results from the GA
	 */
	private JButton previous;
	/**
	 * The Next button, to look through all results from the GA
	 */
	private JButton next;
	/**
	 * A checkbox which determines whether the textual representation
	 * of the cell will be displayed on the structure generator. 
	 */
	private JCheckBox displayCell;
	/**
	 * A button to run the genetic algorithm, using the current rank
	 * and classification from the JTextFields, and the parameters
	 * from the menus
	 */
	private JButton run;
	/**
	 * The loading bar for the genetic algorithm
	 */
	private LoadingBar loadBar;
	/**
	 * THe textField which shows the SMILES representation of each graph
	 * on the bottom of this frame
	 */
	private JTextField SMILES;
	/**
	 * The label which displays the current cell textually. Is turned on or off
	 * by the displayCell check box
	 */
	private JLabel cellLabel;
	/**
	 * Holds the current index of a the list of graphs from the genetic algorithm
	 * that we are currently on. Next button will increment, previous will decrement.
	 */
	private int index;
	/**
	 * The list of graphs in SMILES format, taken from the genetic algorithm.
	 */
	private ArrayList<String> graphs;
	/**
	 * The textual representation of the current cell
	 */
	private String cell;
	/**
	 * The parameter window for the genetic algorithm. Accessed from the menu.
	 */
	private GAWindow gaParams;
	/**
	 * The parameter window for the population, accessed from the menu.
	 */
	private PopulationWindow popParams;
	/**
	 * The Mutation window for the genetic algorthm, accessed from the menu.
	 */
	private MutationWindow mutaParams;

	/**
	 * The main method to
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * THe main constructor to display all elements within this frame
	 */
	public MainFrame() {
		setTitle("Interactive Kekule Theory");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 500);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		//the save button runs the save method, which saves a 
		//png image of the current molecular structure. 
		//the image is labeled "rank" + "classification"
		JMenuItem save = new JMenuItem("Save");
		mnFile.add(save);
		save.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.this.save( 
						MainFrame.this.rank.getText() + " " + 
						MainFrame.this.classification.getText() + ".png" );
			}
		});
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnViewLibarary = new JMenu("View Libarary");
		menuBar.add(mnViewLibarary);
		
		JMenu mnGeneticAlgorithm = new JMenu("Genetic Algorithm");
		this.gaParams = new GAWindow();
		gaParams.setVisible(false);
		
		
		menuBar.add(mnGeneticAlgorithm);
		//genetic algorithm parameter window
		JMenuItem mntmOpenParameterWindow = new JMenuItem("Open Parameter Window");
		mnGeneticAlgorithm.add(mntmOpenParameterWindow);
		mntmOpenParameterWindow.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.this.gaParams.setVisible(true);
			}
		});
		
		JMenu mnPopulation = new JMenu("Population");
		this.popParams = new PopulationWindow();
		this.popParams.setVisible(false);
		
		menuBar.add(mnPopulation);
		//population parameter window
		JMenuItem mntmOpenParameterWindow_1 = new JMenuItem("Open Parameter Window");
		mnPopulation.add(mntmOpenParameterWindow_1);
		mntmOpenParameterWindow_1.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.popParams.setVisible(true);
			}
		});
		
		JMenu mnMutation = new JMenu("Mutation");
		this.mutaParams = new MutationWindow();
		this.mutaParams.setVisible(false);
		
		menuBar.add(mnMutation);
		//mutation parameter window
		JMenuItem mntmOpenParameterWindow_2 = new JMenuItem("Open Parameter Window");
		mnMutation.add(mntmOpenParameterWindow_2);
		mntmOpenParameterWindow_2.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.mutaParams.setVisible(true);
			}	
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		//the left JPanel
		//contains rank, classification, addToLib, Previous
		//and loading bar
		JPanel leftBorder = new JPanel();
		contentPane.add(leftBorder, BorderLayout.WEST);
		leftBorder.setPreferredSize(new Dimension(100, 500));
		leftBorder.setLayout(null);
		
		JLabel lblRank = new JLabel("Rank:");
		lblRank.setBounds(5, 9, 38, 14);
		leftBorder.add(lblRank);
		
		this.rank = new JTextField();
		this.rank.setBounds(7, 34, 86, 20);
		leftBorder.add(this.rank);
		
		JLabel lblClassification = new JLabel("Classification:");
		lblClassification.setBounds(5, 65, 86, 14);
		leftBorder.add(lblClassification);
		
		this.classification = new JTextField();
		this.classification.setBounds(7, 90, 86, 20);
		leftBorder.add(this.classification);
		this.classification.setColumns(10);
		
		this.previous = new JButton("Previous");
		this.previous.setBounds(7, 212, 86, 64);
		this.previous.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				String graph = MainFrame.this.getPreviousGraph();
				
				MainFrame.this.structureDisplayer.setGraph(graph);
		        MainFrame.this.structureDisplayer.drawCurrentSMILES();
			}
		});
		leftBorder.add(this.previous);
		
		//COOL LOADING BAR GOES HERE
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(5, 299, 95, 94);
		panel_1.setLayout(null);
		leftBorder.add(panel_1);
		
		this.loadBar = new LoadingBar(100);
		this.loadBar.setBounds(0, 11, 97, 93);
		panel_1.add( this.loadBar );
		
		JLabel lblSmiles = new JLabel("SMILES:");
		lblSmiles.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSmiles.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSmiles.setBounds(5, 394, 86, 26);
		leftBorder.add(lblSmiles);
		
		this.addToLib = new JButton("Add to Lib.");
		this.addToLib.setFont(new Font("Tahoma", Font.PLAIN, 11));
		this.addToLib.setBounds(4, 146, 91, 51);
		leftBorder.add(this.addToLib);
		
		//the right JPanel of this frame
		//contains textbox, next button, check box, 
		//and small JLabels
		JPanel rightBorder = new JPanel();
		contentPane.add(rightBorder, BorderLayout.EAST);
		rightBorder.setPreferredSize(new Dimension(100,500));
		rightBorder.setLayout(null);
		
		this.next = new JButton("Next");
		this.next.setBounds(7, 211, 83, 67);
		this.next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				String graph = MainFrame.this.getNextGraph();
				
				MainFrame.this.structureDisplayer.setGraph(graph);
		        MainFrame.this.structureDisplayer.drawCurrentSMILES();
			}
		});
		
		rightBorder.add(this.next);
		
		JLabel lblAaronGermuth = new JLabel("Aaron Germuth &");
		lblAaronGermuth.setForeground(Color.LIGHT_GRAY);
		lblAaronGermuth.setBounds(0, 330, 100, 14);
		rightBorder.add(lblAaronGermuth);
		
		JLabel lblAlexAravind = new JLabel("Alex Aravind");
		lblAlexAravind.setForeground(Color.LIGHT_GRAY);
		lblAlexAravind.setBounds(0, 347, 73, 14);
		rightBorder.add(lblAlexAravind);
		
		this.run = new JButton("Run");
		this.run.setBounds(7, 391, 86, 33);
		this.run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.this.run();
			}
		});
		rightBorder.add(this.run);
		
		JTextArea txtWelcomeToInteractive = new JTextArea();
		txtWelcomeToInteractive.setBounds(0, 0, 100, 212);
		rightBorder.add(txtWelcomeToInteractive);
		txtWelcomeToInteractive.setEditable(false);
		txtWelcomeToInteractive.setFont(new Font("Monospaced", Font.PLAIN, 10));
		txtWelcomeToInteractive.setLineWrap(true);
		txtWelcomeToInteractive.setText("Welcome to Interactive Kekule Theory.  " +
				"Here we search for graphs which not only match Kekule Cells, but " +
				"real stable hydrocarbons as well. Begin by selecting a rank " +
				"and classification in the upper left.");
		txtWelcomeToInteractive.setColumns(10);
		
		
		JLabel label = new JLabel("2013");
		label.setForeground(Color.LIGHT_GRAY);
		label.setBounds(0, 366, 46, 14);
		rightBorder.add(label);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		//main window
		this.structureDisplayer = new StructureDisplayer("O=Cc1ccc(O)c(OC)c1");
		structureDisplayer.setBounds(-5, -64, 500, 451);
		
		this.SMILES = new JTextField();
		this.SMILES.setBounds(10, 390, 474, 34);
		panel.add(this.SMILES);
	
		this.cellLabel = new JLabel("");
		cellLabel.setForeground(Color.LIGHT_GRAY);
		cellLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.cellLabel.setVisible(false);
		this.cellLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		this.cellLabel.setBounds(20, 346, 451, 24);
		panel.add(this.cellLabel);
		panel.add(structureDisplayer);
		
		this.displayCell = new JCheckBox("Display Cell?");
		this.displayCell.setSelected(true);
		this.displayCell.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(MainFrame.this.displayCell.isSelected()){
					MainFrame.this.cellLabel.setVisible(true);
				} else{
					MainFrame.this.cellLabel.setVisible(false);
				}
			}
		});
		displayCell.setFont(new Font("Tahoma", Font.PLAIN, 12));
		this.displayCell.setBounds(0, 300, 99, 23);
		rightBorder.add(this.displayCell);
	}

	/**
	 * This method is triggered by the action listener on the run method. 
	 * It parses the current selected rank and classification, and feeds this
	 * to the genetic algorithm.
	 * 
	 * The Genetic Algorithm is run on a different thread to reduce lag on the graphical
	 * user interface, this thread is started by initiating a new GeneticAlgorithmTask
	 */
	public void run(){
		int rank, classification1;
		try{
			//get rank and classification
			rank = Integer.parseInt( this.rank.getText() );
			classification1 = Integer.parseInt( this.classification.getText() );
			
			this.loadBar.setStage(1);
			
			//create and start genetic algorithm on different thread
			GeneticAlgorithmTask task = new GeneticAlgorithmTask(rank, classification1, this );
	        task.addPropertyChangeListener(new PropertyChangeListener(){
	        	@Override
	        	public void propertyChange(PropertyChangeEvent evt) {
	        		if ("progress" == evt.getPropertyName()) {
	                    int progress = (Integer) evt.getNewValue();
	                    MainFrame.this.loadBar.updateProgress(progress);
	                    //taskOutput.append(String.format(
	                    //        "Completed %d%% of task.\n", task.getProgress()));
	                } else{
	                	System.out.println(evt.getNewValue());
	                }
	        	}
	        });
	        task.execute();
	        
		} catch(NumberFormatException e){
			System.err.println("The rank or classification you entered is invalid");
		}
	}
	
	/**
	 * Saves the current image of the content pane in png form. 
	 * The image is currently saved to a non-relative address, 
	 * so this method needs to be changed there TODO
	 * 
	 * The image name is saved as "rank" + " " + "classification"
	 * and saved in the "lib" folder
	 * @param imageFile
	 */
	public void save(String imageFile) {
        Rectangle r = getBounds();

        try {
            BufferedImage i = new BufferedImage(r.width, r.height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = i.getGraphics();
            paint(g);
            File f = new File("C:\\Users\\Aaron\\Documents\\GitHub\\Kekule\\Programming\\Kekule Java\\Kekule\\lib\\" 
            	 + imageFile );
            ImageIO.write(i, "png", f);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
	
	/**
	 * This method is called when the genetic algorithm has finished. 
	 * A list of graphs (in SMILES form) is passed, along with a 
	 * textual representation of the cell we were searching for.
	 * 
	 * This method gives structure displayer the first graph, and sets up 
	 * the next and previous buttons 
	 * 
	 * @param graphs, list of graphs in SMILES notation from GA
	 * @param cell, textual representation of cell we were searching for
	 */
	public void collectAndShowResults(ArrayList<String> graphs, String cell){
		this.index = 0;
		if( graphs.size() > 0){
			this.graphs = graphs;
			this.cell = cell;
			this.structureDisplayer.setGraph( this.graphs.get(0) );
			this.structureDisplayer.drawCurrentSMILES();
			this.SMILES.setText( this.graphs.get(0) );
			int count = 0;
			int length = cell.length();
			while( length > 60){
				count++;
				length -= 10;
			}
			this.cellLabel.setText(cell);
			this.cellLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, (14 - count)));
		} else{
			System.out.println("none of them graphs found yo");
		}
	}
	
	/**
	 * Called by action listener on next button. 
	 * Increases the index in order to prepare to display
	 * the next graph
	 * @return the next graph
	 */
	public String getNextGraph() {
		if (index >= this.graphs.size() - 1) {
			index = 0;
		} else {
			index++;
		}
		this.SMILES.setText( this.graphs.get(index) );
		return graphs.get(index);
	}

	/**
	 * Called by action listener on previous button.
	 * Decrements the index in order to prepare to display
	 * the previous graph
	 * @return the previous graph
	 */
	public String getPreviousGraph() {
		if( index <= 0){
			index = this.graphs.size() - 1;
		} else{
			index--;
		}
		this.SMILES.setText( this.graphs.get(index) );
		return graphs.get(index);
	}
}
