	package Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Section {

	private Integer numSection;
	private Path path;
	private boolean edit;
	private ReentrantReadWriteLock readWriteLock; //reentrantreadwritelock per scrivere e le sezioni
	Lock lock; 
	Lock read; //lock in lettura
	Lock write; //lock in scrittura
	
	public Section(String pathDir, int numSection) {
		
		this.numSection = numSection;
		String filename = pathDir + "/" + "Section_" + this.numSection.toString() + ".txt";
		this.path = Paths.get(filename);
		
		//creo il file .txt della sezione
		File file = new File(filename);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.edit = false;
		
		readWriteLock = new ReentrantReadWriteLock();
		read = readWriteLock.readLock();
		write = readWriteLock.writeLock();
	}
	
	public void sendSection(){
		
	}
	
	public void receiveSection() {
		
	}


	public Path getPath() {
		return path;
	}


	public boolean isEdit() {
		return edit;
	}


	public void setEdit(boolean edit) {
		this.edit = edit;
	}
}
