package mttool.gui;


import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Map;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;


import mttool.gui.util.*;

import java.io.*;

import mttool.MutationSystem;
import mttool.TestExecuter;
import mttool.util.*;
import mttool.test.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;


public class RunTestPanel extends JPanel implements ActionListener  
{
   private static final long serialVersionUID = 108L;

   String target_dir;
   
   //A three-second initial value is required; otherwise, 
   //The program would freeze if the mutant gets stuck in an infinite loop or something similar that makes the program cannot respond
   //The bug is fixed by Nan Li
   //Updated on Dec. 5 2011
   int timeout_secs = 3000;
   
   // add customized timeout setting
   // Lin, 05232015
   int customized_time = 3000;
 
   JTable cmTable;
   JTable tmTable;
   JTable cResultTable;
   JTable tResultTable;
   JComboBox classCB;
   JComboBox methodCB; 
   JComboBox timeCB;
   
   // add a new textfield for customized timeout
   JTextField timeoutTextField;
   boolean isCustomizedTimeout = false;
   
   JList cLiveList = new JList();
   JList tLiveList = new JList();
   JList cKilledList = new JList();
   JList tKilledList = new JList();
   JLabel cmTotalLabel = new JLabel("Total= ", JLabel.LEFT);
   JLabel tmTotalLabel = new JLabel("Total= ", JLabel.LEFT);
   JRadioButton onlyClassButton = new JRadioButton("Execute only class mutants");
   JRadioButton onlyTraditionalButton = new JRadioButton("Execute only traditional mutants");
   JRadioButton onlyExceptionButton = new JRadioButton("Execute only exception mutants");
   JRadioButton bothButton = new JRadioButton("Execute all mutants");

   JComboBox testCB;
   JButton runB = new JButton("RUN");
   JButton runAllButton = new JButton("Run All Test Cases");
   
   JProgressBar progressBar = new JProgressBar();
   
   JPanel resultPanel;

   JPanel tResultPanel = new JPanel();
   JPanel cResultPanel = new JPanel();
   final int CLASS = 1;
   final int TRADITIONAL = 2;
   final int BOTH = 3;
   
   Vector allmutants = new Vector();

   public RunTestPanel()
   {
      try 
      {
         jbInit();
      }
      catch (Exception ex) 
      {
         ex.printStackTrace();
      }
   }

   void jbInit()
   {
      this.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();

      onlyClassButton.setActionCommand("CLASS");
      onlyClassButton.addActionListener(this);
      onlyClassButton.setSelected(true);

      onlyTraditionalButton.setActionCommand("TRADITIONAL");
      onlyTraditionalButton.addActionListener(this);

      onlyExceptionButton.setActionCommand("TRADITIONAL");
      onlyExceptionButton.addActionListener(this);

      bothButton.setActionCommand("BOTH");
      bothButton.addActionListener(this);
 
      ButtonGroup group = new ButtonGroup();
      group.add(onlyClassButton);
      group.add(onlyTraditionalButton);
      group.add(bothButton);

      JPanel optionP = new JPanel(new GridLayout(0, 1));
      optionP.add(onlyClassButton);
      optionP.add(onlyTraditionalButton);
      optionP.add(bothButton);

      c.gridx = 0;
      c.gridy = 0;
      this.add(optionP, c);

      // Summary Tables for traditioanl mutants and class mutants (x,y) = (0,1)
      JPanel summaryPanel = new JPanel();
      summaryPanel.setLayout(new FlowLayout());

      JPanel traditional_summaryPanel = new JPanel();
      traditional_summaryPanel.setLayout(new BoxLayout(traditional_summaryPanel,BoxLayout.PAGE_AXIS));
 
      JScrollPane tmTablePanel = new JScrollPane();
      TMSummaryTableModel tmodel = new TMSummaryTableModel();
      tmTable = new JTable(tmodel);
      adjustSummaryTableSize(tmTable, tmodel);
      tmTablePanel.getViewport().add(tmTable);
      tmTablePanel.setPreferredSize(new Dimension(120, 500));
      tmTablePanel.setMaximumSize(new Dimension(120, 500));

      traditional_summaryPanel.add(tmTablePanel);
      traditional_summaryPanel.add(tmTotalLabel);

      JPanel class_summaryPanel = new JPanel();
      class_summaryPanel.setLayout(new BoxLayout(class_summaryPanel, BoxLayout.PAGE_AXIS));

      JScrollPane cmTablePanel = new JScrollPane();
      CMSummaryTableModel cmodel = new CMSummaryTableModel();
      cmTable = new JTable(cmodel);
      adjustSummaryTableSize(cmTable, cmodel);
      cmTablePanel.getViewport().add(cmTable);
      cmTablePanel.setPreferredSize(new Dimension(120, 500));
      cmTablePanel.setMaximumSize(new Dimension(120, 500));

      traditional_summaryPanel.setPreferredSize(new Dimension(100, 520));
      traditional_summaryPanel.setMaximumSize(new Dimension(100, 520));
      class_summaryPanel.setPreferredSize(new Dimension(100, 520));
      class_summaryPanel.setMaximumSize(new Dimension(100, 520));

      class_summaryPanel.add(cmTablePanel);
      class_summaryPanel.add(cmTotalLabel);

      summaryPanel.add(traditional_summaryPanel);
      summaryPanel.add(class_summaryPanel);

      c.gridx = 0;
      c.gridy = 1;
      this.add(summaryPanel, c);

      // Selection part for class, test cases names ==>  (x,y) = (1,0)
      JPanel selectPanel = new JPanel();
      selectPanel.setLayout(new GridBagLayout());
      GridBagConstraints selectConstraints = new GridBagConstraints();
      selectConstraints.gridx = 0;
      selectConstraints.gridy = 0;
      JLabel label1 = new JLabel("Class       : ", SwingConstants.RIGHT);
      label1.setPreferredSize(new Dimension(100,28));  
      label1.setMaximumSize(new Dimension(100,28));
      selectPanel.add(label1 , selectConstraints);

      File classF = new File(MutationSystem.MUTANT_HOME);
      String[] c_list = classF.list(new DirFileFilter());
      classCB = new JComboBox(c_list);
      classCB.addActionListener(new java.awt.event.ActionListener()
      {
         @Override
		public void actionPerformed(ActionEvent e)
         {
            changeContents();
         }
      });

      selectConstraints.gridx = 1;
      selectConstraints.gridy = 0;
      selectConstraints.gridwidth = 2;
      classCB.setPreferredSize(new Dimension(400, 28));
      classCB.setMaximumSize(new Dimension(400, 28));
      selectPanel.add(classCB, selectConstraints); 

      selectConstraints.gridx = 0;
      selectConstraints.gridy = 1;
      selectConstraints.gridwidth = 1;
      JLabel label_method = new JLabel("Method    : ", SwingConstants.RIGHT);
      label_method.setPreferredSize(new Dimension(100, 28));
      label_method.setMaximumSize(new Dimension(100, 28));
      selectPanel.add(label_method, selectConstraints);

      methodCB = new JComboBox();
      methodCB.addActionListener(new java.awt.event.ActionListener()
      {
         @Override
		public void actionPerformed(ActionEvent e)
         {
            changeMethodContents();
         }
      });

      selectConstraints.gridx = 1;
      selectConstraints.gridy = 1;
      //selectConstraints.gridwidth = 2;
      methodCB.setPreferredSize(new Dimension(320, 28));
      methodCB.setMaximumSize(new Dimension(320, 28));
      selectPanel.add(methodCB, selectConstraints);

      selectConstraints.gridx = 0;
      selectConstraints.gridy = 2;
      selectConstraints.gridwidth = 1;
      JLabel label2 = new JLabel("TestCase  : ", SwingConstants.RIGHT);
      label2.setPreferredSize(new Dimension(100, 28));
      label2.setMaximumSize(new Dimension(100, 28));
      selectPanel.add(label2, selectConstraints);

      String[] t_list = getTestSetNames();
      testCB = new JComboBox(eraseExtension(t_list, "class"));
      testCB.setPreferredSize(new Dimension(320, 28));
      testCB.setMaximumSize(new Dimension(320, 28));
      selectConstraints.gridx = 1;
      selectConstraints.gridy = 2;
      selectPanel.add(testCB, selectConstraints);

      selectConstraints.gridx = 2;
      selectConstraints.gridy = 2;
      selectPanel.add(runB, selectConstraints);
      runB.setPreferredSize(new Dimension(80, 28));
      runB.setMaximumSize(new Dimension(80, 28));
      runB.setBackground(Color.yellow);
      runB.addMouseListener(new java.awt.event.MouseAdapter()
      {
         @Override
		public void mouseClicked(MouseEvent e)
         {
            testRunB_mouseClicked(e);
         }
      });

      selectConstraints.gridx = 2;
      selectConstraints.gridy = 1;
      selectPanel.add(runAllButton,selectConstraints);
      runAllButton.setPreferredSize(new Dimension(80,28));
      runAllButton.setMaximumSize(new Dimension(80,28));
      runAllButton.setBackground(Color.blue);
      runAllButton.addMouseListener(new java.awt.event.MouseAdapter()
      {
    	  @Override
    	  public void mouseClicked(MouseEvent e)
    	  {
    		  testRunAllButton_mouseClicked(e);
    	  }
    	  
      });
      
      selectConstraints.gridx = 0;
      selectConstraints.gridy = 3;
      JLabel label_time = new JLabel("Time-Out : ", SwingConstants.RIGHT);
      label_time.setPreferredSize(new Dimension(100, 28));
      label_time.setMaximumSize(new Dimension(100, 28));
      selectPanel.add(label_time, selectConstraints);
  
      String[] time_list = {"3 seconds", "5 seconds", "10 seconds", "Other"};
	  timeCB = new JComboBox(time_list);
      timeCB.addActionListener(new java.awt.event.ActionListener()
      {
         @Override
		public void actionPerformed(ActionEvent e)
         {
            changeTimeOut();
         }
      });

      timeoutTextField = new JTextField();
      timeoutTextField.setHorizontalAlignment(SwingConstants.CENTER);
      
      // adjust the gui to have a textfield for customized timeout
      // Lin 05232015
      selectConstraints.gridx = 1;
      selectConstraints.gridy = 3;
//      selectConstraints.gridwidth = 2;
      timeCB.setPreferredSize(new Dimension(320, 28));
      timeCB.setMaximumSize(new Dimension(320, 28));
      selectPanel.add(timeCB, selectConstraints);
      

      timeoutTextField.setPreferredSize(new Dimension(74, 28));
      timeoutTextField.setMaximumSize(new Dimension(74, 28));
      timeoutTextField.setEnabled(false);
      timeoutTextField.setText("3");
      selectConstraints.gridx = 2;
      selectConstraints.gridy = 3;
      selectPanel.add(timeoutTextField, selectConstraints);
//      
      selectConstraints.gridx = 3;
      selectConstraints.gridy = 3;
      selectPanel.add(new JLabel("s"), selectConstraints);
      
      c.gridx = 1;
      c.gridy = 0;
      this.add(selectPanel, c);

      // Mutants ==> (x,y) = (1,1)
      //JPanel resultPanel = new JPanel();
      resultPanel = new JPanel();
      resultPanel.setLayout(new FlowLayout());

      tResultPanel.setBorder(new TitledBorder("Traditional Mutants Result"));
      tResultPanel.setLayout(new GridBagLayout());
      GridBagConstraints tResultConstraints = new GridBagConstraints();
      ResultTableModel tResultTableModel = new ResultTableModel();
      tResultTable = new JTable(tResultTableModel);
      setResultTableSize(tResultTable);
      tResultConstraints.gridx = 0;
      tResultConstraints.gridy = 0;
      tResultConstraints.gridwidth = 2;
      tResultPanel.add(tResultTable, tResultConstraints);
      JScrollPane t_livePanel = new JScrollPane();
      setSPSize(t_livePanel);
      t_livePanel.setBorder(new TitledBorder("Live"));
      t_livePanel.getViewport().add(tLiveList);
      tResultConstraints.gridx = 0;
      tResultConstraints.gridy = 1;
      tResultConstraints.gridwidth = 1;
      tResultPanel.add(t_livePanel, tResultConstraints);
      JScrollPane t_killedPanel = new JScrollPane();
      setSPSize(t_killedPanel);
      t_killedPanel.setBorder(new TitledBorder("Killed"));
      t_killedPanel.getViewport().add(tKilledList);
      tResultConstraints.gridx = 1;
      tResultConstraints.gridy = 1;
      tResultPanel.add(t_killedPanel, tResultConstraints);
      resultPanel.add(tResultPanel);

      cResultPanel.setBorder(new TitledBorder("Class Mutants Result"));
      cResultPanel.setLayout(new GridBagLayout());
      GridBagConstraints cResultConstraints = new GridBagConstraints();
      ResultTableModel cResultTableModel = new ResultTableModel();
      cResultTable = new JTable(cResultTableModel);
      setResultTableSize(cResultTable);
      cResultConstraints.gridx = 0;
      cResultConstraints.gridy = 0;
      cResultConstraints.gridwidth = 2;
      cResultPanel.add(cResultTable, cResultConstraints);
      JScrollPane c_livePanel = new JScrollPane();
      setSPSize(c_livePanel);
      c_livePanel.setBorder(new TitledBorder("Live"));
      c_livePanel.getViewport().add(cLiveList);
      cResultConstraints.gridx = 0;
      cResultConstraints.gridy = 1;
      cResultConstraints.gridwidth = 1;
      cResultPanel.add(c_livePanel, cResultConstraints);
      JScrollPane c_killedPanel = new JScrollPane();
      setSPSize(c_killedPanel);
      c_killedPanel.setBorder(new TitledBorder("Killed"));
      c_killedPanel.getViewport().add(cKilledList);
      cResultConstraints.gridx = 1;
      cResultConstraints.gridy = 1;
      cResultPanel.add(c_killedPanel, cResultConstraints);
      resultPanel.add(cResultPanel);
      
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

      resultPanel.setPreferredSize(new Dimension(500, 520));
      resultPanel.setMaximumSize(new Dimension(500, 520));
      resultPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

      c.gridx = 1;
      c.gridy = 1;
      this.add(resultPanel, c);

      this.addFocusListener(new java.awt.event.FocusAdapter()
      {
         @Override
		public void focusGained(FocusEvent e)
         {
            changeContents();
         }
      });
   }
 
   String[] eraseExtension(String[] list,String extension)
   {
      String[] result = new String[list.length];
      for (int i=0; i<list.length; i++)
      {
         result[i] = list[i].substring(0, list[i].length()-extension.length()-1);
      }
      return result;
   }

   String[] getTestSetNames()
   {
      Vector v = new Vector();
      getTestSetNames(new File(MutationSystem.TESTSET_PATH), v);
      String[] result = new String[v.size()];  
      for (int i=0; i<v.size(); i++)
      {
         result[i] = v.get(i).toString();
      }
      return result;
   }

   //    File testF = new File(MutationSystem.TESTSET_PATH);
   void getTestSetNames(File testDir, Vector v)
   {
      String[] t_list = testDir.list(new ExtensionFilter("class"));
      int start_index = MutationSystem.TESTSET_PATH.length();
      int end_index = testDir.getAbsolutePath().length();
      if (start_index < end_index) 
    	 start_index ++;
      String suffix = testDir.getAbsolutePath().substring(start_index, end_index);
      if (suffix == null || suffix.equals(""))
      {
         suffix = "";
      } 
      else
      {
         String temp = "";
         for (int k=0; k<suffix.length(); k++)
         {
            char ch = suffix.charAt(k);
            if( (ch=='/') || (ch=='\\') )
            {
               temp = temp + ".";
            }
            else
            { 
               temp = temp + ch;
            }
         }
         suffix = temp + ".";
      }

      if (t_list == null)
      {
         System.out.println(" [Error] No test suite is detected. ");
      } 
      else
      {
         for (int i=0; i<t_list.length; i++)
         {
            v.add(suffix+t_list[i]);
         }

         File[] subDir = testDir.listFiles(new DirFileFilter());
         for (int i=0; i<subDir.length; i++)
         {
            getTestSetNames(subDir[i], v);
         }
      }
   }

   void testRunB_mouseClicked(MouseEvent e)
   {
	   // check if the customized timeout is used
	   // added by Lin, 05/23/2015
		if (isCustomizedTimeout) {
			try {
				timeout_secs = 1000*Integer.parseInt(timeoutTextField.getText());
				// what if a negative or zero, set it to 3000
				if (timeout_secs <= 0) {
					timeout_secs = 3000;
				}

			} catch (NumberFormatException ex) {
				// if not a number, set to be 3000
				timeout_secs = 3000;
			}
		}
	   	   
      // class name whose mutants are executed
      Object targetClassObj = classCB.getSelectedItem();
      // class name whose mutants are executed
      Object methodSignature = methodCB.getSelectedItem();
      
      if (methodSignature == null) 
    	 methodSignature = "All method";
  
      // name of test suite to apply
      Object testSetObject  = testCB.getSelectedItem();

      if((targetClassObj != null) && (testSetObject != null))
      {
         String targetClassName = classCB.getSelectedItem().toString();
         String testSetName = testCB.getSelectedItem().toString();

         TestExecuter test_engine = new TestExecuter(targetClassName);
         test_engine.setTimeOut(timeout_secs);

         // First, read (load) test suite class.
         test_engine.readTestSet(testSetName);

         TestResult test_result = new TestResult();
         try
         {
            if (onlyClassButton.isSelected())
            {
               cResultPanel.setVisible(true);
               tResultPanel.setVisible(false);
               test_engine.computeOriginalTestResults();
               test_result = test_engine.runClassMutants();
               showResult(test_result, cResultTable, cKilledList, cLiveList);
            } 
            else if (onlyTraditionalButton.isSelected())
            {
               cResultPanel.setVisible(false);
               tResultPanel.setVisible(true);
               test_engine.computeOriginalTestResults();
               test_result = test_engine.runTraditionalMutants(methodSignature.toString());
               showResult(test_result, tResultTable, tKilledList, tLiveList);
            }
            else if (bothButton.isSelected())
            {
               cResultPanel.setVisible(true);
               tResultPanel.setVisible(true);
               test_engine.computeOriginalTestResults();
               test_result = test_engine.runClassMutants();
               showResult(test_result, cResultTable, cKilledList, cLiveList);
               test_result = test_engine.runTraditionalMutants(methodSignature.toString());
               showResult(test_result, tResultTable, tKilledList, tLiveList);
            }
         } 
         catch (NoMutantException e1)
         {
         } 
         catch (NoMutantDirException e2)
         {
         }
      } 
      else
      {
         System.out.println(" [Error] Please check test target or test suite ");
      }
   }
   
   void testRunAllButton_mouseClicked(MouseEvent e)
   {
	   ExecutionProgressGUI f = new ExecutionProgressGUI();
	   f.setLocationRelativeTo(null);
	   f.setVisible(true);
	   
	   Runnable r = new Runnable(){

		   public void run(){
			   
			   		   
			   String homepath = "G:\\mujava";
			   String path = "G:\\mujava\\rawtestcase";
			   String srcpath = "G:\\mujava\\src";
			   String srcclasspath = "G\\mujava\\classes";
			   String testcaseclasspath = "G:\\mujava\\testset";
			   String testcasefilename;
			   String testcasename;
			   String targetprogramfilename;
			   String targetprogramname;
			   
			   Vector allMutants;
			   Vector allTraditionalMutants;
			   Vector allClassMutants;
			   //JOptionPane.showMessageDialog(null, "OK!", null, JOptionPane.INFORMATION_MESSAGE);
			   try
			   {
				   //File file = new File(MutationSystem.RAW_TEST_CASE_PATH);
				   File file = new File(path);
				   
				   if(!file.exists())
				   {
					   System.out.println("Test cases do not exist!");
					   f.dispose();
					   JOptionPane.showMessageDialog(null,
                               "Test cases do not exist!", null, JOptionPane.ERROR_MESSAGE);
				   }else{//文件如果存在则将文件的名字记录到realtestcasenames中
					   File[] array = file.listFiles();
					   file = null;
					   //存放存放执行脚本的名字
					   ArrayList<String> realtestcasenames = new ArrayList<String>();
					   for(int i = 0; i < array.length; i++) {
						   realtestcasenames.add(array[i].getName());
					   }
					   array = null;
					   //获取存在一个执行脚本的名字文件
					   File testcasefile = new File(path + "\\" + realtestcasenames.get(0));
					   File[] javafile = testcasefile.listFiles();
					   //获取执行脚本的名字
					   testcasefilename = javafile[0].getName();
					   String b[] = testcasefilename.split("\\.");
					   //执行脚本去掉后缀的名字
					   testcasename = b[0];
					   
					   File targetprogramfile = new File(srcpath);
					   File[] targetprogram = targetprogramfile.listFiles();
					   //获取原始文件的名字
					   targetprogramfilename = targetprogram[0].getName();
					   String a[] = targetprogramfilename.split("\\.");
					   //原始文件去掉后缀的名字
					   targetprogramname = a[0];
					   
					   //Ϊprogressbar�ṩ�����Ϣ
					   f.progressBar.setMaximum(realtestcasenames.size());
					   f.progressBar.setMinimum(0);
					   f.progressBar.setValue(0);
					   f.progressBar.setString("0/" + realtestcasenames.size());
					   f.progressBar.setStringPainted(true);
					   f.repaint();
					   
					   //read all traditional mutants
					   allTraditionalMutants = new Vector();			   
					   Vector v = new Vector();
					   File file1 = new File(MutationSystem.TRADITIONAL_MUTANT_PATH, "method_list");
					   FileReader reader1 = new FileReader(file1);
					   BufferedReader reader = new BufferedReader(reader1);
					   String methodSignatureOfMutants = reader.readLine();
					         
					   while (methodSignatureOfMutants != null)
					   {
					       File mutant_dir = new File(MutationSystem.TRADITIONAL_MUTANT_PATH + "/" + methodSignatureOfMutants);
//					       System.out.println(MutationSystem.TRADITIONAL_MUTANT_PATH + "/" + methodSignatureOfMutants);
					       String[] mutants = mutant_dir.list(new MutantDirFilter());
					       for (int i=0; i<mutants.length; i++)
					       {
					           v.add(mutants[i]);
					       }
					       mutants = null;
					       methodSignatureOfMutants= reader.readLine();
					    }
					    reader.close();
					    reader1.close();
					    allTraditionalMutants = v;
					    
					    //read all class mutants
						allClassMutants = new Vector();			   
						Vector v1 = new Vector();
						//setMutantPath();
						File file2 = new File(MutationSystem.CLASS_MUTANT_PATH);
						
						if(!file2.exists()){
							throw new NoMutantDirException();
						}
						//mutantDirectoryMatch the names of mutants
						String[] mutantDirectories = file2.list(new MutantDirFilter());
						for(int flag = 0; flag < mutantDirectories.length;flag ++){
							v1.add(mutantDirectories[flag]);
						}
						
						allClassMutants = v1;
						
						allMutants = new Vector();
						for(int i = 0; i < allTraditionalMutants.size();i++){
							allMutants.add(allTraditionalMutants.get(i));
						}
						
						
						for(int i = 0; i < allClassMutants.size();i++){
						   if (allClassMutants.size() == 0)
						      break;
						   else
							allMutants.add(allClassMutants.get(i));
						}


					    XSSFWorkbook workbook;
					    //记录信息到excel中
					    ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
					    
						XSSFSheet spreadsheet;
						String fileName = "results";
						String fileType = "xlsx";
						
						String mutant_score = null;
						
						int startPosition = 0;
						int endPosition = 0;
						int interrecordGap = 2000;
						int fileSequenceNum = 0;


						//测试执行的部分
					    for(int i = 0; i < realtestcasenames.size() ; i++)
					    {
					    	
					    	Date date1 = new Date();
					    	workbook = new XSSFWorkbook();
					    	spreadsheet = workbook.createSheet("result");
	  
						    //复制一个执行脚本到src文件下
						    copyFile(path + "\\" + realtestcasenames.get(i) + "\\" + testcasefilename,srcpath + "\\" + testcasefilename);
						    //在cmd中执行的命令
						    String cmdStr = "javac -cp .;G:\\HOME_JAR\\junit\\junit.jar;G:\\HOME_JAR\\junit\\org.hamcrest.core.jar G:\\mujava\\src\\*.java";
						    try{
							    Process process = Runtime.getRuntime().exec(cmdStr);
							    //等待将程序编译完
							    InputStream in = process.getInputStream();
							    while(in.read() != -1){
								   System.out.println(in.read());
							    }
							    in.close();
							    process.destroy();
						    }catch(Exception e1){
							    e1.printStackTrace();
						    }
						   
						    //分别将源程序以及执行脚本的编译文件放到对应的class文件中
						    copyFile(srcpath + "\\" + testcasename + ".class",testcaseclasspath + "\\" + testcasename + ".class");
						    deleteFile(srcpath + "\\" + testcasename + ".class");
						   
						    File src = new File(srcpath);
						    // create new filename filter
					        FilenameFilter filter = new FilenameFilter() {
						        @Override
						        public boolean accept(File dir, String name) {
						           if(name.lastIndexOf('.')>0)
						           {
						              // get last index for '.' char
						              int lastIndex = name.lastIndexOf('.');
						                  
						              // get extension
						              String str = name.substring(lastIndex);
						                  
						              // match path name extension
						              if(str.equals(".class"))
						              {
						                 return true;
						              }
						           }
						            return false;
						       }
					       };

						   String [] srcFileNames = src.list(filter);
						   for(int flag = 0; flag < srcFileNames.length;flag ++){
							   copyFile(srcpath + "\\" + srcFileNames[flag],srcclasspath + "\\" + srcFileNames[flag]);
							   deleteFile(srcpath + "\\" + srcFileNames[flag]);
						   }
						   src = null;
						   filter = null;

						   // check if the customized timeout is used
						   // added by Lin, 05/23/2015
							if (isCustomizedTimeout) {
								try {
									timeout_secs = 1000*Integer.parseInt(timeoutTextField.getText());
									// what if a negative or zero, set it to 3000
									if (timeout_secs <= 0) {
										timeout_secs = 3000;
									}

								} catch (NumberFormatException ex) {
									// if not a number, set to be 3000
									timeout_secs = 3000;
								}
							}
							
							// class name whose mutants are executed
						    Object targetClassObj = classCB.getSelectedItem();
						    //String targetClassObjName = targetClassObj.toString();
						    // class name whose mutants are executed
						    Object methodSignature = methodCB.getSelectedItem();
						      
						    if (methodSignature == null) 
						    	methodSignature = "All method";
						    
						    //refresh the content of testCB
						    String[] t_list = getTestSetNames();
						    testCB = new JComboBox(eraseExtension(t_list, "class"));
						    
						    // name of test suite to apply
						    Object testSetObject  = testCB.getSelectedItem();
						    //String targetTestSetObjectName = testSetObject.getClass().getName();
						    TestResult test_result_traditional;
						    TestResult test_result_class;
						    
						    Map<String,String> testResultOfOriginalProgram = null;
						    TestExecuter test_engine;

						    if((targetClassObj != null) && (testSetObject != null))
						    {
						    	
						        String targetClassName = targetprogramname;
						        String testSetName = testcasename;

						        test_engine = new TestExecuter(targetClassName);
						        test_engine.setTimeOut(timeout_secs);

						        // First, read (load) test suite class.
						        test_engine.readTestSet(testSetName);

						        test_result_traditional = new TestResult();
						        test_result_class = new TestResult();
				    
						        try
						        {
						           if (onlyClassButton.isSelected())
						           {
						              test_engine.computeOriginalTestResults();
						              //get the result of original program
						              testResultOfOriginalProgram = test_engine.getOriginalResults();
						              
						              test_result_traditional = null;
						              test_result_class = test_engine.runClassMutants();
						              //showResult(test_result, cResultTable, cKilledList, cLiveList);
						              test_engine = null;
						              
						           } 
						           else if (onlyTraditionalButton.isSelected())
						           {
						              test_engine.computeOriginalTestResults();
						              //get the result of original program
						              testResultOfOriginalProgram = test_engine.getOriginalResults();
						              
						              test_result_traditional = test_engine.runTraditionalMutants(methodSignature.toString());
						              test_result_class = null;
						              //showResult(test_result, tResultTable, tKilledList, tLiveList);
						              test_engine = null;
						           }
						           else if (bothButton.isSelected())
						           {
						              test_engine.computeOriginalTestResults();
						              //get the result of original program
						              testResultOfOriginalProgram = test_engine.getOriginalResults();
						              test_result_traditional = test_engine.runTraditionalMutants(methodSignature.toString());
						              test_result_class = test_engine.runClassMutants();
						              test_engine = null;
						           }
						        } 
						        catch (NoMutantException e1)
						        {
						        	e1.printStackTrace();
						        } 
						        catch (NoMutantDirException e2)
						        {
						        	e2.printStackTrace();
						        }


								//分别记录原始变异体与类变异体个杀死的数量
								int killedClassMutantNum = test_result_class.killed_mutants.size();
								int killedTraditionalMutantNum = test_result_traditional.killed_mutants.size();
                               //分别记录原始变异体与类变异体剩下的数量
								int livedClassMutantNum = test_result_class.live_mutants.size();
								int livedTraditionalMutantNum = test_result_traditional.live_mutants.size();
                               //分别记录杀死的总数目以及活着的总数目
								int killedAllMutantNum = killedClassMutantNum + killedTraditionalMutantNum;
								int livedAllMutantNum = livedClassMutantNum + livedTraditionalMutantNum;
								//计算总的个数
								int total = killedAllMutantNum + livedAllMutantNum;
								//计算变异得分
								double mutant_score_raw = (double)( killedAllMutantNum * 100) /(killedAllMutantNum + livedAllMutantNum);

								//记录所有杀死的变异体
								Vector allKilledMutants = new Vector();
								for(int m = 0; m < test_result_traditional.killed_mutants.size(); m ++){
									allKilledMutants.add(test_result_traditional.killed_mutants.get(m));
								}
								
								for(int m = 0; m < test_result_class.killed_mutants.size(); m ++){
									allKilledMutants.add(test_result_class.killed_mutants.get(m));
								}

								//记录所有活着的变异体
								Vector allLivedMutants = new Vector();
								for(int m = 0; m < test_result_traditional.live_mutants.size(); m ++){
									allLivedMutants.add(test_result_traditional.live_mutants.get(m));
								}
								for(int m = 0; m < test_result_class.live_mutants.size(); m ++){
									allLivedMutants.add(test_result_class.live_mutants.get(m));
								}
								test_result_class = null;
								test_result_traditional = null;
								
								mutant_score = new java.text.DecimalFormat("#.00").format(mutant_score_raw);

								ArrayList<String> temp;

								//判断测试结果
								ArrayList<String> currTCResult = new ArrayList<String>();
								currTCResult.add(realtestcasenames.get(i));
								if(testResultOfOriginalProgram.get(testcasename).equals("pass")){
									currTCResult.add(String.valueOf(0));
								}else{
									currTCResult.add(String.valueOf(1));
								}
								
								for(int k = 0; k < allMutants.size(); k++){
									String mutantName = allMutants.get(k).toString();
									if(allKilledMutants.size()>0){
										for(int m = 0; m < allKilledMutants.size();m++){
											if(mutantName.equals(allKilledMutants.get(m).toString()))
											{
												currTCResult.add(String.valueOf(1));
												break;
											}
											if(m == allKilledMutants.size() - 1)
											{
												currTCResult.add(String.valueOf(0));
											}
										}
									}else{
										currTCResult.add(String.valueOf(0));
									}
									
									//����������һ��mutant�Ƿ�ɱ���ļ�¼��������һ��cell���mutant score
									if(k == allMutants.size() - 1){
										currTCResult.add(mutant_score);
									}
								}
								resultList.add(currTCResult);

								if(((i+1)%interrecordGap == 0)||(i + 1 == realtestcasenames.size()))
								{
									//��ǰ�����¼λ��
									endPosition = i;
									fileSequenceNum ++;
									//fileSequenceNum = (i + 1)/interrecordGap;
									
									//д��һ��
									XSSFRow row = spreadsheet.createRow(0);
									//Ϊ��ͷд�����������  
									row.createCell(0).setCellValue("");
									row.createCell(1).setCellValue("original result");
									for(int j = 0; j < killedAllMutantNum + livedAllMutantNum; j++)
									{
										row.createCell(j + 2).setCellValue(allMutants.get(j).toString());
										//��������һ��mutant������ʱ������������һ��cell������Ϊmutant score
										if(j == killedAllMutantNum + livedAllMutantNum -1){
											row.createCell(j + 2 + 1).setCellValue("Mutation Score");
										}
									}
									
									for(int flag = startPosition; flag <= endPosition; flag ++){
										row = spreadsheet.createRow(flag%interrecordGap + 1);
										row.createCell(0).setCellValue(resultList.get(flag).get(0));
										
										for(int position = 1;position < resultList.get(flag).size() - 1;position++){
											row.createCell(position).setCellValue(Integer.valueOf(resultList.get(flag).get(position)));
										}
										row.createCell(resultList.get(flag).size()-1).setCellValue(Double.valueOf(resultList.get(flag).get(resultList.get(flag).size()-1)));
									}
									
									OutputStream outputstream = new FileOutputStream(homepath + "\\" + fileName + fileSequenceNum + "." + fileType);
									workbook.write(outputstream);
									outputstream.close();
									
									startPosition = i + 1;//���startposition��Ϊǰһ��endPosition����һλ
									workbook.close();
									spreadsheet = null;
									
									allLivedMutants = null;
									allKilledMutants = null;
									testResultOfOriginalProgram = null;
									testSetObject = null;
									targetClassObj = null;
									methodSignature = null;
								}
						    } 
						    else
						    {
						        System.out.println(" [Error] Please check test target or test suite ");
						    }
						    //JOptionPane.showMessageDialog(null, "Test Case :" + realtestcasenames.get(i) + " .Wait A Minute!", null, JOptionPane.INFORMATION_MESSAGE);
						    int progress = i + 1;
						    f.progressBar.setString(progress + "//" + realtestcasenames.size());
						    f.progressBar.setValue(progress);
						    //f.repaint();
						    
						    Date date2 = new Date();
						    long timestamp = date2.getTime() - date1.getTime();
						    System.out.println("time = " + Long.toString(timestamp));
						    System.out.println("mutant score = " + mutant_score);

						    System.out.println("****************************************************************************************************************");
						    
						    date1 = null;
						    date2 = null;
					   }
					   
				   }
			   }
			   catch(NoMutantDirException nmde){
				   nmde.printStackTrace();
			   }
			   catch(IOException ex)
			   {
				  ex.printStackTrace(); 
			   } 
			   
		   }
	   };
	   
	   Thread thread = new Thread(r);
	   thread.start();

   }
   
   public void copyFile(String oldPath, String newPath)
   {
	   try
	   {
		   FileInputStream fis = new FileInputStream(oldPath);
		   BufferedInputStream bufis = new BufferedInputStream(fis);
		   FileOutputStream  fos = new FileOutputStream(newPath);
		   BufferedOutputStream bufos = new BufferedOutputStream(fos);
		   
		   int len = 0;
		   while((len = bufis.read()) != -1){
			   bufos.write(len);
		   }

		   bufis.close();
		   bufos.close();
		   fis.close();
		   fos.close();
	   }
	   catch(IOException ex)
	   {
		   ex.printStackTrace();
	   }
   }
   
   public void deleteFile(String path){
	   
	   File f = new File(path);
	   if(f.exists()){
		   f.delete();
	   }
   }
   
   private void showEmptyResult(JTable table, JList killed_list, JList live_list)
   {
      // Show the result on resultTable
      ResultTableModel resultModel = (ResultTableModel)(table.getModel());
      resultModel.setValueAt( "  " + (new Integer(0)).toString() , 0 , 1 );   // live mutant
      resultModel.setValueAt( "  " + (new Integer(0)).toString() , 1 , 1 );   // killed mutant
      resultModel.setValueAt( "  " + (new Integer(0)).toString() , 2 , 1 );   // total
      resultModel.setValueAt( "  " + " - %" , 3 , 1 );   // mutant score

      killed_list.setListData(new String[0]);
      live_list.setListData(new String[0]);
      killed_list.repaint();
      live_list.repaint();
   }

   private void showResult(TestResult tr, JTable table,JList killed_list, JList live_list)
   { 
      int i;
      // Mutation Score
      if (tr == null)
    	 System.out.println("-----------");
      int killed_num = tr.killed_mutants.size();
      int live_num = tr.live_mutants.size();

      if ((killed_num + live_num) == 0)
      {
         showEmptyResult(table, killed_list, live_list);
         System.out.println("[Notice] There are no mutants to apply");
         return;
      }

      Float mutant_score = new Float((killed_num * 100) / (killed_num + live_num));

      // Show the result on resultTable
      ResultTableModel resultModel = (ResultTableModel)(table.getModel());
      resultModel.setValueAt( "  " + (new Integer(live_num)).toString() , 0 , 1 );   // live mutant
      resultModel.setValueAt( "  " + (new Integer(killed_num)).toString() , 1 , 1 ); // killed mutant
      resultModel.setValueAt( "  " + (new Integer(live_num+killed_num)).toString() , 2 , 1 );   //total
      resultModel.setValueAt( "  " + mutant_score.toString() + "%" , 3 , 1 );   // mutant score
 
      // List of Killed, Live Mutants

      String[] killed_mutants = new String[killed_num];
      String[] live_mutants = new String[live_num];
      for (i=0; i<killed_num; i++)
      {
         killed_mutants[i] = tr.killed_mutants.get(i).toString();
      } 
      for (i=0; i<live_num; i++)
      {
         live_mutants[i] = tr.live_mutants.get(i).toString();
      }

      killed_list.setListData(killed_mutants);
      live_list.setListData(live_mutants);
      killed_list.repaint();
      live_list.repaint();
   }

   void changeTimeOut()
   {
      String tstr = timeCB.getSelectedItem().toString();
      if (tstr.equals("3 seconds"))
      {
    	  timeoutTextField.setEnabled(false);
    	  timeoutTextField.setText("3");
    	  isCustomizedTimeout = false;
         timeout_secs = 3000;
      } 
      else if (tstr.equals("5 seconds"))
      {
    	  timeoutTextField.setEnabled(false);
    	  timeoutTextField.setText("5");
    	  isCustomizedTimeout = false;
         timeout_secs = 5000;
      }
      else if (tstr.equals("10 seconds"))
      {
    	  timeoutTextField.setEnabled(false);
    	  timeoutTextField.setText("10");
    	  isCustomizedTimeout = false;
         timeout_secs = 10000;
	  }
      else if (tstr.equals("Other"))
      {
    	  timeoutTextField.setEnabled(true);
    	  isCustomizedTimeout = true;
    	  //timeout_secs = customized_time;
      }
   } 

   void showTraditionalMutants()
   {
      try
      {
         Vector v = new Vector();
         //setMutantPath();
         File f = new File(MutationSystem.TRADITIONAL_MUTANT_PATH, "method_list");
         FileReader r = new FileReader(f);
         BufferedReader reader = new BufferedReader(r);
         String methodSignature = reader.readLine();
         
         while (methodSignature != null)
         {
            File mutant_dir = new File(MutationSystem.TRADITIONAL_MUTANT_PATH + "/" + methodSignature);
            String[] mutants = mutant_dir.list(new MutantDirFilter());
            for (int i=0; i<mutants.length; i++)
            {
               v.add(mutants[i]);
            }
            mutants = null;
            methodSignature = reader.readLine();
         }
         reader.close();
         int mutant_num = v.size();
         String[] mutants = new String[mutant_num];
         for (int i=0; i<mutant_num; i++)
         {
            mutants[i]=v.get(i).toString();
         }
         showGeneratedTraditionalMutantsNum(mutants);
      } catch (Exception e)
      {
         System.err.println("Error in update() in TraditioanlMutantsViewerPanel.java");
      }
   }

   void showTraditionalMutants(String methodSignature)
   {
      File mutant_dir = new File(MutationSystem.TRADITIONAL_MUTANT_PATH + "/" + methodSignature);
      String[] mutants = mutant_dir.list(new MutantDirFilter());
      showGeneratedTraditionalMutantsNum(mutants);
   }

   void changeMethodContents()
   {
      Object item = methodCB.getSelectedItem();
      if (item == null) 
         return;
      
      String methodSignature = item.toString();
      if (methodSignature == null) 
         return;
  
      if (methodSignature.equals("All method"))
      {
         showTraditionalMutants();
      } 
      else
      {
         showTraditionalMutants(methodSignature);
      }
   }

   void changeContents()
   {
      target_dir = classCB.getSelectedItem().toString();
      MutationSystem.setJMutationPaths(target_dir);

      MutationSystem.MUTANT_PATH = MutationSystem.CLASS_MUTANT_PATH;

      File mutant_dir = new File(MutationSystem.CLASS_MUTANT_PATH);
      String[] mutants = mutant_dir.list(new MutantDirFilter());
      showGeneratedClassMutantsNum(mutants);

      MutationSystem.MUTANT_PATH = MutationSystem.TRADITIONAL_MUTANT_PATH;

      showTraditionalMutants();

      methodCB.removeAllItems();
      methodCB.addItem("All method");
      try
      {
         File f = new File(MutationSystem.TRADITIONAL_MUTANT_PATH, "method_list");
         FileReader r = new FileReader(f);
         BufferedReader reader = new BufferedReader(r);
         String str = reader.readLine();
         while(str != null)
         {
            methodCB.addItem(str);
            str = reader.readLine();
         }
         reader.close();
      } catch (java.io.FileNotFoundException fnfe)
      {
      } catch (Exception e)
      {
         System.err.println("error at updateClassComboBox() in RunTestPanel");
      }
      this.repaint();
   }

   void showGeneratedClassMutantsNum(String[] name)
   {
      if (name != null)
      {
         int[] num = new int[MutationSystem.cm_operators.length];
         for (int i=0; i<MutationSystem.cm_operators.length; i++)
         {
            num[i] = 0;
         }
         
         for (int i=0; i<name.length; i++)
         {
            for (int j=0; j<MutationSystem.cm_operators.length; j++)
            {
               if (name[i].indexOf(MutationSystem.cm_operators[j] + "_") == 0)
               { 
                  num[j]++;
               }
            }
         }

         int total = 0;
         CMSummaryTableModel myModel = (CMSummaryTableModel)(cmTable.getModel());
         
         for (int i=0; i<MutationSystem.cm_operators.length; i++)
         {
            myModel.setValueAt(new Integer(num[i]), i, 1);
            total = total + num[i];
         }
         cmTotalLabel.setText("Total : " + total);
      }
   }

/*  void updateContents(String methodSignature){
    setMutantPath();
    File mutant_dir = new File(getMutantPath()+"/"+methodSignature);
    String[] mutants = mutant_dir.list(new MutantDirFilter());
    showGeneratedMutantsNum(mutants);
    mList.setListData(mutants);
    mList.repaint();
    clearSourceContents();
    showOriginal();
  }
 */
   void showGeneratedTraditionalMutantsNum(String[] name)
   {
      if (name != null)
      {
         int[] num = new int[MutationSystem.tm_operators.length];
         for (int i=0; i<MutationSystem.tm_operators.length; i++)
         {
            num[i] = 0;
         }
         
         for (int i=0; i<name.length; i++)
         {
            for (int j=0; j<MutationSystem.tm_operators.length; j++)
            {
               if (name[i].indexOf(MutationSystem.tm_operators[j] + "_") == 0)
               { 
            	  num[j]++;
               }
            }
         }
 
         int total = 0;
         TMSummaryTableModel myModel = (TMSummaryTableModel)(tmTable.getModel());
         for (int i=0; i<MutationSystem.tm_operators.length; i++)
         {
            myModel.setValueAt(new Integer(num[i]), i, 1);
            total = total + num[i];
         }
         tmTotalLabel.setText("Total : " + total);
      }
   }
  
   private void setResultTableSize(JTable table)
   {
      TableColumn column = null;

      for (int i = 0; i < table.getColumnCount(); i++) 
      {
         column = table.getColumnModel().getColumn(i);
         switch(i)
         {
            case 0 :  column.setMaxWidth(110);
                      column.setPreferredWidth(110);
                      break;
            case 1 :  column.setMaxWidth(50);
                      break;
         }
      };
   }

//   private void setSPSize(JScrollPane p)
//   {
//      p.setPreferredSize(new Dimension(100, 410));
//      p.setMaximumSize(new Dimension(100, 410));
//      p.setMinimumSize(new Dimension(100, 410));
//   }
   private void setSPSize(JScrollPane p)
   {
      p.setPreferredSize(new Dimension(100, 310));
      p.setMaximumSize(new Dimension(100, 310));
      p.setMinimumSize(new Dimension(100, 310));
   }


   /** Listens to the radio buttons. */
   @Override
public void actionPerformed(ActionEvent e) 
   {
      String cmd = e.getActionCommand();
      if (cmd.equals("CLASS"))
      {  
    	 // do nothing
      }
      else if (cmd.equals("TRADITIONAL"))
      {
    	 // do nothing
      }else if (cmd.equals("BOTH"))
      {
    	 // do nothing
      }
   }

   protected void adjustSummaryTableSize(JTable table, AbstractTableModel model) 
   {
      TableColumn column = null;

      for (int i = 0; i < table.getColumnCount(); i++) 
      {
         column = table.getColumnModel().getColumn(i);
         switch(i)
         {
            case 0 :
                  column.setPreferredWidth(60);
                  column.setMaxWidth(60);
                  break;
            case 1 :  
            	  column.setPreferredWidth(60);
                  column.setMaxWidth(60);
                  break;
         }
      }
   }
}


class ResultTableModel extends AbstractTableModel 
{
   private static final long serialVersionUID = 109L;

   String[] columnHeader = new String[]{" Operator "," value "};

   // This part is used during implementation and testing
   Object[][] data = {   {"  Live Mutants # ", ""} ,
   	                     {"  Killed Mutants # ",""},
                         {"  Total Mutants # ", ""},
                         {"  Mutant Score ", ""}};

  /**
   * AbstractTable Implementation �Լ�
   */
   @Override
public String getColumnName(int col)
   {
      return columnHeader[col];
   }

   @Override
public int getColumnCount() 
   {
      return columnHeader.length; 
   }

   @Override
public Object getValueAt(int row, int col) 
   {
      return data[row][col];
   }

   @Override
public int getRowCount() 
   {
      return data.length;
   }

  /*
   * JTable uses this method to determine the default renderer/
   * editor for each cell.  If we didn't implement this method,
   * then the last column would contain text ("true"/"false"),
   * rather than a check box.
   */

   @Override
public Class getColumnClass(int c) 
   {
      return getValueAt(0, c).getClass();
   }

  /*
   * Don't need to implement this method unless your table's
   * data can change.
   */
   @Override
public void setValueAt(Object value, int row, int col) 
   {
      data[row][col] = value;
      fireTableCellUpdated(row, col);
   }

   @Override
public boolean isCellEditable(int row, int col) 
   {
     //Note that the data/cell address is constant,
     //no matter where the cell appears onscreen.
      return false;
   }

}