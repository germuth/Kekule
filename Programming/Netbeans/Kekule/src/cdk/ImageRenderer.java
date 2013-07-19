package cdk;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.RingGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * 
 * @author Hari
 */
public class ImageRenderer extends javax.swing.JComponent {

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

	/** Creates new form ImageRenderer */
	public ImageRenderer(ArrayList<String> graphs) {
		this.graphs = graphs;
		this.index = 0;
		initComponents();

		Dimension dim = new Dimension(500, 500);
		
		jButton1.doClick();
		
		this.setVisible(true);
		this.setMinimumSize(dim);
		this.validate();
	}
	
	public void setGraphs(ArrayList<String> graphs){
		this.graphs = graphs;
		this.index = 0;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		jTextField1 = new javax.swing.JTextField();
		jLabel1 = new javax.swing.JLabel();
		jButton1 = new javax.swing.JButton();
		nextButton = new javax.swing.JButton("Next");
		previousButton = new JButton("Previous");
		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();

		jTextField1.setText(this.graphs.get(0));

		jLabel1.setText("Smiles String");

		jButton1.setText("Generate");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				jTextField1.setText(ImageRenderer.this.getNextGraph());
				jButton1.doClick();
			}
		});
		
		previousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				jTextField1.setText(ImageRenderer.this.getPreviousGraph());
				jButton1.doClick();
			}
		});

		jPanel1.setBackground(new java.awt.Color(255, 255, 255));
		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jPanel1.setForeground(new java.awt.Color(236, 233, 216));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 376,
				Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 246,
				Short.MAX_VALUE));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				this );
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jPanel1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).addContainerGap())
				.addGroup(
						layout.createSequentialGroup()
								.addGap(18, 18, 18)
								.addComponent(previousButton)
								.addComponent(jLabel1)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jTextField1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										209, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jButton1)
								.addComponent(nextButton).addGap(20, 20, 20)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(previousButton)
												.addComponent(jLabel1)
												.addComponent(jButton1)
												.addComponent(nextButton)
												.addComponent(
														jTextField1,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jPanel1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).addContainerGap()));

	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			int WIDTH = 400;
			int HEIGHT = 400;
			// the draw area and the image should be the same size
			Rectangle drawArea = new Rectangle(WIDTH, HEIGHT);
			Image image = new BufferedImage(WIDTH, HEIGHT,
					BufferedImage.TYPE_INT_RGB);

			// get molecule from SMILES
			Molecule molecule = (Molecule) new SmilesParser(
					DefaultChemObjectBuilder.getInstance())
					.parseSmiles(jTextField1.getText());

			// assign coordinates for each atom of molecule
			StructureDiagramGenerator sdg = new StructureDiagramGenerator();
			sdg.setMolecule(molecule);
			try {
				sdg.generateCoordinates();
			} catch (Exception ex) {
				// Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
				// null, ex);
			}

			// grab new molecule with coordinates
			IMolecule newMolecule = sdg.getMolecule();

			// create generators for renderer
			List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
			generators.add(new BasicSceneGenerator());
			generators.add(new RingGenerator());
			//generators.add(new BasicBondGenerator());
			generators.add(new BasicAtomGenerator());
				
			// the renderer needs to have a toolkit-specific font manager
			AtomContainerRenderer renderer = new AtomContainerRenderer(
					generators, new AWTFontManager());
			RendererModel model = renderer.getRenderer2DModel();

			// attempt to highlight the first and last atom of our rendered molecule
			IAtom first = newMolecule.getFirstAtom();
			IAtom last = newMolecule.getLastAtom();

			Color highlight = Color.blue;

			// map from atom to its highlighted colour
			Map<IChemObject, Color> colors = new HashMap<IChemObject, Color>();

			colors.put(first, highlight);
			colors.put(last, highlight);

			// give colour hash map to renderer
			model.getParameter(RendererModel.ColorHash.class).setValue(colors);
			
			
			
			// the call to 'setup' only needs to be done on the first paint
			renderer.setup(newMolecule, drawArea);
			// paint the background
			Graphics2D g2 = (Graphics2D) image.getGraphics();
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, WIDTH, HEIGHT);
			// the paint method also needs a toolkit-specific renderer
			renderer.paint(newMolecule, new AWTDrawVisitor(g2));
			jLabel2.setIcon(new ImageIcon(image));
			this.validate();
		} catch (InvalidSmilesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(final ArrayList<String> graphs) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ImageRenderer(graphs).setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	private int index;
	private ArrayList<String> graphs;
	private javax.swing.JButton jButton1;
	private JButton nextButton;
	private JButton previousButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JTextField jTextField1;
	// End of variables declaration

}