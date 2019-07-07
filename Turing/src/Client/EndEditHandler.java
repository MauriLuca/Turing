package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.JOptionPane;

import GUI.GUIEditing;
import GUI.GUILogged;
import Server.Chat;
import Server.Configuration;

public class EndEditHandler extends Thread {

	private Socket clientSock; //Socket TCP
	private DataOutputStream outStream; //Stream in output
	private BufferedReader inStream; //Stream in input
	private GUIEditing frameEditing;
	private GUILogged frameLogged;
	private String nameDocument;
	private String section;

	public EndEditHandler(Socket clientSock, DataOutputStream outStream, BufferedReader inStream, GUIEditing frameEditing, GUILogged frameLogged, String nameDocument, String section) {
		
		if(frameLogged == null || frameEditing == null || clientSock == null || outStream == null || inStream == null) throw new NullPointerException();

		//controllo che il socket non sia chiuso
		if(clientSock.isClosed()) throw new IllegalArgumentException();

		this.clientSock = clientSock;
		this.outStream = outStream;
		this.inStream = inStream;
		this.frameEditing = frameEditing;
		this.frameLogged = frameLogged;
		this.nameDocument = nameDocument;
		this.section = section;
	}
	
	public void run() {
		
		try {
			//invio l'operazione da effettuare
			outStream.writeBytes("endedit" + '\n');
			
			//comunico il nome del documento e il numero della sezione che ho finito di editare
			outStream.writeBytes(nameDocument + '\n');
			outStream.writeBytes(section + '\n');

			int port = Integer.parseInt(inStream.readLine());
			
			String username = inStream.readLine();
			
			SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(port));
			Path path = Paths.get(Configuration.clientPath + "/" + username + "/" + nameDocument + "/Section_" + section + ".txt");
			System.out.println(path.toString());

			FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);

			long position = 0L;
			long size = fc.size();

			while(position < size) {
				position+= fc.transferTo(position, 2048, socketChannel);
			}

			fc.close();
			fc = null; 	
			socketChannel.close();
			socketChannel = null;
			
			//chiudo la GUI di editing
			frameEditing.setVisible(false);
			frameEditing.dispose();
			
			//apro la GUI Logged
			frameLogged = new GUILogged();
			frameLogged.setVisible(true);
			
			Chat.endChat();
			
			//----------------------------------------------------------------------------------------------------------------
			
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

			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

}
