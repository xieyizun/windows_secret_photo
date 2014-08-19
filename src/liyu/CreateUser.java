package liyu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class CreateUser {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String name = "Louise.li";
		String password = "BabyIloveyou";
		String filePath = "./info.bin";
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
			System.out.println(s.get(0) + " " + s.get(1) + " " + "Successfully");
		} catch(Exception e1) {
			e1.printStackTrace();
		}
		

	}

}
