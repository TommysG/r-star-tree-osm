import java.io.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        DataManagement dataManagement = new DataManagement(new File("map.osm"));
        dataManagement.readFile();
        dataManagement.writeNodes("map.txt");

//
//        char[] buffer = new char[32000];
//        BufferedReader in = new BufferedReader(new FileReader("file.dat"));
//        int bytesRead = in.read(buffer);
//        int totalBytes = 0;
//        while(bytesRead != -1) {
//
//
//            // next read
//            bytesRead = in.read(buffer, 0, 32000);
//            totalBytes += bytesRead;
//
//            if(totalBytes == 32000 * 26634 )
//                break;
//        }
//
//        System.out.println(buffer);


    }



}
