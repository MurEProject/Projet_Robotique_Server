package codeEleve;

import com.pi4j.io.i2c.*;
import com.pi4j.io.gpio.*;

public class Moteurtest {

	public I2CBus bus;
	public I2CDevice PIC;

	int val1;
	int val2;
	float bat;

	public Moteurtest() throws Exception {
		bus = I2CFactory.getInstance(1);
		PIC = bus.getDevice(0x70);
	}

	public void start() throws Exception {
		PIC.write((byte) 0x01);
		PIC.write((byte) 0x00);
		System.out.println("Ready to move");
	}

	public void stop() throws Exception {
		PIC.write((byte) 0x00);
		PIC.write((byte) 0x00);
		System.out.println("Feel tired");
	}

	public void avancer(int vitesse) throws Exception {
		PIC.write((byte) 0x10);
		PIC.write((byte) vitesse);
		System.out.println("and we gooooo");
	}

	public void arreter(int vitesse) throws Exception {
		PIC.write((byte) 0x11);
		PIC.write((byte) vitesse);
		System.out.println("and we stooooop");
	}

	public void tourner_d(int vitesse) throws Exception {
		PIC.write((byte) 0x12);
		PIC.write((byte) vitesse);
		System.out.println("all right");
	}

	public void tourner_g(int vitesse) throws Exception {
		PIC.write((byte) 0x13);
		PIC.write((byte) vitesse);
		System.out.println("all left");
	}

	public float etat_bat() throws Exception {
		PIC.write((byte) 0x70);
		PIC.write((byte) 0x00);
		Thread.sleep(1);
		val1 = PIC.read();
		val2 = PIC.read();
		bat = ((val1 << 8) + val2) * (3.3f * 3.36f) / (1024f) + 1;
		System.out.println("Etat batterie: " + bat);
		return bat;
	}

	public void reculer(int vitesse) throws Exception {
		PIC.write((byte) 0x14);
		PIC.write((byte) vitesse);
		System.out.println("and baaaaack");
	}

	public void ouvrirpince() throws Exception {
		PIC.write((byte) 0x20);
		PIC.write((byte) 0x00);
		System.out.println("ecarte les cuisseeees");
	}

	public void fermerpince() throws Exception {
		PIC.write((byte) 0x21);
		PIC.write((byte) 0x00);
		System.out.println("referme les cuisseeees");
	}

	public void leverpince() throws Exception {
		PIC.write((byte) 0x22);
		PIC.write((byte) 0x00);
		System.out.println("monte sur le bureau");
	}

	public void descendrepince() throws Exception {
		PIC.write((byte) 0x23);
		PIC.write((byte) 0x00);
		System.out.println("descend du bureau");
	}
	
	public I2CDevice getPIC() {
		return PIC;
	}

	public void capteurs() throws Exception {

		int capt;
		int ir_avg;
		int ir_avd;
		int ir_arg;
		int ir_ard;

		Moteurtest mt = new Moteurtest();
		mt.start();
		while (true) {
			Thread.sleep(50);
			PIC.write((byte) 0x50);
			PIC.write((byte) 0x00);
			Thread.sleep(1);
			capt = PIC.read();
			ir_avg = capt & 0x08;
			ir_avd = capt & 0x04;
			ir_arg = capt & 0x02;
			ir_ard = capt & 0x01;
			
			if (ir_ard == 1) {
				System.out.println("Attention ARD");
				mt.arreter(0);
			}
			else if (ir_avg == 1) {
				System.out.println("Attention AVG");
				mt.arreter(0);
			}
			else if (ir_avd == 1) {
				System.out.println("Attention AVD");
				mt.arreter(0);
			}
			else if (ir_arg == 1) {
				System.out.println("Attention ARG");
				mt.arreter(0);
			}
		}

	}

	public static void main(String[] args) throws Exception {

		Runtime runtime = Runtime.getRuntime();
		Moteurtest mot = new Moteurtest();
		mot.start();

		while (true) {
			mot.avancer(100);
			mot.etat_bat();
			Thread.sleep(500);
			mot.arreter(0);
			Thread.sleep(500);
		}
	}

}
