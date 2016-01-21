import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xieyizun.database_operation.DatabaseOperation;

public class UserServlet extends HttpServlet {
	private DatabaseOperation op;
	private Connection conn;
	/**
	 * Constructor of the object.
	 */
	public UserServlet() {
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
		response.setContentType("text/html;charset=utf-8");

		response. setCharacterEncoding("utf-8");
		//��ȡ�û���������
		String userName = request.getParameter("username");
		String userPwd = request.getParameter("password");
		System.out.println("ip: " + request.getRemoteAddr() + " " + userName + " " + userPwd);
		OutputStream out = response.getOutputStream();
		try {
			if (op.loginUser(userName, userPwd)) {
				out.write("��¼�ɹ�".getBytes());
			} else {
				out.write("��¼ʧ��".getBytes());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		OutputStream out = response.getOutputStream();
		
		request.setCharacterEncoding("utf-8");
		
		//��ȡע����Ϣ
		String name = request.getParameter("username");
		String email = request.getParameter("useremail");
		String password = request.getParameter("userpassword");
		System.out.println("name: "+name+" password: "+password+" email:"+email);
		
		try {
			int resultCode = op.registerUser(name, password, email);
			if (resultCode == 0) {
				out.write("ע��ɹ�".getBytes());
			} else if (resultCode == 1) {
				out.write("�û����Ѵ���".getBytes());
			} else if (resultCode == 2) {
				out.write("�����Ѵ���".getBytes());
			} else {
				out.write("����������".getBytes());
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.flush();
		out.close();
		
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		//���������ݿ������
		op = DatabaseOperation.getInstance();
		try {
			conn = op.getConnection();
			op.initPreparedStat(conn);
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
