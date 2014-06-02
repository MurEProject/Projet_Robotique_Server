package codeEleve;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.*;

public class ServeurRobot {

	public static void main(String[] zero) {

		ServerSocket socketserver;
		Socket socketduserveur;
		PrintWriter out;
		String cmd = "1";
		Moteurtest mt;
		String oldCmd = "";

		float[] dat_gps;
		float etat;

		try {
			/** Initialisation du robot **/
			mt = new Moteurtest();
			GPS satelitte = new GPS();
			mt.start();

			socketserver = new ServerSocket(2009);
			System.out.println("Le serveur est à l'écoute du port "
					+ socketserver.getLocalPort());
			socketduserveur = socketserver.accept();
			java.io.File fichier = new java.io.File("/home/pi/ok");
			fichier.createNewFile();

			System.out.println("Un zéro s'est connecté");
			out = new PrintWriter(socketduserveur.getOutputStream());
			out.println("Vous êtes connecté zéro !");
			out.flush();

			Process p = Runtime
					.getRuntime()
					.exec("raspistill -t 12000000 -tl 500 -q 10 -w 300 -h 200 -o /var/www/image2.jpg");

			Thread capteurs = new Thread(new Capteurs(out));
			capteurs.start();

			/** Boucle de commande **/
			while (true) {
				/** On récupère le JSON dans un buffer **/
				InputStreamReader inputStream = new InputStreamReader(
						socketduserveur.getInputStream());
				BufferedReader input = new BufferedReader(inputStream);
				cmd = input.readLine();
				if (cmd != null) {
					oldCmd = cmd;
				}
				else
				{
					if(oldCmd == null)
						break;
					cmd = oldCmd;
					
				}
					
					/** Lecture du json **/
					JSONParser parser = new JSONParser();
					JSONObject jsonObject = null;
					System.out.println("Command: " + cmd);
					jsonObject = (JSONObject) parser.parse(cmd); // On lit la
																	// valeur
																	// pour la
																	// direction

					System.out.println("Commande recue : " + cmd); // trace
																	// locale

					if (jsonObject.get("direction") != null) {
						if (jsonObject.get("direction").equals("avancer")) {
							System.out.println("Avannnncceeee "); // trace
																	// locale
							mt.avancer(200);
							Thread.sleep(10);

						}

						if (jsonObject.get("direction").equals("gauche")) {
							System.out.println("Gauchhhe touuuteeee "); // trace
																		// locale
							mt.tourner_g(100);
							Thread.sleep(10);

						}

						if (jsonObject.get("direction").equals("droite")) {
							System.out.println("Droiiiitee touuuuteeee"); // trace
																			// locale
							mt.tourner_d(100);
							Thread.sleep(10);

						}

						if (jsonObject.get("direction").equals("stop")) {
							System.out.println("Arreteeee toiii grooos "); // trace
																			// locale
							mt.arreter(0);
							Thread.sleep(10);

						}

						if (jsonObject.get("direction").equals("recule")) {
							System.out.println("Reccculllleee "); // trace
																	// locale
							mt.reculer(100);
							Thread.sleep(10);
						}
					}
					if (jsonObject.get("pince") != null) {
						if (jsonObject.get("pince").equals("ouvrir")) {
							System.out.println("ecarte les cuisses "); // trace
																		// locale
							mt.ouvrirpince();
							Thread.sleep(10);

						}

						if (jsonObject.get("pince").equals("fermer")) {
							System.out.println("ressere les cuisses "); // trace
																		// locale
							mt.fermerpince();
							Thread.sleep(10);

						}

						if (jsonObject.get("pince").equals("monter")) {
							System.out.println("monte sur le bureau "); // trace
																		// locale
							mt.leverpince();
							Thread.sleep(10);

						}

						if (jsonObject.get("pince").equals("descendre")) {
							System.out.println("descend du bureau"); // trace
																		// locale
							mt.descendrepince();
							Thread.sleep(10);

						}
					}
					if (cmd.equals("9")) {
						System.out.println("transfert postion GPS "); // trace
																		// locale
						dat_gps = satelitte.data();
						out.flush();
						socketduserveur.getOutputStream().write(
								("Lat = " + Float.toString(dat_gps[0])
										+ "   Long=" + Float
										.toString(dat_gps[1])).getBytes());
						out.flush();

					}

					if (cmd.equals("15")) {
						System.out.println("rÃ©cupÃ©ration Ã©tat batterie "); // trace
																				// locale
						etat = mt.etat_bat();
						out.flush();
						socketduserveur.getOutputStream().write(
								("etat batterie = " + Float.toString(etat))
										.getBytes());
						out.flush();
					}

					if (cmd.equals("20")) {
						System.out.println("goodbye"); // trace locale
						Runtime.getRuntime().exec("sudo shutdown -h 0");
						break;
					}

					else {
						System.out
								.println("donne moi des ordres cochonnnneeesss");
						Thread.sleep(50);
						
					}
			}
			socketduserveur.close();
			socketserver.close();
			p.destroy();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
