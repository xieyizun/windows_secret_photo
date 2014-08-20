package liyu;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;


public class TestReadBin {
	private Image image;
	public TestReadBin() {
		String inbiFilePath = "/home/xyz/sky.bin";
		String outPath="/home/xyz/shixun/sky.jpg";
		File inFile = new File(inbiFilePath);
		File outFile = new File(outPath);
		try {
			FileInputStream fs = new FileInputStream(inFile);
			byte[] binImage = new byte[(int)inFile.length()];
			fs.read(binImage, 0, (int)inFile.length());
			FileOutputStream os = new FileOutputStream(outFile);
			os.write(binImage);
			fs.close();
			os.close();
		} catch (Exception e1) {
			e1.getMessage();
		}
	}
	protected Image myWrite(String filePath) {
	     try {
	        int w = image.getWidth(null);
	        int h = image.getHeight(null);
	        
	        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
	        Graphics g = bi.getGraphics();
	        //draw out the image by an instance of Class Graphics
	        g.drawImage(image, 0, 0, null);
	        //write image of type bmp to disk
	        ImageIO.write(bi, "jpg", new File(filePath));
	        
	        return image;
	     } catch(Exception e) {
	        e.getMessage();
	     }
	     return (Image)null;
	}
	public static void main(String[]args) {
		new TestReadBin();
	}
}
