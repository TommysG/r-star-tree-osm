public class Main {

    public static void main(String[] args) {
        DataManagement dataManagement = new DataManagement("map.osm");

        dataManagement.show();
        dataManagement.createFile();
        dataManagement.writeData();
        dataManagement.createDataFile();
    }
}
