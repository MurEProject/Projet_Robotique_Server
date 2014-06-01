import com.pi4j.io.i2c.*;
import com.pi4j.io.gpio.*;

public class Etatbatterie
 {

        public I2CBus bus;
        public I2CDevice PIC;
        int val1;
        int val2;
        float bat;

        public Etatbatterie() throws Exception
         {
                bus = I2CFactory.getInstance(1);
                PIC = bus.getDevice(0x70);
         }

	 public float etat_bat() throws Exception
         {
                PIC.write ((byte)0x70);
                PIC.write ((byte)0x00);
                Thread.sleep(1);
                val1 = PIC.read();
                val2 = PIC.read();
                bat = ((val1<<8)+val2)*(3.3f*3.36f)/(1024f)+1;
                System.out.println("Etat batterie: " + bat);
                return bat;
         }




}
