package Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Document {

	private User creator;
	private String nameDocument;
	private int numOfSections;
	private static ConcurrentHashMap<String, User> authorizedUsers; //HashMap che memorizza tutti gli utenti autorizzati all'edit del documento
	private ArrayList<Section> doc; 
	private Path path;
	
	
	public Document(User creator, String nameDocument, int numOfSections) {
		this.creator = creator;
		this.nameDocument = nameDocument;
		this.numOfSections = numOfSections;
		authorizedUsers = new ConcurrentHashMap<String, User>();
		authorizedUsers.put(creator.getUser(), creator);
		this.doc = new ArrayList<Section>();
		
		path = Paths.get(Configuration.path + "/" + nameDocument);
		try {
			Files.createDirectory(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i<numOfSections; i++) {
			doc.add(new Section(Configuration.path + "/" + nameDocument, i));
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
}
