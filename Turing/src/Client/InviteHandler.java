package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import GUI.GUILogged;

public class InviteHandler extends Thread {

	private DataOutputStream outStream; //Stream in output
	private BufferedReader inStream; //Stream in input
	private GUILogged frameLogged;
	
	public InviteHandler(DataOutputStream outStream, BufferedReader inStream, GUILogged frameLogged) {	
		
		if(frameLogged == null || outStream == null || inStream == null) throw new NullPointerException();
		
		this.outStream = outStream;
		this.inStream = inStream;
		this.frameLogged = frameLogged;
		
	}
	
	public void run() {
		
		try {
			outStream.writeBytes("invite" + '\n');
			
			String username = frameLogged.getInviteName();
			String nameDocument = frameLogged.getNameDocument();
			
			outStream.writeBytes(username + '\n');
			outStream.writeBytes(nameDocument + '\n');
			
			String esito = inStream.readLine();
			
			if(esito.contains("trovato")) {
				JOptionPane.showMessageDialog(null, "Documento non trovato");
			}
			
			else if(esito.contains("negato")){
				JOptionPane.showMessageDialog(null, "Accesso al documento negato");
			}
			
			else if(esito.contains("esistente")) {
				JOptionPane.showMessageDialog(null, "Utente non esistente");
			}
			
			else if(esito.contains("collaboratore")) {
				JOptionPane.showMessageDialog(null, "L'utente è già un collaboratore");
			}
			else {
				JOptionPane.showMessageDialog(null, "Utente invitato con successo");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
