import java.util.ArrayList;

public class BestRangeQuery {
    public static ArrayList<Record> bestRangeQuery(Node node, BoundingBox queryBoundingBox) {
        ArrayList<Record> results = new ArrayList<>();

        int dims = FilesManager.getDataDimensions();
        double[] minCoordinate = new double[dims];
        double[] maxCoordinate = new double[dims];

        ArrayList<Bounds> boundsList = queryBoundingBox.getBounds();
        for (int i = 0; i < dims; i++) {
            Bounds b = boundsList.get(i);
            minCoordinate[i] = b.getLower();
            maxCoordinate[i] = b.getUpper();
        }

        for (Entry entry : node.getEntries()) {
            BoundingBox entryBoundingBox = entry.getBoundingBox();

            if (BoundingBox.checkOverlap(entryBoundingBox, queryBoundingBox)) {
                if (node.getNodeLevelInTree() == RStarTree.getLeafLevel()) {
                    ArrayList<Record> recordsList = FilesManager.readDataFileBlock(entry.getChildNodeBlockId());
                    if (recordsList != null) {
                        for (Record record : recordsList) {
                            if (isRecordInRange(record, minCoordinate, maxCoordinate)) {
                                results.add(record);
                            }
                        }
                    }
                } else {
                    Node childNode = FilesManager.readIndexFileBlock(entry.getChildNodeBlockId());
                    if (childNode != null) {
                        results.addAll(bestRangeQuery(childNode, queryBoundingBox));
                    }
                }
            }
        }

        return results;
    }

    private static boolean isRecordInRange(Record record, double[] minCoordinate, double[] maxCoordinate) {
        ArrayList<Double> coordinates = record.getCoordinates();
        for (int i = 0; i < coordinates.size(); i++) {
            double val = coordinates.get(i);
            if (val < minCoordinate[i] || val > maxCoordinate[i]) {
                return false;
            }
        }
        return true;
    }
}
