package gui;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LabeledSlider extends JPanel{
	private JLabel myLabel;
	private JSlider mySlider;
	private JLabel myValue;
	
	public LabeledSlider(String name, int min, int max, int starting){
		super();
		//create label with name
		myLabel = new JLabel(name);
		myLabel.setFont(new Font("Serif", Font.PLAIN, 16));
		
		//create slider
		mySlider = new JSlider(min, max);
		mySlider.setValue( starting );
		//add listener to change other label which shows current value
		mySlider.addChangeListener( new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				LabeledSlider.this.myValue.setText( LabeledSlider.this.mySlider.getValue() + "" );
			}
			
		});
		
		//create label
		myValue = new JLabel(starting + "");
		
		this.add( myLabel );
		//add empty JPanel for spacing
		this.add( new JPanel() );
		this.add( mySlider );
		this.add( myValue );
	}
	
	public int getValue(){
		return this.mySlider.getValue();
	}
}
