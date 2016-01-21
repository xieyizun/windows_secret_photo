package com.xieyizun.internet_action;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.UUID;

import org.omg.CORBA.portable.OutputStream;

public class UploadPhoto {
	
	public static void uploadPhotoToInternet(String filePath) {
		//构造Http头需要
		String BOUNDARY = UUID.randomUUID().toString();
		String PREFIX = "--";
		String LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data";//内容类型
		
		//服务器URL
		String serverUrl = "http://localhost:8080/PhotoServer/servlet/PhotoServlet";
		File photoFile = new File(filePath);
		
		try {
			URL url = new URL(serverUrl);
			//打开与服务器的连接
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			//设置连接超时时间
			connection.setConnectTimeout(100000);
			connection.setReadTimeout(10000);
			
			//设置即可从服务器读取数据，也可以往服务器写数据
			connection.setDoInput(true);
			connection.setDoOutput(true);
		
			//该连接为提交数据请求
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "utf-8");
			connection.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			
			//图片文件不为空
			if (photoFile != null) {
				//获取连接的输出流			
				DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				
				//以下为设置图片的类型，名称信息，在服务器端使用
				sb.append("Content-Disposition: form-data; name=\"photo\"; filename=\""
						+ photoFile.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset=" + "utf-8" + LINE_END);
				sb.append(LINE_END);
				
				//写该头部到服务器
				dos.write(sb.toString().getBytes());
				
				//构造好头部后，读入图片文件
				InputStream in = new FileInputStream(photoFile);
				byte[] bytes = new byte[1024];
				
				int numberRead = 0;
				
				while ((numberRead = in.read(bytes, 0, 1024)) > 0) {
					dos.write(bytes, 0, numberRead);
				}
				//图片文件读入完成
				in.close();
				
				//
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				//此时已经完整构造http首部和图片文件内容，传输到服务器
				dos.flush();
				
				//获取响应码，200=成功
				if (connection.getResponseCode() == 200) {
					System.out.println("upload successfully");
				}
				
			}
			
		} catch(MalformedURLException e) {
			System.out.println("相片上传失败");
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
