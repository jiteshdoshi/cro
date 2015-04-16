package cro;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.FileDataSet;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;

/*
 * Algorithm to select best subset of informative genes using Chemical Reaction Optimization
 */

public class MainGUI {
	
	public static void main(String[] args) {
		
		try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    //UIManager.getSystemLookAndFeelClassName()
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				initComponent();
			}
		});		
	
	}// main method end

	public static void initComponent() {
	
		//frame
		frame=new JFrame("CRO based Gene Selection");
		frame.setSize(720, 520);
		frame.setLocation(350, 100);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//MenuBar
		menuBar=new JMenuBar();
		menuBar.setBackground(Color.gray);
		file=new JMenu();
		file.setText("File");
		menuBar.add(file);
		frame.setJMenuBar(menuBar);
		
		saveas=new JMenuItem();
		saveas.setText("Save");
		saveas.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc=new JFileChooser();
				int val=jfc.showSaveDialog(null);
				if(val==JFileChooser.APPROVE_OPTION){
					
				}
			}
		});
		file.add(saveas);
		
		
		close=new JMenuItem();
		close.setText("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				frame.dispose();
			}
		});
		file.add(close);
		
		
		
		help=new JMenu();
		help.setText("Help");
		menuBar.add(help);
		
		readme=new JMenuItem();
		readme.setText("Open Readme");
		readme.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Desktop dt=Desktop.getDesktop();
				System.out.println();
				try {
					dt.open(new File("README.txt"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		help.add(readme);
		
		
		//textArea : OutputStream
		textArea=new JTextArea();		
		textArea.setEditable(false);
		textArea.setVisible(true);
		textArea.setText("==========================================\n\n      Chemical Reaction Based Optimization for Gene Selection\n\n==========================================\n..Please upload your data file..\n");
		
		//JPanel		
		panel1=new JPanel();
		panel1.setSize(240, 473);
		panel1.setLocation(2, 2);
		panel1.setBackground(new java.awt.Color(158, 157, 150));
        panel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
		panel1.setVisible(true);
		panel1.setLayout(null);
		
		
		inputPanel=new JLabel("Input panel");
		inputPanel.setForeground(new java.awt.Color(15, 7, 129));
		inputPanel.setLocation(30, 5);
		inputPanel.setSize(180, 40);
		inputPanel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		inputPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		panel1.add(inputPanel);
		
			
		upload=new JLabel("Upload File");
		upload.setSize(100, 30);
		upload.setLocation(10, 60);
		upload.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		upload.setToolTipText("Upload data file in .libsvm, .csv or .arff format");
		panel1.add(upload);
		
		uploadButton=new JButton("Upload");
		panel1.add(uploadButton);
		uploadButton.setSize(100, 30);
		uploadButton.setLocation(120, 60);
		uploadButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		uploadButton.addActionListener(new ActionListener() {
		
		
			@Override
		public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				fname=null;
				 JFileChooser jfc=new JFileChooser();
			        jfc.setDialogTitle("Open Data File");
			        int returnVal=jfc.showOpenDialog(null);
			        if(returnVal==JFileChooser.APPROVE_OPTION){
			        fname=jfc.getSelectedFile().getAbsolutePath();
			        Algorithm.filename=fname;
			        System.out.println(fname+" slected\nClick the above button \"Click to run algorithm\" to initialize gene selection");}
			        else System.out.println("File Opening Cancelled");
			}
		});
		
		//SVM c param
		cLabel = new JLabel("SVM param -c");
		cLabel.setSize(120, 30);
		cLabel.setLocation(10, 100);
		cLabel.setToolTipText("c (cost) parameter for SVM kernel");
		cLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(cLabel);
		
		cVal=new JTextField("50");
		cVal.setSize(80, 30);
		cVal.setLocation(140, 100);
		panel1.add(cVal);
		c=cVal.getText();
	    DocumentListener dlc=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					c=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					c=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					c=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		cVal.getDocument().addDocumentListener(dlc);
		
		//SVM g param
		gLabel = new JLabel("SVM param -g");
		gLabel.setSize(120, 30);
		gLabel.setLocation(10, 130);
		gLabel.setToolTipText("g (gamma) parameter for SVM kernel");
		gLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(gLabel);
		
		gVal=new JTextField("0.02");
		gVal.setSize(80, 30);
		gVal.setLocation(140, 130);
		panel1.add(gVal);
		g=gVal.getText();
	    DocumentListener dlg=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					g=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					g=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					g=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		gVal.getDocument().addDocumentListener(dlg);
		
		subLabel = new JLabel("Subset Size");
		subLabel.setSize(120, 30);
		subLabel.setLocation(10, 160);
		subLabel.setToolTipText("Subset size of the entities");
		subLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(subLabel);
		
		subVal=new JTextField("20");
		subVal.setSize(80, 30);
		subVal.setLocation(140, 160);
		panel1.add(subVal);
		sub=subVal.getText();
	    DocumentListener dlsub=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					sub=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					sub=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					sub=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		subVal.getDocument().addDocumentListener(dlsub);
		
		initPopLabel = new JLabel("Initial Population");
		initPopLabel.setSize(120, 30);
		initPopLabel.setLocation(10, 190);
		initPopLabel.setToolTipText("Initial Population Size of the molecules");
		initPopLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(initPopLabel);
		
		initPopVal=new JTextField("100");
		initPopVal.setSize(80, 30);
		initPopVal.setLocation(140, 190);
		panel1.add(initPopVal);
		initPop=initPopVal.getText();
	    DocumentListener dlinitPop=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					initPop=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					initPop=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					initPop=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		initPopVal.getDocument().addDocumentListener(dlinitPop);
		
		enBuffLabel = new JLabel("Energy Buffer");
		enBuffLabel.setSize(120, 30);
		enBuffLabel.setLocation(10, 220);
		enBuffLabel.setToolTipText("Energy in Buffer");
		enBuffLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(enBuffLabel);
		
		enBuffVal=new JTextField("10000");
		enBuffVal.setSize(80, 30);
		enBuffVal.setLocation(140, 220);
		panel1.add(enBuffVal);
		enBuff=enBuffVal.getText();
	    DocumentListener dlenBuff=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					enBuff=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					enBuff=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					enBuff=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		enBuffVal.getDocument().addDocumentListener(dlenBuff);
		
		initKELabel = new JLabel("Initial KE");
		initKELabel.setSize(120, 30);
		initKELabel.setLocation(10, 250);
		initKELabel.setToolTipText("Initial Kinetic Energy of Molecules");
		initKELabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(initKELabel);
		
		initKEVal=new JTextField("10000");
		initKEVal.setSize(80, 30);
		initKEVal.setLocation(140, 250);
		panel1.add(initKEVal);
		initKE=initKEVal.getText();
	    DocumentListener dlinitKE=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					initKE=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					initKE=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					initKE=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		initKEVal.getDocument().addDocumentListener(dlinitKE);
		
		collRateLabel = new JLabel("Collision Rate");
		collRateLabel.setSize(120, 30);
		collRateLabel.setLocation(10, 280);
		collRateLabel.setToolTipText("Collision rate");
		collRateLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(collRateLabel);
		
		collRateVal=new JTextField("0.1");
		collRateVal.setSize(80, 30);
		collRateVal.setLocation(140, 280);
		panel1.add(collRateVal);
		collRate=collRateVal.getText();
	    DocumentListener dlcollRate=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					collRate=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					collRate=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					collRate=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		collRateVal.getDocument().addDocumentListener(dlcollRate);
		
		enLossLabel = new JLabel("EnergyLoss Rate");
		enLossLabel.setSize(120, 30);
		enLossLabel.setLocation(10, 310);
		enLossLabel.setToolTipText("Rate of loss of energy");
		enLossLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(enLossLabel);
		
		enLossVal=new JTextField("0.2");
		enLossVal.setSize(80, 30);
		enLossVal.setLocation(140, 310);
		panel1.add(enLossVal);
		enLoss=enLossVal.getText();
	    DocumentListener dlenLoss=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					enLoss=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					enLoss=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					enLoss=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		enLossVal.getDocument().addDocumentListener(dlenLoss);
		
		decompLabel = new JLabel("Decomp Threshold");
		decompLabel.setSize(120, 30);
		decompLabel.setLocation(10, 340);
		decompLabel.setToolTipText("Threshold for Decomposition");
		decompLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(decompLabel);
		
		decompVal=new JTextField("1500");
		decompVal.setSize(80, 30);
		decompVal.setLocation(140, 340);
		panel1.add(decompVal);
		decomp=decompVal.getText();
	    DocumentListener dldecomp=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					decomp=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					decomp=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					decomp=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		decompVal.getDocument().addDocumentListener(dldecomp);
		
		synthLabel = new JLabel("Synth Threshold");
		synthLabel.setSize(120, 30);
		synthLabel.setLocation(10, 370);
		synthLabel.setToolTipText("Threshold for Synthesis");
		synthLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(synthLabel);
		
		synthVal=new JTextField("10");
		synthVal.setSize(80, 30);
		synthVal.setLocation(140, 370);
		panel1.add(synthVal);
		synth=synthVal.getText();
	    DocumentListener dlsynth=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					synth=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					synth=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					synth=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		synthVal.getDocument().addDocumentListener(dlsynth);
		
		stepLabel = new JLabel("Step Size");
		stepLabel.setSize(120, 30);
		stepLabel.setLocation(10, 400);
		stepLabel.setToolTipText("Step Size");
		stepLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(stepLabel);
		
		stepVal=new JTextField("0.2");
		stepVal.setSize(80, 30);
		stepVal.setLocation(140, 400);
		panel1.add(stepVal);
		step=stepVal.getText();
	    DocumentListener dlstep=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					step=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					step=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					step=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		stepVal.getDocument().addDocumentListener(dlstep);
		
		maxGenLabel = new JLabel("Max Generations");
		maxGenLabel.setSize(120, 30);
		maxGenLabel.setLocation(10, 430);
		maxGenLabel.setToolTipText("Number of maximum generations (Iterations)");
		maxGenLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		panel1.add(maxGenLabel);
		
		maxGenVal=new JTextField("1000");
		maxGenVal.setSize(80, 30);
		maxGenVal.setLocation(140, 430);
		panel1.add(maxGenVal);
		maxGen=maxGenVal.getText();
	    DocumentListener dlmaxGen=new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					maxGen=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					maxGen=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				try {
					maxGen=e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength());
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		maxGenVal.getDocument().addDocumentListener(dlmaxGen);
		
		
		//Panel 2 components
		
		panel2=new JPanel();
		panel2.setSize(450, 473);
		panel2.setLocation(245, 2);
		panel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
		panel2.setVisible(true);
		panel2.setLayout(null);
		
		outputPanel=new JLabel("Output panel");
		outputPanel.setLocation(6, 5);
		outputPanel.setSize(438, 40);
		outputPanel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		outputPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
		panel2.add(outputPanel);
		
		//run button
		runButton=new JButton("Click to run algorithm");
		runButton.setSize(438, 35);
		runButton.setLocation(6, 57);
		panel2.add(runButton);
		
		runButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				graph.setEnabled(false);
				save.setEnabled(false);
				if(fname!=null){
				Algorithm al=new Algorithm(c, g, sub, initPop, enBuff, initKE, collRate, enLoss, decomp, synth, step, maxGen);
				try {
					al.run();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				}
				
				
				else {System.out.println("Please upload file..");}
				
			}
		});
		
		
		scrollPane=new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVisible(true);
		scrollPane.setBounds(6, 100, 438, 250);
		panel2.add(scrollPane);
		
		graph=new JButton("Show Graph");
		graph.setSize(140, 30);
		graph.setLocation(6, 420);
		graph.setToolTipText("Click to show Accuracy v/s iterations graph; Requires GNUPlot to be installed");
		graph.setEnabled(false);
		panel2.add(graph);
		
		graph.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				 JavaPlot p = new JavaPlot();
		            try {
						p.addPlot(new FileDataSet(new File("graph.dat")));
					} catch (NumberFormatException
							| ArrayIndexOutOfBoundsException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		            p.setTitle("Accuracy vs Iterations (for file: \""+fname.substring(fname.lastIndexOf("/")+1)+"\")");
		            p.getAxis("x").setLabel(maxGen+" iterations");
		            p.getAxis("y").setLabel("Libsvm accuracy");
		            PlotStyle stl = ((AbstractPlot) p.getPlots().get(0)).getPlotStyle();
		            stl.setStyle(Style.LINES);
		            
		            p.plot();
				
			}
		});
		
		save=new JButton("Save Files");
		save.setSize(140, 30);
		save.setToolTipText("Save log file, graph data file and graph image file for most recent session");
		save.setLocation(153, 420);
		save.setEnabled(false);
		panel2.add(save);
		
		
		exit=new JButton("Exit");
		exit.setSize(140, 30);
		exit.setLocation(300, 420);
		panel2.add(exit);
		
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				frame.dispose();
				
			}
		});
		
		frame.getContentPane().add(panel1);
		frame.getContentPane().add(panel2);
		frame.setVisible(true);
		frame.getContentPane().setVisible(true);
		
		PrintStream out= new PrintStream(new outputStreamer(textArea));
		System.setOut(out); 
		System.setErr(out);
	
		
		
	}
	
	

	static JFrame frame;
	static JTextArea textArea;
	static JScrollPane scrollPane;
	static JPanel panel1, panel2;
	static JMenuBar menuBar;
	static JMenu file, help;
	static JMenuItem close, saveas, readme, about;
	static JLabel inputPanel, outputPanel;
	static JLabel upload;
	static JButton uploadButton, runButton;
	static JLabel cLabel, gLabel, subLabel, initPopLabel, enBuffLabel, initKELabel, collRateLabel, enLossLabel, decompLabel, synthLabel, stepLabel, maxGenLabel;
	static JTextField cVal, gVal, subVal, initPopVal, enBuffVal, initKEVal, collRateVal, enLossVal, decompVal, synthVal, stepVal, maxGenVal;
	static String c, g, sub, initPop, enBuff, initKE, collRate, enLoss, decomp, synth, step, maxGen;
	static String fname;
	static JButton graph, save, exit;
}
