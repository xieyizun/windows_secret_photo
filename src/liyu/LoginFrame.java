package liyu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.ByteArrayOutputStream;


import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class LoginFrame extends JFrame {

	/**
	 * 
	 */
	//用于传递给登录成功的界面
	public static String username;
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPasswordField password;
	private JTextField userName;
	//登录按钮
	private JButton loginButton;
	//注册按钮
	private JButton registerButton;
	
	private JPanel loginButtonPanel;
	
	
	
	public LoginFrame() {
		
		this.setResizable(false);
		setTitle("登录");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir")+"\\res\\love.png"));
		setBounds(450, 200, 540, 440);
		contentPane = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				ImageIcon icon = new ImageIcon(System.getProperty("user.dir")+"\\res\\background7.jpg");
				Image image = icon.getImage();
				g.drawImage(image,0,0,null);
			}
		};
		contentPane.setBorder(new EmptyBorder(15,15,15,15));
		contentPane.setLayout(new GridLayout(6,1,5,5));
		setContentPane(contentPane);
		//login
		JPanel loginPanel = new JPanel();
		loginPanel.setOpaque(false);
		
		JLabel login = new JLabel("登录");
		login.setFont(new Font("", Font.ITALIC, 40));
		login.setForeground(Color.WHITE);
		loginPanel.add(login);
		contentPane.add(loginPanel);
		
		//user name
		JPanel namePanel = new JPanel();
		namePanel.setOpaque(false);
		
		
		JLabel nameLabel = new JLabel("       用户名:      ");
		nameLabel.setFont(new Font("", Font.ITALIC,20));
		nameLabel.setForeground(Color.WHITE);
		userName = new JTextField();
		userName.setColumns(15);
		userName.setPreferredSize(new Dimension(30,25));
		namePanel.add(nameLabel);
		namePanel.add(userName);
		
		contentPane.add(namePanel);
		//password
		final JPanel passwordPanel = new JPanel();
		passwordPanel.setOpaque(false);
		
		JLabel passwordLabel = new JLabel("       密码: ");
		passwordLabel.setFont(new Font("", Font.ITALIC, 20));
		passwordLabel.setForeground(Color.WHITE);
		password = new JPasswordField();
		password.setColumns(15);
		password.setPreferredSize(new Dimension(30,25));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(password);
		contentPane.add(passwordPanel);
		
		//login button
		loginButtonPanel = new JPanel();
		loginButtonPanel.setOpaque(false);
		loginButton = new JButton("登录");
		loginButton.setForeground(Color.BLACK);
		loginButton.setFont(new Font("", Font.ITALIC,20));
		loginButton.setPreferredSize(new Dimension(120,30));
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					loginButtonClick();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		loginButtonPanel.add(loginButton);
		
		//register
		
		registerButton = new JButton("注册");
		registerButton.setForeground(Color.BLACK);
		registerButton.setFont(new Font("", Font.ITALIC,20));
		registerButton.setPreferredSize(new Dimension(120,30));
		
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRegisterButtonClick();
	    		
			}
		});
		loginButtonPanel.add(registerButton);
		
		contentPane.add(loginButtonPanel);
		
	}
	//转向注册页面
	protected void onRegisterButtonClick() {
		RegisterFrame.launch();
		this.setVisible(false);
	}
	@SuppressWarnings("deprecation")
	protected void loginButtonClick() throws IOException {
		String name = userName.getText();
		String pw = password.getText();
		if (name.isEmpty() || pw.isEmpty()) {
	    		JOptionPane.showMessageDialog(this, "登录失败","用户名或密码不能为空", JOptionPane.WARNING_MESSAGE);		
		} else {
			String serverUrl = "http://localhost:8080/PhotoServer/servlet/UserServlet?username=" + 
									name + "&password=" + pw;
			URL url = new URL(serverUrl);
			
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "utf-8");
			
			//连接
			conn.connect();
			//读取返回结果
			if (conn.getResponseCode() == 200) {
				InputStream in = conn.getInputStream();
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] bytes = new byte[1024];
				int len = -1;
				while ((len = in.read(bytes)) > 0) {
					out.write(bytes, 0, len);
				}
				
				in.close();
				//String result = out.toByteArray().toString();这种方式会乱码
				String result = new String(out.toByteArray());
				
				System.out.println(result);
				if (result != null && result.equals("登录成功")) {
					//用于传递给登录后的页面显示
					this.username = name;
					
					JOptionPane.showMessageDialog(this, "登录成功","登录成功",JOptionPane.INFORMATION_MESSAGE);
		    		LiyuImages.launch();
		    		this.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(this, "登录失败","用户名或密码错误", JOptionPane.WARNING_MESSAGE);
				}
							
			} else {
				JOptionPane.showMessageDialog(this, "服务器出错","登录失败",JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LoginFrame().setVisible(true);
			}
		});
	}
	
}
