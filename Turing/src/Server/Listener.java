package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class Listener extends Thread{
	
	private ConcurrentHashMap<String,User> registeredUsers; //HashMap che memorizza tutti gli utenti registrati
	private ConcurrentHashMap<String, User> onlineUsers; //HashMap che memorizza tutti gli utenti online
	private ConcurrentHashMap<String,Document> documentList; //HashMap che memorizza tutti i documenti creati
	private ServerSocket serverSock; //socket per effettuare l'accept delle connessioni
	private ThreadPoolExecutor tp; //ThreadPool
	
	public Listener(ConcurrentHashMap<String,User> registeredUsers, ConcurrentHashMap<String,User> onlineUsers,ConcurrentHashMap<String,Document> documentList, ServerSocket serverSock, ThreadPoolExecutor tp) {
		this.registeredUsers = registeredUsers;
		this.serverSock = serverSock;
		this.tp = tp;
		this.onlineUsers = onlineUsers;
		this.documentList = documentList;
		
	}
	
	public void run() {
		
		while(true) {
			
			Socket connectionSocket = null;
			
			try {
				connectionSocket = serverSock.accept();
				RequestHandler task = new RequestHandler(registeredUsers, onlineUsers, documentList, connectionSocket);
				tp.execute(task);
				
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
	
}
