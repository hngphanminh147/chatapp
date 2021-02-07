import server.Server;

public class ServerLauncher {
	public static void main(String[] args) {
		new Thread(Server.getInstance()).start();
	}
}
