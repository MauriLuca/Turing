package Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import GUI.GUILogged;
import GUI.GUITuring;

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

		//imposto il tipo di operazione
		String op = "login" + '\n';

		try {
			//comunico l'operazione all'handler
			outStream.writeBytes(op);

			//ottengo username e password
			username = frame.getUsername() + '\n';

			password = frame.getPassword() + '\n';

			outStream.writeBytes(username);
			outStream.writeBytes(password);

			String temp = inStream.readLine();

			//se ho effettuato il login con successo posso avviare la GUI del Client
			if(temp.contains("successo")) {
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
							Thread inviteThread = new InviteHandler(clientSock, outStream, inStream, frameLogged);
							inviteThread.start();
						}
					}

				});

			}

			//Thread listener = new Thread(new NotificationListener());
			//listener.start();

		}
		catch(IOException e){
			e.printStackTrace();	
		}

	}
}
