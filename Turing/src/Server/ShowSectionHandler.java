package Server;

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

import GUI.GUILogged;

public class ShowSectionHandler extends Thread {

	private Socket clientSock; //Socket TCP
	private DataOutputStream outStream; //Stream in output
	private BufferedReader inStream; //Stream in input
	private GUILogged frameLogged;

	public ShowSectionHandler(Socket clientSock, DataOutputStream outStream, BufferedReader inStream, GUILogged frameLogged) {

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
		String op = "showsection" + '\n';

		try {
			outStream.writeBytes(op);

			String nameDocument = frameLogged.getNameDocument();

			outStream.writeBytes(nameDocument + '\n');
			outStream.writeBytes(frameLogged.getSection().toString() + '\n');

			String esito = inStream.readLine();

			//caso in cui il documento richiesto non esista
			if(esito.contains("esistente")) {
				JOptionPane.showMessageDialog(null, "Documento non esistente");
			}
			//caso l'utente non possegga i diritti di accesso al documento
			else if(esito.contains("negato")){
				JOptionPane.showMessageDialog(null, "Permesso di accesso al documento negato");
			}
			//caso la sezione non esista
			else if(esito.contains("valido")) {
				JOptionPane.showMessageDialog(null, "Sezione non valida");
			}
			//caso campo sezione errato
			else if(esito.contains("errato")) {
				JOptionPane.showMessageDialog(null, "Campo sezione errato");
			}
			//caso in cui vada tutto bene
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
				//routine di ricezione file
				FileChannel fc = null;
				//leggo il nome dell'utente
				String nomeUtente = inStream.readLine();
				String numOfSection = inStream.readLine();
				String pathdocument = Configuration.clientPath + "/" + nomeUtente + "/" + nameDocument;
				Files.createDirectories(Paths.get(pathdocument));

				String edit = inStream.readLine();

				String path = pathdocument + "/Section_" + numOfSection + ".txt";

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
				frameLogged.getTextArea().append("[Document: " + nameDocument + "]: " + "Section_" + numOfSection + " downloaded; Editing status: " + edit + '\n' );

				//chiudo filechannel e socketchannel
				fc.close();
				socketChannel.close();
				fc = null;
				socketChannel = null;

				JOptionPane.showMessageDialog(null, "Sezione scaricata correttamente");

				//chiudo il serversocketchannel
				serverSocketChannel.close();
				serverSocketChannel = null;
			}

		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
