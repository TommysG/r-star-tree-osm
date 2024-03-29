package rstar;

import rstar.dto.NodeDTO;
import rstar.dto.PointDTO;
import rstar.dto.TreeDTO;
import rstar.interfaces.IDiskQuery;
import rstar.nodes.RStarInternal;
import rstar.nodes.RStarLeaf;
import rstar.nodes.RStarNode;
import util.Constants;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * provides all disk related functionality like
 * loading and saving of nodes, points and tree.
 */
public class StorageManager implements IDiskQuery {
    RandomAccessFile dataStore;
    FileChannel dataChannel;
    int i = 0;
    long MAX_SIZE_BLOCK;
    int dimension;

    public StorageManager(long MAX_SIZE_BLOCK, int dimension) {
        try {
            this.MAX_SIZE_BLOCK = MAX_SIZE_BLOCK;
            this.dimension = dimension;
            dataStore = new RandomAccessFile(Constants.DATA_FILE, "rw");
            dataChannel = dataStore.getChannel();
            metaData();
        } catch (FileNotFoundException e) {
            System.err.println("Data File failed to be loaded/created. Exiting");
            System.exit(1);
        }
    }

    public void metaData(){
        try {
            dataStore.write(i);
            dataStore.write(dimension);
            dataStore.writeLong(MAX_SIZE_BLOCK);

            while(dataStore.length() < MAX_SIZE_BLOCK){
                dataStore.writeByte(0);
            }

            i++;
            dataStore.write(i);
            i++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveNode(RStarNode node) {
        if (node.isLeaf()) {
            try {
                RStarLeaf leaf = (RStarLeaf) node;

                if (leaf.hasUnsavedPoints()) {
                    //save unsaved points to disk first.
                    for (int i = leaf.loadedChildren.size() - 1; i >= 0; i--) {
                        leaf.childPointers.add(savePoint(leaf.loadedChildren.remove(i).toDTO()));
                    }
                }

                FileOutputStream fos = new FileOutputStream(new File(constructFilename(leaf.getNodeId())));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(leaf.toDTO());
                oos.flush();
                fos.write(bos.toByteArray());

                oos.close();
                fos.close();

            } catch (IOException e) {
                System.err.println("Exception while saving node to disk");
            }
        } else {
            try {
                RStarInternal internal = (RStarInternal) node;

                FileOutputStream fos = new FileOutputStream(new File(constructFilename(internal.getNodeId())));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(internal.toDTO());
                oos.flush();

                fos.write(bos.toByteArray());
                oos.close();
                fos.close();

            } catch (IOException e) {
                System.err.println("Exception while saving node to disk");
            }
        }
    }

    @Override
    public RStarNode loadNode(long nodeId) throws FileNotFoundException {
        return nodeFromDisk(constructFilename(nodeId));
    }

    /**
     * saves a Spatial Point to dataFile on disk and
     * returns the offset of the point in the file.
     *
     * @param pointDTO DTO of the point to be saved
     * @return the location where the point was saved in
     * datafile
     */
    @Override
    public long savePoint(PointDTO pointDTO) {
        try {
            long pos = dataStore.length();
            dataStore.seek(pos);

            long oid = pointDTO.oid;
            String name = pointDTO.name;
            double lat = pointDTO.coords[0];
            double lon = pointDTO.coords[1];

            if( pos + 24 + name.getBytes().length > MAX_SIZE_BLOCK * i ){
                while(dataStore.length() < MAX_SIZE_BLOCK * i){
                    dataStore.writeByte(0);
                }
                i++;
                dataStore.write(i);
            }

            pos = dataStore.length();
            dataStore.seek(pos);

            dataStore.writeLong(oid);

            if(!pointDTO.name.isEmpty())
                dataStore.writeUTF(name);
            else
                dataStore.writeUTF("-");

            dataStore.writeDouble(lat);
            dataStore.writeDouble(lon);

            System.out.println(pointDTO.oid + " size: " + dataStore.length());

            return pos;
        } catch (IOException e) {
            System.err.println("Exception occurred while saving data to disk.");
            return -1;
        }
    }

    /**
     * loads a SpatialPoint from dataFile
     * @param pointer the offset of the point
     *                in dataFile
     * @return DTO of the point. Full SpatialPoint
     * can be easily constructed from the DTO
     */
    @Override
    public PointDTO loadPoint(long pointer) {
        try {
            dataStore.seek(pointer);
            DataInputStream ois = getPointObjectStream();

            long oid = ois.readLong();
            String name = ois.readUTF();
            double lat = ois.readDouble();
            double lon = ois.readDouble();

            double[] coords = {lat,lon};

            return new PointDTO(oid, name, coords);

        } catch (IOException e) {
            System.err.println("Exception occurred while loading point from disk.");
        }
        return null;
    }

    private RStarNode nodeFromDisk(String filename) throws FileNotFoundException {
        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            NodeDTO dto = (NodeDTO) ois.readObject();
            ois.close();

            RStarNode result;
            if (dto.isLeaf)
                result = new RStarLeaf(dto, nodeIdFromFilename(filename));
            else
                result = new RStarInternal(dto, nodeIdFromFilename(filename));

            return result;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException occurred while loading node from disk");
        }
        return null;
    }

//    /**
//     * saves the R* Tree to saveFile.
//     * doesn't use RandomAccessFile
//     * @param tree the DTO of the tree to be saved
//     * @param saveFile saveNode file location
//     * @return 1 is successful, else -1
//     */
//    @Override
//    public int saveTree(TreeDTO tree, File saveFile) {
//        int status = -1;
//        try {
//            if(saveFile.exists()) {
//                saveFile.delete();
//            }
//
//            FileOutputStream fos = new FileOutputStream(saveFile);
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//
//            oos.writeObject(tree);
//            oos.flush();
//            oos.close();
//            status = 1;             // successful saveNode
//        } catch (IOException e) {
//            System.err.println("Error while saving Tree to " + saveFile.toURI());
//        }
//        return status;
//    }

//    /**
//     * loads a R* Tree from disk
//     * @param saveFile the file to loadNode the tree from
//     * @return DTO of the loaded R* Tree, null if none found
//     * @throws FileNotFoundException
//     */
//    @Override
//    public TreeDTO loadTree(File saveFile) {
//        try {
//            FileInputStream fis = new FileInputStream(saveFile);
//            ObjectInputStream ois = new ObjectInputStream(fis);
//
//            return (TreeDTO) ois.readObject();
//
//        } catch (IOException | ClassNotFoundException e) {
//            System.err.println("Exception while loading tree from " + saveFile);
//        }
//        return null;
//    }

    public String constructFilename(long nodeId) {
        return Constants.TREE_DATA_DIRECTORY + "/" + Constants.NODE_FILE_PREFIX + nodeId + Constants.NODE_FILE_SUFFIX;
    }

    public long nodeIdFromFilename(String filename) {
        int i2 = filename.indexOf(Constants.NODE_FILE_SUFFIX);
        assert i2 != -1;
        return Long.parseLong(filename.substring((Constants.TREE_DATA_DIRECTORY+"/"+Constants.NODE_FILE_PREFIX).length(), i2));
    }

    private DataInputStream getPointObjectStream() throws IOException {
        return new DataInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                return dataStore.read();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                return dataStore.read(b, off, len);
            }
        });
    }

    public void createDataDir(File saveFile) {
        // check for the node-data directory. create one if doesn't exist
        File dataDir = new File(saveFile.getParentFile(), Constants.TREE_DATA_DIRECTORY);
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            if (!dataDir.mkdir()) {
                System.err.println("Failed to create data directory of the tree. Exiting..");
                System.exit(1);
            }
            System.out.println("Data directory created");
        }
    }
}
