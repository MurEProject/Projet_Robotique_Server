package codeEleve;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;


import org.json.simple.JSONObject;

import com.pi4j.io.i2c.I2CDevice;

public class Camera {
	String path;
	public I2CDevice PIC;

	Camera(I2CDevice PIC) {
		path = "/var/www/image2.jpg";
		this.PIC = PIC;
	}

	// Renvoie l'image sous forme de byte au client web
	void getImage(PrintWriter out) throws IOException {
		File fi = new File(path);
		byte [] filecontent = Files.readAllBytes(fi.toPath());
		
		JSONObject obj = new JSONObject();
		obj.put("Image", filecontent);
		out.write("" + obj);
		out.flush();
	}

	// Tourne la camera sur sa gauche
	public void cameraGauche() throws Exception {
		PIC.write((byte) 0x30);
		PIC.write((byte) 0x00);
		System.out.println("camera left");
	}
	
	// Tourne la camera sur sa droite
	public void cameraDroite() throws Exception {
		PIC.write((byte) 0x31);
		PIC.write((byte) 0x00);
		System.out.println("camera right");
	}

	// Centre la camera
	public void cameraCentre() throws Exception {
		PIC.write((byte) 0x32);
		PIC.write((byte) 0x00);
		System.out.println("camera center");
	}

}
