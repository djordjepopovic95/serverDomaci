import java.io.*;
import java.net.*;
import java.util.LinkedList;

import javax.swing.SingleSelectionModel;

public class ServerNit extends Thread {

	BufferedReader ulazniTokOdKlijenta = null;
	PrintStream izlazniTokKaKlijentu = null;
	Socket soketZaKomunikaciju = null;
	LinkedList<ServerNit> klijenti;
	DatagramSocket dSoket = null;
	String ime;
	String pol;
	static boolean kraj = false;
	static String listaKlijenata = "";
	int port;
	InetAddress adr;
	// PrintWriter out;

	public ServerNit(Socket soket, LinkedList<ServerNit> klijenti, DatagramSocket dSoket) {
		this.soketZaKomunikaciju = soket;
		this.klijenti = klijenti;
		this.dSoket = dSoket;
	}

	public String getIme() {
		return ime;
	}

	public String getPol() {
		return pol;
	}

	public int getPort() {
		return port;
	}

	public InetAddress getAdr() {
		return adr;
	}

	public Socket getSoketZaKomunikaciju() {
		return soketZaKomunikaciju;
	}

	@Override
	public void run() {
		String linija;
		String to;

		int postoji = 0;

		try {
			ulazniTokOdKlijenta = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
			izlazniTokKaKlijentu = new PrintStream(soketZaKomunikaciju.getOutputStream());
			// out = new
			// PrintWriter("D:\\Faks\\Treæa\\RMT\\podaciSaServera.txt");

			getInformationUDP();

			izlazniTokKaKlijentu.println("Unesite ime: ");
			ime = ulazniTokOdKlijenta.readLine();
			izlazniTokKaKlijentu.println("Unesite pol (M/Z): ");
			pol = ulazniTokOdKlijenta.readLine();
			pol = pol.toUpperCase();
			
			while(!(pol.equals("Z") || pol.equals("M"))){
				izlazniTokKaKlijentu.println("Greska pri unosu pola, ponovite unos.");
				pol = ulazniTokOdKlijenta.readLine();
				pol = pol.toUpperCase();
			}

			izlazniTokKaKlijentu.println("Zdravo, " + ime + ". Za izlaz unesite ///quit.");

			while (true) {
				linija = ulazniTokOdKlijenta.readLine();

				if (linija.startsWith("///quit")) {
					kraj = true;
					break;
				}

				

				izlazniTokKaKlijentu.println("Kome saljete poruku?");

				listaKlijenata = "";
				for (int i = 0; i < klijenti.size(); i++) {
					if (!klijenti.get(i).getPol().equals(pol)) {
						listaKlijenata = listaKlijenata + klijenti.get(i).getIme() + " ";
					}
				}

				byte[] podaciZaKlijenta = new byte[1024];

				podaciZaKlijenta = listaKlijenata.getBytes();

				DatagramPacket dPaket = new DatagramPacket(podaciZaKlijenta, podaciZaKlijenta.length, adr, port);
				System.out.println(port);
				dSoket.send(dPaket);

				System.out.println("poslat datagram");
				System.out.println(listaKlijenata);

				to = ulazniTokOdKlijenta.readLine();
				for(int i = 0; i < klijenti.size(); i++){
					if (to.contains(klijenti.get(i).getIme())){
						postoji++;
					}
				}
				
				if (postoji == 0){
					izlazniTokKaKlijentu.println("Niste uneli ime sa liste ili je klijent napustio chat. Ponovite slanje.");
					continue;
				} else {
					postoji = 0;
				}
				
				System.out.println(to);

				for (int i = 0; i < klijenti.size(); i++) {
					if (to.contains(klijenti.get(i).getIme())) {
						if (!klijenti.get(i).getSoketZaKomunikaciju().isClosed()){
						klijenti.get(i).izlazniTokKaKlijentu.println("[" + ime + "]: " + linija);
						System.out.println("ovo se izvrsava");
						}else{
							izlazniTokKaKlijentu.println("Nije moguce poslati poruku> " + klijenti.get(i).getIme());
						}					
						
					}
				}
				
			}
			for (int i = 0; i < klijenti.size(); i++) {
				if (klijenti.get(i) != this) {
					klijenti.get(i).izlazniTokKaKlijentu.println("Korisnik " + ime + " je napustio sobu.");
				}
			}

			izlazniTokKaKlijentu.println("///Dovidjenja!");

			soketZaKomunikaciju.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < klijenti.size(); i++) {
			if (klijenti.get(i) == this) {
				klijenti.remove(i);
			}
		}
	}

	public void getInformationUDP() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (!kraj) {
					try {

						System.out.println("Usao u petlju");

						byte[] podaciOdKlijenta = new byte[1024];

						DatagramPacket paketOdKlijenta = new DatagramPacket(podaciOdKlijenta, podaciOdKlijenta.length);
						dSoket.receive(paketOdKlijenta);
						System.out.println("Primio paket.");
						adr = paketOdKlijenta.getAddress();
						System.out.println("Adresa> " + adr);
						port = paketOdKlijenta.getPort();
						System.out.println("Port> " + port);

					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {

					}
					return;
				}
			}
		});
		t.start();
	}

}
