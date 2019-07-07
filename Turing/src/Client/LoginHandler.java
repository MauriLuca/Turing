package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import GUI.GUILogged;
import GUI.GUITuring;
import Server.Notify;

public class LoginHandler extends Thread {

	private Socket clientSock; //Socket TCP
	private DataOutputStream outStream; //Stream in output
	private BufferedReader inStream; //Stream in input
	private String username; //Username che effettua la richiesta di login
	private GUITuring frame; //Interfaccia grafica
	private String password; //password dell'utente
	private GUILogged frameLogged;

	public LoginHandler(Socket clientSock, DataOutputStream outStream, BufferedReader inStream, GUITuring frame) {

		if(frame == null || clientSock == null || outStream == null || inStream == null) throw new NullPointerException();

		//controllo che il socket non sia chiuso
		if(clientSock.isClosed()) throw new IllegalArgumentException();

		this.clientSock = clientSock;
		this.outStream = outStream;
		this.inStream = inStream;
		this.frame = frame;

	}

	public void run() {

		try {
			//comunico l'operazione all'handler
			outStream.writeBytes("login" + '\n');

			//ottengo username e password
			username = frame.getUsername();

			password = frame.getPassword();

			outStream.writeBytes(username + '\n');
			outStream.writeBytes(password + '\n');

			String temp = inStream.readLine();
			
			if(temp.contains("errata")) {
				JOptionPane.showMessageDialog(null, "Password Errata");
			}
			else if(temp.contains("online")) {
				JOptionPane.showMessageDialog(null, "Utente già online");
			}
			else if(temp.contains("registrato")) {
				JOptionPane.showMessageDialog(null, "Utente già registrato");
			}
			//se ho dei documenti in attesa
			else if(temp.contains("pending")) {
				
				int pendingInvites = Integer.parseInt(inStream.readLine());
				String invites = "";
				for(int i = 0; i < pendingInvites; i ++) {
					invites = invites + '\n'+ inStream.readLine() + '\n'; 
				}
				
				//comunico al server che ho letto gli inviti pendeneti e che deve rimuoverli dalla lista dell'utente
				outStream.writeBytes("inviti letti" + '\n');
				
				JOptionPane.showMessageDialog(null, "Sei stato invitato a collaborare ai seguenti documenti: " + invites);
				initializeFormAndButtons();
				Thread listenerNotifiche = new Thread(new Notify());
				listenerNotifiche.start();
			}
			
			//se ho effettuato il login con successo posso avviare la GUI del Client
			else if(temp.contains("successo")) {
				initializeFormAndButtons();
				Thread listenerNotifiche = new Thread(new Notify());
				listenerNotifiche.start();
			}

		}
		catch(IOException e){
			e.printStackTrace();	
		}

	}
	
	public void initializeFormAndButtons() {
		
		//chiudo la GUI di login
		frame.setVisible(false);
		frame.dispose();

		//avvia l'interfaccia grafica di login
		try {
			frameLogged = new GUILogged();
			frameLogged.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Inizializzo il pulsante di Logout
		frameLogged.getLogout().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//parte il Thread
				Thread logoutThread = new LogoutHandler(clientSock, outStream, inStream, frameLogged);
				logoutThread.start();
			}

		});

		//Inizializzo il pulsante di CreateDocument
		frameLogged.getCreate().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//controllo che il campo nome documento e numero di sezione non sia vuoto
				if(frameLogged.getNameDocument().length() == 0 || frameLogged.getNumOfSections().length() == 0) {
					JOptionPane.showMessageDialog(null, "Insert valid document name or number of sections");
				}
				else {
					//parte il Thread
					Thread createThread = new CreateDocumentHandler(clientSock, outStream, inStream, frameLogged);
					createThread.start();
				}
			}

		});

		//Inizializzo il pulsante di EditDocument
		frameLogged.getEdit().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//controllo che il campo nome documento e numero della sezione non sia vuoto
				if(frameLogged.getNameDocument().length() == 0 || frameLogged.getSection().length() == 0) {
					JOptionPane.showMessageDialog(null, "Insert valid document name or number of section");
				}
				else {
					//parte il Thread
					Thread editThread = new EditHandler(clientSock, outStream, inStream, frameLogged);
					editThread.start();
				}
			}

		});

		//Inizializzo il pulsante di showdocument
		frameLogged.getShow().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//controllo che il campo nome documento non sia vuoto
				if(frameLogged.getNameDocument().length() == 0) {
					JOptionPane.showMessageDialog(null, "Insert valid document name");
				}
				else {
					//caso parametro sezione mancante mostro il documento intero
					if(frameLogged.getSection().length() == 0) {
						//parte il Thread
						Thread showDocumentThread = new ShowDocumentHandler(clientSock, outStream, inStream, frameLogged);
						showDocumentThread.start();
					}
					//caso parametro sezione esistente mostro la sezione del documento
					else {
						Thread showSectionThread = new ShowSectionHandler(clientSock, outStream, inStream, frameLogged);
						showSectionThread.start();
					}
				}
			}

		});

		//Inizializzo il pulsante di ListDocument
		frameLogged.getList().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//parte il Thread
				Thread listThread = new ListHandler(clientSock, outStream, inStream, frameLogged);
				listThread.start();
			}

		});

		//Inizializzo il pulsante di invite
		frameLogged.getInvite().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(frameLogged.getInviteName().length()==0) {
					JOptionPane.showMessageDialog(null, "Campo nome invito vuoto");
				}
				else {
					//parte il Thread
					Thread inviteThread = new InviteHandler(outStream, inStream, frameLogged);
					inviteThread.start();
				}
			}

		});
	}
}
