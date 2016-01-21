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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;


public class RegisterFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPasswordField password;
	private JTextField userName;
	
	//注册按钮
	private JButton registerButton;
	
	private JPanel passwordConfirmPanel;
	private JLabel passwordConfirmLabel;
	private JPasswordField passwordConfirm;
	private JPanel rollBackPanel;
	private JLabel rollBackLabel;
	private JTextField rollBackCode;

	private JPanel registerButtonPanel;
	
	public RegisterFrame() {
		this.setResizable(false);
		setTitle("注册");
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
		contentPane.add(loginPanel);
				
		final JLabel login = new JLabel("注册");
		login.setFont(new Font("", Font.ITALIC, 40));
		login.setForeground(Color.WHITE);
		loginPanel.add(login);
		
		//user name
		JPanel namePanel = new JPanel();
		namePanel.setOpaque(false);
		contentPane.add(namePanel);
		
		JLabel nameLabel = new JLabel("       用户名:      ");
		nameLabel.setFont(new Font("", Font.ITALIC,20));
		nameLabel.setForeground(Color.WHITE);
		userName = new JTextField();
		userName.setColumns(15);
		userName.setPreferredSize(new Dimension(30,25));
		namePanel.add(nameLabel);
		namePanel.add(userName);	
		
		rollBackPanel = new JPanel();
		rollBackPanel.setOpaque(false);
		rollBackLabel = new JLabel("邮箱:");
		rollBackLabel.setFont(new Font("",Font.ITALIC, 20));
		rollBackLabel.setForeground(Color.WHITE);
		
		rollBackCode = new JTextField();
		rollBackCode.setColumns(15);
		rollBackCode.setPreferredSize(new Dimension(30,25));
		
		rollBackPanel.add(rollBackLabel);
		rollBackPanel.add(rollBackCode);
		
		contentPane.add(rollBackPanel);
		//password
		final JPanel passwordPanel = new JPanel();
		passwordPanel.setOpaque(false);
		contentPane.add(passwordPanel);
		
		JLabel passwordLabel = new JLabel("       密码: ");
		passwordLabel.setFont(new Font("", Font.ITALIC, 20));
		passwordLabel.setForeground(Color.WHITE);
		password = new JPasswordField();
		password.setColumns(15);
		password.setPreferredSize(new Dimension(30,25));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(password);
					
		passwordConfirmPanel = new JPanel();
		passwordConfirmPanel.setOpaque(false);
		passwordConfirmLabel = new JLabel("          密码确定: ");
		passwordConfirmLabel.setFont(new Font("", Font.ITALIC, 20));
		passwordConfirmLabel.setForeground(Color.WHITE);
		
		passwordConfirm = new JPasswordField();
		passwordConfirm.setColumns(15);
		passwordConfirm.setPreferredSize(new Dimension(30,25));
		
		passwordConfirmPanel.add(passwordConfirmLabel);
		passwordConfirmPanel.add(passwordConfirm);	
		
		contentPane.add(passwordConfirmPanel);	
		
		//register button
		registerButtonPanel = new JPanel();
		registerButtonPanel.setOpaque(false);
		registerButton = new JButton("注册");
		registerButton.setForeground(Color.BLACK);
		registerButton.setFont(new Font("", Font.ITALIC,20));
		registerButton.setPreferredSize(new Dimension(120,30));
		
		registerButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				 try {
					registerAccount();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		registerButtonPanel.add(registerButton);
		contentPane.add(registerButtonPanel);
	}
	//注册账号
	private void registerAccount() throws IOException {
		String name = userName.getText();
		String email = rollBackCode.getText();
		String passwd = password.getText();
		String passwdConfirm = passwordConfirm.getText();
		
		if (name.isEmpty() || email.isEmpty() || passwd.isEmpty() || passwdConfirm.isEmpty()) {
			JOptionPane.showMessageDialog(this, "以上各项均不能为空","注册失败",JOptionPane.INFORMATION_MESSAGE);
		} else if (!passwd.equals(passwdConfirm)){
			JOptionPane.showMessageDialog(this, "两次输入密码不一致","注册失败",JOptionPane.INFORMATION_MESSAGE);
		} else {
			StringBuilder params = new StringBuilder();
			params.append("username="+name+"&");
			params.append("userpassword="+passwd+"&");
			params.append("useremail="+email);
			
			sendToServer(params.toString());
			
		}
	}
	//发送到服务器
	private void sendToServer(String params) throws IOException {
		String serverUrl = "http://localhost:8080/PhotoServer/servlet/UserServlet";
		URL url = new URL(serverUrl);
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		
		conn.setReadTimeout(10000);
		conn.setRequestProperty("Charset", "utf-8");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setUseCaches(false);
		//设置请求体为文本类型
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		//设置请求体的长度
		conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
		
		//发往服务器
		OutputStream out = conn.getOutputStream();
		out.write(params.getBytes());
		
		//接收注册结果
		int resultCode = conn.getResponseCode();
		if (resultCode == 200) {
			InputStream in = conn.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] bytes = new byte[512];
			
			int len = -1;
			while ((len = in.read(bytes, 0, 512)) > 0) {
				bos.write(bytes, 0, len);
			}
			
			String result = new String(bos.toByteArray());
			
			if (result != null && result.equals("注册成功")) {
				JOptionPane.showMessageDialog(this, "注册成功","注册成功",JOptionPane.INFORMATION_MESSAGE);
	    		LiyuImages.launch();
	    		this.setVisible(false);
			} else if (result != null && result.equals("用户名已存在"))	{
				JOptionPane.showMessageDialog(this, result,"注册失败",JOptionPane.INFORMATION_MESSAGE);
			} else if (result != null && result.equals("邮箱已存在")) {
				JOptionPane.showMessageDialog(this, result,"注册失败",JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "服务器出错","注册失败",JOptionPane.INFORMATION_MESSAGE);
			}
			
		} else {
			JOptionPane.showMessageDialog(this, "服务器错误","注册失败",JOptionPane.INFORMATION_MESSAGE);
		}

	}
	//public static void main(String[] args) {
	public static void launch() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new RegisterFrame().setVisible(true);
			}
		});
	}
}
