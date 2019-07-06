package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrationInterface extends Remote {
	
	public boolean registrationRequest(String username, String password) throws RemoteException;
	
	public boolean isRegistered(String username) throws RemoteException;

}
