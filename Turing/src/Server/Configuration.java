package Server;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration {
	
	public static int PORT_RMI = 9999;
	public static int PORT_NOTIFY = 9998;
	public static int PORT_TCP = 6789;
	public static int PORT_MULTICAST = 9997;
	public static Path path = Paths.get("./Documents");
	public static Path clientPath = Paths.get("./Clients");
}
