package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

public class RequestHandler implements Runnable{

	private ConcurrentHashMap<String,User> registeredUsers; //HashMap che memorizza tutti gli utenti registrati
	private static ConcurrentHashMap<String, User> onlineUsers; //HashMap che memorizza tutti gli utenti online
	private ConcurrentHashMap<String,Document> documentList; //HashMap che memorizza tutti i documenti creati
	private ServerSocket notifySock; //socket per le notifiche
	private Socket connSock; //socket per effettuare l'accept delle connessioni
	private Socket notifySocket;//socket per le notifiche
	private List<String> multicastAddressList;

	public RequestHandler(ConcurrentHashMap<String,User> registeredUsers, ConcurrentHashMap<String, User> onlineUsers, ConcurrentHashMap<String,Document> documentList, Socket connectionSocket, ServerSocket notifySock) {
		this.registeredUsers = registeredUsers;
		this.connSock = connectionSocket;
		RequestHandler.onlineUsers = onlineUsers;
		this.documentList = documentList;
		this.notifySock = notifySock;
		this.notifySocket = null;
		multicastAddressList = Collections.synchronizedList(new ArrayList<String>());

	}

	public void run() {

		DataOutputStream outStream; //Stream in output
		BufferedReader inStream; //Stream in input
		User utente = null; //Struttura utente
		Section editingSection = null; //Struttura sezione

		//case switch
		try {
			while(true) {
				
				//stream per la comunicazione col client
				outStream = new DataOutputStream(connSock.getOutputStream());
				inStream = new BufferedReader(new InputStreamReader(connSock.getInputStream()));	

				//leggo la stringa del client che mi specifica l'operazione da eseguire
				String request = inStream.readLine();

				if(request != null) {

					if(request.equals("login")) {

						String username = inStream.readLine();
						String password = inStream.readLine();

						if(registeredUsers.containsKey(username)) {

							User temp = registeredUsers.get(username);

							//se l'utente è registrato lo controllo la password
							if(temp.getStato() == Stato.registered) {

								//se la password è corretta cambio lo stato dell'utente e lo inserisco nella HashMap degli utenti online
								if(temp.getPassword().equals(password)) {

									utente = temp;
									utente.setStato(Stato.logged);
									onlineUsers.put(username, utente);

									//se l'utente è stato invitato mentre era offline
									if(utente.hasPendingOfflineInvites()) {
										//comunico che ha degli inviti pendenti
										outStream.writeBytes("pending" + '\n');
										//comunico quanti inviti dovrà ricevere
										outStream.writeBytes(Integer.toString(utente.getInvitiOffline().size()) + '\n');

										for (int i = 0; i<utente.getInvitiOffline().size(); i++) {
											outStream.writeBytes(utente.getInvitiOffline().get(i) + '\n');
										}

										String esito = inStream.readLine();
										if(esito.contains("letti")) {
											utente.clearInvitiOffline();
										}
									}
									else {
										outStream.writeBytes("Login effettuato con successo" + '\n');
									}
									notifySocket = notifySock.accept();
									Thread notifyThread = new NotificationHandler(notifySocket, utente, documentList);
									notifyThread.start();
								}

								else {
									outStream.writeBytes("Password errata" + '\n');
								}

							}
							else {
								outStream.writeBytes("Utente già online" + '\n');
							}
						}

						else {
							outStream.writeBytes("Utente non registrato" + '\n');
						}
					}

					if(request.equals("logout")){

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

					if(request.equals("create")) {

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

									//genero un indirizzo 
									String multicastAddress = generateMulticastAddress();
									//creo il documento
									Document document = new Document(utente, nameDocument, numOfSectionsInt, multicastAddress);
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

					if(request.equals("edit")) {

						if(utente != null) {
							String nameDocument = inStream.readLine();
							String numSection = inStream.readLine();

							outStream.writeBytes(utente.getUser() + '\n');

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
											editingSection = section;
											outStream.writeBytes("pronto per la modifica" + '\n' );
											section.setEdit(true);

											int port = Integer.parseInt(inStream.readLine());
											outStream.writeBytes(utente.getUser() + '\n');

											SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(port));
											
											editingSection.sendSection(port, socketChannel);
											
											socketChannel.close();
											socketChannel = null;

											utente.setStato(Stato.editing);
											String multicastAddress = documentList.get(nameDocument).getMulticastAddress();
											outStream.writeBytes(multicastAddress + '\n');

										}

									}
									else {
										outStream.writeBytes("Permesso di accesso negato" + '\n');
									}

								}
								catch(NumberFormatException e) {
									outStream.writeBytes("Campo sezione errato" + '\n');
								}
							}
							else {
								outStream.writeBytes("Documento non esistente" + '\n');
							}
						}
					}

					if(request.equals("showdocument")) {

						String nameDocument = inStream.readLine();
						String username = utente.getUser();

						if(documentList.containsKey(nameDocument)) {

							Document doctemp = documentList.get(nameDocument);

							if(utente.canEdit(nameDocument)) {

								outStream.writeBytes("pronto per l'invio del documento" + '\n');

								//invio il nome dell'utente che ha effettuato la richiesta
								outStream.writeBytes(username + '\n');

								//ricevo la porta per l'invio
								int port = Integer.parseInt(inStream.readLine());

								outStream.writeBytes(Integer.toString(doctemp.getNumOfSections()) + '\n');
								//invio il documento
								doctemp.sendDocument(port, outStream);

							}
							else {
								outStream.writeBytes("Permesso di accesso negato" + '\n');
							}
						}
						else {
							outStream.writeBytes("Documento non esistente" + '\n');
						}
					}

					if(request.equals("showsection")) {

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
										
										SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(port));
										
										//prendo la sezione da inviare
										editingSection = doctemp.getSection(numOfSection);
										//invio il documento
										editingSection.sendSection(port, socketChannel);
										
									}
									else {
										outStream.writeBytes("numero di sezione non valido" + '\n');
									}

								}
								else {
									outStream.writeBytes("accesso al documento negato" + '\n');
								}
							}
							else {
								outStream.writeBytes("documento non esistente" + '\n');
							}
						}catch(NumberFormatException e) {
							outStream.writeBytes("campo sezione errato" + '\n');
						}
					}

					if(request.equals("list")) {
						//ottengo l'array che contiene la lista di documenti al quale l'utente può accedere
						List<String> list = utente.getDocumentList();

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

					if(request.equals("endedit")) {

						//ottengo nome del documento e numero di sezione
						String nameDocument = inStream.readLine();
						String numOfSection = inStream.readLine();

						Document doctemp = documentList.get(nameDocument);
						
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

						Section sectemp = doctemp.getSection(Integer.parseInt(numOfSection));
						
						sectemp.receiveSection(socketChannel, nameDocument);
						
						socketChannel.close();
						socketChannel = null;

						JOptionPane.showMessageDialog(null, "Sezione ricevuta correttamente");

						//chiudo il serversocketchannel
						serverSocketChannel.close();
						serverSocketChannel = null;	
						
						//tolgo il flag di edit dalla sezione e dall'utente
						editingSection.setEdit(false);;
						editingSection = null;
						utente.setStato(Stato.logged);

					}

					if(request.equals("invite")) {

						if(utente != null) {

							String invitedUsername = inStream.readLine();
							String nameDocument = inStream.readLine();
							Document document = documentList.get(nameDocument);

							//se esiste il documento
							if(documentList.containsKey(nameDocument)) {

								//se l'utente è il creatore allora può invitare
								if(documentList.get(nameDocument).getCreator() == utente) {

									//se l'utente da invitare esiste
									if(registeredUsers.containsKey(invitedUsername)){

										User invitedUser = registeredUsers.get(invitedUsername);

										//se l'utente non è già un collaboratore del documento
										if(!registeredUsers.get(invitedUsername).getDocumentList().contains(nameDocument)) {

											//aggiungo l'utente alla lista di utenti che possono accedere al documento
											document.addAtuthorizedUser(invitedUsername, invitedUser);
											invitedUser.addDocument(nameDocument);

											//controllo se è online e va notificato subito
											if(onlineUsers.containsKey(invitedUsername)) {
												invitedUser.addOnlineInvite(nameDocument);
											}
											//aggiungo nella lista degli inviti offline
											else {
												invitedUser.addOfflineInvite(nameDocument);
											}
											outStream.writeBytes("l'utente è stato invitato con successo" + '\n');
										}
										else {
											outStream.writeBytes("l'utente è già un collaboratore" + '\n');
										}
									}
									else {
										outStream.writeBytes("l'utente da invitare non esiste" + '\n');
									}
								}
								else {
									outStream.writeBytes("accesso al documento negato" + '\n');
								}
							}
							else {
								outStream.writeBytes("il documento non è stato trovato" + '\n');
							}
						}
					}

					//continuo il case switch
					
				}
				else {
					if(utente != null) {
						onlineUsers.remove(utente.getUser());
						utente.setStato(Stato.registered);
						utente = null;
					}
					if(editingSection != null) {
						editingSection.setEdit(false);
						//poi ricordati le lock
						editingSection = null;
					}
					connSock.close();
					outStream.close();
					inStream.close();
					break;
				}
			}
		}
		catch(IOException e) {
			//funzione che dealloca e chiude le connessioni aperte
			try {
				if(utente != null) {
					onlineUsers.remove(utente.getUser());
					utente.setStato(Stato.registered);
					utente = null;
				}
				if(editingSection != null) {
					editingSection.setEdit(false);
					//poi ricordati le lock
					editingSection = null;
				}
				connSock.close();
				return;
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	//metodo per generare un indirizzo multicast non in uso
	public String generateMulticastAddress() {
		int tmp = (int)(Math.random()*40);

		while(tmp < 24 || tmp > 40) {
			tmp = (int)(Math.random()*40);
		}

		tmp += 200;
		String res = tmp + "." + (int)(Math.random()*256) + "." + (int)(Math.random()*256) + "." + (int)(Math.random()*256);

		if(multicastAddressList.contains(res)) {
			res = generateMulticastAddress();
		}
		else{
			multicastAddressList.add(res);
		}

		return res;
	}

}
