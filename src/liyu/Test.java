package liyu;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class Test extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image image;
	public Test() {
		String filePath = "./images/test.bin";
		String outPath = "./images/fang.jpg";		
		File infile = new File(filePath);
			try {			
				int w =image.getWidth(null);
				int h = image.getHeight(null);
				BufferedImage bi = ImageIO.read(infile);
				ImageIO.write(bi, "jpg", new File(outPath));	
			} catch(Exception e1) {
				e1.getMessage();
			}
		/*
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath));
			FileInputStream fs = new FileInputStream(inPath);
			byte[] binImage = new byte[(int)infile.length()];
			fs.read(binImage, 0, (int)infile.length());
			out.write(binImage, 0, (int)infile.length());
			out.close();
			fs.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Test().setVisible(true);
			}
		});
	}
}
