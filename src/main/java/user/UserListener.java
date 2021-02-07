package user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import data.Constant;
import lombok.Setter;

public class UserListener implements Runnable {
	@Setter
	private User user;
	private Socket socket;

	public UserListener(Socket socket, User user) {
		this.socket = socket;
		this.user = user;
	}

	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			while (true) {
				String s = null;
				while ((s = reader.readLine()) != null) {
					System.out.println("ul: " + s);
					JSONObject obj = (JSONObject) new JSONParser().parse(s);
					String flag = (String) obj.get("flag");

					//
					if (flag.equals(Constant.LOGIN)) {
						String message = (String) obj.get("message");
						if (message.equals("accepted")) {
							String username = (String) obj.get("username");
							Set<String> users = new HashSet<>((JSONArray) obj.get("users"));

							user.login(users, username);

						} else if (message.equals("duplicate")) {
							user.getLogin().popupMessage("Login duplicate!");
						} else {
							// rejected
							user.getLogin().popupMessage("Login falied!");
						}
					} else
					//
					if (flag.equals(Constant.NEW_LOGIN)) {
						// update users list
						String username = (String) obj.get("username");
						user.updateUsers(username);
					} else
					//
					if (flag.equals(Constant.LOGOUT)) {
						user.logout();
					} else
					//
					if (flag.equals(Constant.TEXT)) {
						String source = (String) obj.get("source");
						String message = (String) obj.get("message");

						user.appendMessageText(source + ": " + message);
					} else
					//
					if (flag.equals(Constant.REGISTER)) {
						String message = (String) obj.get("message");
						if (message.equals("OK")) {
							user.getRegister().popupMessage("Register successfully!");
						} else {
							user.getRegister().popupMessage("Username already exists!");
						}
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
