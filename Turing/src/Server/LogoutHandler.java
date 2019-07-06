package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import GUI.GUILogged;
import GUI.GUITuring;

public class LogoutHandler extends Thread {

	//private Socket clientSock; //Socket TCP
	private DataOutputStream outStream; //Stream in output
	private BufferedReader inStream; //Stream in input
	private String username; //Username che effettua la richiesta di logout
	private GUILogged frame; //Interfaccia grafica
	private String password; //password dell'utente
	
	public LogoutHandler(Socket clientSock, DataOutputStream outStream, BufferedReader inStream, GUILogged frame) {
		
		if(frame == null || clientSock == null || outStream == null || inStream == null) throw new NullPointerException();
		
		//controllo che il socket non sia chiuso
		if(clientSock.isClosed()) throw new IllegalArgumentException();
		
		//this.clientSock = clientSock;
		this.outStream = outStream;
		this.inStream = inStream;
		this.frame = frame;
		
	}

	public void run() {
		
		//imposto il tipo di operazione
		String op = "logout" + '\n';
		
		try {
			//comunico l'operazione all'handler
			outStream.writeBytes(op);
			
			String temp = inStream.readLine();
			
			//se ho effettuato il logout con successo
			if(temp.contains("successo")) {
				JOptionPane.showMessageDialog(null, "Logout effettuato con successo");
				//chiudo la GUI di logout
				frame.setVisible(false);
				frame.dispose();
				
				//clientSock.close();
				outStream.close();
				inStream.close();
			}
			else {
				JOptionPane.showMessageDialog(null, "Errore nell'eseguire il logout");
			}
		}
		catch(IOException e){
			e.printStackTrace();	
		}
		
	}
}
