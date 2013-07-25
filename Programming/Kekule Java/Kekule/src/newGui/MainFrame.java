package newGui;

import geneticAlgorithm.GeneticAlgorithmTask;
import gui.ProgressBarDemo.Task;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {
	private JPanel contentPane;
	private StructureDisplayer structureDisplayer; 
	
	private JTextField rank;
	private JTextField classification;
	
	private JButton addToLib;
	private JButton previous;
	
	//private LoadingBar loadingBar;
	
	private JButton next;
	private JCheckBox displayCell;
	private JButton run;
	
	private JTextField SMILES;
	
	private int index;
	private ArrayList<String> graphs;

	/**
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
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("Interactive Kekule Theory");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 500);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnViewLibarary = new JMenu("View Libarary");
		menuBar.add(mnViewLibarary);
		
		JMenu mnGeneticAlgorithm = new JMenu("Genetic Algorithm");
		menuBar.add(mnGeneticAlgorithm);
		
		JMenu mnPopulation = new JMenu("Population");
		menuBar.add(mnPopulation);
		
		JMenu mnMutation = new JMenu("Mutation");
		menuBar.add(mnMutation);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
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
		panel_1.setBounds(7, 299, 86, 94);
		leftBorder.add(panel_1);
		JLabel lblCoolMiniLoading = new JLabel("cool mini loading bar");
		panel_1.add(lblCoolMiniLoading);
		
		JLabel lblSmiles = new JLabel("SMILES:");
		lblSmiles.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSmiles.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSmiles.setBounds(5, 394, 86, 26);
		leftBorder.add(lblSmiles);
		
		this.addToLib = new JButton("Add to Lib.");
		this.addToLib.setFont(new Font("Tahoma", Font.PLAIN, 11));
		this.addToLib.setBounds(4, 146, 91, 51);
		leftBorder.add(this.addToLib);
		
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
		
		this.displayCell = new JCheckBox("Display Cell?");
		displayCell.setFont(new Font("Tahoma", Font.PLAIN, 12));
		this.displayCell.setBounds(0, 300, 99, 23);
		rightBorder.add(this.displayCell);
		
		JLabel label = new JLabel("2013");
		label.setForeground(Color.LIGHT_GRAY);
		label.setBounds(0, 366, 46, 14);
		rightBorder.add(label);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		this.structureDisplayer = new StructureDisplayer("O=Cc1ccc(O)c(OC)c1");
		structureDisplayer.setBounds(-5, -64, 500, 451);
		panel.add(structureDisplayer);
		
		this.SMILES = new JTextField();
		this.SMILES.setBounds(10, 390, 474, 34);
		panel.add(this.SMILES);
	}

	public void run(){
		int rank, classification1;
		try{
			rank = Integer.parseInt( this.rank.getText() );
			
			classification1 = Integer.parseInt( this.classification.getText() );
			
			ExecutorService executor = Executors.newCachedThreadPool();
	        ArrayList<String> result = 
	        		executor.invokeAny(Arrays.asList(
	        				new GeneticAlgorithmTask(rank, classification1)));
	        
	        this.graphs = result;
	        
	        this.index = 0;
	        this.structureDisplayer.setGraph(this.graphs.get(0));
	        this.structureDisplayer.drawCurrentSMILES();
	        
	        executor.shutdown();
	        
		} catch(NumberFormatException e){
			System.err.println("The rank or classification you entered is invalid");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getNextGraph() {
		if (index >= this.graphs.size() - 1) {
			index = 0;
		} else {
			index++;
		}
		return graphs.get(index);
	}

	public String getPreviousGraph() {
		if( index <= 0){
			index = this.graphs.size() - 1;
		} else{
			index--;
		}
		return graphs.get(index);
	}
}
