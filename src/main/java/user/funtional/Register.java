package user.funtional;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import data.Constant;
import net.miginfocom.swing.MigLayout;
import user.User;
import user.UserSender;

public class Register {

	private JFrame window;
	private User user;

	private JPanel contentPane;

	private JTextField tfUsername;
	private JPasswordField tfPassword;
	private JLabel lblPassword;
	private JLabel lblUsername;
	private JButton btnRegister;

	public Register(User user) {
		this.user = user;
		init();
		eventHandler();
	}

	private void init() {
		window = new JFrame("Register");
		
		System.out.println("Register");

		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setBounds(100, 100, 300, 150);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		window.setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][][grow][]", "[][][][]"));

		lblUsername = new JLabel("Username");
		lblPassword = new JLabel("Password");
		tfUsername = new JTextField();
		tfPassword = new JPasswordField();
		btnRegister = new JButton("Register");

		contentPane.add(lblUsername, "cell 1 1,alignx trailing");
		contentPane.add(tfUsername, "cell 2 1,growx");
		contentPane.add(lblPassword, "cell 1 2,alignx trailing");
		contentPane.add(tfPassword, "cell 2 2,growx");

		contentPane.add(btnRegister, "flowx,cell 2 3,alignx center");

		window.setVisible(true);
	}

	private void eventHandler() {

		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				user.getLogin().setEnabled(true);
				window.dispose();
//				System.exit(0);
			}
		});

		btnRegister.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				register();
			}
		});
	}

	private void register() {
		System.out.println("register()");
		String username = tfUsername.getText().trim();
		String password = String.valueOf(tfPassword.getPassword()).trim();

		try {
			if (username.equals("") || password.equals("")) {
				popupMessage("Username and password can not be blank!");
			} else {

				JSONObject obj = new JSONObject();
				obj.put("username", username);
				obj.put("flag", Constant.REGISTER);
				obj.put("password", password);

				new UserSender(user.getSocket(), obj);
			}
		} catch (IOException ioEx) {
			popupMessage("Error!");
			ioEx.printStackTrace();
		}
	}

	public void popupMessage(String message) {
		JOptionPane.showMessageDialog(this.window, message);
	}

}
