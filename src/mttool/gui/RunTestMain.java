package mttool.gui;

import mttool.MutationSystem;
import mttool.util.Debug;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.*;
import org.jvnet.substance.theme.SubstanceAquaTheme;
import org.jvnet.substance.theme.SubstanceBarbyPinkTheme;
import org.jvnet.substance.theme.SubstanceTerracottaTheme;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * <p>GUI program for running mutants against test cases</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class RunTestMain extends JFrame
{
   private static final long serialVersionUID = 107L;

   JTabbedPane testTabbedPane = new JTabbedPane();

   /** Panel for running mutants */
   RunTestPanel runPanel;

   /** Panel for viewing detail of class mutants */
   ClassMutantsViewerPanel cvPanel;

   /** Panel for viewing detail of traditional mutants */
   TraditionalMutantsViewerPanel tvPanel;

   
   ConPanel conPanel;

   /** Panel for viewing detail of exceltion-related mutants (not used) */
   //ExceptionMutantsViewerPanel evPanel;

   public RunTestMain() 
   { 
      try 
      {
         jbInit();
      }
      catch (Exception e) 
      {
         e.printStackTrace();
      }
   }

  /** <p> Main program for running mutants (no parameter required for run).</p>
   *  <p>- supporting functions: (1) selection of test suite of Java class format,
   *  (2) selection of a Java f whose mutants are to run </p> */
   public static void main(String[] args) 
   {

      JFrame.setDefaultLookAndFeelDecorated(true);
      SubstanceLookAndFeel.setSkin(new BusinessBlueSteelSkin());
//      SubstanceLookAndFeel.setCurrentTheme(new SubstanceAquaTheme());
//      SubstanceLookAndFeel.setCurrentTheme(new SubstanceTerracottaTheme());
      JDialog.setDefaultLookAndFeelDecorated(true);
      //Debug.setDebugLevel(2);
      Debug.setDebugLevel(0);
      MutationSystem.setJMutationStructure();
      RunTestMain main = new RunTestMain();
      main.pack();
      main.setLocationRelativeTo(null);
      main.setVisible(true);


   }

   private void jbInit() throws Exception 
   {
      runPanel = new RunTestPanel();
      cvPanel = new ClassMutantsViewerPanel();
      tvPanel = new TraditionalMutantsViewerPanel();
      conPanel = new ConPanel();
      //evPanel = new ExceptionMutantsViewerPanel();
//      this.setTitle("AMT");

      this.getContentPane().add(testTabbedPane, BorderLayout.CENTER);
      testTabbedPane.add("Define MRs",conPanel);
      testTabbedPane.add("Configurate Info", tvPanel);
//      testTabbedPane.add("Class Mutants", cvPanel);
      testTabbedPane.add("Run", runPanel);

      //testTabbedPane.add("Exception Mutants Viewer", evPanel);

      this.addWindowListener(new java.awt.event.WindowAdapter()
      {
         @Override
		public void windowClosing(WindowEvent e)
         {
            this_windowClosing(e);
         }
      });
   }
  
   void this_windowClosing(WindowEvent e)
   {
      System.exit(0);
   }
}
