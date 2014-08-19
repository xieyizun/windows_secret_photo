package liyu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class LoginFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPasswordField password;
	private JTextField userName;
	private JButton loginButton;
	
	public LoginFrame() {
		this.setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(450, 200, 540, 440);
		contentPane = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				ImageIcon icon = new ImageIcon("./res/background7.jpg");
				Image image = icon.getImage();
				g.drawImage(image,0,0,null);
			}
		};
		contentPane.setBorder(new EmptyBorder(15,15,15,15));
		contentPane.setLayout(new GridLayout(4,1,5,5));
		setContentPane(contentPane);
		//login
		JPanel loginPanel = new JPanel();
		loginPanel.setOpaque(false);
		contentPane.add(loginPanel);
		JLabel login = new JLabel("Login");
		login.setFont(new Font("", Font.ITALIC, 40));
		login.setForeground(Color.WHITE);
		loginPanel.add(login);
		//user name
		JPanel namePanel = new JPanel();
		namePanel.setOpaque(false);
		contentPane.add(namePanel);
		JLabel nameLabel = new JLabel("  Name:      ");
		nameLabel.setFont(new Font("", Font.ITALIC,20));
		nameLabel.setForeground(Color.WHITE);
		userName = new JTextField();
		userName.setColumns(15);
		userName.setPreferredSize(new Dimension(30,25));
		namePanel.add(nameLabel);
		namePanel.add(userName);
		//password
		JPanel passwordPanel = new JPanel();
		passwordPanel.setOpaque(false);
		contentPane.add(passwordPanel);
		JLabel passwordLabel = new JLabel(" Password: ");
		passwordLabel.setFont(new Font("", Font.ITALIC, 20));
		passwordLabel.setForeground(Color.WHITE);
		password = new JPasswordField();
		password.setColumns(15);
		password.setPreferredSize(new Dimension(30,25));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(password);
		//login button
		JPanel loginButtonPanel = new JPanel();
		loginButtonPanel.setOpaque(false);
		contentPane.add(loginButtonPanel);
		loginButton = new JButton("Login");
		loginButton.setForeground(Color.BLACK);
		loginButton.setFont(new Font("", Font.ITALIC,20));
		loginButton.setPreferredSize(new Dimension(100,30));
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginButtonClick();
			}
		});
		loginButtonPanel.add(loginButton);
	}
	@SuppressWarnings("deprecation")
	protected void loginButtonClick() {
		String name = userName.getText();
		String pw = password.getText();
		String infoPath = "./info.bin";
		ArrayList<String> infoMessage = new ArrayList<String>();
		try {
			@SuppressWarnings("resource")
			DataInputStream info = new DataInputStream(new FileInputStream(infoPath));
			String str = "";
			while ((str=info.readLine()) != null) {
				infoMessage.add(str);
			}
		} catch(Exception e) {
			e.getMessage();
		}
		if (!name.isEmpty() && !pw.isEmpty() && name.equals(infoMessage.get(0))
				&& pw.equals(infoMessage.get(1))) {
		    JOptionPane.showMessageDialog(this, "Login successfully","Successful",JOptionPane.INFORMATION_MESSAGE);
		    //LiyuImages.launch();
		    this.setVisible(false);
		} else {
			JOptionPane.showMessageDialog(this, "Login Failure","Failure", JOptionPane.WARNING_MESSAGE);
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
