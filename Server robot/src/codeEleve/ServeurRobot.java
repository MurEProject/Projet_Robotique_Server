package codeEleve;

import java.io.BufferedReader;
import java.io.DataInputStream;
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
			etat = mt.etat_bat();
			System.out.println("Etat de la batterie = " + Float.toString(etat));

			socketserver = new ServerSocket(2009);
			System.out.println("Le serveur est à l'écoute du port "
					+ socketserver.getLocalPort());
			socketduserveur = socketserver.accept();
			System.out.println("Un client s'est connecté");
			Process p = Runtime
					.getRuntime()
					.exec("raspistill -t 12000000 -tl 500 -q 10 -w 300 -h 200 -o /var/www/image2.jpg");
			out = new PrintWriter(socketduserveur.getOutputStream());
			Thread capteurs = new Thread(new Capteurs(out));
			capteurs.start();

			JSONObject jsonObject = null;
			/** Boucle de commande **/
			while (true) {
				/** On récupère le JSON dans un buffer **/
				socketduserveur = socketserver.accept();
				out = new PrintWriter(socketduserveur.getOutputStream());
				DataInputStream in = new DataInputStream(
						socketduserveur.getInputStream());
				byte[] messageByte = new byte[1000];
				int bytesRead = in.read(messageByte);
				System.out.println("bytesRead = " + bytesRead);
				if (bytesRead > 0) {
					// Une nouvelle commande a été reçue
					cmd = new String(messageByte, 0, bytesRead);

					/** Lecture du json **/
					JSONParser parser = new JSONParser();
					jsonObject = (JSONObject) parser.parse(cmd);
					if (jsonObject.get("direction") != null)
						oldCmd = cmd;
					else if (oldCmd == null)
						oldCmd = "1";

				} else {
					// Si aucune nouvelle commande est arrivée, on execute
					// l'ancien ordre
					cmd = oldCmd;
					/** Lecture du json **/
					JSONParser parser = new JSONParser();
					jsonObject = (JSONObject) parser.parse(cmd);
				}
				// Contrôle des roues
				if (jsonObject.get("direction") != null) {
					if (jsonObject.get("direction").equals("avancer")) {
						System.out.println("Avancer ");
						Integer vitesse = Integer.valueOf(jsonObject.get(
								"vitesse").toString());
						mt.avancer(vitesse.intValue());
						Thread.sleep(10);
					}

					if (jsonObject.get("direction").equals("gauche")) {
						System.out.println("Gauche"); // trace
						Integer vitesse = Integer.valueOf(jsonObject.get(
								"vitesse").toString());
						mt.tourner_g(vitesse.intValue());
						Thread.sleep(10);

					}

					if (jsonObject.get("direction").equals("droite")) {
						System.out.println("Droite");
						Integer vitesse = Integer.valueOf(jsonObject.get(
								"vitesse").toString());
						mt.tourner_d(vitesse.intValue());
						Thread.sleep(10);

					}

					if (jsonObject.get("direction").equals("stop")) {
						System.out.println("Arret");
						mt.arreter(0);
						Thread.sleep(10);
					}

					if (jsonObject.get("direction").equals("recule")) {
						System.out.println("Recule");
						Integer vitesse = Integer.valueOf(jsonObject.get(
								"vitesse").toString());
						mt.reculer(vitesse.intValue());
						Thread.sleep(10);
					}
				}
				// Contrôle de la pince
				if (jsonObject.get("pince") != null) {
					if (jsonObject.get("pince").equals("ouvrir")) {
						System.out.println("Ouverture de la pince"); // trace
																	// locale
						mt.ouvrirpince();
						Thread.sleep(10);

					}

					if (jsonObject.get("pince").equals("fermer")) {
						System.out.println("Fermeture de la pince"); // trace
																	// locale
						mt.fermerpince();
						Thread.sleep(10);

					}

					if (jsonObject.get("pince").equals("monter")) {
						System.out.println("Montée de la pince"); // trace
																	// locale
						mt.leverpince();
						Thread.sleep(10);

					}

					if (jsonObject.get("pince").equals("descendre")) {
						System.out.println("Descente de la pince"); // trace
																	// locale
						mt.descendrepince();
						Thread.sleep(10);

					}
				}
				// Contrôle de la camera
				if (jsonObject.get("camera") != null) {
					Camera cam = new Camera(mt.getPIC());
					if (jsonObject.get("camera").equals("image")) {
						cam.getImage(out);
					}
					if (jsonObject.get("camera").equals("droite")) {
						cam.cameraDroite();
					}
					if (jsonObject.get("camera").equals("gauche")) {
						cam.cameraGauche();
					}
					if (jsonObject.get("camera").equals("centre")) {
						cam.cameraCentre();
					}
				}
				// Contrôle du GPS
				if (jsonObject.get("GPS") != null) {
					System.out.println("Envoie des coordonnées GPS"); // trace
																	// locale
					dat_gps = satelitte.data();
					out.flush();

					JSONObject obj = new JSONObject();
					obj.put("coordoX", Float.toString(dat_gps[0]));
					obj.put("coordoY", Float.toString(dat_gps[1]));
					System.out.println(obj);
					out.write("" + obj);
					out.flush();
				}
				// Contrôle de la batterie
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
					System.out.println("Aucun ordre à effectuer");
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
