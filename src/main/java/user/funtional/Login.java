package user.funtional;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import data.Constant;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import user.User;
import user.UserSender;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

public class Login {

	private JFrame window;
	@Getter
	private Thread listener;
	private User user;

	private JPanel contentPane;
	private JTextField tfUsername;
	private JPasswordField tfPassword;
	private JLabel lblPassword;
	private JLabel lblUsername;
	private JButton btnLogin;
	private JButton btnRegister;

	public Login(User user) {
		this.user = user;
		init();
		eventHandler();
	}

	private void init() {
		window = new JFrame("Login");

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(100, 100, 300, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		window.setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][][grow][]", "[][][][]"));

		lblUsername = new JLabel("Username");
		lblPassword = new JLabel("Password");
		tfUsername = new JTextField();
		tfPassword = new JPasswordField();
		btnLogin = new JButton("Log in");

		contentPane.add(lblUsername, "cell 1 1,alignx trailing");
		contentPane.add(tfUsername, "cell 2 1,growx");
		contentPane.add(lblPassword, "cell 1 2,alignx trailing");
		contentPane.add(tfPassword, "cell 2 2,growx");

		contentPane.add(btnLogin, "flowy,cell 2 3,alignx center");
		
		btnRegister = new JButton("Register");
		contentPane.add(btnRegister, "cell 2 3,alignx center");

		window.setVisible(true);
	}

	private void eventHandler() {
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		btnLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("login()");
				login();
			}
		});
		
		btnRegister.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setEnabled(false);
				new Register(user);
			}
		});
	}

	private void login() {
		String username = tfUsername.getText().trim();
		String password = String.valueOf(tfPassword.getPassword()).trim();

		try {
			if (username.equals("") || password.equals("")) {
				popupMessage("Username and password can not be blank!");
			} else {

				JSONObject obj = new JSONObject();
				obj.put("username", username);
				obj.put("flag", Constant.LOGIN);
				obj.put("password", password);

				new UserSender(user.getSocket(), obj);
			}
		} catch (IOException ioEx) {
			popupMessage("Login failed!");
			ioEx.printStackTrace();
		}
	}

	public void popupMessage(String message) {
		JOptionPane.showMessageDialog(this.window, message);
	}

	public void setVisible(boolean isVisible) {
		this.window.setVisible(isVisible);
	}

	public void dispose() {
		this.window.dispose();
	}
	
	public void setEnabled(boolean isEnabled) {
		window.setEnabled(isEnabled);
	}
}
