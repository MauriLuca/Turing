package Server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class RegistrationInterfaceImpl extends RemoteObject implements RegistrationInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String,User> registeredUsers;
	
	public RegistrationInterfaceImpl(ConcurrentHashMap<String,User> utenti) throws RemoteException{	
		
		registeredUsers = utenti;
		
	}
	
	
	//Registrazione di un nuovo utente
	public boolean registrationRequest(String username, String password) throws RemoteException {
			
		if(registeredUsers.containsKey(username)) {
			System.out.println("Utente già registrato");
			return false;
		}
		
		else{
			User utente = new User(username, password);
			registeredUsers.put(username, utente);
			System.out.println(registeredUsers.keySet() + " " + username);
			return true;
		}
	}


	public boolean isRegistered(String username) throws RemoteException {
		if(registeredUsers.containsKey(username)) {
			System.out.println("Utente già registrato");
			return true;
		}
		
		else {
			return false;
		}
	}

}
