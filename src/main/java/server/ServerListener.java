package server;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import data.Constant;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServerListener implements Runnable {
	private Socket socket;
	private Server server;

	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			while (true) {
				String s = null;
				while ((s = reader.readLine()) != null) {
					JSONObject obj = (JSONObject) new JSONParser().parse(s);
					String username = (String) obj.get("username");
					String flag = (String) obj.get("flag");					

					System.out.println("sl: " + obj);

					// login request
					if (flag.equals(Constant.LOGIN)) {
						String password = (String) obj.get("password");
						if (server.isUser(username, password)) {
							// duplicate login
							if (server.isLoggedin(username)) {
								server.sendNotification(username + " is duplicate log in");

								// response
								obj = new JSONObject();
								obj.put("flag", Constant.LOGIN);
								obj.put("username", username);
								obj.put("message", "duplicate");

								new ServerSender(socket, obj);
							} else {
								// notify all
								obj = new JSONObject();
								obj.put("flag", Constant.NEW_LOGIN);
								obj.put("username", username);

								new ServerSender(server.getUserSockets(), obj);

								// add to users list
								server.sendNotification(username + " is logged in");
								server.putUsers(socket, username);

								// response
								obj = new JSONObject();
								obj.put("flag", Constant.LOGIN);
								obj.put("message", "accepted");
								obj.put("username", username);

								JSONArray users = new JSONArray();
								users.addAll(server.getUsers());
								obj.put("users", users);

								new ServerSender(socket, obj);
							}
						} else {
							// rejected login
							obj = new JSONObject();
							obj.put("flag", Constant.LOGIN);
							obj.put("message", "rejected");

							new ServerSender(socket, obj);
						}
					} else

					// logout request
					if (flag.equals(Constant.LOGOUT)) {
						if (username.equals(Constant.INCOGNITO)) {
							return;
						} else {
							server.sendNotification(username + " logout!");
							// update users
							server.removeUsers(socket, username);
							// response
							obj = new JSONObject();
							obj.put("flag", Constant.LOGOUT);
							new ServerSender(socket, obj);
							return;
						}
					} else

					// register request
					if (flag.equals(Constant.REGISTER)) {
						String password = (String) obj.get("password");
						// validate
						if (server.isUser(username, password)) {
							obj = new JSONObject();
							obj.put("flag", Constant.REGISTER);
							obj.put("message", "username");
							
							new ServerSender(socket, obj);
						} else
						// register
						{
							server.register(username, password);
							
							obj = new JSONObject();
							obj.put("flag", Constant.REGISTER);
							obj.put("message", "OK");
							
							new ServerSender(socket, obj);
						}
					} else

					// message
					if (flag.equals(Constant.TEXT)) {
						String target = (String) obj.get("target");
						String message = (String) obj.get("message");

						obj = new JSONObject();
						obj.put("source", username);
						obj.put("flag", Constant.TEXT);
						obj.put("message", message);

						Socket targetSocket = server.getByUser(target);

						new ServerSender(targetSocket, obj);
					}
				}
			}
		} catch (Exception e) {
			server.sendNotification("Error while receive client message!");
			e.printStackTrace();
		}
	}
}
