package user;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONObject;

public class UserSender {
	public UserSender(Socket socket, JSONObject obj) throws IOException {
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		writer.println(obj.toJSONString());
	}
}
