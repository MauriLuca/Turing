package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
import Server.Configuration;

public class ShowDocumentHandler extends Thread {

	private DataOutputStream outStream; //Stream in output
	private BufferedReader inStream; //Stream in input
	private GUILogged frameLogged;

	public ShowDocumentHandler(Socket clientSock, DataOutputStream outStream, BufferedReader inStream, GUILogged frameLogged) {

		if(frameLogged == null || clientSock == null || outStream == null || inStream == null) throw new NullPointerException();

		//controllo che il socket non sia chiuso
		if(clientSock.isClosed()) throw new IllegalArgumentException();

		this.outStream = outStream;
		this.inStream = inStream;
		this.frameLogged = frameLogged;
	}

	public void run() {

		//imposto il tipo di operazione
		String op = "showdocument" + '\n';

		try {
			outStream.writeBytes(op);

			String nameDocument = frameLogged.getNameDocument();

			outStream.writeBytes(nameDocument + '\n');

			String esito = inStream.readLine();

			//caso in cui il documento richiesto non esista
			if(esito.contains("esistente")) {
				JOptionPane.showMessageDialog(null, "Documento non esistente");
				System.out.println("Documento non esistente");
			}
			//caso l'utente non possegga i diritti di accesso al documento
			else if(esito.contains("negato")){
				JOptionPane.showMessageDialog(null, "Permesso di accesso al documento negato");
				System.out.println("Permesso di accesso al documento negato");
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
				String pathdocument = Configuration.CLIENT_PATH + "/" + nomeUtente + "/" + nameDocument;
				Files.createDirectories(Paths.get(pathdocument));
				int numOfSections = Integer.parseInt(inStream.readLine());
				for (int i = 0; i < numOfSections; i++) {
					
					String edit = inStream.readLine();
					
					String path = pathdocument + "/Section_" + i + ".txt";
					//apro il file channel in mdalità scrittura e creo il file
					fc = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
					//alloco il buffer supponendo file di testo di dimensione inferiore a 2kb
					ByteBuffer buf = ByteBuffer.allocate(2048);

					//leggo e scrivo sul buffer
					while(socketChannel.read(buf) > 0) {
						buf.flip();
						fc.write(buf);
						buf.clear();
					}
					frameLogged.getTextArea().append("[Document: " + nameDocument + "]: " + "Section_" + i + " downloaded; Editing status: " + edit + '\n' );
				}
				//chiudo filechannel e socketchannel
				fc.close();
				socketChannel.close();
				fc = null;
				socketChannel = null;
				
				JOptionPane.showMessageDialog(null, "Documento scaricato correttamente");

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
