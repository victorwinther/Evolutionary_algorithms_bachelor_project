package group.ea.problem.TSP;
import group.ea.searchspace.SearchSpace;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class TSPParser extends SearchSpace {
    private String filename;
    private int dimension = 0;
    private Map<String, double[]> tspCitiesDict = new HashMap<>();

    public TSPParser(String filename) {
        super();
        this.filename = filename;
        clearData();
        onFileSelected();
    }

    public int returnLength(){
        return dimension;
    }

    private void onFileSelected() {
        openTSPFile();
    }

    public String getLastPartOfFilename() {
        String[] parts = filename.split("/");
        String fullName = parts[parts.length - 1];
        return fullName.contains(".") ? fullName.substring(0, fullName.lastIndexOf('.')) : fullName;
    }

    private void openTSPFile() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    if (line.startsWith("DIMENSION")) {
                        String[] parts = line.split(":");
                        dimension = Integer.parseInt(parts[1].trim());
                    } else if (line.equals("NODE_COORD_SECTION")) {
                        getCitiesDict(reader);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCitiesDict(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 3) {
                try {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    tspCitiesDict.put(parts[0], new double[]{x, y});
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line);
                }
            }
        }
    }


    private void clearData() {
        tspCitiesDict.clear();
        dimension = 0;
    }


    public Map<String, double[]> getTspCitiesDict() {
        return tspCitiesDict;
    }
    public void printTspCitiesDict() {
        for (Map.Entry<String, double[]> entry : tspCitiesDict.entrySet()) {
            System.out.println("City ID: " + entry.getKey() + ", Coordinates: " +
                    Arrays.toString(entry.getValue()));
        }
    }

    @Override
    public String init() {
        return null;
    }
    public int getDimension(){
        return dimension;
    }
}