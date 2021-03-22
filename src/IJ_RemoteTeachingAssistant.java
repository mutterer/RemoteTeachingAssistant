import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import ij.CommandListener;
import ij.Executer;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.gui.Overlay;
import ij.gui.TextRoi;
import ij.plugin.Colors;
import ij.plugin.PlugIn;

/**
 * Adds a text window that displays the command being run
 * to enhance viewer experience during remote video learning sessions.
 * @author jmutterer
 */


public class IJ_RemoteTeachingAssistant implements PlugIn, CommandListener {

  ImagePlus imp;
  Font f,f2;   
  String cmds;
  String fname;
  String[] colors;

  public void run(String arg) {
    colors = new String[]{"black","green","orange"};
    fname = TextRoi.getDefaultFontName();
  
    GenericDialog gd = new GenericDialog("IJ Remote Teaching Assistant");
    gd.addStringField("Background Color", colors[0]);
    gd.addStringField("Last command color", colors[1]);
    gd.addStringField("History color", colors[2]);
    gd.showDialog();
    if (gd.wasCanceled())
      return; 
    colors[0] = gd.getNextString();    
    colors[1] = gd.getNextString();    
    colors[2] = gd.getNextString();
  
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    ImageWindow.centerNextImage();
    imp = IJ.createImage("Assistant", "8-bit", 600, 100, 1);
    imp.getProcessor().setColor(Colors.decode(colors[0]));
    imp.getProcessor().fill();
    f = new Font(fname,Font.BOLD,40);
    f2 = new Font(fname,Font.BOLD,30);
    imp.show();
    alert("Assistant ready.");
    cmds="";
    imp.getWindow().setLocation((int)(screenSize.getWidth() - imp.getWindow().getWidth()),0);
    imp.getWindow().setBackground(Colors.decode(colors[0]));
    Executer.addCommandListener(this);
  }
  public String commandExecuting(String command) {
    if ((command=="Clear")&&IJ.getImage()==imp) {
      cmds="";
      alert("");
      cmds="";
    return null;}
    else {
      alert(command);
      return command;
    }
  }

  void alert(String s) {
    cmds = s+";"+cmds;
    if (imp == null) {
      Executer.removeCommandListener(this);
    } else {
      imp.setOverlay(null);
      Overlay o = new Overlay();
      TextRoi roi = new TextRoi(0, 0, s, f);
      roi.setStrokeColor(Colors.decode(colors[1]));
      roi.setJustification(TextRoi.LEFT);
      String history = cmds.substring(cmds.indexOf(";")+1,Math.min(cmds.length(),60)).replaceAll(";"," | ");
      TextRoi roi2 = new TextRoi(0, 50, (history.startsWith("null"))?"":history, f2);
      roi2.setStrokeColor(Colors.decode(colors[2]));
      roi2.setJustification(TextRoi.LEFT);      
      o.add(roi);
      o.add(roi2);
      imp.setOverlay(o);
    } 
  }
}

