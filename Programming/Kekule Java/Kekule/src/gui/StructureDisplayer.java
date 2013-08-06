package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

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
 * Structure Displayer
 * 
 * This class takes a SMILES representation of a molecule and 
 * displays it to the user. THe Open Source Chemistry Development
 * Kit finds the actual atom coordinates. 
 * @author Aaron
 */
public class StructureDisplayer extends JComponent{
	/**
	 * SMILES representation of current graph
	 */
	private String graph;
	/**
	 * The main panel for displaying structures
	 */
	private javax.swing.JPanel jPanel1;
	/**
	 * A Jlabel
	 */
	private JLabel jLabel2;

	/** Creates new form ImageRenderer */
	public StructureDisplayer(String graph) {
		this.graph = graph;
		initComponents();

		Dimension dim = new Dimension(500, 500);

		this.setVisible(true);
		this.setMinimumSize(dim);
		setLayout(null);
		add(jPanel1);
		this.drawCurrentSMILES();
		this.validate();
	}
	
	/**
	 * Sets the current graph of this structure displayer
	 * @param graph
	 */
	public void setGraph(String graph) {
		this.graph = graph;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jPanel1.setBounds(18, 74, 466, 365);

		jPanel1.setBackground(new java.awt.Color(255, 255, 255));
		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		jPanel1.setForeground(new java.awt.Color(236, 233, 216));

		this.jLabel2 = new JLabel("");

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				jPanel1Layout
						.createSequentialGroup()
						.addContainerGap(33, Short.MAX_VALUE)
						.addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 401,
								GroupLayout.PREFERRED_SIZE).addGap(28)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				Alignment.TRAILING).addGroup(
				jPanel1Layout
						.createSequentialGroup()
						.addContainerGap(31, Short.MAX_VALUE)
						.addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 302,
								GroupLayout.PREFERRED_SIZE).addGap(28)));
		jPanel1.setLayout(jPanel1Layout);

	}

	/**
	 * This method uses the chemistry development kit to draw the current SMILES graph
	 */
	public void drawCurrentSMILES() {
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
					.parseSmiles(this.graph);

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
			// generators.add(new BasicBondGenerator());
			generators.add(new BasicAtomGenerator());

			// the renderer needs to have a toolkit-specific font manager
			AtomContainerRenderer renderer = new AtomContainerRenderer(
					generators, new AWTFontManager());
			RendererModel model = renderer.getRenderer2DModel();

			// attempt to highlight the first and last atom of our rendered
			// molecule
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
			System.err.println("Invalid SMILES String inputed");
			e.printStackTrace();
		}
	}
}
