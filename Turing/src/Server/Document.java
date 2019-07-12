package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Document {

	private User creator;
	private String nameDocument;
	private int numOfSections;
	private static ConcurrentHashMap<String, User> authorizedUsers; //HashMap che memorizza tutti gli utenti autorizzati all'edit del documento
	private List<Section> doc; 
	private Path path;
	private String multicastAddress;


	public Document(User creator, String nameDocument, int numOfSections, String multicastAddress) {
		this.creator = creator;
		this.nameDocument = nameDocument;
		this.numOfSections = numOfSections;
		this.multicastAddress = multicastAddress;
		authorizedUsers = new ConcurrentHashMap<String, User>();
		authorizedUsers.put(creator.getUser(), creator);
		this.doc = new ArrayList<Section>();

		path = Paths.get(Configuration.SERVER_PATH + "/" + nameDocument);
		try {
			Files.createDirectory(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i = 0; i<numOfSections; i++) {
			doc.add(new Section(Configuration.SERVER_PATH + "/" + nameDocument, i));
		}
	}

	//funzione di supporto per l'invio di un documento
	public void sendDocument(int port, DataOutputStream outStream) {

		try {
			SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(port));
			for(int i = 0; i < numOfSections; i++) {

				Section sectiontemp = getSection(i);

				//comunico al server se la sezione è in fase di editing
				outStream.writeBytes(Boolean.toString(sectiontemp.isEdit()) + '\n');

				sectiontemp.sendSection(port, socketChannel);
			}
			socketChannel.close();
			socketChannel = null;
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public Path getPath() {
		return this.path;
	}

	public User getCreator() {
		return creator;
	}


	public String getNameDocument() {
		return nameDocument;
	}


	public int getNumOfSections() {
		return numOfSections;
	}

	public Section getSection(int numOfSection) {
		return doc.get(numOfSection); 
	}

	public static ConcurrentHashMap<String, User> getAuthorizedUsers() {
		return authorizedUsers;
	}

	public void addAtuthorizedUser(String username, User utente) {
		authorizedUsers.put(username, utente);
	}

	public String getMulticastAddress() {
		return multicastAddress;
	}
}
