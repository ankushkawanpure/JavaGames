package applet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {

	public static ServerSocket serverSocket;
	public static Socket socket;
	public static DataOutputStream out;
	public static DataInputStream in;
	public static Users[] user = new Users[10];
	Random rand = new Random();
	public static int xLoc;
	public static int yLoc;

	public Server(int port) throws IOException {
		System.out.println("Server Initializing...");
		serverSocket = new ServerSocket(7777);
		System.out.println("Server started...");
		xLoc = rand.nextInt(400);
		yLoc = rand.nextInt(400);
//		xLoc = 200;
//		yLoc = 200;
	}

	public void startServer() throws IOException {
		while (true) {
			socket = serverSocket.accept();
			for (int i = 0; i < 10; i++) {
				if (user[i] == null) {
					System.out.println(
							"Connection from :" + socket.getInetAddress());
					out = new DataOutputStream(socket.getOutputStream());
					in = new DataInputStream(socket.getInputStream());
					user[i] = new Users(out, in, user, i);
					Thread thread = new Thread(user[i]);
					thread.start();
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			Server server = new Server(7777);
			server.startServer();
		} catch (IOException e) {
			System.out.println("Port already in Use try another port");
			e.printStackTrace();
		}
	}

	class Users implements Runnable {
		DataOutputStream out;
		DataInputStream in;
		Users[] user = new Users[10];
		String name;
		int playerId;
		int playeridin;
		int xin;
		int yin;

		public Users(DataOutputStream out, DataInputStream in, Users[] user,
				int pid) {
			this.out = out;
			this.in = in;
			this.user = user;
			this.playerId = pid;
		}

		@Override
		public void run() {
			try {
				out.writeInt(playerId);
				out.writeInt(xLoc);
				out.writeInt(yLoc);
			} catch (IOException e1) {
				System.out.println("Failed tp connect");
			}
			while (true) {
				try {
					playeridin = in.readInt();
					xin = in.readInt();
					if(xin >= 10100) {
						for (int i = 0; i < 10; i++) {
							if (user[i] != null) {
								user[i].out.writeInt(10100 + playeridin);
							}
						}
						//break;
					} else {
						yin = in.readInt();
						for (int i = 0; i < 10; i++) {
							if (user[i] != null) {
								user[i].out.writeInt(playeridin);
								user[i].out.writeInt(xin);
								user[i].out.writeInt(yin);
							}
						}
					}
					
				} catch (IOException e) {
					user[playerId] = null;
				}
			}
			

		}

	}

}