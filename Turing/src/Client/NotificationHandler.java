package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import Server.Document;
import Server.Stato;
import Server.User;

public class NotificationHandler extends Thread {

	private Socket notifySocket;
	private DataOutputStream outStream;
	private User utente;
	private ConcurrentHashMap<String, Document> documentList;

	public NotificationHandler(Socket notifySocket, User utente, ConcurrentHashMap<String, Document> documentList) {
		this.notifySocket = notifySocket;
		this.utente = utente;
		this.documentList = documentList;
	}

	public void run() {

		try {

			outStream = new DataOutputStream(notifySocket.getOutputStream());

			while(true) {

				List<String> documentListInvitations = utente.getInvitiOnline(); 
				
				//se l'utente viene disconnesso posso terminare il thread
				if(utente.getStato()==Stato.registered)
					break;
				
				//se l'utente ha inviti online
				if(!documentListInvitations.isEmpty()) {
					System.out.println("loop");
					for(String invite : documentListInvitations) {
						outStream.writeBytes("ok" + '\n');
						Document docTemp = documentList.get(invite);

						String invitingUser = docTemp.getCreator().getUser();
						String nameDocument = invite;

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
