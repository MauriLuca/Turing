package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Notify extends Thread {

	private static Socket notifySock;
	private static BufferedReader inStream;
	
	public void run() {

		try {
			notifySock = new Socket("localhost", Configuration.PORT_NOTIFY);
			inStream = new BufferedReader(new InputStreamReader(notifySock.getInputStream()));
			
			while(true) {
				System.out.println("zio");
				String esito = inStream.readLine();
				System.out.println("bella");
				if(esito.equals("ok")) {
					String username = inStream.readLine();
					String nameDocument = inStream.readLine();
					JOptionPane.showMessageDialog(null, "Sei stato invitato da " + username + " a collaborare al documento: " + nameDocument);
				}
				else {
					System.out.println("ciaone");
					break;
				}
			}
		}
		catch(IOException e) {
			try {
				inStream.close();
				notifySock.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}
