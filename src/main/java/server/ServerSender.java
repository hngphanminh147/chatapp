package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

import org.json.simple.JSONObject;

import data.Constant;

public class ServerSender {

	public ServerSender(Set<Socket> sockets, JSONObject obj) throws IOException {
		PrintWriter writer = null;
		for (Socket socket : sockets) {
			writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(obj.toJSONString());
		}
	}

	public ServerSender(Socket socket, JSONObject obj) throws IOException {
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		writer.println(obj.toJSONString());
	}
}
