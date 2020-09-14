import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManagement {


    private String filePath;
    private BufferedReader reader;
    private int count = 0;
    private List<Node> nodes = new ArrayList<>();

    public DataManagement(String filePath){
        this.filePath = filePath;
    }

    public String getAttr(String line, String attribute){
        String id = "";

        if(line.contains(" " + attribute)){
            int indexFirst = line.indexOf(attribute) + attribute.length() + 2;

            String tmp = line.substring(indexFirst);

            int indexLast;
            if(!attribute.equals("lon"))
                 indexLast = tmp.indexOf(" ") - 1;
            else{
                if(tmp.contains("/")){
                    indexLast = tmp.indexOf("/") - 1;
                }
                else{
                    indexLast = tmp.indexOf(">") - 1;
                }
            }

            id = tmp.substring(0, indexLast);

        }

        return id;
    }

    public void skipLines(int n, BufferedReader reader) throws IOException {
        for (int i = 0; i < n; i++)
            reader.readLine();
    }

    public void show(){
        try {
            reader = new BufferedReader(new FileReader(filePath));

            skipLines(3, reader);

            String line = reader.readLine();

            while (line != null) {
//                System.out.println("id:" + getAttr(line, "id") + " lat: " + getAttr(line, "lat")
//                + " lon: " + getAttr(line, "lon"));

                Node node = new Node(getAttr(line, "id"),  getAttr(line, "lon"),  getAttr(line, "lat"));

                if(!(getAttr(line, "lon").isEmpty() && getAttr(line, "lat").isEmpty()))
                    nodes.add(node);

                // read next line
                line = reader.readLine();
                count++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFile(){
        try {
            File myObj = new File("temp.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeData(){
        try {
            FileWriter myWriter = new FileWriter("temp.txt");

            for(Node x: nodes){
                myWriter.write( x.getId() + " " + x.getLat() + " " + x.getLon() + " " + "\n" );
            }

            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
