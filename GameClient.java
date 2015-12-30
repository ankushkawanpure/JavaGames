package applet;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JLabel;

public class Client extends Applet implements Runnable, KeyListener {

	public static final int WIDTH = 400;
	public static final int HEIGHT = 400;
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;
	int playerId;
	int x[] = new int[10];
	int y[] = new int[10];
	public static int xFinal;
	public static int yFinal;

	boolean left;
	boolean right;
	boolean up;
	boolean down;
	int playerx;
	int playery;
	boolean win = false;
	boolean lose = false;

	public void init() {
		setSize(WIDTH, HEIGHT);
		addKeyListener(this);
	}

	public void start() {

		try {
			System.out.println("Connecting...");
			socket = new Socket("localhost", 7777);
			System.out.println("Connected successfully");

			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			playerId = in.readInt();
			xFinal = in.readInt();
			yFinal = in.readInt();

			Input input = new Input(in, this);

			Thread inputThread = new Thread(input);
			inputThread.start();

			Thread outputThread = new Thread(this);
			outputThread.start();

		} catch (Exception e) {
			System.out.println("unable to connect to client ");
		}

	}
	public void updateCordinates(int pid, int x2, int y2) {
		this.x[pid] = x2;
		this.y[pid] = y2;
	}

	public void paint(Graphics g) {
		g.fillRect(xFinal, yFinal, 10, 10);
		for (int i = 0; i < 10; i++) {
			g.drawOval(x[i], y[i], 5, 5);
		}
	}

	@Override
	public void run() {
		int px = 0;
		int py = 0;
		while (true) {
			if (right == true) {
				px = playerx + 10;
				if (px > WIDTH) {
					playerx = 0;
				} else {
					playerx = px;
				}
			}
			if (left == true) {
				px = playerx - 10;
				if (px < 0) {
					playerx = WIDTH;
				} else {
					playerx = px;
				}
			}
			if (down == true) {
				py = playery + 10;
				if (py > HEIGHT) {
					playery = 0;
				} else {
					playery = py;
				}
			}
			if (up == true) {
				py = playery - 10;
				if (py < 0) {
					playery = HEIGHT;
				} else {
					playery = py;
				}
			}

			if (win) {
				try {
					out.writeInt(playerId);
					out.writeInt(10100);
//					JLabel j = new JLabel("You win");
//					j.setVisible(true);
					repaint();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (lose) {
				try {
					out.writeInt(playerId);
					out.writeInt(10100+playerId);
					Label l = new Label("Loose");
					
//					JLabel j = new JLabel("You lose");
//					j.setVisible(true);
					repaint();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (right || left || up || down) {
				try {
					out.writeInt(playerId);
					out.writeInt(playerx);
					out.writeInt(playery);
				} catch (Exception e) {
					System.out.println("Error sendign co-ordinates");
				}
			}
			repaint();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			left = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			up = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			right = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			down = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			left = false;

		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			up = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			right = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			down = false;
		}
	}

	class Input implements Runnable {

		DataInputStream in;
		Client client;
		public Input(DataInputStream in, Client c) {
			this.in = in;
			this.client = c;
		}

		@Override
		public void run() {
			while (true) {
				try {
					int playerId = in.readInt();
					int x = in.readInt();
					int y =0;
					if (x > 10100) {
						System.out.println("x greater than 10100");
						int c = x - 10100;
						if (playerId == c) {
							win = true;
						} else {
							lose = true;
						}
					} else {
						y = in.readInt();
						if ((x == xFinal) && (y == yFinal)) {
							System.out.println(playerId + "Wins");
							win = true;
						}
						client.updateCordinates(playerId, x, y);
					}
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}