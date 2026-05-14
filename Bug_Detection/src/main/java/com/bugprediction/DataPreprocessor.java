package com.bugprediction;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class DataPreprocessor {
    
    public static class SoftwareModule {
        private String moduleName;
        private double loc;
        private double cyclomaticComplexity;
        private double numChanges;
        private double bugFixes;
        private double codeChurn;
        private double authorCount;
        private boolean isBuggy;
        
        public SoftwareModule(String moduleName, double loc, double cyclomaticComplexity, 
                            double numChanges, double bugFixes, double codeChurn, 
                            double authorCount, boolean isBuggy) {
            this.moduleName = moduleName;
            this.loc = loc;
            this.cyclomaticComplexity = cyclomaticComplexity;
            this.numChanges = numChanges;
            this.bugFixes = bugFixes;
            this.codeChurn = codeChurn;
            this.authorCount = authorCount;
            this.isBuggy = isBuggy;
        }
        
        // Getters
        public String getModuleName() { return moduleName; }
        public double getLoc() { return loc; }
        public double getCyclomaticComplexity() { return cyclomaticComplexity; }
        public double getNumChanges() { return numChanges; }
        public double getBugFixes() { return bugFixes; }
        public double getCodeChurn() { return codeChurn; }
        public double getAuthorCount() { return authorCount; }
        public boolean isBuggy() { return isBuggy; }
        
        // Get features as double array for ML
        public double[] getFeatures() {
            return new double[]{loc, cyclomaticComplexity, numChanges, bugFixes, codeChurn, authorCount};
        }
        
        // Get label as integer (1 for buggy, 0 for not buggy)
        public int getLabel() {
            return isBuggy ? 1 : 0;
        }
        
        @Override
        public String toString() {
            return String.format("Module: %s, Buggy: %s, Risk Features: LOC=%.0f, Complexity=%.0f, Changes=%.0f", 
                               moduleName, isBuggy ? "Yes" : "No", loc, cyclomaticComplexity, numChanges);
        }
    }
    
    private List<SoftwareModule> modules;
    
    public DataPreprocessor() {
        this.modules = new ArrayList<>();
    }
    
    /**
     * Load data from CSV file
     */
    public void loadDataFromCSV(String filePath) throws IOException {
        modules.clear();
        
        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {
            
            for (CSVRecord record : csvParser) {
                try {
                    String moduleName = record.get("module_name");
                    double loc = parseDouble(record.get("loc"));
                    double cyclomaticComplexity = parseDouble(record.get("cyclomatic_complexity"));
                    double numChanges = parseDouble(record.get("num_changes"));
                    double bugFixes = parseDouble(record.get("bug_fixes"));
                    double codeChurn = parseDouble(record.get("code_churn"));
                    double authorCount = parseDouble(record.get("author_count"));
                    String isBuggyStr = record.get("is_buggy").toLowerCase().trim();
                    boolean isBuggy = isBuggyStr.equals("buggy") || isBuggyStr.equals("true") || isBuggyStr.equals("1");
                    
                    SoftwareModule module = new SoftwareModule(moduleName, loc, cyclomaticComplexity, 
                                                             numChanges, bugFixes, codeChurn, 
                                                             authorCount, isBuggy);
                    modules.add(module);
                    
                } catch (Exception e) {
                    System.err.println("Error parsing record: " + record + " - " + e.getMessage());
                }
            }
        }
        
        System.out.println("Loaded " + modules.size() + " software modules from dataset.");
    }
    
    /**
     * Parse double with fallback for missing values
     */
    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("?") || value.equals("NA")) {
            return 0.0; // Simple replacement for missing values
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0; // Simple replacement for invalid values
        }
    }
    
    /**
     * Get all modules
     */
    public List<SoftwareModule> getModules() {
        return new ArrayList<>(modules);
    }
    
    /**
     * Get features matrix for ML algorithms
     */
    public double[][] getFeaturesMatrix() {
        double[][] features = new double[modules.size()][];
        for (int i = 0; i < modules.size(); i++) {
            features[i] = modules.get(i).getFeatures();
        }
        return features;
    }
    
    /**
     * Get labels array for ML algorithms
     */
    public int[] getLabelsArray() {
        int[] labels = new int[modules.size()];
        for (int i = 0; i < modules.size(); i++) {
            labels[i] = modules.get(i).getLabel();
        }
        return labels;
    }
    
    /**
     * Split data into training and testing sets
     */
    public void splitData(double trainRatio, List<SoftwareModule> trainSet, List<SoftwareModule> testSet) {
        trainSet.clear();
        testSet.clear();
        
        int trainSize = (int) (modules.size() * trainRatio);
        
        // Simple random split - in real implementation, you'd want better shuffling
        for (int i = 0; i < modules.size(); i++) {
            if (i < trainSize) {
                trainSet.add(modules.get(i));
            } else {
                testSet.add(modules.get(i));
            }
        }
    }
    
    /**
     * Print dataset statistics
     */
    public void printStatistics() {
        int buggyCount = 0;
        int totalCount = modules.size();
        
        for (SoftwareModule module : modules) {
            if (module.isBuggy()) {
                buggyCount++;
            }
        }
        
        double buggyPercentage = (double) buggyCount / totalCount * 100;
        
        System.out.println("\n=== Dataset Statistics ===");
        System.out.println("Total modules: " + totalCount);
        System.out.println("Buggy modules: " + buggyCount + " (" + String.format("%.1f", buggyPercentage) + "%)");
        System.out.println("Non-buggy modules: " + (totalCount - buggyCount) + " (" + String.format("%.1f", 100 - buggyPercentage) + "%)");
        System.out.println("========================\n");
    }
}
