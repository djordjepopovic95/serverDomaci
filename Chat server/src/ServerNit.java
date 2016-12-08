import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class ServerNit extends Thread {

	BufferedReader ulazniTokOdKlijenta = null;
	PrintStream izlazniTokKaKlijentu = null;
	Socket soketZaKomunikaciju = null;
	LinkedList<ServerNit> klijenti;
	DatagramSocket dSoket = null;
	String ime;
	String pol;

	public ServerNit(Socket soket, LinkedList<ServerNit> klijenti) {
		this.soketZaKomunikaciju = soket;
		this.klijenti = klijenti;
	}

	public String getIme() {
		return ime;
	}

	public String getPol() {
		return pol;
	}

	@Override
	public void run() {
		String linija;
		String listaKlijenata = "";
		String to;
		int[] potvrda = new int[klijenti.size()];

		try {
			ulazniTokOdKlijenta = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
			izlazniTokKaKlijentu = new PrintStream(soketZaKomunikaciju.getOutputStream());

			izlazniTokKaKlijentu.println("Unesite ime: ");
			ime = ulazniTokOdKlijenta.readLine();
			izlazniTokKaKlijentu.println("Unesite pol (M/Z): ");
			pol = ulazniTokOdKlijenta.readLine();
			pol = pol.toUpperCase();

			izlazniTokKaKlijentu.println("Zdravo, " + ime + ". Za izlaz unesite ***quit.");

			while (true) {
				linija = ulazniTokOdKlijenta.readLine();
				if (linija.startsWith("***quit")) {
					break;
				}

				izlazniTokKaKlijentu.println("Kome saljete poruku?");

				dSoket = new DatagramSocket();
				byte[] podaciZaKlijenta = new byte[1024];
				for (int i = 0; i < klijenti.size(); i++) {
					if (!klijenti.get(i).getPol().equals(pol)) {
						listaKlijenata = listaKlijenata + " " + klijenti.get(i).getIme();
					}
				}
				InetAddress IPAdresa = InetAddress.getByName("localhost");
				podaciZaKlijenta = listaKlijenata.getBytes();
				DatagramPacket dPaket = new DatagramPacket(podaciZaKlijenta, podaciZaKlijenta.length,
						IPAdresa, 6666);
				dSoket.send(dPaket);
				System.out.println("poslat datagram");
				System.out.println(listaKlijenata);
				to = ulazniTokOdKlijenta.readLine();
				System.out.println(to);

				for (int i = 0; i < klijenti.size(); i++) {
					// if (to.contains(klijenti.get(i).getIme())) {
					klijenti.get(i).izlazniTokKaKlijentu.println("[" + ime + "]: " + linija);
					// if
					// (klijenti.get(i).ulazniTokOdKlijenta.readLine().equals("1"))
					// {
					// potvrda[i] = 1;
					// }
					// fali upisivanje podataka u fajl
					// izbaci potvrdu iz zadatka pa je uradi naknadno
				}
			}
			/*
			  for (int i = 0; i < potvrda.length; i++) { if (potvrda[i] != 1) {
			 izlazniTokKaKlijentu.println("Poruka nije dostavljena klijentu: "
			 + klijenti.get(i).getIme()); } potvrda[i] = 0; }
			 */

			for (int i = 0; i < klijenti.size(); i++) {
				if (klijenti.get(i) != this) {
					klijenti.get(i).izlazniTokKaKlijentu.println("Korisnik " + ime + " je napustio sobu.");
				}
			}

			izlazniTokKaKlijentu.println("***Dovidjenja!");

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

}
