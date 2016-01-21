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
		//����Httpͷ��Ҫ
		String BOUNDARY = UUID.randomUUID().toString();
		String PREFIX = "--";
		String LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data";//��������
		
		//������URL
		String serverUrl = "http://localhost:8080/PhotoServer/servlet/PhotoServlet";
		File photoFile = new File(filePath);
		
		try {
			URL url = new URL(serverUrl);
			//���������������
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			//�������ӳ�ʱʱ��
			connection.setConnectTimeout(100000);
			connection.setReadTimeout(10000);
			
			//���ü��ɴӷ�������ȡ���ݣ�Ҳ������������д����
			connection.setDoInput(true);
			connection.setDoOutput(true);
		
			//������Ϊ�ύ��������
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "utf-8");
			connection.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			
			//ͼƬ�ļ���Ϊ��
			if (photoFile != null) {
				//��ȡ���ӵ������			
				DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				
				//����Ϊ����ͼƬ�����ͣ�������Ϣ���ڷ�������ʹ��
				sb.append("Content-Disposition: form-data; name=\"photo\"; filename=\""
						+ photoFile.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset=" + "utf-8" + LINE_END);
				sb.append(LINE_END);
				
				//д��ͷ����������
				dos.write(sb.toString().getBytes());
				
				//�����ͷ���󣬶���ͼƬ�ļ�
				InputStream in = new FileInputStream(photoFile);
				byte[] bytes = new byte[1024];
				
				int numberRead = 0;
				
				while ((numberRead = in.read(bytes, 0, 1024)) > 0) {
					dos.write(bytes, 0, numberRead);
				}
				//ͼƬ�ļ��������
				in.close();
				
				//
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				//��ʱ�Ѿ���������http�ײ���ͼƬ�ļ����ݣ����䵽������
				dos.flush();
				
				//��ȡ��Ӧ�룬200=�ɹ�
				if (connection.getResponseCode() == 200) {
					System.out.println("upload successfully");
				}
				
			}
			
		} catch(MalformedURLException e) {
			System.out.println("��Ƭ�ϴ�ʧ��");
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
