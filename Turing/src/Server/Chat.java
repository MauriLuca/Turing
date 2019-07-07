package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import javax.swing.JTextArea;

public class Chat extends Thread{

	private JTextArea textArea;
	private MulticastSocket chatSock;
	private InetAddress address;
	private static boolean go;


	public Chat(JTextArea textArea, MulticastSocket chatSock, InetAddress address) {
		this.textArea = textArea;
		this.chatSock = chatSock;
		this.address = address;
		go = true;
	}


	public void run() {
		byte[] buffer = new byte[1024];
		DatagramPacket packet;

		try {
			chatSock.joinGroup(address);
			chatSock.setSoTimeout(1000);

			while(go) {
				packet = null;
				try {
					packet = new DatagramPacket(buffer, buffer.length);
					chatSock.receive(packet);

					String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
					textArea.append(msg + '\n');

				} 
				catch(SocketTimeoutException e) {
					
				}
			}



		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void endChat() {
		go = false;
		System.out.println("nonono" + Boolean.toString(go));
	}
}
