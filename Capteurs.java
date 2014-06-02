package codeEleve;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.*;
import java.net.UnknownHostException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.*;



 public  class Capteurs implements Runnable {


        public I2CBus bus;
        public I2CDevice PIC;
        PrintWriter out;

        public Capteurs(PrintWriter _out) throws Exception
         {
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


                                try{

			while (lancement = true) {

                                PIC.write ((byte)0x50);
                                PIC.write ((byte)0x00);
                                Thread.sleep(1);
                                capt = PIC.read();
                                ir_avg = capt & 0x08;
                                ir_avd = capt & 0x04;
                                ir_arg = capt & 0x02;
                                ir_ard = capt & 0x01;

                        if ((ir_ard == 0))
                         {   
                             System.out.println("clean");
                             out.println("");
                             out.flush();

                          }

	 		else if (ir_ard == 1)
                         {      
                                System.out.println("Attention ARD");
                                out.println("Attention ARD");
                                out.flush();

                        }


                                Thread.sleep(1000);

                                                }
                         }
                      catch ( Exception ex ){System.out.println(ex);}

                        }

                                        }

















