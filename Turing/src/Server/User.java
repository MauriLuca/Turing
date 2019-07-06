package Server;

import java.util.ArrayList;
import java.util.Hashtable;

public class User {
	
	private String username;
	private String password;
	private Stato stato;
	private ArrayList<String> documentiUtente;
	
	public User(String username, String password2) {
		this.username = username;
		this.password = password2;
		this.stato = Stato.registered;
		this.documentiUtente = new ArrayList<String>();		
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
	
	public ArrayList<String> getDocumentList() {
		return documentiUtente;
	}
	
	//metodo che mi comunica se l'utente è abilitato alla modifica del documento
	public boolean canEdit(String document) {
		return documentiUtente.contains(document);
	}
}
