package liyu;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Test {
	private Image image;
	public Test() {
		String outfilePath = "/home/xyz/sky.bin";
		String infilePath = "/home/xyz/sky.jpg";
		
		File infile = new File(infilePath);
		/*
		try {			
			BufferedImage bi = ImageIO.read(infile);
			ImageIO.write(bi, "jpg", new File(outPath));
			File tempfile = new File("/home/xyz/nba.jpg");
			tempfile.delete();
		} catch(Exception e1) {
			e1.getMessage();
		}
		*/
		
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(outfilePath));
			FileInputStream fs = new FileInputStream(infile);
			byte[] binImage = new byte[(int)infile.length()];
			fs.read(binImage, 0, (int)infile.length());
			image = Toolkit.getDefaultToolkit().createImage(binImage);
			out.write(binImage, 0, (int)infile.length());
			
			out.close();
			fs.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
		String resultPath = "/home/xyz/shixun";
		myWrite(resultPath);
		System.out.println(image.getHeight(null));
	}
	public Image myWrite(String filePath) {
	     try {
	        int w = image.getWidth(null);
	        int h = image.getHeight(null);

	        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
	        Graphics g = bi.getGraphics();
	        //draw out the image by an instance of Class Graphics
	        g.drawImage(image, 0, 0, null);
	        //write image of type bmp to disk
	        ImageIO.write(bi, "jpg", new File(filePath));
	        System.out.println("success");
	        return image;
	     } catch(Exception e) {
	        e.getMessage();
	     }
	     return (Image)null;
	    }
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Test();
			}
		});
	}
}
