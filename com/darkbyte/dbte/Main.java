//Package Declaration
package com.darkbyte.dbte;

//Imports from libraries: [Java Default Libraries, Guava]
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.google.common.io.Files;

//Main class
public class Main {
	//Creating a File to store the current file being used
	private static File currentFile = null;
	//The text area for the text area
	private static JTextArea textArea = new JTextArea();
	
	//Main method
	public static void main(String[] args) {
		//Creating the window
		Window window = new Window(1280, 720, "DBTE Text Editor", true);
		//Setting the text area editable
		textArea.setEditable(true);
		
		//Creating the menu bar
		JMenuBar menuBar = new JMenuBar();
		//Creating the button to create a new file
		JMenu newButton = new JMenu("New");
		/*
		This uses the DBTEMenuListener class which is an object of the MenuListener interface.
		This class has a constructor of Runnable of which I created an anonymous inner class.
		This anonymous inner class contains a method called run(), this is used in the listener and is called whenever the button is selected.
		*/
		newButton.addMenuListener(new DBTEMenuListener(new Runnable() {

			@Override
			public void run() {
				if(!textArea.getText().isEmpty() || currentFile != null) {
					int confirmVal = JOptionPane.showConfirmDialog(null, "Are you sure you want to close the current file?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
					
					if(confirmVal == JOptionPane.YES_OPTION) {
						currentFile = null;
						textArea.setText("");
					}
				}
			}
		}));
		
		JMenu openButton = new JMenu("Open");
		openButton.addMenuListener(new DBTEMenuListener(new Runnable() {
			
			@Override
			public void run() {
				JFileChooser chooser = new JFileChooser();
				FileReader reader = new FileReader();
				
				chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				
				int resultVal = chooser.showOpenDialog(null);
				
				if(resultVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					
					if(selectedFile.exists()) {
						currentFile = selectedFile;
						
						try {
							reader.openFile(selectedFile.getCanonicalPath());
							textArea.setText(reader.read());
							reader.closeFile();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null, "An unexpected error occured!", "Error", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(null, "The selected file doesn't exist!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
		}));
		
		JMenu saveButton = new JMenu("Save");
		saveButton.addMenuListener(new DBTEMenuListener(new Runnable() {

			@Override
			public void run() {
				JFileChooser chooser = new JFileChooser();
				FileWriter writer = new FileWriter();
				
				chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				
				int resultVal = chooser.showSaveDialog(null);
				
				if(resultVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					
					if(selectedFile.exists()) {
						currentFile = selectedFile;
						int confirmVal = JOptionPane.showConfirmDialog(null, "Are you sure you want to overwrite this file?", "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						if(confirmVal == JOptionPane.OK_OPTION) {
							try {
								writer.openFile(selectedFile.getCanonicalPath());
								writer.write(textArea.getText());
								writer.closeFile();
							} catch (IOException e) {
								JOptionPane.showMessageDialog(null, "An unexpected error occurred!", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					} else {
						try {
							selectedFile.createNewFile();
							writer.openFile(selectedFile.getCanonicalPath());
							writer.write(textArea.getText());
							writer.closeFile();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null, "The file couldn't be created!", "Error", JOptionPane.ERROR_MESSAGE);
						}
						
						currentFile = selectedFile;
					}
				}
			}
			
		}));
		
		menuBar.add(newButton);
		menuBar.add(openButton);
		menuBar.add(saveButton);
		
		window.setJMenuBar(menuBar);
		window.add(new JScrollPane(textArea), BorderLayout.CENTER);
		window.repaint();
	}
	
	public static File getCurrentFile() {
		return currentFile;
	}
}

class DBTEMenuListener implements MenuListener {
	
	public DBTEMenuListener(Runnable onClick) {
		this.onClick = onClick;
	}
	
	private Runnable onClick;

	@Override
	public void menuCanceled(MenuEvent e) {
		
	}

	@Override
	public void menuDeselected(MenuEvent e) {
		
	}

	@Override
	public void menuSelected(MenuEvent e) {
		onClick.run();
	}
}

class Window extends JFrame {
	private static final long serialVersionUID = 1L;

	public Window(int width, int height, String title, boolean resizable) {
		setTitle(title);
		setSize(width, height);
		setResizable(resizable);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}

class FileWriter {
	private Formatter formatter;
	
	public void openFile(String file) throws FileNotFoundException {
		formatter = new Formatter(file);
	}
	
	public void closeFile() {
		formatter.close();
	}
	
	public void write(String toWrite) {
		formatter.format("%s", toWrite);
	}
}

class FileReader {
	
	private String file;
	
	public void openFile(String file) {
		this.file = file;
	}
	
	public String read() throws IOException {
		return Files.toString(new File(this.file), Charset.forName("UTF-8"));
	}
	
	public void closeFile() {
		this.file = "";
	}
}
