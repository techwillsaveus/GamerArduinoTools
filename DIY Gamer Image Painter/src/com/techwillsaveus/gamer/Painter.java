/**
 * @author George Profenza for TWSU
 **/

package com.techwillsaveus.gamer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import processing.app.Editor;
import processing.app.tools.Tool;
import processing.core.PApplet;
import processing.core.PImage;

public class Painter implements Tool,PainterListener,ComponentListener,AncestorListener {
	private static Editor editor;
	private PainterApp app;
	private JFrame appFrame;
	private boolean isAppOpen;
	
	private JOptionPane optionPane;
	private JTextField nameField;
	private JDialog dialog;
	
	public void init(Editor editor) {
	    Painter.editor = editor;
	}
	private void setupSketch(){
		app = new PainterApp();
		appFrame = new JFrame(getMenuTitle());
		appFrame.setIconImage(new ImageIcon(app.loadBytes("twsu.png")).getImage());
		PImage bg = app.loadImage("Gamer_painter_screen_background.png");
		appFrame.setLayout(new GridLayout());
		appFrame.setPreferredSize(new Dimension(bg.width, bg.height+20));
		appFrame.add(app);
		app.init();
		app.setListener(this);
		appFrame.pack();
		appFrame.setResizable(false);
		appFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		appFrame.setVisible(true);
		  
		final JPanel panel = new JPanel();
		nameField = new JTextField(10);
		nameField.addAncestorListener(this);
	    JPanel namePanel = new JPanel(new BorderLayout());
	    namePanel.add(new JLabel("name your creation"),BorderLayout.PAGE_START);
        namePanel.add(nameField, BorderLayout.CENTER);
        optionPane = new JOptionPane(namePanel,JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        dialog = optionPane.createDialog(panel, "Copy Gamer image to clipboard");
        dialog.setModal(false);
        dialog.addComponentListener(this);
	}
	
	public String getMenuTitle() {
		return "DIY Gamer Image Painter";
	}

	public void run() {
		if(!isAppOpen){
			setupSketch();
			isAppOpen = true;
		}else appFrame.setVisible(true);
	}
	private void setCode(String code){
		try{
			editor.startCompoundEdit();
			String editorCode = editor.getText();
			if(editorCode.length() >= 0){
				editor.setSelection(0, editorCode.length());//overwrite
			    editor.setSelectedText(code);
			    editor.stopCompoundEdit();
			}
		}catch(Exception e){
			System.out.println("caught error inserting code:\n");
			e.printStackTrace();
		}
	}
	public void onPublishCode(String code) {
		setCode(code);
	}
	public void onCopyCode(){
		nameField.setText("");
		dialog.setVisible(true);
	}
	public void onUploadCode(String code){
		setCode(code);
		editor.handleExport(false); 
	}
	public static String getSketchCode(){
		return editor.getText();
	}
	//ComponentListener implementation, check result in the non modal name dialog
	public void componentHidden(ComponentEvent e) {
		 Integer value = (Integer) optionPane.getValue();
         if (value == null) {
            return;
         }
         if (value == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            if(name != null) {
	  		    StringSelection stringSelection = new StringSelection (PApplet.join(app.getCode(name),"\n"));
	  		    Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
	  		    clpbrd.setContents (stringSelection, null);
	  		    System.out.println("Your code is now copied to your clipboard!");
  		  }
         }
	}
	public void componentMoved(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	//AncestorListener implementation, make sure the nameField gets focused
	public void ancestorRemoved(AncestorEvent e) {}
	public void ancestorMoved(AncestorEvent e) {}
	public void ancestorAdded(AncestorEvent e) {
		JComponent component = e.getComponent();
		component.requestFocusInWindow();
		component.removeAncestorListener( this );
	}
	
}
