package codeEleve;

import java.io.PrintWriter;
import com.pi4j.io.i2c.*;

public class Capteurs implements Runnable {

	public I2CBus bus;
	public I2CDevice PIC;
	PrintWriter out;
	Moteurtest mt;

	public Capteurs(PrintWriter _out) throws Exception {
		out = _out;
		bus = I2CFactory.getInstance(1);
		PIC = bus.getDevice(0x70);
	}

	int capt;
	int ir_avg;
	int ir_avd;
	int ir_arg;
	int ir_ard;

	public void run() {
		boolean lancement = true;

		try {
			mt = new Moteurtest();
			mt.start();
			
			while (lancement = true) {

				PIC.write((byte) 0x50);
				PIC.write((byte) 0x00);
				Thread.sleep(1);
				capt = PIC.read();
				ir_avg = capt & 0x08;
				ir_avd = capt & 0x04;
				ir_arg = capt & 0x02;
				ir_ard = capt & 0x01;

				/*if ((ir_ard == 0)) {
					System.out.println("clean");
					out.println("");
					out.flush();

				}*/

				if (ir_ard == 1) {
					System.out.println("Attention ARD");
					mt.arreter(0);
					out.println("Attention ARD");
					out.flush();
				}
				else if (ir_avg == 1) {
					System.out.println("Attention AVG");
					mt.arreter(0);
					out.println("Attention AVG");
					out.flush();
				}
				else if (ir_avd == 1) {
					System.out.println("Attention AVD");
					mt.arreter(0);
					out.println("Attention AVD");
					out.flush();
				}
				else if (ir_arg == 1) {
					System.out.println("Attention ARG");
					mt.arreter(0);
					out.println("Attention ARG");
					out.flush();
				}
				Thread.sleep(1000);
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}

	}

}
