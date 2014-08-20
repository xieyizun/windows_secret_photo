package liyu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class CreateUser {
	private static String filePath = "./user/info.bin";
	@SuppressWarnings("deprecation")
	public CreateUser(String name, String password) {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath));
			out.writeBytes(name);
			out.writeBytes("\n");
			out.writeBytes(password);
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
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
		}
	}

}
