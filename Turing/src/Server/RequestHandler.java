package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

public class RequestHandler implements Runnable{

	private ConcurrentHashMap<String,User> registeredUsers; //HashMap che memorizza tutti gli utenti registrati
	private static ConcurrentHashMap<String, User> onlineUsers; //HashMap che memorizza tutti gli utenti online
	private ConcurrentHashMap<String,Document> documentList; //HashMap che memorizza tutti i documenti creati
	//private ServerSocket serverSock;
	//private Socket notiSock; //socket per le notifiche
	private Socket connSock; //socket per effettuare l'accept delle connessioni

	public RequestHandler(ConcurrentHashMap<String,User> registeredUsers, ConcurrentHashMap<String, User> onlineUsers, ConcurrentHashMap<String,Document> documentList, Socket connectionSocket) {
		this.registeredUsers = registeredUsers;
		this.connSock = connectionSocket;
		this.onlineUsers = onlineUsers;
		this.documentList = documentList;

	}

	public void run() {

		DataOutputStream outStream; //Stream in output
		BufferedReader inStream; //Stream in input
		User utente = null;

		//case switch
		try {
			while(true) {

				//stream per la comunicazione col client
				outStream = new DataOutputStream(connSock.getOutputStream());
				inStream = new BufferedReader(new InputStreamReader(connSock.getInputStream()));		

				//leggo la stringa del client che mi specifica l'operazione da eseguire
				String op = inStream.readLine();

				if(op != null) {

					if(op.equals("login")) {

						System.out.println("login ok");


						String username = inStream.readLine();
						String password = inStream.readLine();
						System.out.println("lettura di pass e user");

						System.out.println(registeredUsers.keySet());
						if(registeredUsers.containsKey(username)) {

							System.out.println("utente ok");

							User temp = registeredUsers.get(username);

							//se l'utente è registrato lo controllo la password
							if(temp.getStato() == Stato.registered) {

								//se la password è corretta cambio lo stato dell'utente e lo inserisco nella HashMap degli utenti online
								if(temp.getPassword().equals(password)) {

									utente = temp;
									System.out.println("password ok");
									utente.setStato(Stato.logged);
									onlineUsers.put(username, utente);
									//scrivo sullo stream che ho effettuato il login
									outStream.writeBytes("Login effettuato con successo" + '\n');

								}

								else {
									//scrivo sullo stream che l'utente ha sbagliato password
									outStream.writeBytes("Password errata" + '\n');
									System.out.println("Password errata");
								}

							}

							else {
								//scrivo sullo stream che l'utente è già online
								outStream.writeBytes("Utente già online" + '\n');
								System.out.println("Utente già online");
							}

						}

						else {
							//scrivo sullo stream che l'utente non è registrato
							outStream.writeBytes("Utente non registrato" + '\n');
							System.out.println("Utente non registrato");
						}

					}
				}

				if(op.equals("logout")){

					//rimuovo l'utente dalla lista online
					onlineUsers.remove(utente.getUser());
					//imposto l'utente da loggato a registrato
					utente.setStato(Stato.registered);
					utente = null;
					//mando l'esito
					outStream.writeBytes("Logout eseguito con successo" + '\n');

					//chiudo socket e stream di output e input
					connSock.close();
					outStream.close();
					inStream.close();
					return;

				}

				if(op.equals("newdocument")) {

					String nameDocument = inStream.readLine();
					String numOfSections = inStream.readLine();

					//caso in cui il documento già esista
					if(documentList.containsKey(nameDocument)) {
						outStream.writeBytes("Documento già esistente" + '\n');
					}
					else {

						//provo a vedere se è stato inserito un numero di sezioni corretto
						try {
							int numOfSectionsInt = Integer.parseInt(numOfSections);

							//controllo che il numero di sezioni sia > 0
							if(numOfSectionsInt <= 0) {
								outStream.writeBytes("Numero di sezioni inferiore ad 1" + '\n');
							}
							//tutto ok
							else {
								//creo il documento
								Document document = new Document(utente, nameDocument, numOfSectionsInt);
								//lo aggiungo alla lista documenti
								documentList.put(nameDocument, document);
								//lo aggiungo alla lista dei documenti che l'utente può modificare
								utente.addDocument(nameDocument);

								outStream.writeBytes("Documento creato con successo" + '\n');
							}

						}catch(NumberFormatException e) {
							outStream.writeBytes("Campo numero di sezioni non riconosciuto" + '\n');
						}
					}
				}

				if(op.equals("edit")) {

					if(utente != null) {
						String nameDocument = inStream.readLine();
						String numSection = inStream.readLine();

						if(documentList.containsKey(nameDocument)) {

							try {
								//leggo il numero della sezione
								int numSectionInt = Integer.parseInt(numSection);

								//se il numero della sezione è maggiore del numero di sezioni esistenti ERRORE
								if(numSectionInt >= documentList.get(nameDocument).getNumOfSections()) {
									outStream.writeBytes("Numero di sezione errato" + '\n');
								}

								//se l'utente è abilitato alla lettura del documento
								else if(utente.canEdit(nameDocument)) {

									//prendo la sezione richiesta del client
									Section section = documentList.get(nameDocument).getSection(numSectionInt);

									if(section.isEdit()) {
										outStream.writeBytes("Sezione in editing" + '\n');
										System.out.println("Sezione già in editing");
									}

									else {
										outStream.writeBytes("pronto per la modifica" + '\n' );
										section.setEdit(true);

										int port = Integer.parseInt(inStream.readLine());
										outStream.writeBytes(utente.getUser() + '\n');

										SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(port));
										Path path = Paths.get(Configuration.path + "/" + nameDocument + "/Section_" + numSection + ".txt");
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

										utente.setStato(Stato.editing);


									}

								}
								else {
									outStream.writeBytes("Permesso di accesso negato" + '\n');
								}

							}
							catch(NumberFormatException e) {
								outStream.writeBytes("Campo sezione non riconosciuto" + '\n');
							}
						}
						else {
							outStream.writeBytes("Documento non esistente" + '\n');
						}
					}
				}

				if(op.equals("showdocument")) {

					String nameDocument = inStream.readLine();
					String username = utente.getUser();

					if(documentList.containsKey(nameDocument)) {

						Document doctemp = documentList.get(nameDocument);

						if(utente.canEdit(nameDocument)) {

							outStream.writeBytes("pronto per l'invio del documento" + '\n');

							//invio il nome dell'utente che ha effettuato la richiesta
							outStream.writeBytes(username + '\n');

							//ricevo la porta per l'invios
							int port = Integer.parseInt(inStream.readLine());

							outStream.writeBytes(Integer.toString(doctemp.getNumOfSections()) + '\n');
							//invio il documento
							sendDocument(doctemp, port, outStream);

						}
						else {
							outStream.writeBytes("Permesso di accesso negato" + '\n');
						}
					}
					else {
						outStream.writeBytes("Documento non esistente" + '\n');
					}
				}

				if(op.equals("showsection")) {

					String nameDocument = inStream.readLine();
					String username = utente.getUser();
					try {
						int numOfSection = Integer.parseInt(inStream.readLine());
						System.out.println(numOfSection);
						if(documentList.containsKey(nameDocument)) {

							Document doctemp = documentList.get(nameDocument);

							if(utente.canEdit(nameDocument)) {

								if(numOfSection >= 0 && numOfSection < doctemp.getNumOfSections()) {
									//posso inviare la sezione
									outStream.writeBytes("pronto per l'invio della sezione" + '\n');

									//invio il nome dell'utente che ha effettuato la richiesta
									outStream.writeBytes(username + '\n');

									//ricevo la porta per l'invio
									int port = Integer.parseInt(inStream.readLine());

									//mando il numero di sezione da ricevere
									outStream.writeBytes(Integer.toString(numOfSection) + '\n');

									//mando se la sezione è in fase di editing o no
									outStream.writeBytes(Boolean.toString(doctemp.getSection(numOfSection).isEdit()) + '\n');
									//invio il documento
									sendSection(nameDocument, numOfSection, port);
								}
								else {
									outStream.writeBytes("numero di sezione non valido" + '\n');
									System.out.println("numero di sezione non valido");
								}

							}
							else {
								outStream.writeBytes("accesso al documento negato" + '\n');
								System.out.println("accesso la documento negato");
							}
						}
						else {
							outStream.writeBytes("documento non esistente" + '\n');
							System.out.println("documento non esistente");
						}
					}catch(NumberFormatException e) {
						outStream.writeBytes("campo sezione errato" + '\n');
					}
				}
				
				if(op.equals("list")) {
					//ottengo l'array che contiene la lista di documenti al quale l'utente può accedere
					ArrayList<String> list = utente.getDocumentList();
					
					String username = utente.getUser();
					
					//comunico il nome dell'utente che ha effettuato la richiesta
					outStream.writeBytes(username + '\n');
					
					//mando il numero di documenti al quale l'utente può accedere
					outStream.writeBytes(Integer.toString(list.size()) + '\n');
					
					//mando il nome di ogni documento
					for(int i = 0; i< list.size(); i++) {
						outStream.writeBytes(list.get(i) + '\n');
					}
				}
				
				if(op.equals("endedit")) {
					
					//ottengo nome del documento e numero di sezione
					String nameDocument = inStream.readLine();
					String numOfSection = inStream.readLine();
					
					//apro il serversocketchannel
					ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
					//creo un socketaddress random
					InetSocketAddress isa = new InetSocketAddress(0);
					//effettuo la bind sull'address
					serverSocketChannel.bind(isa);
					//ottengo la porta random sulla quale ho fatto la bind
					Integer port = serverSocketChannel.socket().getLocalPort();
					//invio la porta e il nome utente al client
					outStream.writeBytes(port.toString() + '\n');
					outStream.writeBytes(utente.getUser() + '\n');
					//accetto la connessione in arrivo dal server
					SocketChannel socketChannel = serverSocketChannel.accept();
					
					//routine di ricezione file
					FileChannel fc = null;
					String path = Configuration.path + "/" + nameDocument;
					Files.createDirectories(Paths.get(path));

					path = path + "/Section_" + numOfSection + ".txt";

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

					JOptionPane.showMessageDialog(null, "Sezione ricevuta correttamente");

					//chiudo il serversocketchannel
					serverSocketChannel.close();
					serverSocketChannel = null;					
				
				}
				//continuo il case switch

			}

		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	//funzione di supporto per l'invio di un documento
	public void sendDocument(Document document, int port, DataOutputStream outStream) {

		try {
			SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(port));
			String nameDocument = document.getNameDocument();
			int numOfSections = document.getNumOfSections();
			FileChannel fc = null;
			for(int i = 0; i < numOfSections; i++) {

				Section sectiontemp = document.getSection(i);

				//comunico al server se la sezione è in fase di editing
				outStream.writeBytes(Boolean.toString(sectiontemp.isEdit()) + '\n');

				Path path = Paths.get(Configuration.path + "/" + nameDocument + "/Section_" + i + ".txt");
				fc = FileChannel.open(path, StandardOpenOption.READ);

				long position = 0L;
				long size = fc.size();

				while(position < size) {
					position+= fc.transferTo(position, 2048, socketChannel);
				}
			}
			fc.close();
			fc = null; 	
			socketChannel.close();
			socketChannel = null;
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void sendSection(String nameDocument, int numSection, int port) {
		try {
			SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(port));
			Path path = Paths.get(Configuration.path + "/" + nameDocument + "/Section_" + numSection + ".txt");
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
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

}
