package rstar.dto;

public class PointDTO extends AbstractDTO{
    public String oid;
    public String name;
    public double[] coords;

    public PointDTO(String oid, String name, double[] coords) {
        this.oid = oid;
        this.name = name;
        this.coords = coords;
    }
}
