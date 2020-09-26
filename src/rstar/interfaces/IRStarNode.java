package rstar.interfaces;

import rstar.spatial.HyperRectangle;

public interface IRStarNode{

    boolean isLeaf();

    boolean isNotFull();

    <T> int insert(T newChild);

    HyperRectangle getMBR();

    void setMbr(HyperRectangle mbr);

    void createId();

    long getNodeId();

    void setNodeId(long nodeId);
}
