import java.io.Serializable;
import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

class BoundingBox implements Serializable {
    private ArrayList<Bounds> bounds; // Bounds of each dimension
    private Double area; // Area covered by the bounding box
    private Double margin; // Total perimeter of the bounding box
    private ArrayList<Double> center; // Centre point coordinates

    public BoundingBox(ArrayList<Bounds> bounds) {
        this.bounds = bounds;
        this.area = calculateArea();
        this.margin = calculateMargin();
        this.center = getCenter();
    }

    public BoundingBox(Record record) {
        ArrayList<Double> coords = record.getCoordinates();
        ArrayList<Bounds> boundsList = new ArrayList<>();
        for (double coord : coords) {
            boundsList.add(new Bounds(coord, coord));
        }
        this.bounds = boundsList;
        this.area = calculateArea();
        this.margin = calculateMargin();
        this.center = getCenter();
    }

    ArrayList<Bounds> getBounds() {
        return bounds;
    }

    private double calculateMargin() {
        double sum = 0;
        for (int d = 0; d < FilesManager.getDataDimensions(); d++)
            sum += abs(bounds.get(d).getUpper() - bounds.get(d).getLower());
        return sum;
    }

    private double calculateArea() {
        double productOfEdges = 1;
        for (int d = 0; d < FilesManager.getDataDimensions(); d++)
            productOfEdges = productOfEdges * (bounds.get(d).getUpper() - bounds.get(d).getLower());
        return abs(productOfEdges);
    }

    double getArea() {
        if (area == null)
            area = calculateArea();

        return area;
    }

    double getMargin() {
        if (margin == null)
            margin = calculateMargin();

        return margin;
    }

    public double minSum(){
        double sum = 0.0;
        for (Bounds b: this.bounds){
            sum += b.getLower();
        }
        return sum;
    }

    boolean checkOverLapWithPoint(ArrayList<Double> point, double radius){
        // If the minimum distance from the point is less or equal the point's radius then the bounding box is in the range
        return findMinDistanceFromPoint(point) <= radius;
    }

    double findMinDistanceFromPoint(ArrayList<Double> point){
        double minDistance = 0;
        double rd;
        for (int d = 0; d < FilesManager.getDataDimensions(); d++)
        {
            if(getBounds().get(d).getLower() > point.get(d))
                rd = getBounds().get(d).getLower();
            else if (getBounds().get(d).getUpper() < point.get(d))
                rd = getBounds().get(d).getUpper();
            else
                rd = point.get(d);

            minDistance += Math.pow(point.get(d) - rd,2);
        }
        return sqrt(minDistance);
    }

    public ArrayList<Double> getCenter() {
        if (center == null)
        {
            center = new ArrayList<>();

            for (int d = 0; d < FilesManager.getDataDimensions(); d++)
                center.add((bounds.get(d).getUpper()+bounds.get(d).getLower())/2);
        }
        return center;
    }

    static boolean checkOverlap(BoundingBox MBRA, BoundingBox MBRB) {
        for (int d = 0; d < FilesManager.getDataDimensions(); d++)
        {
            double overlapD = Math.min(MBRA.getBounds().get(d).getUpper(), MBRB.getBounds().get(d).getUpper())
                    - Math.max(MBRA.getBounds().get(d).getLower(), MBRB.getBounds().get(d).getLower());

            if (overlapD < 0)
                return false;
        }
        return true;
    }

    static double calculateOverlapValue(BoundingBox MBRA, BoundingBox MBRB) {
        double overlapValue = 1;
        for (int d = 0; d < FilesManager.getDataDimensions(); d++)
        {
            double overlapD = Math.min(MBRA.getBounds().get(d).getUpper(), MBRB.getBounds().get(d).getUpper())
                    - Math.max(MBRA.getBounds().get(d).getLower(), MBRB.getBounds().get(d).getLower());

            if (overlapD <= 0)
                return 0;
            else
                overlapValue = overlapD*overlapValue;
        }
        return overlapValue;
    }

    static double findDistanceBetweenBoundingBoxes(BoundingBox MBRA, BoundingBox MBRB) {
        double distance = 0;
        for (int d = 0; d < FilesManager.getDataDimensions(); d++)
        {
            distance += Math.pow(MBRA.getCenter().get(d) - MBRB.getCenter().get(d),2);
        }
        return sqrt(distance);
    }
}