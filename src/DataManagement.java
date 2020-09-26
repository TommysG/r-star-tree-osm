import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManagement {

    File file;
    List<Node> nodes;
    Node node;


    public DataManagement(File file){
        this.file = file;

        nodes = new ArrayList<>();
    }

    public void readFile() throws IOException {
        boolean foundWayTag = false;
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;

        while ((line = br.readLine()) != null){

            /* skip these lines from file*/
            if(line.contains("</way>")) foundWayTag = false;

            if(foundWayTag) continue;

            if(line.contains("<way")){
                foundWayTag = true;
                continue;
            }
            /* up to here */

            //don't skip these lines (these xml tags)
            if(!line.contains("<node") && !line.contains("</node>") && !line.contains("<tag")) continue;


            //get attributes from node
            if(line.contains("<node")){
                String id = getNodeAttribute(line,"id");
                String lat = getNodeAttribute(line,"lat");
                String lon = getNodeAttribute(line,"lon");
                node = new Node(id, lat, lon);
            }

            //get name attribute from node's tag
            if(line.contains("<tag") && line.contains("k=\"name\"")){
                node.setName(getTagAttribute(line, "v"));
            }

            //add node when is a single xml tag or when the tag closes
            if(line.contains("</node>") || (line.contains("<node") && line.contains("/>"))){
                nodes.add(node);
            }

        }
    }

    private String getNodeAttribute(String line, String attribute){
        String attr = "";
        attribute = attribute + "=\"";

        if(line.contains(" " + attribute)){
            //find the starting point of the searching attribute
            int indexFirst = line.indexOf(attribute) + attribute.length();

            //cut the string from the starting point of the attribute's value till the end
            String tmp = line.substring(indexFirst);

            //find the last index of the attributes value
            int indexLast;
            if(!attribute.equals("lon=\""))
                indexLast = tmp.indexOf(" ") - 1;
            else{
                if(tmp.contains("/")){
                    indexLast = tmp.indexOf("/") - 1;
                }
                else{
                    indexLast = tmp.indexOf(">") - 1;
                }
            }

            //take attribute's value
            attr = tmp.substring(0, indexLast);

        }

        return attr;

    }

    private String getTagAttribute(String line, String attribute){
        String attr = "";
        attribute = attribute + "=\"";

        //check if value exists (not required)
        if(line.contains("v=\"")){
            int indexFirst, indexLast;
            String tmp;

            indexFirst = line.indexOf(attribute) + attribute.length();
            tmp = line.substring(indexFirst);
            indexLast = tmp.indexOf("/") - 1;
            attr = tmp.substring(0, indexLast);
        }

        return attr;
    }

    public void writeNodes(String filePath){
        try {
            FileWriter myWriter = new FileWriter(filePath);

            for(Node node: nodes){
                if(node.getName().isEmpty()){
                    myWriter.write(node.getId() + " " + node.getLat() + " " + node.getLon() + "\n");
                }
                else {
                    myWriter.write(node.getId() + " " + node.getName().replace(" ", "_") + " " + node.getLat() + " " + node.getLon() + "\n");
                }

            }

            myWriter.close();
            System.out.println("Successfully wrote to the file.");

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}
