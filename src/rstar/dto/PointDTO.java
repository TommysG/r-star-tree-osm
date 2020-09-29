package rstar.dto;

public class PointDTO extends AbstractDTO{
    public long oid;
    public String name;
    public double[] coords;

    public PointDTO(){}

    public PointDTO(long oid, String name, double[] coords) {
        this.oid = oid;
        this.name = name;
        this.coords = coords;
    }
}
