package com.bugprediction;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Main GUI application for the Bug Prediction System
 */
public class BugPredictionGUI extends JFrame {
    
    // Main components
    private JTabbedPane tabbedPane;
    private JPanel datasetPanel, trainingPanel, predictionPanel, evaluationPanel;
    
    // Dataset components
    private JButton uploadButton, loadDefaultButton;
    private JTable datasetTable;
    private DefaultTableModel datasetTableModel;
    private JLabel datasetInfoLabel;
    
    // Training components
    private JComboBox<String> algorithmComboBox;
    private JButton trainButton;
    private JTextArea trainingLog;
    private JLabel trainingStatusLabel;
    
    // Prediction components
    private JTable predictionTable;
    private DefaultTableModel predictionTableModel;
    private JButton predictAllButton, predictCustomButton;
    private JTextField customModuleName, customLOC, customComplexity, customChanges;
    private JTextField customBugFixes, customCodeChurn, customAuthors;
    
    // Evaluation components
    private JLabel accuracyLabel, precisionLabel, recallLabel, f1ScoreLabel;
    private JTextArea evaluationLog;
    
    // Service and data
    private BugPredictionService service;
    private boolean datasetLoaded = false;
    private boolean modelTrained = false;
    
    public BugPredictionGUI() {
        service = new BugPredictionService();
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Java-Based Automated Bug Prediction System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Initialize panels
        initializeDatasetPanel();
        initializeTrainingPanel();
        initializePredictionPanel();
        initializeEvaluationPanel();
        
        // Add panels to tabbed pane
        tabbedPane.addTab("Dataset", datasetPanel);
        tabbedPane.addTab("Training", trainingPanel);
        tabbedPane.addTab("Prediction", predictionPanel);
        tabbedPane.addTab("Evaluation", evaluationPanel);
        
        // Add tabbed pane to frame
        add(tabbedPane);
        
        // Initially disable training and prediction tabs
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);
        tabbedPane.setEnabledAt(3, false);
    }
    
    private void initializeDatasetPanel() {
        datasetPanel = new JPanel(new BorderLayout());
        datasetPanel.setBorder(new TitledBorder("Dataset Management"));
        
        // Top panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        uploadButton = new JButton("Upload CSV Dataset");
        uploadButton.setFont(new Font("Arial", Font.PLAIN, 12));
        uploadButton.addActionListener(e -> uploadDataset());
        
        loadDefaultButton = new JButton("Load Default Dataset");
        loadDefaultButton.setFont(new Font("Arial", Font.PLAIN, 12));
        loadDefaultButton.addActionListener(e -> loadDefaultDataset());
        
        controlPanel.add(uploadButton);
        controlPanel.add(loadDefaultButton);
        
        // Dataset info label
        datasetInfoLabel = new JLabel("No dataset loaded");
        datasetInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        datasetInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Dataset table
        String[] columns = {"Module Name", "LOC", "Complexity", "Changes", "Bug Fixes", "Code Churn", "Authors", "Is Buggy"};
        datasetTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        datasetTable = new JTable(datasetTableModel);
        datasetTable.setFont(new Font("Arial", Font.PLAIN, 11));
        datasetTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        datasetTable.setRowHeight(20);
        
        JScrollPane scrollPane = new JScrollPane(datasetTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Add components to panel
        datasetPanel.add(controlPanel, BorderLayout.NORTH);
        datasetPanel.add(datasetInfoLabel, BorderLayout.CENTER);
        datasetPanel.add(scrollPane, BorderLayout.SOUTH);
    }
    
    private void initializeTrainingPanel() {
        trainingPanel = new JPanel(new BorderLayout());
        trainingPanel.setBorder(new TitledBorder("Model Training"));
        
        // Top panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithmLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        algorithmComboBox = new JComboBox<>(new String[]{
            "Naive Bayes", "Decision Tree (J48)", "Logistic Regression", "Random Forest"
        });
        algorithmComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        
        trainButton = new JButton("Train Model");
        trainButton.setFont(new Font("Arial", Font.BOLD, 12));
        trainButton.addActionListener(e -> trainModel());
        
        controlPanel.add(algorithmLabel);
        controlPanel.add(algorithmComboBox);
        controlPanel.add(trainButton);
        
        // Training status
        trainingStatusLabel = new JLabel("Model not trained");
        trainingStatusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        trainingStatusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Training log
        trainingLog = new JTextArea();
        trainingLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        trainingLog.setEditable(false);
        trainingLog.setBackground(Color.WHITE);
        JScrollPane logScrollPane = new JScrollPane(trainingLog);
        logScrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Add components to panel
        trainingPanel.add(controlPanel, BorderLayout.NORTH);
        trainingPanel.add(trainingStatusLabel, BorderLayout.CENTER);
        trainingPanel.add(logScrollPane, BorderLayout.SOUTH);
    }
    
    private void initializePredictionPanel() {
        predictionPanel = new JPanel(new BorderLayout());
        predictionPanel.setBorder(new TitledBorder("Bug Prediction"));
        
        // Top panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        predictAllButton = new JButton("Predict All Test Modules");
        predictAllButton.setFont(new Font("Arial", Font.PLAIN, 12));
        predictAllButton.addActionListener(e -> predictAllModules());
        
        predictCustomButton = new JButton("Predict Custom Module");
        predictCustomButton.setFont(new Font("Arial", Font.PLAIN, 12));
        predictCustomButton.addActionListener(e -> predictCustomModule());
        
        controlPanel.add(predictAllButton);
        controlPanel.add(predictCustomButton);
        
        // Custom prediction panel
        JPanel customPanel = new JPanel(new GridBagLayout());
        customPanel.setBorder(BorderFactory.createTitledBorder("Custom Module Prediction"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Add input fields
        gbc.gridx = 0; gbc.gridy = 0;
        customPanel.add(new JLabel("Module Name:"), gbc);
        gbc.gridx = 1;
        customModuleName = new JTextField(15);
        customPanel.add(customModuleName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        customPanel.add(new JLabel("Lines of Code:"), gbc);
        gbc.gridx = 1;
        customLOC = new JTextField(15);
        customPanel.add(customLOC, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        customPanel.add(new JLabel("Cyclomatic Complexity:"), gbc);
        gbc.gridx = 1;
        customComplexity = new JTextField(15);
        customPanel.add(customComplexity, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        customPanel.add(new JLabel("Number of Changes:"), gbc);
        gbc.gridx = 1;
        customChanges = new JTextField(15);
        customPanel.add(customChanges, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        customPanel.add(new JLabel("Bug Fixes:"), gbc);
        gbc.gridx = 1;
        customBugFixes = new JTextField(15);
        customPanel.add(customBugFixes, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        customPanel.add(new JLabel("Code Churn:"), gbc);
        gbc.gridx = 1;
        customCodeChurn = new JTextField(15);
        customPanel.add(customCodeChurn, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        customPanel.add(new JLabel("Author Count:"), gbc);
        gbc.gridx = 1;
        customAuthors = new JTextField(15);
        customPanel.add(customAuthors, gbc);
        
        // Prediction results table
        String[] columns = {"Module Name", "Actual Status", "Predicted Status", "Risk Probability", "Risk Level"};
        predictionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        predictionTable = new JTable(predictionTableModel);
        predictionTable.setFont(new Font("Arial", Font.PLAIN, 11));
        predictionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        predictionTable.setRowHeight(20);
        
        // Color code risk levels
        predictionTable.getColumnModel().getColumn(4).setCellRenderer(new RiskLevelRenderer());
        
        JScrollPane scrollPane = new JScrollPane(predictionTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        
        // Add components to panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(customPanel, BorderLayout.CENTER);
        
        predictionPanel.add(topPanel, BorderLayout.NORTH);
        predictionPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void initializeEvaluationPanel() {
        evaluationPanel = new JPanel(new BorderLayout());
        evaluationPanel.setBorder(new TitledBorder("Model Evaluation"));
        
        // Metrics panel
        JPanel metricsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        metricsPanel.setBorder(BorderFactory.createTitledBorder("Performance Metrics"));
        
        accuracyLabel = new JLabel("Accuracy: --");
        accuracyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        accuracyLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        precisionLabel = new JLabel("Precision: --");
        precisionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        precisionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        recallLabel = new JLabel("Recall: --");
        recallLabel.setFont(new Font("Arial", Font.BOLD, 14));
        recallLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        f1ScoreLabel = new JLabel("F1-Score: --");
        f1ScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        f1ScoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        metricsPanel.add(accuracyLabel);
        metricsPanel.add(precisionLabel);
        metricsPanel.add(recallLabel);
        metricsPanel.add(f1ScoreLabel);
        
        // Evaluation log
        evaluationLog = new JTextArea();
        evaluationLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        evaluationLog.setEditable(false);
        evaluationLog.setBackground(Color.WHITE);
        JScrollPane logScrollPane = new JScrollPane(evaluationLog);
        logScrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Add components to panel
        evaluationPanel.add(metricsPanel, BorderLayout.NORTH);
        evaluationPanel.add(logScrollPane, BorderLayout.CENTER);
    }
    
    private void uploadDataset() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadDataset(selectedFile.getAbsolutePath());
        }
    }
    
    private void loadDefaultDataset() {
        loadDataset("src/main/resources/bug_dataset.csv");
    }
    
    private void loadDataset(String filePath) {
        try {
            service.loadDataset(filePath);
            datasetLoaded = true;
            
            // Update dataset info
            List<DataPreprocessor.SoftwareModule> allModules = service.getAllModules();
            datasetInfoLabel.setText(String.format("Dataset loaded: %d modules (%d training, %d testing)", 
                                                  allModules.size(),
                                                  service.getTrainingModulesCount(), service.getTestModulesCount()));
            
            // Update dataset table
            updateDatasetTable();
            
            // Enable training tab
            tabbedPane.setEnabledAt(1, true);
            tabbedPane.setSelectedIndex(1);
            
            JOptionPane.showMessageDialog(this, "Dataset loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load dataset: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateDatasetTable() {
        datasetTableModel.setRowCount(0);
        
        // Add training modules
        for (DataPreprocessor.SoftwareModule module : service.getTrainingModules()) {
            Object[] row = {
                module.getModuleName(),
                module.getLoc(),
                module.getCyclomaticComplexity(),
                module.getNumChanges(),
                module.getBugFixes(),
                module.getCodeChurn(),
                module.getAuthorCount(),
                module.isBuggy() ? "Buggy" : "Not Buggy"
            };
            datasetTableModel.addRow(row);
        }
        
        // Add test modules
        for (DataPreprocessor.SoftwareModule module : service.getTestModules()) {
            Object[] row = {
                module.getModuleName(),
                module.getLoc(),
                module.getCyclomaticComplexity(),
                module.getNumChanges(),
                module.getBugFixes(),
                module.getCodeChurn(),
                module.getAuthorCount(),
                module.isBuggy() ? "Buggy" : "Not Buggy"
            };
            datasetTableModel.addRow(row);
        }
    }
    
    private void trainModel() {
        if (!datasetLoaded) {
            JOptionPane.showMessageDialog(this, "Please load a dataset first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Disable training button during training
        trainButton.setEnabled(false);
        trainingLog.setText("Training model...\n");
        
        // Run training in background thread
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                MLModelTrainer.ModelType modelType = getModelType();
                
                publish("Training " + modelType.getDisplayName() + " model...\n");
                service.trainModel(modelType);
                
                publish("Model training completed!\n");
                publish("Training Accuracy: " + String.format("%.2f%%", service.getModelInfo().contains("Accuracy:") ? 
                    Double.parseDouble(service.getModelInfo().split("Accuracy:")[1].trim().replace("%", "")) : 0.0) + "\n");
                
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String chunk : chunks) {
                    trainingLog.append(chunk);
                }
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    modelTrained = true;
                    trainingStatusLabel.setText("Model trained successfully: " + service.getModelInfo());
                    
                    // Enable other tabs
                    tabbedPane.setEnabledAt(2, true);
                    tabbedPane.setEnabledAt(3, true);
                    
                    JOptionPane.showMessageDialog(BugPredictionGUI.this, "Model training completed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception e) {
                    trainingLog.append("Training failed: " + e.getMessage() + "\n");
                    JOptionPane.showMessageDialog(BugPredictionGUI.this, "Training failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    trainButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private MLModelTrainer.ModelType getModelType() {
        int selectedIndex = algorithmComboBox.getSelectedIndex();
        switch (selectedIndex) {
            case 0: return MLModelTrainer.ModelType.NAIVE_BAYES;
            case 1: return MLModelTrainer.ModelType.DECISION_TREE;
            case 2: return MLModelTrainer.ModelType.LOGISTIC_REGRESSION;
            case 3: return MLModelTrainer.ModelType.RANDOM_FOREST;
            default: return MLModelTrainer.ModelType.NAIVE_BAYES;
        }
    }
    
    private void predictAllModules() {
        if (!modelTrained) {
            JOptionPane.showMessageDialog(this, "Please train a model first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            predictionTableModel.setRowCount(0);
            
            // Predict for test modules
            for (DataPreprocessor.SoftwareModule module : service.getTestModules()) {
                MLModelTrainer.PredictionResult result = service.predictModule(module);
                
                Object[] row = {
                    module.getModuleName(),
                    module.isBuggy() ? "Buggy" : "Not Buggy",
                    result.isBuggy() ? "Buggy" : "Not Buggy",
                    String.format("%.1f%%", result.getRiskProbability()),
                    getRiskLevel(result.getRiskProbability())
                };
                predictionTableModel.addRow(row);
            }
            
            JOptionPane.showMessageDialog(this, "Predictions completed for all test modules!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Prediction failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void predictCustomModule() {
        if (!modelTrained) {
            JOptionPane.showMessageDialog(this, "Please train a model first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Get input values
            String moduleName = customModuleName.getText().trim();
            double loc = Double.parseDouble(customLOC.getText().trim());
            double complexity = Double.parseDouble(customComplexity.getText().trim());
            double changes = Double.parseDouble(customChanges.getText().trim());
            double bugFixes = Double.parseDouble(customBugFixes.getText().trim());
            double codeChurn = Double.parseDouble(customCodeChurn.getText().trim());
            double authors = Double.parseDouble(customAuthors.getText().trim());
            
            // Make prediction
            MLModelTrainer.PredictionResult result = service.predictModule(moduleName, loc, complexity, 
                                                                          changes, bugFixes, codeChurn, authors);
            
            // Add to results table
            Object[] row = {
                moduleName,
                "Unknown",
                result.isBuggy() ? "Buggy" : "Not Buggy",
                String.format("%.1f%%", result.getRiskProbability()),
                getRiskLevel(result.getRiskProbability())
            };
            predictionTableModel.addRow(row);
            
            JOptionPane.showMessageDialog(this, "Prediction completed for " + moduleName + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Prediction failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getRiskLevel(double probability) {
        if (probability >= 70) return "High";
        else if (probability >= 40) return "Medium";
        else return "Low";
    }
    
    // Custom renderer for risk level
    private class RiskLevelRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value.equals("High")) {
                component.setBackground(new Color(255, 200, 200)); // Light red
                setForeground(Color.RED);
            } else if (value.equals("Medium")) {
                component.setBackground(new Color(255, 255, 200)); // Light yellow
                setForeground(Color.ORANGE);
            } else {
                component.setBackground(new Color(200, 255, 200)); // Light green
                setForeground(Color.GREEN.darker());
            }
            
            return component;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                BugPredictionGUI gui = new BugPredictionGUI();
                gui.setVisible(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Failed to start application: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
