package codeEleve;

import java.util.Date;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

public class GPS implements SerialDataListener {
	public Serial uart0;
	public String trame = "";
	public String[] lat_long;
	public int i = 0;
	public String[] GPGGA;
	public float lat;
	public float lg;
	public float qt_gps;
	public float nb_gps;

	public GPS() throws Exception {
		System.out.println("Démarrage gps");

		uart0 = SerialFactory.createInstance();

		System.out
				.println("connection utilisant les réglages suivants: 9600/N/8/l");
		uart0.open(Serial.DEFAULT_COM_PORT, 9600);

		uart0.addListener(this);
		System.out.println("données reçues sur le port be displayed below");
		System.out.println("SERIAL");
		uart0.flush();
	}

	public void dataReceived(SerialDataEvent event) {
		trame = trame + event.getData();

		if (trame.contains("$GPGGA") && trame.contains("$GPRMC")
				&& trame.contains("$GPVTG")) {
			/* System.out.println("trame = " + trame); */
			lat_long = trame.split("\n");

			for (int i = 0; i < lat_long.length; i++) {
				if (lat_long[i].contains("$GPGGA") && lat_long[i].contains("*")) {
					GPGGA = lat_long[i].split(",");
					lat = Float.valueOf(GPGGA[2].substring(0, 2))
							+ Float.valueOf(GPGGA[2].substring(2)) / 60;
					lg = Float.valueOf(GPGGA[4].substring(0, 3))
							+ Float.valueOf(GPGGA[4].substring(3)) / 60;
					qt_gps = Float.valueOf(GPGGA[6]);
					nb_gps = Float.valueOf(GPGGA[7]);
				}
			}

			System.out.println("latitude = " + lat + "°");
			System.out.println("longitude = " + lg + "°");
			System.out.println("qualité du signal = " + qt_gps);
			System.out.println("nombre de gps = " + nb_gps);

			uart0.removeListener(this);
		}
	}

	public float[] data() throws Exception {
		float[] dat_gps = new float[2];
		dat_gps[0] = lat;
		dat_gps[1] = lg;

		System.out.println("latitude = " + dat_gps[0] + "°");
		System.out.println("longitude = " + dat_gps[1] + "°");

		return dat_gps;
	}

	public static void main(String[] args) throws Exception {
		GPS satelitte = new GPS();
		Thread.sleep(3000);
		satelitte.data();
	}

}
