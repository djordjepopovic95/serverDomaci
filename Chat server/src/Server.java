import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Server {

	static LinkedList<ServerNit> klijenti = new LinkedList<ServerNit>();
	
	public static void main(String[] args) {
		int port = 6666;
		Socket soket = null;
		
		try {
			ServerSocket serverSoket = new ServerSocket(port);
			while(true){
				soket = serverSoket.accept();
				klijenti.addLast(new ServerNit(soket, klijenti));
				klijenti.getLast().start();
				//break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
