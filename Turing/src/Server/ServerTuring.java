package Server;

import java.io.File;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import Server.Configuration;

public class ServerTuring {
	
	private static ConcurrentHashMap<String,User> registeredUsers; //HashMap che memorizza tutti gli utenti registrati
	private static ConcurrentHashMap<String, User> onlineUsers; //HashMap che memorizza tutti gli utenti online
	private static ConcurrentHashMap<String,Document> documentList; //HashMap che memorizza tutti i documenti creati
	private static ThreadPoolExecutor tp; //ThreadPool
	private static ServerSocket serverSock;//Socket per la connessione dei client
	private static ServerSocket notifySock;//Socket per le notifiche

	public static void main(String args[]) throws Exception{
		
		System.out.println("Server in ascolto...");
		
		registeredUsers = new ConcurrentHashMap<String,User>();
		onlineUsers = new ConcurrentHashMap<String,User>();
		tp = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		serverSock = new ServerSocket(Configuration.PORT_TCP);
		notifySock = new ServerSocket(Configuration.PORT_NOTIFY);
		
		documentList = new ConcurrentHashMap<String, Document>();
		
		//elimino se esiste la cartella contenente tutti i file dei clients
		if(Files.exists(Configuration.clientPath)) {
			deleteFolderRecursively(Configuration.clientPath);
		}
		//se la cartella già esiste la elimina, eliminando ricorsivamente tutti gli elementi in essa contenuti
		if(Files.exists(Configuration.path)) {
			deleteFolderRecursively(Configuration.path);
		}
		//poi crea la cartella che conterrà tutti i documenti
		Files.createDirectory(Configuration.path);
		
		try {
			
			RegistrationInterfaceImpl serverRMI = new RegistrationInterfaceImpl(registeredUsers);
			
			RegistrationInterface stub = (RegistrationInterface) UnicastRemoteObject.exportObject(serverRMI, 0);
			
			LocateRegistry.createRegistry(Configuration.PORT_RMI);
			Registry reg = LocateRegistry.getRegistry(Configuration.PORT_RMI);
			reg.bind("REG_SERVICE", stub);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		//Avvio il Listener
		Thread listener = new Listener(registeredUsers, onlineUsers, documentList, serverSock, notifySock, tp);	
		listener.start();
		System.out.println("Listener avviato...");
	}
	
	
	//funzione che elimina le cartelle ricorsivamente dato un path in input
	public static void deleteFolderRecursively(Path path) {

	        String folder = path.toString();
	        recursiveDelete(new File(folder));

	}
	
	public static void recursiveDelete(File file) {
		
        if (!file.exists())
            return;
        
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }
        file.delete();
    }
}
