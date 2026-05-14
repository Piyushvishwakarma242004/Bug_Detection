package com.bugprediction;

/**
 * Demonstration class for the Bug Prediction System
 * Shows automated workflow without user interaction
 */
public class BugPredictionDemo {
    
    public static void main(String[] args) {
        try {
            System.out.println("========================================");
            System.out.println("  Bug Prediction System Demo");
            System.out.println("========================================\n");
            
            // Initialize the service
            BugPredictionService service = new BugPredictionService();
            
            // Load dataset
            System.out.println("1. Loading dataset...");
            service.loadDataset("src/main/resources/bug_dataset.csv");
            
            // Train model with Naive Bayes
            System.out.println("\n2. Training Naive Bayes model...");
            service.trainModel(MLModelTrainer.ModelType.NAIVE_BAYES);
            
            // Evaluate model
            System.out.println("\n3. Evaluating model performance...");
            MLModelTrainer.ModelEvaluation evaluation = service.evaluateModel();
            
            // Predict for some example modules
            System.out.println("\n4. Making predictions for example modules...");
            
            // Example 1: High-risk module
            System.out.println("\n--- Example 1: High-risk module ---");
            service.predictModule("NewPaymentGateway", 350, 28, 18, 10, 980, 5);
            
            // Example 2: Low-risk module
            System.out.println("\n--- Example 2: Low-risk module ---");
            service.predictModule("SimpleUtils", 45, 3, 2, 0, 65, 1);
            
            // Example 3: Medium-risk module
            System.out.println("\n--- Example 3: Medium-risk module ---");
            service.predictModule("DataAccessLayer", 180, 14, 9, 4, 520, 3);
            
            // Predict all test modules
            System.out.println("\n5. Predicting all test modules...");
            service.predictAllTestModules();
            
            // Show final summary
            System.out.println("\n=== DEMO SUMMARY ===");
            System.out.println("Model Information: " + service.getModelInfo());
            System.out.println("Training Modules: " + service.getTrainingModulesCount());
            System.out.println("Test Modules: " + service.getTestModulesCount());
            System.out.println("Evaluation Results: " + evaluation);
            System.out.println("===================\n");
            
            System.out.println("Demo completed successfully!");
            System.out.println("Run BugPredictionSystem.java for interactive mode.");
            
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
