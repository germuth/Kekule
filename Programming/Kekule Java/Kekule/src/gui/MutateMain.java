package gui;

import graphs.Graph;
import graphs.GraphToSMILES;
import gui.parameterWindow.GAWindow;
import gui.parameterWindow.MutationWindow;
import gui.parameterWindow.PopulationWindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
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

import shared.Cell;
import shared.GraphtoCell;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;


/**
 * MainFrame
 * 
 * This class represents the JFrame which contains the entire 
 * graphical user interface. Was made using an eclipse attachment.
 * 
 * @author Aaron
 *
 */
public class MutateMain extends JFrame{
	private JPanel contentPane;
	/**
	 * The center piece of the graphical user interface. It is the JPanel
	 * which displays the molecular structure
	 */
	private StructureDisplayer structureDisplayer; 
	private JTextField rank;
	private JTextField classification;
	private JButton addToLib;
	private JButton previous;
	private JButton next;
	/**
	 * A checkbox which determines whether the textual representation
	 * of the cell will be displayed on the structure generator. 
	 */
	private JCheckBox displayCell;
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
	private ArrayList<String> currentSMILES;
	
	private ArrayList<String> cells;
	private ArrayList<Graph> currentGraphs;
	private ArrayList<Graph> originalGraphs;
	
	/**
	 * The textual representation of the current cell
	 */
	private String cell;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MutateMain frame = new MutateMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void showGraphs(ArrayList<Graph> graphs) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MutateMain frame = new MutateMain(graphs);
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
	public MutateMain() {
		setUpGUI();
		
		ArrayList<Graph> gg = shared.Utils.getRank6Graphs(this);
		
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<String> cells = new ArrayList<String>();
		this.currentGraphs = new ArrayList<Graph>();
		this.originalGraphs = new ArrayList<Graph>();
		int i = 1;
		for(Graph g : gg){
			String next = GraphToSMILES.convertSMILES(g);
			smiles.add(next);
			this.currentGraphs.add(g);
			this.originalGraphs.add(new Graph(g));
			cells.add(i + "");
			i++;
		}
		this.collectAndShowResults(smiles, cells);
	}
	
	public MutateMain(ArrayList<Graph> graphs){
		setUpGUI();
		
		ArrayList<String> smiles = new ArrayList<String>();
		ArrayList<String> cells = new ArrayList<String>();
		this.currentGraphs = new ArrayList<Graph>();
		this.originalGraphs = new ArrayList<Graph>();
		int i = 1;
		for(Graph g : graphs){
			String next = GraphToSMILES.convertSMILES(g);
			smiles.add(next);
			this.currentGraphs.add(g);
			this.originalGraphs.add(new Graph(g));
			cells.add(i + "");
			i++;
		}
		this.collectAndShowResults(smiles, cells);
	}
	
	public void setUpGUI(){
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
				MutateMain.this.save( 
						MutateMain.this.rank.getText() + " " + 
						MutateMain.this.classification.getText() + ".png" );
			}
		});
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnViewLibarary = new JMenu("View Libarary");
		menuBar.add(mnViewLibarary);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		// the left JPanel
		// contains rank, classification, addToLib, Previous
		// and loading bar
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
			public void actionPerformed(ActionEvent arg0) {
				String graph = MutateMain.this.getPreviousGraph();

				MutateMain.this.structureDisplayer.setGraph(graph);
				MutateMain.this.structureDisplayer.drawCurrentSMILES();
			}
		});
		leftBorder.add(this.previous);

		this.next = new JButton("Next");
		this.next.setBounds(7, 285, 86, 64);
		this.next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String graph = MutateMain.this.getNextGraph();

				MutateMain.this.structureDisplayer.setGraph(graph);
				MutateMain.this.structureDisplayer.drawCurrentSMILES();
			}
		});

		this.addToLib = new JButton("Revert");
		this.addToLib.setBounds(7, 138, 86, 64);
		this.addToLib.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MutateMain.this.currentGraphs.set(index, MutateMain.this.originalGraphs.get(index));
				String smiles = GraphToSMILES.convertSMILES(MutateMain.this.currentGraphs.get(index));
				MutateMain.this.currentSMILES.set(index, smiles);

				MutateMain.this.currentSMILES.set(MutateMain.this.index, smiles);
				MutateMain.this.SMILES.setText(smiles);

				MutateMain.this.structureDisplayer.setGraph(smiles);
				MutateMain.this.structureDisplayer.drawCurrentSMILES();

			}
		});

		leftBorder.add(this.addToLib);

		leftBorder.add(this.next);

		JLabel lblSmiles = new JLabel("SMILES:");
		lblSmiles.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSmiles.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblSmiles.setBounds(5, 394, 86, 26);
		leftBorder.add(lblSmiles);
		
		//the right JPanel of this frame
		//contains textbox, next button, check box, 
		//and small JLabels
		JPanel rightBorder = new JPanel();
		contentPane.add(rightBorder, BorderLayout.EAST);
		rightBorder.setPreferredSize(new Dimension(110,500));
		//BoxLayout bl = new BoxLayout(rightBorder, BoxLayout.Y_AXIS);
		GridLayout gl = new GridLayout();
		gl.setColumns(1);
		gl.setRows(6);
		rightBorder.setLayout(gl);
		
		JButton fixRings = new JButton("Fix all Rings");
		rightBorder.add(fixRings);
		fixRings.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Graph g = MutateMain.this.currentGraphs.get(MutateMain.this.index);
				g.tryToFixCycleSize();
				String smiles = GraphToSMILES.convertSMILES(g);
				MutateMain.this.currentSMILES.set(MutateMain.this.index, smiles);
				MutateMain.this.SMILES.setText(smiles);
				
				MutateMain.this.structureDisplayer.setGraph(smiles);
		        MutateMain.this.structureDisplayer.drawCurrentSMILES();
			}
		});
		
		JButton expandNode = new JButton("Split Node");
		expandNode.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Graph g = MutateMain.this.currentGraphs.get(MutateMain.this.index);
				Graph g2 = g.expandNode();
				if( g2 != null){
					MutateMain.this.currentGraphs.set(MutateMain.this.index, g2);
					String smiles = GraphToSMILES.convertSMILES(g2);
					MutateMain.this.currentSMILES.set(MutateMain.this.index, smiles);
					MutateMain.this.SMILES.setText(smiles);
				
					MutateMain.this.structureDisplayer.setGraph(smiles);
					MutateMain.this.structureDisplayer.drawCurrentSMILES();
				}
			}
		});
		rightBorder.add(expandNode);
		
		JButton condenseNode = new JButton("Merge Node");
		condenseNode.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Graph g = MutateMain.this.currentGraphs.get(MutateMain.this.index);
				Graph g2 = g.mergeNode();
				if( g2 != null){
					MutateMain.this.currentGraphs.set(MutateMain.this.index, g2);
					String smiles = GraphToSMILES.convertSMILES(g2);
					MutateMain.this.currentSMILES.set(MutateMain.this.index, smiles);
					MutateMain.this.SMILES.setText(smiles);
				
					MutateMain.this.structureDisplayer.setGraph(smiles);
					MutateMain.this.structureDisplayer.drawCurrentSMILES();
				}
			}
		});
		rightBorder.add(condenseNode);
		
		JButton disjoint = new JButton("Con Disjoint");
		disjoint.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Graph g = MutateMain.this.currentGraphs.get(MutateMain.this.index);
				if(g.tryToConnect()){
					MutateMain.this.currentGraphs.set(MutateMain.this.index, g);
					String smiles = GraphToSMILES.convertSMILES(g);
					MutateMain.this.currentSMILES.set(MutateMain.this.index, smiles);
					MutateMain.this.SMILES.setText(smiles);
					
					MutateMain.this.structureDisplayer.setGraph(smiles);
					MutateMain.this.structureDisplayer.drawCurrentSMILES();					
				}
			}
		});
		
		rightBorder.add(disjoint);
		
		JButton portExt = new JButton("Extend Ports");
		portExt.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Graph g = MutateMain.this.currentGraphs.get(MutateMain.this.index);
				Graph g2 = g.extendPorts();
				if( g2 != null){
					MutateMain.this.currentGraphs.set(MutateMain.this.index, g2);
					String smiles = GraphToSMILES.convertSMILES(g2);
					MutateMain.this.currentSMILES.set(MutateMain.this.index, smiles);
					MutateMain.this.SMILES.setText(smiles);
				
					MutateMain.this.structureDisplayer.setGraph(smiles);
					MutateMain.this.structureDisplayer.drawCurrentSMILES();
				}
			}
		});
		rightBorder.add(portExt);
		
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
				if(MutateMain.this.displayCell.isSelected()){
					MutateMain.this.cellLabel.setVisible(true);
				} else{
					MutateMain.this.cellLabel.setVisible(false);
				}
			}
		});
		displayCell.setFont(new Font("Tahoma", Font.PLAIN, 12));
		this.displayCell.setBounds(0, 300, 99, 23);
		rightBorder.add(this.displayCell);
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
            //TODO hardcoded
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
	public void collectAndShowResults(ArrayList<String> graphs, ArrayList<String> cell){
		this.index = 0;
		if( graphs.size() > 0){
			this.currentSMILES = graphs;
			this.cells = cell;
			this.structureDisplayer.setGraph( this.currentSMILES.get(index) );
			this.structureDisplayer.drawCurrentSMILES();
			this.SMILES.setText( this.currentSMILES.get(index) );
			int count = 0;
			int length = cell.get(0).length();
			while( length > 60){
				count++;
				length -= 10;
			}
			this.cellLabel.setText(cell.get(index));
			this.cellLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, (14 - count)));
		} else{
			System.out.println("Graphs Not Found");
		}
	}
	
	/**
	 * Called by action listener on next button. 
	 * Increases the index in order to prepare to display
	 * the next graph
	 * @return the next graph
	 */
	public String getNextGraph() {
		if (index >= this.currentSMILES.size() - 1) {
			index = 0;
		} else {
			index++;
		}
		this.SMILES.setText( this.currentSMILES.get(index) );
		this.cellLabel.setText( this.currentGraphs.get(index).getName().replace("graph", ""));
		return currentSMILES.get(index);
	}

	/**
	 * Called by action listener on previous button.
	 * Decrements the index in order to prepare to display
	 * the previous graph
	 * @return the previous graph
	 */
	public String getPreviousGraph() {
		if( index <= 0){
			index = this.currentSMILES.size() - 1;
		} else{
			index--;
		}
		this.SMILES.setText( this.currentSMILES.get(index) );
		this.cellLabel.setText( this.currentGraphs.get(index).getName().replace("graph", ""));
		return currentSMILES.get(index);
	}
}
