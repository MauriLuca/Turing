package GUI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JPasswordField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;

public class GUITuring extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton btnLogin;
	private JButton btnRegister;
	
	/**
	 * Create the frame.
	 */
	public GUITuring() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 722, 593);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(243, 243, 243));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblUsername.setBounds(10, 10, 111, 33);
		contentPane.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblPassword.setBounds(10, 53, 111, 33);
		contentPane.add(lblPassword);
		
		usernameField = new JTextField();
		usernameField.setFont(new Font("Tahoma", Font.PLAIN, 18));
		usernameField.setBounds(131, 10, 130, 29);
		contentPane.add(usernameField);
		usernameField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 18));
		passwordField.setBounds(131, 53, 130, 29);
		contentPane.add(passwordField);
		
		btnLogin = new JButton("LOGIN");
		btnLogin.setForeground(new Color(0, 0, 0));
		btnLogin.setBackground(new Color(0, 51, 255));
		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnLogin.setBounds(131, 92, 130, 33);
		contentPane.add(btnLogin);
		
		btnRegister = new JButton("REGISTER");
		btnRegister.setBackground(new Color(0, 51, 255));
		btnRegister.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnRegister.setBounds(131, 135, 130, 33);
		contentPane.add(btnRegister);
		
		JLabel label = new JLabel("");
		Image logo = new ImageIcon (this.getClass().getResource("/Turing logo.png")).getImage();
		label.setIcon(new ImageIcon(logo));
		label.setBounds(273, 199, 390, 320);
		contentPane.add(label);
	}
	
	public String getUsername() {
		return usernameField.getText();
	}
	
	public String getPassword() {
		String password = new String(passwordField.getPassword());
		return password;
	}
	
	public JButton getLogin() {
		return btnLogin;
	}
	
	public JButton getRegister() {
		return btnRegister;
	}

}