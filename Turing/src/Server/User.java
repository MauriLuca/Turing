package Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
	
	private String username;
	private String password;
	private Stato stato;
	private List<String> documentiUtente;
	private List<String> invitiOnline;
	private List<String> invitiOffline;
	
	public User(String username, String password2) {
		this.username = username;
		this.password = password2;
		this.stato = Stato.registered;
		this.documentiUtente = Collections.synchronizedList(new ArrayList<String>());		
		this.invitiOnline = Collections.synchronizedList(new ArrayList<String>()); 
		this.invitiOffline = Collections.synchronizedList(new ArrayList<String>()); 		
	}
	
	public void clearInvitiOnline() {
		invitiOnline.clear();
	}
	
	public void clearInvitiOffline() {
		invitiOffline.clear();
	}
	
	public List<String> getInvitiOnline() {
		return invitiOnline;
	}

	public List<String> getInvitiOffline() {
		return invitiOffline;
	}

	public String getUser() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setStato(Stato stato) {
		this.stato = stato;
	}
	
	public Stato getStato() {
		return this.stato;
	}
	
	//metodo che aggiunge il nuovo documento alla lista dei docymenti che l'utente può modificare 
	public void addDocument(String nameDocument) {
		documentiUtente.add(nameDocument);
	}
	
	public List<String> getDocumentList() {
		return documentiUtente;
	}
	
	//metodo che mi comunica se l'utente è abilitato alla modifica del documento
	public boolean canEdit(String document) {
		return documentiUtente.contains(document);
	}
	
	public void addOnlineInvite(String nameDocument) {
		invitiOnline.add(nameDocument);
	}
	
	public void addOfflineInvite(String nameDocument) {
		invitiOffline.add(nameDocument);
	}
	
	public boolean hasPedingOnlineInvites() {
		return !invitiOnline.isEmpty();
	}
	
	public boolean hasPendingOfflineInvites() {
		return !invitiOffline.isEmpty();
	}
}
