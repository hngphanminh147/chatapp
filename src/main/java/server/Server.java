package server;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTextArea;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import data.*;
import lombok.Getter;

public class Server extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;

	static Server instance = null;
	private ServerSocket serverSocket;

	private JFrame window;
	private JTextArea notifications;
	@Getter
	private Map<Socket, String> map;
	private JList<String> users;

	public static Server getInstance() {
		if (instance == null) {
			instance = new Server();
		}
		return instance;
	}

	private Server() {
		initComponents();
		initServer();
		eventHandler();
	}

	private void initServer() {
		map = new HashMap<>();
		try {
			serverSocket = new ServerSocket(Constant.PORT);
			sendNotification("Initialize server successfully!");
		} catch (IOException e) {
			popupMessage("Error during initialization!");
			e.printStackTrace();
		}
	}

	private void initComponents() {
		window = new JFrame("Server");
		window.setBounds(200, 200, 500, 350);
		window.setResizable(false);

		notifications = new JTextArea();
		notifications.setBounds(10, 70, 340, 320);
		notifications.setEditable(false);
		notifications.setLineWrap(true);
		notifications.setWrapStyleWord(true);
		window.getContentPane().setLayout(new MigLayout("", "[340px][182px]", "[][grow]"));

		JLabel lblNoti = new JLabel("Notifications");
		window.getContentPane().add(lblNoti, "cell 0 0,alignx center,aligny top");

		JScrollPane paneText = new JScrollPane(notifications);
		window.getContentPane().add(paneText, "cell 0 1,alignx left,growy");

		JLabel lblUsers = new JLabel("Users");
		window.getContentPane().add(lblUsers, "cell 1 0,growx,aligny top");

		users = new JList<String>();
		window.getContentPane().add(users, "cell 1 1,grow");

		window.setVisible(true);
	}

	private void eventHandler() {
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (map.size() != 0) {
					try {
						JSONObject obj = new JSONObject();
						obj.put("flag", "shutdown");
						obj.put("message", "Server is down!");
						new ServerSender(map.keySet(), obj);
					} catch (IOException ioExceptionioEx) {
						ioExceptionioEx.printStackTrace();
					}
				}
				System.exit(0);
			}
		});
	}
	
	public void register(String username, String password) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constant.DATA_PATH,true))){
			JSONObject obj = new JSONObject();
			obj.put("username", username);
			obj.put("password", password);
			
			writer.write(obj.toJSONString());
			writer.write(Constant.CR_NL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void putUsers(Socket socket, String username) {
		map.put(socket, username);
		users.setListData(map.values().toArray(new String[0]));
	}

	public boolean removeUsers(Socket socket, String username) {
		return map.remove(socket, username);
	}

	public Socket getByUser(String username) {
		return map.entrySet().stream().filter(entry -> entry.getValue().equals(username)).map(Map.Entry::getKey)
				.findFirst().get();
	}

	public boolean isLoggedin(String username) {
		return map.containsValue(username);
	}

	public boolean isUser(String username, String password) {
		try (BufferedReader reader = new BufferedReader(new FileReader(Constant.DATA_PATH))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				JSONObject obj = (JSONObject) new JSONParser().parse(line);
				if (username.equals(obj.get("username")) && password.equals(obj.get("password"))) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Set<String> getUsers(){
		return map.entrySet().stream().filter(entry -> !entry.getValue().equals("")).map(Map.Entry::getValue).collect(Collectors.toCollection(HashSet::new));		
	}

	public Set<Socket> getUserSockets() {
		return map.entrySet().stream().filter(entry -> !entry.getValue().equals("")).map(Map.Entry::getKey).collect(Collectors.toCollection(HashSet::new));
	}

	public void run() {
		Socket socket = null;
		while (true) {
			try {
				socket = serverSocket.accept();
				map.put(socket, "");
				new Thread(new ServerListener(socket, this)).start();
			} catch (Exception e) {
				popupMessage("Unknown error!");
				e.printStackTrace();
			}
		}
	}

	public void sendNotification(String message) {
		notifications.append(message);
		notifications.append(Constant.CR_NL);
		notifications.setCaretPosition(notifications.getText().length());
	}

	public void popupMessage(String message) {
		JOptionPane.showMessageDialog(this.window, message);
	}

}
