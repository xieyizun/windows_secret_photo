import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


public class PhotoServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public PhotoServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		
		//��ȡ�����ļ���Ŀ����
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//��ȡ�ļ��ϴ���Ҫ�����·����upload�ļ���Ҫ����
		String path = request.getSession().getServletContext().getRealPath("/upload");
		System.out.println(path);
		
		//���ô���ļ��Ĵ洢�ң�����洢�ҿ��Ժ����մ洢�ļ����ļ��в�ͬ����Ϊ���ļ��ܴ�Ļ���ռ�ù�����ڴ棬�������ô洢��
		factory.setRepository(new File(path));
		
		//���û���Ĵ�С�����ϴ��ļ���������������ʱ���ͷŵ���ʱ�洢��
		factory.setSizeThreshold(1024*1024); //1M
		//�ϴ��������࣬��ˮƽAPI�ϴ�����
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		try {
			//����parseRequest(request)����������ϴ��ļ�FileItem�ļ���list��ʵ�ֶ��ļ��ϴ�			
			List<FileItem> list = (List<FileItem>)upload.parseRequest(request);
			System.out.println(list.size());
			
			for (FileItem item : list) {
				//��ȡ����������
				String name = item.getFieldName();
				//�����ȡ�ı���Ϣ����ͨ���ı���Ϣ����ͨ��ҳ�����ʽ���������ַ���
				if (item.isFormField()) {
					//��ȡ�û�����������ַ���
					String value = item.getString();
					request.setAttribute(name, value);
				} else {
					//���������ǷǼ��ַ���������ͼƬ����Ƶ����Ƶ�ȶ������ļ�
					//��ȡ·����
					String value = item.getName();
					//ȡ�����һ����б��
					int start = value.lastIndexOf("\\");
					//��ȡ�ϴ��ļ����ַ������� +1��ȥ����б��
					String filename = value.substring(start + 1);
					request.setAttribute(name, filename);
					System.out.println(name+" " + filename);
					//�������ṩ�ķ���ֱ��д���ļ�
					item.write(new File(path, filename));						
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
