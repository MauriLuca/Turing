package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.JOptionPane;

import GUI.GUIEditing;
import GUI.GUILogged;
import Server.Configuration;

public class EditHandler extends Thread {

	private DataOutputStream outStream; //Stream in output
	private BufferedReader inStream; //Stream in input
	private GUILogged frameLogged; //Interfaccia grafica di logged
	private GUIEditing frameEditing; //interfaccia grafica di editing
	private Socket clientSock; //Socket TCP

	public EditHandler(Socket clientSock, DataOutputStream outStream, BufferedReader inStream, GUILogged frameLogged) {

		if(frameLogged == null || clientSock == null || outStream == null || inStream == null) throw new NullPointerException();

		//controllo che il socket non sia chiuso
		if(clientSock.isClosed()) throw new IllegalArgumentException();

		this.clientSock = clientSock;
		this.outStream = outStream;
		this.inStream = inStream;
		this.frameLogged = frameLogged;

	}

	public void run() {

		//imposto il tipo di operazione
		String op = "edit" + '\n';

		try {
			//comunico l'operazione all'handler
			outStream.writeBytes(op);

			//ottengo il nome del documento e la sezione che voglio editare
			String nameDocument = frameLogged.getNameDocument();
			String section = frameLogged.getSection();

			//comunico il nome del documento e il numero della sezione
			outStream.writeBytes(nameDocument + '\n');
			outStream.writeBytes(section + '\n');

			//leggo l'esito
			String temp = inStream.readLine();

			if(temp.contains("errato")) {
				JOptionPane.showMessageDialog(null, "Numero di sezione errato");
			}
			else if(temp.contains("editing")) {
				JOptionPane.showMessageDialog(null, "Sezione già in fase di editing");
			}
			else if(temp.contains("negato")){
				JOptionPane.showMessageDialog(null, "Permesso di accesso NEGATO");
			}
			else if(temp.contains("esistente")) {
				JOptionPane.showMessageDialog(null, "Documento non esistente");
			}
			else {
				//apro il serversocketchannel
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
				//creo un socketaddress random
				InetSocketAddress isa = new InetSocketAddress(0);
				//effettuo la bind sull'address
				serverSocketChannel.bind(isa);
				//ottengo la porta random sulla quale ho fatto la bind
				Integer port = serverSocketChannel.socket().getLocalPort();
				//invio la porta al server 
				outStream.writeBytes(port.toString() + '\n');
				//accetto la connessione in arrivo dal server
				SocketChannel socketChannel = serverSocketChannel.accept();
				//leggo il nome dell'utente che ha effettuato la richiesta
				String nomeUtente = inStream.readLine();

				//routine di ricezione file
				FileChannel fc = null;
				String path = Configuration.clientPath + "/" + nomeUtente + "/" + nameDocument;
				Files.createDirectories(Paths.get(path));

				path = path + "/Section_" + section + ".txt";

				//apro il file channel in mdalità scrittura
				fc = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
				//alloco il buffer supponendo file di testo di dimensione inferiore a 2kb
				ByteBuffer buf = ByteBuffer.allocate(2048);

				//leggo e scrivo sul buffer
				while(socketChannel.read(buf) > 0) {
					buf.flip();
					fc.write(buf);
					buf.clear();
				}

				//chiudo filechannel e socketchannel
				fc.close();
				socketChannel.close();
				fc = null;
				socketChannel = null;

				JOptionPane.showMessageDialog(null, "Sezione Scaricata correttamente e pronta per la modifica");

				//chiudo il serversocketchannel
				serverSocketChannel.close();
				serverSocketChannel = null;
				
				//chiudo la finestra logged
				frameLogged.setVisible(false);
				frameLogged.dispose();

				//apro la finestra di editing con la chat
				frameEditing = new GUIEditing();
				frameEditing.setVisible(true);
				
				
				//Inizializzo il pulsante di EndEditDocument
				frameEditing.getEndEdit().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
							//parte il Thread
							Thread endEditThread = new EndEditHandler(clientSock, outStream, inStream, frameEditing, frameLogged, nameDocument, section);
							endEditThread.start();
					}
					
				});

			}


		}
		catch(IOException e){
			e.printStackTrace();	
		}

	}


}


