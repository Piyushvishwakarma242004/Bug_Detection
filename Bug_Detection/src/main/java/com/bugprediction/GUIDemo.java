package com.bugprediction;

import javax.swing.SwingUtilities;

/**
 * Simple demo to launch the GUI application
 */
public class GUIDemo {
    public static void main(String[] args) {
        System.out.println("Starting Bug Prediction GUI Demo...");
        System.out.println("This will launch the Swing-based GUI application.");
        System.out.println("The GUI includes:");
        System.out.println("1. Dataset upload and preview");
        System.out.println("2. Model training with multiple algorithms");
        System.out.println("3. Bug prediction with risk probabilities");
        System.out.println("4. Model evaluation metrics");
        System.out.println("5. Interactive tables and visual feedback");
        
        // Launch the GUI
        SwingUtilities.invokeLater(() -> {
            try {
                BugPredictionGUI gui = new BugPredictionGUI();
                gui.setVisible(true);
                System.out.println("GUI launched successfully!");
            } catch (Exception e) {
                System.err.println("Failed to launch GUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
