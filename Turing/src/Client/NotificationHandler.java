package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import Server.Document;
import Server.User;

public class NotificationHandler extends Thread {

	private String username;
	private Socket notifySocket;
	private DataOutputStream outStream;
	private User utente;
	private ConcurrentHashMap<String, Document> documentList;

	public NotificationHandler(String username, Socket notifySocket, User utente, ConcurrentHashMap<String, Document> documentList) {
		this.username = username;
		this.notifySocket = notifySocket;
		this.utente = utente;
		this.documentList = documentList;
	}

	public void run() {

		try {

			outStream = new DataOutputStream(notifySocket.getOutputStream());

			while(true) {

				ArrayList<String> documentListInvitations = utente.getInvitiOnline(); 

				if (utente == null)
					break;

				//se l'utente ha inviti online
				if(!documentListInvitations.isEmpty()) {

					for(int i = 0; i < documentListInvitations.size(); i++) {
						outStream.writeBytes("ok" + '\n');
						Document docTemp = documentList.get(documentListInvitations.get(i));

						String invitingUser = docTemp.getCreator().getUser();
						String nameDocument = documentListInvitations.get(i);

						outStream.writeBytes(invitingUser + '\n');
						outStream.writeBytes(nameDocument + '\n');
					}

					utente.clearInvitiOnline();
				}

			}

			outStream.close();
			notifySocket.close();
		}
		catch(IOException e) {
			try {
				outStream.close();
				notifySocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

		try {
			outStream.close();
			notifySocket.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
