package user;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;

import data.Constant;
import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import user.funtional.Login;
import user.funtional.Register;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class User {
	private JFrame window;
	private Thread listener;
	private String username;
	@Getter
	private Socket socket;
	@Getter
	private Login login;
	@Getter
	private Register register;

	private JPanel contentPane;
	private JTextField tfText;
	private JTextArea taText;
	private JButton btnSend;
	@Setter
	private JList<String> lUsers;
	@Setter
	private Set<String> users;

	public User() {
		try {
			this.socket = new Socket(Constant.IP, Constant.PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
		listener = new Thread(new UserListener(socket, this));
		listener.start();
		this.users = new HashSet<>();

		this.login = new Login(this);

		initComponents();
		eventHandler();
	}

//	public User(Socket socket, String username, Thread listener, Set<String> users) {
//		this.username = username;
//		this.socket = socket;
//		this.listener = listener;
//		this.users = users;
//
//		this.login = new LoginFrame();
//
//		initComponents();
//		eventHandler();
//	}

	private void initComponents() {

		window = new JFrame();

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(100, 100, 550, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new MigLayout("", "[grow]", "[grow][]"));
		window.setContentPane(contentPane);

		JPanel pnMain = new JPanel();
		taText = new JTextArea();

		contentPane.add(pnMain, "cell 0 0,grow");
		pnMain.setLayout(new MigLayout("", "[grow][100px:n:100px,grow]", "[grow]"));
		pnMain.add(taText, "cell 0 0,grow");

		lUsers = new JList<String>();
		lUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pnMain.add(lUsers, "cell 1 0,grow");

		JPanel pnMsg = new JPanel();
		tfText = new JTextField();
		btnSend = new JButton("Send");

		contentPane.add(pnMsg, "cell 0 1,grow");
		pnMsg.setLayout(new MigLayout("", "[grow][]", "[]"));
		pnMsg.add(tfText, "cell 0 0,growx");
		pnMsg.add(btnSend, "cell 1 0");

		window.setVisible(false);
	}

	private void eventHandler() {
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (socket != null) {
					try {
						JSONObject obj = new JSONObject();
						obj.put("username", username);
						obj.put("flag", "logout");

						new UserSender(socket, obj);

					} catch (IOException ioEx) {
//						popupMessage("Error while closing!");
						System.err.println("User error on closing");
						ioEx.printStackTrace();
					}
				}
				window.setVisible(false);
				window.dispose();
//				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				listener.interrupt();
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String target = lUsers.getSelectedValue();
				String message = tfText.getText();
				if (target == null || message.length() == 0) {
					return;
				}

				try {
					JSONObject obj = new JSONObject();
					obj.put("username", username);
					obj.put("flag", Constant.TEXT);
					obj.put("target", target);
					obj.put("message", message);

					new UserSender(socket, obj);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				appendMessageText("Me: " + message);
				tfText.setText("");
			}

		});
	}

	public void logout() {
		listener.interrupt();
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		window.setVisible(false);
//		window.dispose();
	}

	public void login(Set<String> users, String username) {
		this.login.dispose();
		// update title
		this.username = username;
		this.window.setTitle("Hello " + username);
		// update users
		this.users = users;
		this.users.remove(username);
		this.lUsers.setListData(users.toArray(new String[0]));
		// update...
		this.window.setVisible(true);
	}

	public void appendMessageText(String message) {
		taText.append(message);
		taText.append(Constant.CR_NL);
		taText.setCaretPosition(taText.getText().length());
	}

	public void updateUsers(String username) {
		if (!username.equals(this.username)) {
			String selected = lUsers.getSelectedValue();
			users.add(username);
			lUsers.setListData(users.toArray(new String[0]));
			if (selected != null) {
				lUsers.setSelectedValue(selected, true);
			}
		}
	}

	public void popupMessage(String message) {
		JOptionPane.showMessageDialog(this.window, message);
	}
}
