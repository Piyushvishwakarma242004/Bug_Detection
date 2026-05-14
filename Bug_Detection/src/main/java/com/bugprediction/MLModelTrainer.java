package com.bugprediction;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Instance;

import java.util.ArrayList;
import java.util.List;

public class MLModelTrainer {
    
    public enum ModelType {
        NAIVE_BAYES("Naive Bayes"),
        DECISION_TREE("Decision Tree (J48)"),
        LOGISTIC_REGRESSION("Logistic Regression"),
        RANDOM_FOREST("Random Forest");
        
        private final String displayName;
        
        ModelType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private Classifier trainedModel;
    private ModelType modelType;
    private Instances datasetStructure;
    private double trainingAccuracy;
    private double trainingPrecision;
    private double trainingRecall;
    
    public MLModelTrainer(ModelType modelType) {
        this.modelType = modelType;
    }
    
    /**
     * Train the model using the provided data
     */
    public void trainModel(double[][] features, int[] labels) throws Exception {
        System.out.println("Training " + modelType.getDisplayName() + " model...");
        
        // Create dataset structure
        datasetStructure = createDatasetStructure(features[0].length);
        
        // Create instances for training
        Instances trainingData = new Instances(datasetStructure);
        trainingData.setClassIndex(datasetStructure.numAttributes() - 1);
        
        // Add training instances
        for (int i = 0; i < features.length; i++) {
            Instance instance = createInstance(features[i], labels[i]);
            trainingData.add(instance);
        }
        
        // Create and train the classifier
        switch (modelType) {
            case NAIVE_BAYES:
                trainedModel = new NaiveBayes();
                break;
            case DECISION_TREE:
                trainedModel = new J48();
                break;
            case LOGISTIC_REGRESSION:
                trainedModel = new Logistic();
                break;
            case RANDOM_FOREST:
                trainedModel = new RandomForest();
                break;
            default:
                throw new IllegalArgumentException("Unsupported model type: " + modelType);
        }
        
        // Train the model
        trainedModel.buildClassifier(trainingData);
        
        // Evaluate on training data
        Evaluation eval = new Evaluation(trainingData);
        eval.evaluateModel(trainedModel, trainingData);
        
        trainingAccuracy = eval.correct() / trainingData.numInstances();
        trainingPrecision = eval.precision(1); // Precision for buggy class
        trainingRecall = eval.recall(1); // Recall for buggy class
        
        System.out.println("Model training completed!");
        System.out.println("Training Accuracy: " + String.format("%.2f%%", trainingAccuracy * 100));
        System.out.println("Training Precision: " + String.format("%.2f%%", trainingPrecision * 100));
        System.out.println("Training Recall: " + String.format("%.2f%%", trainingRecall * 100));
    }
    
    /**
     * Predict if a module is buggy
     */
    public PredictionResult predict(double[] features) throws Exception {
        if (trainedModel == null) {
            throw new IllegalStateException("Model has not been trained yet!");
        }
        
        Instance instance = createInstance(features, 0); // Label doesn't matter for prediction
        instance.setDataset(datasetStructure);
        
        double prediction = trainedModel.classifyInstance(instance);
        double[] distribution = trainedModel.distributionForInstance(instance);
        
        boolean isBuggy = prediction == 1.0;
        double riskProbability = distribution[1] * 100; // Probability of being buggy
        
        return new PredictionResult(isBuggy, riskProbability);
    }
    
    /**
     * Evaluate the model on test data
     */
    public ModelEvaluation evaluate(double[][] testFeatures, int[] testLabels) throws Exception {
        if (trainedModel == null) {
            throw new IllegalStateException("Model has not been trained yet!");
        }
        
        Instances testData = new Instances(datasetStructure);
        testData.setClassIndex(datasetStructure.numAttributes() - 1);
        
        // Add test instances
        for (int i = 0; i < testFeatures.length; i++) {
            Instance instance = createInstance(testFeatures[i], testLabels[i]);
            testData.add(instance);
        }
        
        // Evaluate
        Evaluation eval = new Evaluation(testData);
        eval.evaluateModel(trainedModel, testData);
        
        double accuracy = eval.correct() / testData.numInstances();
        double precision = eval.precision(1); // Precision for buggy class
        double recall = eval.recall(1); // Recall for buggy class
        double f1Score = eval.fMeasure(1); // F1-score for buggy class
        
        return new ModelEvaluation(accuracy, precision, recall, f1Score);
    }
    
    /**
     * Create the dataset structure
     */
    private Instances createDatasetStructure(int numFeatures) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        
        // Add feature attributes
        attributes.add(new Attribute("loc"));
        attributes.add(new Attribute("cyclomatic_complexity"));
        attributes.add(new Attribute("num_changes"));
        attributes.add(new Attribute("bug_fixes"));
        attributes.add(new Attribute("code_churn"));
        attributes.add(new Attribute("author_count"));
        
        // Add class attribute (buggy/not buggy)
        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("not_buggy");
        classValues.add("buggy");
        attributes.add(new Attribute("is_buggy", classValues));
        
        return new Instances("bug_prediction_dataset", attributes, 0);
    }
    
    /**
     * Create an instance from features and label
     */
    private Instance createInstance(double[] features, int label) {
        DenseInstance instance = new DenseInstance(features.length + 1);
        
        // Set feature values
        for (int i = 0; i < features.length; i++) {
            instance.setValue(i, features[i]);
        }
        
        // Set class value
        instance.setValue(features.length, label);
        
        return instance;
    }
    
    /**
     * Get training metrics
     */
    public double getTrainingAccuracy() { return trainingAccuracy; }
    public double getTrainingPrecision() { return trainingPrecision; }
    public double getTrainingRecall() { return trainingRecall; }
    public ModelType getModelType() { return modelType; }
    
    /**
     * Container class for prediction results
     */
    public static class PredictionResult {
        private final boolean isBuggy;
        private final double riskProbability;
        
        public PredictionResult(boolean isBuggy, double riskProbability) {
            this.isBuggy = isBuggy;
            this.riskProbability = riskProbability;
        }
        
        public boolean isBuggy() { return isBuggy; }
        public double getRiskProbability() { return riskProbability; }
        
        @Override
        public String toString() {
            return String.format("Predicted: %s, Risk Probability: %.1f%%", 
                               isBuggy ? "Buggy" : "Not Buggy", riskProbability);
        }
    }
    
    /**
     * Container class for model evaluation results
     */
    public static class ModelEvaluation {
        private final double accuracy;
        private final double precision;
        private final double recall;
        private final double f1Score;
        
        public ModelEvaluation(double accuracy, double precision, double recall, double f1Score) {
            this.accuracy = accuracy;
            this.precision = precision;
            this.recall = recall;
            this.f1Score = f1Score;
        }
        
        public double getAccuracy() { return accuracy; }
        public double getPrecision() { return precision; }
        public double getRecall() { return recall; }
        public double getF1Score() { return f1Score; }
        
        @Override
        public String toString() {
            return String.format("Accuracy: %.2f%%, Precision: %.2f%%, Recall: %.2f%%, F1-Score: %.2f%%", 
                               accuracy * 100, precision * 100, recall * 100, f1Score * 100);
        }
    }
}
