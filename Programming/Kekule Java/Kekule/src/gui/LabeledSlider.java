package gui;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BoxLayout;

public class LabeledSlider extends JPanel{
	private JLabel myLabel;
	private JSlider mySlider;
	private JLabel myValue;
	
	public LabeledSlider(String name, int min, int max, int starting){
		super();
		
		//create slider
		mySlider = new JSlider(min, max);
		mySlider.setBounds(101, 0, 222, 23);
		mySlider.setValue( starting );
		//add listener to change other label which shows current value
		mySlider.addChangeListener( new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				LabeledSlider.this.myValue.setText( LabeledSlider.this.mySlider.getValue() + "" );
			}
			
		});
		setLayout(null);
		//create label with name
		myLabel = new JLabel(name);
		myLabel.setBounds(0, 0, 108, 22);
		myLabel.setFont(new Font("Serif", Font.PLAIN, 16));
		this.add( myLabel );
		this.add( mySlider );
		
		//create label
		myValue = new JLabel(starting + "");
		myValue.setBounds(333, 2, 49, 22);
		this.add( myValue );
	}
	
	public int getValue(){
		return this.mySlider.getValue();
	}
}
