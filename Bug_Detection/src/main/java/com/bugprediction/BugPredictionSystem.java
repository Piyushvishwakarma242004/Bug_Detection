package com.bugprediction;

import java.util.Scanner;

/**
 * Main console application for the Bug Prediction System
 */
public class BugPredictionSystem {
    
    private static BugPredictionService service;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        service = new BugPredictionService();
        scanner = new Scanner(System.in);
        
        System.out.println("========================================");
        System.out.println("  Java Automated Bug Prediction System");
        System.out.println("========================================");
        System.out.println("Predict which software modules are likely to contain bugs");
        System.out.println("using Machine Learning algorithms.\n");
        
        try {
            // Load the default dataset
            loadDefaultDataset();
            
            // Main menu loop
            boolean running = true;
            while (running) {
                showMainMenu();
                int choice = getUserChoice();
                
                try {
                    switch (choice) {
                        case 1:
                            trainModel();
                            break;
                        case 2:
                            evaluateModel();
                            break;
                        case 3:
                            predictModule();
                            break;
                        case 4:
                            predictAllTestModules();
                            break;
                        case 5:
                            showModelInfo();
                            break;
                        case 6:
                            loadCustomDataset();
                            break;
                        case 7:
                            showHelp();
                            break;
                        case 0:
                            running = false;
                            System.out.println("Thank you for using Bug Prediction System!");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
                
                if (running) {
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                }
            }
            
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    private static void loadDefaultDataset() throws Exception {
        String defaultDataset = "src/main/resources/bug_dataset.csv";
        System.out.println("Loading default dataset: " + defaultDataset);
        service.loadDataset(defaultDataset);
        System.out.println("Dataset loaded successfully!\n");
    }
    
    private static void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Train Model");
        System.out.println("2. Evaluate Model");
        System.out.println("3. Predict Single Module");
        System.out.println("4. Predict All Test Modules");
        System.out.println("5. Show Model Information");
        System.out.println("6. Load Custom Dataset");
        System.out.println("7. Help");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private static int getUserChoice() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void trainModel() throws Exception {
        if (!service.isModelTrained()) {
            System.out.println("\n=== MODEL SELECTION ===");
            System.out.println("Available ML algorithms:");
            System.out.println("1. Naive Bayes");
            System.out.println("2. Decision Tree (J48)");
            System.out.println("3. Logistic Regression");
            System.out.println("4. Random Forest");
            System.out.print("Select algorithm (1-4): ");
            
            int choice = getUserChoice();
            MLModelTrainer.ModelType modelType;
            
            switch (choice) {
                case 1:
                    modelType = MLModelTrainer.ModelType.NAIVE_BAYES;
                    break;
                case 2:
                    modelType = MLModelTrainer.ModelType.DECISION_TREE;
                    break;
                case 3:
                    modelType = MLModelTrainer.ModelType.LOGISTIC_REGRESSION;
                    break;
                case 4:
                    modelType = MLModelTrainer.ModelType.RANDOM_FOREST;
                    break;
                default:
                    System.out.println("Invalid choice. Using Naive Bayes as default.");
                    modelType = MLModelTrainer.ModelType.NAIVE_BAYES;
            }
            
            service.trainModel(modelType);
            System.out.println("Model training completed successfully!");
        } else {
            System.out.println("Model is already trained. You can evaluate or predict modules.");
        }
    }
    
    private static void evaluateModel() throws Exception {
        if (!service.isModelTrained()) {
            System.out.println("Please train the model first (Option 1).");
            return;
        }
        
        service.evaluateModel();
    }
    
    private static void predictModule() throws Exception {
        if (!service.isModelTrained()) {
            System.out.println("Please train the model first (Option 1).");
            return;
        }
        
        System.out.println("\n=== MODULE PREDICTION ===");
        System.out.println("Enter module metrics:");
        
        System.out.print("Module Name: ");
        String moduleName = scanner.nextLine().trim();
        
        System.out.print("Lines of Code (LOC): ");
        double loc = getDoubleInput();
        
        System.out.print("Cyclomatic Complexity: ");
        double complexity = getDoubleInput();
        
        System.out.print("Number of Changes: ");
        double numChanges = getDoubleInput();
        
        System.out.print("Number of Bug Fixes: ");
        double bugFixes = getDoubleInput();
        
        System.out.print("Code Churn: ");
        double codeChurn = getDoubleInput();
        
        System.out.print("Author Count: ");
        double authorCount = getDoubleInput();
        
        service.predictModule(moduleName, loc, complexity, numChanges, bugFixes, codeChurn, authorCount);
    }
    
    private static void predictAllTestModules() throws Exception {
        if (!service.isModelTrained()) {
            System.out.println("Please train the model first (Option 1).");
            return;
        }
        
        service.predictAllTestModules();
    }
    
    private static void showModelInfo() {
        System.out.println("\n=== MODEL INFORMATION ===");
        System.out.println("Training Modules: " + service.getTrainingModulesCount());
        System.out.println("Test Modules: " + service.getTestModulesCount());
        System.out.println(service.getModelInfo());
        System.out.println("========================\n");
    }
    
    private static void loadCustomDataset() throws Exception {
        System.out.print("Enter path to CSV dataset file: ");
        String filePath = scanner.nextLine().trim();
        
        try {
            service.loadDataset(filePath);
            System.out.println("Custom dataset loaded successfully!");
        } catch (Exception e) {
            System.out.println("Failed to load dataset: " + e.getMessage());
        }
    }
    
    private static void showHelp() {
        System.out.println("\n=== HELP ===");
        System.out.println("This system uses Machine Learning to predict software bugs.");
        System.out.println("\nWorkflow:");
        System.out.println("1. Load dataset (default or custom)");
        System.out.println("2. Train a model using one of the ML algorithms");
        System.out.println("3. Evaluate model performance on test data");
        System.out.println("4. Predict bugs for new modules");
        System.out.println("\nMetrics Explanation:");
        System.out.println("- LOC: Lines of Code");
        System.out.println("- Cyclomatic Complexity: Code complexity measure");
        System.out.println("- Number of Changes: How many times the module was modified");
        System.out.println("- Bug Fixes: Number of times bugs were fixed in this module");
        System.out.println("- Code Churn: Total lines added/deleted over time");
        System.out.println("- Author Count: Number of developers who worked on this module");
        System.out.println("\nRisk Probability: Percentage likelihood that the module contains bugs");
        System.out.println("Higher risk modules should be prioritized for testing and review.");
        System.out.println("============\n");
    }
    
    private static double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please enter a valid value: ");
            }
        }
    }
}
