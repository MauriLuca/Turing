package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JOptionPane;

import GUI.GUITuring;
import Server.Configuration;
import Server.RegistrationInterface;

public class ClientTuring {
	
	private static Socket clientSock; //Socket TCP
	private static DataOutputStream outStream; //Stream in output
	private static BufferedReader inStream; //Stream in input
	private static GUITuring frame;
	
	public static void main(String[] args) throws Exception {

		clientSock = new Socket("localhost", Configuration.PORT_TCP);
		outStream = new DataOutputStream(clientSock.getOutputStream());
		inStream = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
		
		//avvia l'interfaccia grafica
		try {
			frame = new GUITuring();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		//Inizializzo il pulsante di registrazione
		frame.getRegister().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(frame.getUsername().length() == 0 || frame.getPassword().length() == 0) {
					JOptionPane.showMessageDialog(null, "Insert valid Username and Password");
				}
				else {
					//parte il Thread
					Thread registrationThread = new RegistrationHandler(frame);	
					registrationThread.start();
				}
			}
			
		});
		
		//Inizializzo il pulsante di Login
		frame.getLogin().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(frame.getUsername().length() == 0 || frame.getPassword().length() == 0) {
					JOptionPane.showMessageDialog(null, "Insert valid Username and Password");
				}
				else {
					//parte il Thread
					Thread loginThread = new LoginHandler(clientSock, outStream, inStream, frame);
					loginThread.start();
				}
			}
			
		});

		try {
			
			Registry reg= LocateRegistry.getRegistry(Configuration.PORT_RMI);
			RegistrationInterface serverInt = (RegistrationInterface) reg.lookup("REG_SERVICE");
			
			System.out.println("Connessione avvenuta");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
