package Client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JOptionPane;

import GUI.GUITuring;
import Server.Configuration;
import Server.RegistrationInterface;

public class RegistrationHandler extends Thread {

	private GUITuring frame;
	private int PORT_RMI;

	public RegistrationHandler(GUITuring frame) {
		this.frame = frame;
		PORT_RMI = Configuration.PORT_RMI;
	}

	public void run() {

		try {
			String username = frame.getUsername();
			String password = frame.getPassword().toString();

			Registry reg = LocateRegistry.getRegistry(PORT_RMI);
			RegistrationInterface server = (RegistrationInterface) reg.lookup("REG_SERVICE");

			if (server.registrationRequest(username, password)) {
				JOptionPane.showMessageDialog(null, "Utente registrato con successo");
			} else {
				JOptionPane.showMessageDialog(null, "Utente già registrato");
			}

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

	}
}
