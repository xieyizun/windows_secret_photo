package liyu;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;



public class CreateUser {
	private static String filePath = System.getProperty("user.dir")+"\\user\\info.dat";
	public CreateUser(String name, String password, String rbc) {
	    File files = new File( System.getProperty("user.dir")+"\\user");
	    if (!files.exists()) {
	    	files.mkdir();
	    }
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath));
			out.writeBytes(name);
			out.writeBytes("\n");
			out.writeBytes(password);
			out.writeBytes("\n");
			out.writeBytes(rbc);
			out.flush();
			out.close();
			//set the info is hiden in the directory user
			File saveOutputFile = new File(filePath);
			String sets = "attrib +H \"" + saveOutputFile.getAbsolutePath() + "\""; 
			Runtime.getRuntime().exec(sets);
		} catch(Exception e) {
			e.printStackTrace();
		}
		/*
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(filePath));
			ArrayList<String> s = new ArrayList<String>();
			String str = "";
			while ((str=in.readLine()) != null) {
				s.add(str);
			}
			in.close();
		} catch(Exception e1) {
			e1.printStackTrace();
		}*/
	}

}
