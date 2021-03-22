import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import ij.CommandListener;
import ij.Executer;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.text.TextWindow;

/**
 * Adds a text window that displays the command being run
 * to enhance viewer experience during remote video learning sessions.
 * @author jmutterer
 */

public class IJ_RemoteTeachingAssistant implements PlugIn, CommandListener {
	String t;
	int fsize;
	int delay;
	TextWindow tw;

	public void run(String arg) {
		t = "Assistant";
		
		GenericDialog gd = new GenericDialog("IJ Remote Teaching Assistant");
		gd.addNumericField("Font Size", 40);
		gd.addNumericField("Display time (ms)", 1500);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return;
		
		fsize = (int) gd.getNextNumber();
		delay = (int) gd.getNextNumber();

		new TextWindow(t, "", 450, 60 + fsize);
		tw = (TextWindow) WindowManager.getWindow(t);
		tw.getTextPanel().setFont(new Font("Monospaced", Font.PLAIN, fsize), true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		tw.setLocation((int) (screenSize.getWidth() - tw.getWidth()), 0);

		Executer.addCommandListener(this);

	}

	@Override
	public String commandExecuting(String command) {
		alert(command);
		return command;
	}

	void alert(String s) {
		if (WindowManager.getWindow(t) == null) {
			Executer.removeCommandListener(this);
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if (tw != null) {
							tw.getTextPanel().setColumnHeadings(s);
							tw.append("");
							Thread.sleep(delay);
							tw.getTextPanel().setColumnHeadings(" ");
							tw.append("");
						}
					} catch (Exception e) {
					}
				}
			}).start();
		}
		return;
	}

}