package mttool.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ExecutionProgressGUI extends JFrame {
	
	JPanel panel;
	JProgressBar progressBar;
	
	public ExecutionProgressGUI(){
		try{
			init();
		}catch(Exception e){
			e.printStackTrace();
		}
		this.pack();
	}
	
	public static void main(String args[]){
		ExecutionProgressGUI main = new ExecutionProgressGUI();
		main.pack();
		main.setLocationRelativeTo(null);
		main.setVisible(true);	
	}
	
	private void init() throws Exception {
		panel = new JPanel();
		progressBar = new JProgressBar();
		this.getContentPane().add(panel, BorderLayout.CENTER);
		//GridBagConstraints c = new GridBagConstraints();
		panel.setBorder(new TitledBorder("Progress"));
		
		//add one jProgressbar
	    progressBar.setPreferredSize(new Dimension(400,20));
	    progressBar.setMaximumSize(new Dimension(400,20));
	    progressBar.setMinimumSize(new Dimension(400,20));
	    progressBar.setMinimum(0);
	    progressBar.setMaximum(100);
	    progressBar.setValue(0);
	    progressBar.setStringPainted(true);
	    progressBar.setString(" ");
	      //resultPanel.add(progressBar);
		
		panel.add(progressBar);
		
		/*
		this.addWindowListener(new java.awt.event.WindowAdapter()
	      {
	         @Override
			public void windowClosing(WindowEvent e)
	         {
	            this_windowClosing(e);
	         }
	      });
		
		*/
	}
	
	void this_windowClosing(WindowEvent e)
	   {
	      System.exit(0);
	   }

}
