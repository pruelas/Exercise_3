
import java.io.*;
import java.io.InputStreamReader;
import java.net.Socket;

public final class Ex3Client {

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("codebank.xyz", 38103)) {
            System.out.println("Connected to server.");
            
            //Initialize an input stream and an output stream to communicate with server
            InputStream is = socket.getInputStream();

            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os, true, "UTF-8");

            //holds the data that will be used to calculate the checksum
            byte[] by = new byte[255];
            int j = 0;
            
            //holds the number bytes that will be sent
            int nBytes = 0;
            int counter = 0;
            int q = 0;
            int k;

            //Reads the bytes from the server and stores them in an array
            while((k = is.read()) != -1){ 
              if(counter == 0){
                 nBytes = k;
                 System.out.println("Reading " + nBytes + " bytes.");
                 System.out.println("Data received: ");
                 System.out.print("  ");
                 q++;
              }else if(counter > 0){
                 by[j] = (byte) k;
                 System.out.print(Integer.toHexString( (int) (by[j])& 0XFF).toUpperCase());
                 if((j+1)% 10== 0)
                 System.out.print("\n" +"  ");
                 j++;
             
              }
              counter++;
              if(counter > nBytes)
              break;

            }
            

            //Calculates the checksum of the data received 
            short checkSum = checkSum(by, nBytes);
            long value = checkSum;

            //Prints out the generated checksum
            System.out.println("\nGenerated checksum: " + "0x" + Long.toHexString((long)(value) & 0XFFFF).toUpperCase());

            String chSum = Long.toHexString((long)(value) & 0XFFFF);
            byte[] c = new byte[2];

            //Turns the calculated checksum into a sequence of two bytes
            if(chSum.length() == 4){ 
            c[0] = (byte) Integer.parseInt(chSum.substring(0,2), 16);
            c[1] = (byte) Integer.parseInt(chSum.substring(2,4), 16);

            }else{
            //Appends 0's to the checksum to be able to create a sequence of two bytes
            String y = chSum;
            while(y.length() !=4){
              y = "0" + y;
            }

            c[0] = (byte) Integer.parseInt(y.substring(0,2), 16);
            c[1] = (byte) Integer.parseInt(y.substring(2,4), 16);
            } 

            //Sends checksum as 2 bytes to the server
            out.write(c);

            //Read response from server
            int res = (int)is.read();

            //Determines whether or not the checksum was sent
            if(res == 1)
            System.out.println("Response good.");
            else
            System.out.println("Response bad.");

            System.out.println("\nDisconnected from server.");
      }
    }

    public static short checkSum(byte[] b, int nBytes){
        String sect = "";
        String b1 = "";
        long sum = 0;
        int q = 0;
        String[] arr = new String[2];

        //Loop appends two bytes at a time and adds them to the total sum. If overflow occurs,
        //it is cleared and added back to the sum
        for(int i = 0; i < ((nBytes+1)/2) ; i ++){ 
          b1 =  Integer.toHexString((int)(b[q]) & 0X0FF);
          if(b1.length() == 1)
          b1 = "0" +b1;

         if(q+1 != nBytes){
           sect = Integer.toHexString((int)(b[q+1]) & 0X0FF);
           if(sect.length() == 1)
             sect = "0" + sect;
           }else{
             sect = Integer.toHexString(0 & 0X0FF);
             if(sect.length() == 1)
             sect = "0" + sect;
           }

           String b2 = b1 +sect;

           sum += Integer.parseInt(b2, 16);

           if((sum & 0XFFFF0000) > 0){
             sum &= 0xFFFF;
             sum++;
           }
            q+=2;          
            b1 = "";
            sect = "";
        }  
      return  (short) ~(sum & 0xFFFF);
    }
    
}















