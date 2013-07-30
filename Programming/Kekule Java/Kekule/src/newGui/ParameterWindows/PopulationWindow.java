package newGui.ParameterWindows;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import newGui.ParameterWindows.PopulationPanel.InitPopParameterPanel;
import newGui.ParameterWindows.PopulationPanel.PopParameterPanel;


public class PopulationWindow extends JFrame {

	private JPanel contentPane;
	private InitPopParameterPanel initPop;
	private PopParameterPanel pop;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PopulationWindow frame = new PopulationWindow();
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
	public PopulationWindow() {
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 777, 310);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		this.initPop = new InitPopParameterPanel();
		this.initPop.setBounds(5, 5, 375, 262);
		
		this.pop = new PopParameterPanel();
		this.pop.setBounds(380, 5, 375, 228);
		contentPane.setLayout(null);
		contentPane.add( this.initPop );
		contentPane.add( this.pop );
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PopulationWindow.this.setVisible(false);
				PopulationWindow.this.setParameters();
			}	
		});
		btnClose.setBounds(666, 244, 89, 23);
		contentPane.add(btnClose);
	}
	
	public void setParameters(){
		this.initPop.setParameters();
		this.pop.setParameters();
	}
}
