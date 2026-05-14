package com.bugprediction;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class that orchestrates the bug prediction process
 */
public class BugPredictionService {
    
    private DataPreprocessor dataPreprocessor;
    private MLModelTrainer modelTrainer;
    private List<DataPreprocessor.SoftwareModule> trainingModules;
    private List<DataPreprocessor.SoftwareModule> testModules;
    private boolean isModelTrained;
    
    public BugPredictionService() {
        this.dataPreprocessor = new DataPreprocessor();
        this.trainingModules = new ArrayList<>();
        this.testModules = new ArrayList<>();
        this.isModelTrained = false;
    }
    
    /**
     * Load and prepare the dataset
     */
    public void loadDataset(String csvFilePath) throws Exception {
        System.out.println("Loading dataset from: " + csvFilePath);
        dataPreprocessor.loadDataFromCSV(csvFilePath);
        dataPreprocessor.printStatistics();
        
        // Split data into training and testing sets (80% training, 20% testing)
        dataPreprocessor.splitData(0.8, trainingModules, testModules);
        
        System.out.println("Training set size: " + trainingModules.size());
        System.out.println("Test set size: " + testModules.size());
    }
    
    /**
     * Train the ML model
     */
    public void trainModel(MLModelTrainer.ModelType modelType) throws Exception {
        if (trainingModules.isEmpty()) {
            throw new IllegalStateException("No training data available. Please load dataset first.");
        }
        
        System.out.println("\n=== Training Model ===");
        modelTrainer = new MLModelTrainer(modelType);
        
        // Prepare training data
        double[][] trainingFeatures = convertModulesToFeatures(trainingModules);
        int[] trainingLabels = convertModulesToLabels(trainingModules);
        
        // Train the model
        modelTrainer.trainModel(trainingFeatures, trainingLabels);
        isModelTrained = true;
        
        System.out.println("Model training completed successfully!");
    }
    
    /**
     * Predict if a module is buggy
     */
    public MLModelTrainer.PredictionResult predictModule(DataPreprocessor.SoftwareModule module) throws Exception {
        if (!isModelTrained) {
            throw new IllegalStateException("Model has not been trained yet. Please train the model first.");
        }
        
        return modelTrainer.predict(module.getFeatures());
    }
    /**
     * Predict for a module with given features
     */
    public MLModelTrainer.PredictionResult predictModule(String moduleName, double loc, 
                                                         double cyclomaticComplexity, double numChanges,
                                                         double bugFixes, double codeChurn, 
                                                         double authorCount) throws Exception {
        if (!isModelTrained) {
            throw new IllegalStateException("Model has not been trained yet. Please train the model first.");
        }
        
        double[] features = {loc, cyclomaticComplexity, numChanges, bugFixes, codeChurn, authorCount};
        MLModelTrainer.PredictionResult result = modelTrainer.predict(features);
        
        System.out.println("\n=== Prediction Result ===");
        System.out.println("Module Name: " + moduleName);
        System.out.println("Features: LOC=" + loc + ", Complexity=" + cyclomaticComplexity + 
                          ", Changes=" + numChanges + ", Bug Fixes=" + bugFixes + 
                          ", Code Churn=" + codeChurn + ", Authors=" + authorCount);
        System.out.println("Prediction: " + result);
        System.out.println("========================\n");
        
        return result;
    }
    
    /**
     * Evaluate the model on test data
     */
    public MLModelTrainer.ModelEvaluation evaluateModel() throws Exception {
        if (!isModelTrained) {
            throw new IllegalStateException("Model has not been trained yet. Please train the model first.");
        }
        
        if (testModules.isEmpty()) {
            throw new IllegalStateException("No test data available for evaluation.");
        }
        
        System.out.println("\n=== Model Evaluation ===");
        
        // Prepare test data
        double[][] testFeatures = convertModulesToFeatures(testModules);
        int[] testLabels = convertModulesToLabels(testModules);
        
        // Evaluate the model
        MLModelTrainer.ModelEvaluation evaluation = modelTrainer.evaluate(testFeatures, testLabels);
        
        System.out.println("Test Set Evaluation Results:");
        System.out.println(evaluation);
        System.out.println("========================\n");
        
        return evaluation;
    }
    
    /**
     * Predict for all test modules and display results
     */
    public void predictAllTestModules() throws Exception {
        if (!isModelTrained) {
            throw new IllegalStateException("Model has not been trained yet. Please train the model first.");
        }
        
        System.out.println("\n=== Predictions for Test Modules ===");
        System.out.printf("%-20s %-15s %-15s%n", "Module Name", "Actual", "Predicted (Risk)");
        System.out.println("------------------------------------------------------------");
        
        for (DataPreprocessor.SoftwareModule module : testModules) {
            MLModelTrainer.PredictionResult prediction = predictModule(module);
            String actual = module.isBuggy() ? "Buggy" : "Not Buggy";
            String predicted = prediction.isBuggy() ? 
                String.format("Buggy (%.1f%%)", prediction.getRiskProbability()) : 
                String.format("Not Buggy (%.1f%%)", prediction.getRiskProbability());
            
            System.out.printf("%-20s %-15s %-15s%n", module.getModuleName(), actual, predicted);
        }
        System.out.println("============================================================\n");
    }
    
    /**
     * Convert list of modules to features matrix
     */
    private double[][] convertModulesToFeatures(List<DataPreprocessor.SoftwareModule> modules) {
        double[][] features = new double[modules.size()][];
        for (int i = 0; i < modules.size(); i++) {
            features[i] = modules.get(i).getFeatures();
        }
        return features;
    }
    
    /**
     * Convert list of modules to labels array
     */
    private int[] convertModulesToLabels(List<DataPreprocessor.SoftwareModule> modules) {
        int[] labels = new int[modules.size()];
        for (int i = 0; i < modules.size(); i++) {
            labels[i] = modules.get(i).getLabel();
        }
        return labels;
    }
    
    /**
     * Get model information
     */
    public String getModelInfo() {
        if (!isModelTrained) {
            return "Model not trained yet";
        }
        
        return String.format("Model Type: %s, Training Accuracy: %.2f%%", 
                           modelTrainer.getModelType().getDisplayName(), 
                           modelTrainer.getTrainingAccuracy() * 100);
    }
    
    /**
     * Check if model is trained
     */
    public boolean isModelTrained() {
        return isModelTrained;
    }
    
    /**
     * Get training modules count
     */
    public int getTrainingModulesCount() {
        return trainingModules.size();
    }
    
    /**
     * Get test modules count
     */
    public int getTestModulesCount() {
        return testModules.size();
    }
    
    /**
     * Get all modules (training + test) for display
     */
    public List<DataPreprocessor.SoftwareModule> getAllModules() {
        List<DataPreprocessor.SoftwareModule> allModules = new ArrayList<>();
        allModules.addAll(trainingModules);
        allModules.addAll(testModules);
        return allModules;
    }
    
    /**
     * Get training modules
     */
    public List<DataPreprocessor.SoftwareModule> getTrainingModules() {
        return new ArrayList<>(trainingModules);
    }
    
    /**
     * Get test modules
     */
    public List<DataPreprocessor.SoftwareModule> getTestModules() {
        return new ArrayList<>(testModules);
    }
}
