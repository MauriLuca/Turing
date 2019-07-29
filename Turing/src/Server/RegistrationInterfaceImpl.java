package Server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class RegistrationInterfaceImpl extends RemoteObject implements RegistrationInterface {
	
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String,User> registeredUsers;
	
	public RegistrationInterfaceImpl(ConcurrentHashMap<String,User> utenti) throws RemoteException{	
		
		registeredUsers = utenti;
		
	}
	
	
	//Registrazione di un nuovo utente
	public boolean registrationRequest(String username, String password) throws RemoteException {
		
			User utente = new User(username, password);
			registeredUsers.putIfAbsent(username, utente);
			return true;
	}


	public boolean isRegistered(String username) throws RemoteException {
		if(registeredUsers.containsKey(username)) {
			return true;
		}
		
		else {
			return false;
		}
	}

}
