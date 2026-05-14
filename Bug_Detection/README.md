# Java-Based Automated Bug Prediction System (GUI-Based)

A machine learning desktop application with Swing GUI that predicts which software modules are more likely to contain bugs using historical bug data and software metrics.

## 🎯 Objective

Help developers identify high-risk modules early so they can focus testing and debugging efforts on the most error-prone parts of a software project through an intuitive graphical interface.

## 🛠️ Technologies

- **Java 11**
- **Java Swing** - GUI Framework
- **Weka ML Library** - Machine Learning algorithms
- **Apache Commons CSV** - CSV data handling
- **Maven** - Build management

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/bugprediction/
│   │   ├── BugPredictionGUI.java           # Main Swing GUI application
│   │   ├── BugPredictionService.java        # Service layer
│   │   ├── DataPreprocessor.java           # Data preprocessing
│   │   ├── MLModelTrainer.java             # ML model training
│   │   ├── BugPredictionSystem.java        # Console version
│   │   ├── BugPredictionDemo.java          # Console demo
│   │   └── GUIDemo.java                  # GUI launcher demo
│   └── resources/
│       └── bug_dataset.csv                # Sample dataset
```

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Installation

1. Clone or download this project
2. Navigate to the project directory
3. Build the project:

```bash
mvn clean compile
```

### Running the GUI Application

```bash
mvn exec:java -Dexec.mainClass="com.bugprediction.BugPredictionGUI"
```

Or run the demo:

```bash
mvn exec:java -Dexec.mainClass="com.bugprediction.GUIDemo"
```

Or run the compiled classes:

```bash
java -cp target/classes com.bugprediction.BugPredictionGUI
```

## 🖥️ GUI Features

### Main Interface

The application provides a tabbed interface with four main sections:

1. **Dataset Tab** - Upload and preview CSV datasets
2. **Training Tab** - Train ML models with different algorithms
3. **Prediction Tab** - Make predictions and view results
4. **Evaluation Tab** - View model performance metrics

### Dataset Management

- **Upload CSV Dataset**: Browse and select custom CSV files
- **Load Default Dataset**: Use the provided sample dataset
- **Dataset Preview**: View all modules in a sortable table
- **Dataset Statistics**: Display training/test split information

### Model Training

- **Algorithm Selection**: Choose from 4 ML algorithms:
  - Naive Bayes
  - Decision Tree (J48)
  - Logistic Regression
  - Random Forest
- **Training Progress**: Real-time training logs
- **Training Status**: Display model information and accuracy

### Bug Prediction

- **Predict All Test Modules**: Batch prediction for test dataset
- **Predict Custom Module**: Manual input for new modules
- **Results Table**: Color-coded risk levels
- **Risk Probability**: Percentage likelihood of bugs

### Model Evaluation

- **Performance Metrics**: Accuracy, Precision, Recall, F1-Score
- **Visual Display**: Clear metric presentation
- **Evaluation Log**: Detailed evaluation information

## 📊 Dataset Format

The system expects a CSV file with the following columns:

| Column | Description | Example |
|--------|-------------|---------|
| module_name | Name of the software module | LoginController |
| loc | Lines of Code | 150 |
| cyclomatic_complexity | Code complexity metric | 12 |
| num_changes | Number of modifications | 8 |
| bug_fixes | Number of bug fixes | 3 |
| code_churn | Total lines added/deleted | 450 |
| author_count | Number of developers | 2 |
| is_buggy | Bug label (buggy/not_buggy) | buggy |

## 🎨 GUI Components

### Tables and Display

- **Dataset Table**: Shows all loaded modules with metrics
- **Prediction Results Table**: Displays predictions with risk levels
- **Color Coding**: 
  - 🔴 High Risk (≥70%): Light red background
  - 🟡 Medium Risk (40-69%): Light yellow background  
  - 🟢 Low Risk (<40%): Light green background

### Input Forms

- **Custom Module Input**: Form fields for manual module metrics
- **File Chooser**: Native file selection dialog
- **Algorithm Selector**: Dropdown for ML algorithm choice

### Interactive Elements

- **Tab Navigation**: Easy switching between functions
- **Progress Indicators**: Real-time training feedback
- **Status Messages**: Clear success/error notifications
- **Responsive Layout**: Adapts to window resizing

## 🤖 Machine Learning Integration

### Supported Algorithms

1. **Naive Bayes**
   - Fast training
   - Good for baseline comparison
   - Probabilistic predictions

2. **Decision Tree (J48)**
   - Interpretable rules
   - Good accuracy
   - Handles non-linear relationships

3. **Logistic Regression**
   - Probabilistic output
   - Linear relationships
   - Well-calibrated probabilities

4. **Random Forest**
   - High accuracy
   - Robust to overfitting
   - Feature importance

### Training Process

1. **Data Split**: 80% training, 20% testing
2. **Feature Scaling**: Automatic normalization
3. **Model Training**: Background processing
4. **Validation**: Cross-validation metrics

## 📈 Output and Results

### Prediction Output

For each software module, the system displays:

- **Module Name**: Identifier of the software module
- **Actual Status**: Ground truth (for test modules)
- **Predicted Status**: Buggy / Not Buggy
- **Risk Probability**: Percentage likelihood (e.g., 78%)
- **Risk Level**: High/Medium/Low with color coding

### Evaluation Metrics

- **Accuracy**: Overall correctness percentage
- **Precision**: True positive rate for buggy modules
- **Recall**: Coverage of actual buggy modules
- **F1-Score**: Balance between precision and recall

## 🎮 Usage Guide

### Basic Workflow

1. **Launch Application**: Start the GUI application
2. **Load Dataset**: Use default or upload custom CSV
3. **Train Model**: Select algorithm and start training
4. **Evaluate Performance**: Check model metrics
5. **Make Predictions**: Test on new modules
6. **Analyze Results**: Review risk assessments

### Advanced Features

- **Custom Module Testing**: Input new module metrics
- **Batch Prediction**: Process multiple modules
- **Model Comparison**: Train different algorithms
- **Export Results**: Copy table data for reports

## 🔧 Customization

### Adding New Features

1. Update `DataPreprocessor.java` for new metrics
2. Modify GUI components in `BugPredictionGUI.java`
3. Extend ML algorithms in `MLModelTrainer.java`

### Custom Datasets

1. Prepare CSV with required columns
2. Use "Upload CSV Dataset" button
3. System automatically validates and loads data

## � Project Deliverables

### ✅ Completed Components

- **Java Source Code**: Complete GUI application with all classes
- **Sample Dataset**: CSV file with historical bug data
- **GUI Application**: Fully functional Swing interface
- **ML Integration**: Weka-based prediction system
- **Documentation**: Comprehensive README and code comments

### 📄 Expected Deliverables

- ✅ **Java Source Code**: Complete implementation
- ✅ **Dataset**: Sample CSV dataset  
- ✅ **GUI Application**: Interactive desktop application
- ✅ **Output Screenshots**: Can be captured from GUI
- � **Final Project Report**: 
  - Abstract
  - Problem Statement
  - Methodology
  - Results
  - Conclusion

## 🐛 Troubleshooting

### Common Issues

1. **GUI Not Starting**: Check Java version and display settings
2. **Dataset Loading**: Verify CSV format and file permissions
3. **Memory Issues**: Increase heap size: `-Xmx2g`
4. **Training Errors**: Check data quality and algorithm choice

### Performance Tips

- Use smaller datasets for initial testing
- Naive Bayes is fastest for large datasets
- Random Forest provides best accuracy
- Close unused tabs to free memory

## 📚 References

- Weka Machine Learning Library: https://www.cs.waikato.ac.nz/ml/weka/
- Java Swing Documentation: https://docs.oracle.com/javase/tutorial/uiswing/
- Software Metrics Research: Empirical studies on bug prediction
- GUI Design Guidelines: Java Swing best practices

## 👥 Contributing

1. Fork the project repository
2. Create a feature branch
3. Implement changes with proper testing
4. Update documentation
5. Submit a pull request

## 📄 License

This project is for educational purposes as part of a Bachelor's level software engineering project demonstrating GUI development and machine learning integration.

---

**Note**: This GUI-based system provides an intuitive interface for bug prediction using machine learning. The Swing-based interface makes it accessible to users without command-line experience while maintaining the full functionality of the underlying ML algorithms.
