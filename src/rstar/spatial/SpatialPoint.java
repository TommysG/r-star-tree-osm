package rstar.spatial;

import rstar.dto.PointDTO;
import rstar.interfaces.IDtoConvertible;

public class SpatialPoint implements IDtoConvertible {
    private int _dimension;
    private double[] _cords;
    private long  _oid;
    private String name;

    public SpatialPoint() {
    }

    public SpatialPoint(int dimension) {
        this._dimension = dimension;
        this._oid = -1;
        this.name = "-";
    }

    public SpatialPoint(double[] cords) {
        this._cords = cords;
        this._dimension = cords.length;
        this._oid = -1;
        this.name = "-";
    }

    public SpatialPoint(double[] cords, long oid, String name) {
        this._cords = cords;
        this._dimension = cords.length;
        this._oid = oid;
        this.name = name;
    }

    public SpatialPoint(PointDTO dto) {
        this._cords = dto.coords;
        this._dimension = dto.coords.length;
        this._oid = dto.oid;
    }

    public int getDimension(){
        return _dimension;
    }

    public void setCords(double[] data){
        this._cords = data;
    }

    public double[] getCords() {
        return _cords;
    }

    public long getOid() {
        return _oid;
    }

    public void setOid(long oid) {
        this._oid = oid;
    }

    /**
     * calculate distance of this point with <pre>otherPoint</pre>
     * @param otherPoint the point from which this point's
     *                   distance is to be calculated
     * @return distance from <pre>otherPoint</pre>
     */
    public float distance(SpatialPoint otherPoint) {
        double[] otherPoints = otherPoint.getCords();
        float distance = 0;
        for (int i = 0; i < _cords.length; i++) {
            double tmp = _cords[i] - otherPoints[i];
            distance += tmp * tmp;
        }
        return (float)Math.sqrt(distance);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[");
        str.append(_oid).append(",");
        for (double cord : _cords) {
            str.append(cord).append(",");
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public PointDTO toDTO() {
        return new PointDTO(_oid, name, _cords);
    }
}
