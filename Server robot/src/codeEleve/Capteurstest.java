package codeEleve;

import com.pi4j.io.i2c.*;
import com.pi4j.io.gpio.*;

        public class Capteurstest
                {

                        public I2CBus bus;
                        public I2CDevice PIC;

        public Capteurstest() throws Exception
         {
                bus = I2CFactory.getInstance(1);
                PIC = bus.getDevice(0x70);
         }

        public void start() throws Exception
         {
                PIC.write ((byte)0x01);
                PIC.write ((byte)0x00);
                System.out.println("Ready to move");
         }

        public void stop() throws Exception
         {
                PIC.write ((byte)0x00);
                PIC.write ((byte)0x00);
                System.out.println("Feel tired");
         }

        public void avancer(int vitesse) throws Exception
         {
                PIC.write ((byte)0x10);
                PIC.write ((byte)vitesse);
                System.out.println("and we gooooo");
         }

	public void capteurs() throws Exception {

                int capt;
                int ir_avg;
                int ir_avd;
                int ir_arg;
                int ir_ard;


                while (true)
                 {
                        Thread.sleep(500);
                        PIC.write ((byte)0x50);
                        PIC.write ((byte)0x00);
                        Thread.sleep(1);
                        capt = PIC.read();
                        ir_avg = capt & 0x08;
                        ir_avd = capt & 0x04;
                        ir_arg = capt & 0x02;
                        ir_ard = capt & 0x01;



                        if ((ir_avg == 1))
                         {

                                System.out.println("attention ARG");

                         }

			else if ((ir_avg == 0))
                       {      
                                System.out.println("clean");

                         }




                      }


                 }


        public static void main(String[] args) throws Exception
         {

                Runtime runtime = Runtime.getRuntime();
                Capteurstest mot = new Capteurstest();
                mot.start();
                mot.avancer(100);
                mot.capteurs();

         }



                }







