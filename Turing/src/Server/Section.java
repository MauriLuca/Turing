package Server;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Section {

	private Integer numOfSection;
	private Path path;
	private boolean edit;

	public Section(String pathDir, int numSection) {

		this.numOfSection = numSection;
		String filename = pathDir + "/" + "Section_" + this.numOfSection.toString() + ".txt";
		this.path = Paths.get(filename);

		//creo il file .txt della sezione
		File file = new File(filename);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.edit = false;
	}

	public synchronized void sendSection(int port, SocketChannel socketChannel) {
		try {
			FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);

			long position = 0L;
			long size = fc.size();

			while(position < size) {
				position+= fc.transferTo(position, 2048, socketChannel);
			}

			fc.close();
			fc = null; 	
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void receiveSection(SocketChannel socketChannel, String nameDocument) {
		//routine di ricezione file
		FileChannel fc = null;
		String path = Configuration.path + "/" + nameDocument;
		try {
			Files.createDirectories(Paths.get(path));

			path = path + "/Section_" + numOfSection + ".txt";

			//apro il file channel in mdalità scrittura
			fc = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			//alloco il buffer supponendo file di testo di dimensione inferiore a 2kb
			ByteBuffer buf = ByteBuffer.allocate(2048);

			//leggo e scrivo sul buffer
			while(socketChannel.read(buf) > 0) {
				buf.flip();
				fc.write(buf);
				buf.clear();
			}

			//chiudo filechannel
			fc.close();
			fc = null;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public synchronized Path getPath() {
		return path;
	}


	public synchronized boolean isEdit() {
		return edit;
	}


	public synchronized void setEdit(boolean edit) {
		this.edit = edit;
	}
}
