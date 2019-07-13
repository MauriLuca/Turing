package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import GUI.GUILogged;

public class ListHandler extends Thread {

	private DataOutputStream outStream; //Stream in output
	private BufferedReader inStream; //Stream in input
	private GUILogged frameLogged;


	public ListHandler(DataOutputStream outStream, BufferedReader inStream, GUILogged frameLogged) {

		if(frameLogged == null || outStream == null || inStream == null) throw new NullPointerException();

		this.outStream = outStream;
		this.inStream = inStream;
		this.frameLogged = frameLogged;

	}

	public void run() {

		//imposto il tipo di operazione
		String op = "list" + '\n';

		try {
			outStream.writeBytes(op);
			
			String username = inStream.readLine();
			
			int numOfDocuments = Integer.parseInt(inStream.readLine());
			
			frameLogged.getTextArea().append("[" + username + "]: Lista documenti accedibli\n");
			for(int i = 0; i < numOfDocuments; i++) {
				frameLogged.getTextArea().append(inStream.readLine() + '\n');
			}


		}catch(IOException e){
			e.printStackTrace();
		}

	}

}
