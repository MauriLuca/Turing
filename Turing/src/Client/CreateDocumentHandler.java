package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import GUI.GUILogged;

public class CreateDocumentHandler extends Thread {

	//private Socket clientSock; //Socket TCP
		private DataOutputStream outStream; //Stream in output
		private BufferedReader inStream; //Stream in input
		private GUILogged frame; //Interfaccia grafica
		
		public CreateDocumentHandler(Socket clientSock, DataOutputStream outStream, BufferedReader inStream, GUILogged frame) {
			
			if(frame == null || clientSock == null || outStream == null || inStream == null) throw new NullPointerException();
			
			//controllo che il socket non sia chiuso
			if(clientSock.isClosed()) throw new IllegalArgumentException();
			
			//this.clientSock = clientSock;
			this.outStream = outStream;
			this.inStream = inStream;
			this.frame = frame;
			
		}

		public void run() {
			
			try {
				//comunico l'operazione all'handler
				outStream.writeBytes("create" + '\n');
				
				//ottengo il nome del documento e il numero di sezioni che lo comporranno
				String nameDocument = frame.getNameDocument() + '\n';
				String numOfSections = frame.getNumOfSections() + '\n';
				
				//comunico il nome del documento e il numero di sezioni che lo compongono
				outStream.writeBytes(nameDocument);
				outStream.writeBytes(numOfSections);
				
				//leggo l'esito
				String temp = inStream.readLine();
				
				//se creato il documento con successo
				if(temp.contains("successo")) {
					JOptionPane.showMessageDialog(null, "Documento creato con successo");
				}
				//se il documento già esiste
				else if(temp.contains("esistente")) {
					JOptionPane.showMessageDialog(null, "Documento già esistente");
					System.out.println("Documento già esistente");
				}
				//se il numero di sezioni è inferiore a 1
				else if(temp.contains("inferiore")) {
					JOptionPane.showMessageDialog(null, "Numero di sezioni inferiore ad 1");
					System.out.println("Numero di sezioni inferiore ad 1");
				}
				//se il numero di sezioni non è un intero
				else if(temp.contains("riconosciuto")) {
					JOptionPane.showMessageDialog(null, "Numero di sezioni errato!" + '\n' + "Inserisci un valore numerico intero" + '\n');
					System.out.println("Campo numero di sezioni non riconosciuto");
				}
				
			}
			catch(IOException e){
				e.printStackTrace();	
			}
			
		}
	}
