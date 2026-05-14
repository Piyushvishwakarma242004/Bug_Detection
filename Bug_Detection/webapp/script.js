// Global variables
let currentDataset = [];
let trainedModel = null;
let isDatasetLoaded = false;
let isModelTrained = false;

// Default dataset
const defaultDataset = [
    { module_name: "LoginController", loc: 150, cyclomatic_complexity: 12, num_changes: 8, bug_fixes: 3, code_churn: 450, author_count: 2, is_buggy: "buggy" },
    { module_name: "UserService", loc: 200, cyclomatic_complexity: 15, num_changes: 12, bug_fixes: 5, code_churn: 600, author_count: 3, is_buggy: "buggy" },
    { module_name: "DatabaseUtil", loc: 80, cyclomatic_complexity: 5, num_changes: 3, bug_fixes: 0, code_churn: 120, author_count: 1, is_buggy: "not_buggy" },
    { module_name: "PaymentProcessor", loc: 300, cyclomatic_complexity: 25, num_changes: 20, bug_fixes: 12, code_churn: 1200, author_count: 4, is_buggy: "buggy" },
    { module_name: "EmailService", loc: 120, cyclomatic_complexity: 8, num_changes: 6, bug_fixes: 2, code_churn: 280, author_count: 2, is_buggy: "not_buggy" },
    { module_name: "AuthHelper", loc: 60, cyclomatic_complexity: 4, num_changes: 2, bug_fixes: 0, code_churn: 80, author_count: 1, is_buggy: "not_buggy" },
    { module_name: "ReportGenerator", loc: 250, cyclomatic_complexity: 18, num_changes: 15, bug_fixes: 8, code_churn: 750, author_count: 3, is_buggy: "buggy" },
    { module_name: "CacheManager", loc: 90, cyclomatic_complexity: 6, num_changes: 4, bug_fixes: 1, code_churn: 150, author_count: 2, is_buggy: "not_buggy" },
    { module_name: "APIController", loc: 180, cyclomatic_complexity: 14, num_changes: 10, bug_fixes: 4, code_churn: 520, author_count: 3, is_buggy: "buggy" },
    { module_name: "ValidationUtil", loc: 45, cyclomatic_complexity: 3, num_changes: 2, bug_fixes: 0, code_churn: 60, author_count: 1, is_buggy: "not_buggy" },
    { module_name: "FileUploader", loc: 220, cyclomatic_complexity: 16, num_changes: 11, bug_fixes: 5, code_churn: 680, author_count: 2, is_buggy: "buggy" },
    { module_name: "Logger", loc: 35, cyclomatic_complexity: 2, num_changes: 1, bug_fixes: 0, code_churn: 25, author_count: 1, is_buggy: "not_buggy" },
    { module_name: "DataMapper", loc: 160, cyclomatic_complexity: 11, num_changes: 9, bug_fixes: 3, code_churn: 420, author_count: 2, is_buggy: "not_buggy" },
    { module_name: "SecurityFilter", loc: 190, cyclomatic_complexity: 13, num_changes: 8, bug_fixes: 4, code_churn: 480, author_count: 3, is_buggy: "buggy" },
    { module_name: "ConfigLoader", loc: 55, cyclomatic_complexity: 3, num_changes: 2, bug_fixes: 0, code_churn: 70, author_count: 1, is_buggy: "not_buggy" },
    { module_name: "SessionManager", loc: 140, cyclomatic_complexity: 10, num_changes: 7, bug_fixes: 2, code_churn: 350, author_count: 2, is_buggy: "not_buggy" },
    { module_name: "ErrorHandler", loc: 75, cyclomatic_complexity: 5, num_changes: 3, bug_fixes: 1, code_churn: 110, author_count: 2, is_buggy: "not_buggy" },
    { module_name: "RESTClient", loc: 210, cyclomatic_complexity: 15, num_changes: 12, bug_fixes: 6, code_churn: 630, author_count: 3, is_buggy: "buggy" },
    { module_name: "QueryBuilder", loc: 95, cyclomatic_complexity: 7, num_changes: 5, bug_fixes: 2, code_churn: 180, author_count: 2, is_buggy: "not_buggy" },
    { module_name: "UnitTestRunner", loc: 85, cyclomatic_complexity: 6, num_changes: 4, bug_fixes: 1, code_churn: 140, author_count: 2, is_buggy: "not_buggy" }
];

// Tab switching
function showTab(tabName) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Remove active class from all buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show selected tab
    document.getElementById(tabName + '-tab').classList.add('active');
    
    // Add active class to clicked button
    event.target.classList.add('active');
}

// File upload handling
document.addEventListener('DOMContentLoaded', function() {
    const uploadArea = document.getElementById('uploadArea');
    const fileInput = document.getElementById('fileInput');
    
    // Click to upload
    uploadArea.addEventListener('click', () => {
        fileInput.click();
    });
    
    // Drag and drop
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.style.background = 'rgba(102, 126, 234, 0.2)';
    });
    
    uploadArea.addEventListener('dragleave', () => {
        uploadArea.style.background = 'rgba(102, 126, 234, 0.05)';
    });
    
    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.style.background = 'rgba(102, 126, 234, 0.05)';
        
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFileUpload(files[0]);
        }
    });
    
    // File input change
    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length > 0) {
            handleFileUpload(e.target.files[0]);
        }
    });
});

// Handle file upload
function handleFileUpload(file) {
    if (!file.name.endsWith('.csv')) {
        showNotification('Please upload a CSV file', 'error');
        return;
    }
    
    showLoading();
    
    const reader = new FileReader();
    reader.onload = function(e) {
        try {
            const csvData = parseCSV(e.target.result);
            loadDataset(csvData);
            hideLoading();
            showNotification('Dataset loaded successfully!', 'success');
        } catch (error) {
            hideLoading();
            showNotification('Error parsing CSV file: ' + error.message, 'error');
        }
    };
    
    reader.readAsText(file);
}

// Parse CSV data
function parseCSV(csvText) {
    const lines = csvText.split('\n').filter(line => line.trim());
    const headers = lines[0].split(',').map(h => h.trim());
    const data = [];
    
    for (let i = 1; i < lines.length; i++) {
        const values = lines[i].split(',').map(v => v.trim());
        if (values.length === headers.length) {
            const row = {};
            headers.forEach((header, index) => {
                row[header] = values[index];
            });
            data.push(row);
        }
    }
    
    return data;
}

// Load default dataset
function loadDefaultDataset() {
    showLoading();
    setTimeout(() => {
        loadDataset(defaultDataset);
        hideLoading();
        showNotification('Default dataset loaded successfully!', 'success');
    }, 500);
}

// Load dataset into application
function loadDataset(data) {
    currentDataset = data;
    isDatasetLoaded = true;
    
    // Split into training (80%) and test (20%)
    const splitIndex = Math.floor(data.length * 0.8);
    const trainingData = data.slice(0, splitIndex);
    const testData = data.slice(splitIndex);
    
    // Store for later use
    window.trainingData = trainingData;
    window.testData = testData;
    
    // Update dataset info
    updateDatasetInfo(data, trainingData, testData);
    
    // Update dataset table
    updateDatasetTable(data);
    
    // Show dataset info and table
    document.getElementById('datasetInfo').style.display = 'block';
    document.getElementById('datasetTableContainer').style.display = 'block';
    
    // Enable training tab
    document.querySelectorAll('.tab-btn')[1].disabled = false;
}

// Update dataset information
function updateDatasetInfo(data, trainingData, testData) {
    const buggyCount = data.filter(row => row.is_buggy === 'buggy').length;
    
    document.getElementById('totalModules').textContent = data.length;
    document.getElementById('trainingModules').textContent = trainingData.length;
    document.getElementById('testModules').textContent = testData.length;
    document.getElementById('buggyModules').textContent = buggyCount + ` (${(buggyCount/data.length*100).toFixed(1)}%)`;
}

// Update dataset table
function updateDatasetTable(data) {
    const tbody = document.getElementById('datasetTableBody');
    tbody.innerHTML = '';
    
    data.forEach(row => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${row.module_name}</td>
            <td>${row.loc}</td>
            <td>${row.cyclomatic_complexity}</td>
            <td>${row.num_changes}</td>
            <td>${row.bug_fixes}</td>
            <td>${row.code_churn}</td>
            <td>${row.author_count}</td>
            <td><span class="status-badge ${row.is_buggy === 'buggy' ? 'buggy' : 'not-buggy'}">${row.is_buggy}</span></td>
        `;
        tbody.appendChild(tr);
    });
}

// Train model
function trainModel() {
    if (!isDatasetLoaded) {
        showNotification('Please load a dataset first', 'error');
        return;
    }
    
    const algorithm = document.getElementById('algorithmSelect').value;
    
    // Disable training button
    const trainBtn = document.getElementById('trainBtn');
    trainBtn.disabled = true;
    trainBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Training...';
    
    // Show training status
    document.getElementById('trainingStatus').style.display = 'block';
    
    // Simulate training process
    simulateTraining(algorithm);
}

// Simulate training process
function simulateTraining(algorithm) {
    const algorithmNames = {
        'naive_bayes': 'Naive Bayes',
        'decision_tree': 'Decision Tree (J48)',
        'logistic_regression': 'Logistic Regression',
        'random_forest': 'Random Forest'
    };
    
    const trainingLog = document.getElementById('trainingLog');
    const progressBar = document.querySelector('.progress-bar::before');
    const progressText = document.getElementById('progressText');
    
    let progress = 0;
    trainingLog.innerHTML = `Starting training with ${algorithmNames[algorithm]}...\n`;
    
    const interval = setInterval(() => {
        progress += Math.random() * 15;
        if (progress > 100) progress = 100;
        
        // Update progress bar
        document.querySelector('.progress-bar').style.setProperty('--progress', progress + '%');
        progressText.textContent = Math.round(progress) + '%';
        
        // Add log messages
        if (progress === 25) {
            trainingLog.innerHTML += 'Processing training data...\n';
        } else if (progress === 50) {
            trainingLog.innerHTML += 'Building model...\n';
        } else if (progress === 75) {
            trainingLog.innerHTML += 'Validating model...\n';
        } else if (progress >= 100) {
            clearInterval(interval);
            completeTraining(algorithmNames[algorithm]);
        }
        
        trainingLog.scrollTop = trainingLog.scrollHeight;
    }, 200);
}

// Complete training
function completeTraining(algorithmName) {
    // Simulate training metrics
    const accuracy = 75 + Math.random() * 20;
    const precision = 70 + Math.random() * 25;
    const recall = 65 + Math.random() * 30;
    
    // Update model info
    document.getElementById('modelAlgorithm').textContent = algorithmName;
    document.getElementById('trainingAccuracy').textContent = accuracy.toFixed(2) + '%';
    document.getElementById('trainingPrecision').textContent = precision.toFixed(2) + '%';
    document.getElementById('trainingRecall').textContent = recall.toFixed(2) + '%';
    
    // Show model info
    document.getElementById('modelInfo').style.display = 'block';
    
    // Store trained model info
    trainedModel = {
        algorithm: algorithmName,
        accuracy: accuracy,
        precision: precision,
        recall: recall
    };
    isModelTrained = true;
    
    // Reset training button
    const trainBtn = document.getElementById('trainBtn');
    trainBtn.disabled = false;
    trainBtn.innerHTML = '<i class="fas fa-play"></i> Train Model';
    
    // Enable prediction and evaluation tabs
    document.querySelectorAll('.tab-btn')[2].disabled = false;
    document.querySelectorAll('.tab-btn')[3].disabled = false;
    
    showNotification('Model training completed!', 'success');
}

// Predict all test modules
function predictAllModules() {
    if (!isModelTrained) {
        showNotification('Please train a model first', 'error');
        return;
    }
    
    showLoading();
    
    setTimeout(() => {
        const results = [];
        window.testData.forEach(module => {
            const prediction = predictModule(module);
            results.push({
                ...module,
                ...prediction
            });
        });
        
        displayPredictionResults(results);
        hideLoading();
        showNotification('Predictions completed for all test modules!', 'success');
    }, 1000);
}

// Predict custom module
function predictCustomModule() {
    if (!isModelTrained) {
        showNotification('Please train a model first', 'error');
        return;
    }
    
    const moduleName = document.getElementById('moduleName').value.trim();
    const loc = parseFloat(document.getElementById('loc').value);
    const complexity = parseFloat(document.getElementById('complexity').value);
    const changes = parseFloat(document.getElementById('changes').value);
    const bugFixes = parseFloat(document.getElementById('bugFixes').value);
    const codeChurn = parseFloat(document.getElementById('codeChurn').value);
    const authors = parseFloat(document.getElementById('authors').value);
    
    if (!moduleName || isNaN(loc) || isNaN(complexity) || isNaN(changes) || 
        isNaN(bugFixes) || isNaN(codeChurn) || isNaN(authors)) {
        showNotification('Please fill in all fields with valid numbers', 'error');
        return;
    }
    
    const module = {
        module_name: moduleName,
        loc, cyclomatic_complexity: complexity, num_changes: changes,
        bug_fixes: bugFixes, code_churn: codeChurn, author_count: authors
    };
    
    const prediction = predictModule(module);
    
    // Add to results
    const results = getCurrentResults();
    results.push({
        ...module,
        ...prediction,
        is_buggy: 'Unknown'
    });
    
    displayPredictionResults(results);
    
    // Clear form
    document.getElementById('moduleName').value = '';
    document.getElementById('loc').value = '';
    document.getElementById('complexity').value = '';
    document.getElementById('changes').value = '';
    document.getElementById('bugFixes').value = '';
    document.getElementById('codeChurn').value = '';
    document.getElementById('authors').value = '';
    
    showNotification('Prediction completed!', 'success');
}

// Predict single module
function predictModule(module) {
    // Simple heuristic-based prediction (in real app, this would use the trained ML model)
    const riskScore = calculateRiskScore(module);
    const isBuggy = riskScore > 0.5;
    const riskProbability = isBuggy ? riskScore * 100 : (1 - riskScore) * 100;
    
    return {
        predicted_status: isBuggy ? 'buggy' : 'not_buggy',
        risk_probability: riskProbability,
        risk_level: getRiskLevel(riskProbability)
    };
}

// Calculate risk score (simplified ML logic)
function calculateRiskScore(module) {
    let score = 0;
    
    // Weight factors based on importance
    score += (module.loc / 500) * 0.2;                    // LOC weight
    score += (module.cyclomatic_complexity / 30) * 0.25;         // Complexity weight
    score += (module.num_changes / 25) * 0.2;                 // Changes weight
    score += (module.bug_fixes / 15) * 0.25;                  // Bug fixes weight
    score += (module.code_churn / 1500) * 0.1;               // Code churn weight
    
    return Math.min(score, 1);
}

// Get risk level
function getRiskLevel(probability) {
    if (probability >= 70) return 'High';
    if (probability >= 40) return 'Medium';
    return 'Low';
}

// Get current results
function getCurrentResults() {
    const results = [];
    const tbody = document.getElementById('resultsTableBody');
    const rows = tbody.querySelectorAll('tr');
    
    rows.forEach(row => {
        const cells = row.querySelectorAll('td');
        results.push({
            module_name: cells[0].textContent,
            is_buggy: cells[1].textContent,
            predicted_status: cells[2].textContent,
            risk_probability: parseFloat(cells[3].textContent),
            risk_level: cells[4].textContent
        });
    });
    
    return results;
}

// Display prediction results
function displayPredictionResults(results) {
    const tbody = document.getElementById('resultsTableBody');
    tbody.innerHTML = '';
    
    results.forEach(result => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${result.module_name}</td>
            <td>${result.is_buggy}</td>
            <td>${result.predicted_status}</td>
            <td>${result.risk_probability.toFixed(1)}%</td>
            <td><span class="risk-badge risk-${result.risk_level.toLowerCase()}">${result.risk_level}</span></td>
        `;
        tbody.appendChild(tr);
    });
    
    // Show results container
    document.getElementById('resultsContainer').style.display = 'block';
}

// Show custom prediction form
function showCustomPrediction() {
    const customDiv = document.getElementById('customPrediction');
    customDiv.style.display = customDiv.style.display === 'none' ? 'block' : 'none';
}

// Evaluate model
function evaluateModel() {
    if (!isModelTrained) {
        showNotification('Please train a model first', 'error');
        return;
    }
    
    showLoading();
    
    setTimeout(() => {
        // Simulate evaluation on test data
        let correct = 0;
        let truePositives = 0;
        let falsePositives = 0;
        let falseNegatives = 0;
        
        window.testData.forEach(module => {
            const prediction = predictModule(module);
            const actualBuggy = module.is_buggy === 'buggy';
            const predictedBuggy = prediction.predicted_status === 'buggy';
            
            if (actualBuggy === predictedBuggy) {
                correct++;
                if (actualBuggy) truePositives++;
            } else {
                if (predictedBuggy) falsePositives++;
                else falseNegatives++;
            }
        });
        
        const accuracy = (correct / window.testData.length) * 100;
        const precision = truePositives / (truePositives + falsePositives) * 100;
        const recall = truePositives / (truePositives + falseNegatives) * 100;
        const f1Score = 2 * (precision * recall) / (precision + recall);
        
        // Update metrics display
        document.getElementById('accuracyValue').textContent = accuracy.toFixed(2) + '%';
        document.getElementById('precisionValue').textContent = precision.toFixed(2) + '%';
        document.getElementById('recallValue').textContent = recall.toFixed(2) + '%';
        document.getElementById('f1ScoreValue').textContent = f1Score.toFixed(2) + '%';
        
        // Show metrics and evaluation log
        document.getElementById('metricsContainer').style.display = 'block';
        document.getElementById('evaluationLog').style.display = 'block';
        
        // Add evaluation details
        const logContent = document.getElementById('evaluationLogContent');
        logContent.innerHTML = `
            <strong>Model Evaluation Results</strong><br>
            Algorithm: ${trainedModel.algorithm}<br>
            Test Set Size: ${window.testData.length} modules<br>
            Correct Predictions: ${correct}/${window.testData.length}<br>
            True Positives: ${truePositives}<br>
            False Positives: ${falsePositives}<br>
            False Negatives: ${falseNegatives}<br>
            <br>
            <strong>Performance Summary:</strong><br>
            The model achieved ${accuracy.toFixed(2)}% accuracy on the test set,
            with ${precision.toFixed(2)}% precision and ${recall.toFixed(2)}% recall.
            This indicates ${accuracy > 80 ? 'good' : 'moderate'} performance in identifying buggy modules.
        `;
        
        hideLoading();
        showNotification('Model evaluation completed!', 'success');
    }, 1000);
}

// Utility functions
function showLoading() {
    document.getElementById('loadingOverlay').style.display = 'flex';
}

function hideLoading() {
    document.getElementById('loadingOverlay').style.display = 'none';
}

function showNotification(message, type) {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
        ${message}
    `;
    
    // Add to page
    document.body.appendChild(notification);
    
    // Position notification
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.zIndex = '2000';
    notification.style.padding = '15px 20px';
    notification.style.borderRadius = '8px';
    notification.style.boxShadow = '0 4px 16px rgba(0, 0, 0, 0.1)';
    notification.style.display = 'flex';
    notification.style.alignItems = 'center';
    notification.style.gap = '10px';
    notification.style.fontWeight = '500';
    notification.style.fontSize = '0.95rem';
    
    if (type === 'success') {
        notification.style.background = '#d4edda';
        notification.style.color = '#155724';
        notification.style.border = '1px solid #c3e6cb';
    } else {
        notification.style.background = '#f8d7da';
        notification.style.color = '#721c24';
        notification.style.border = '1px solid #f5c6cb';
    }
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 3000);
}

// Add CSS for status badges and risk badges
const style = document.createElement('style');
style.textContent = `
    .status-badge {
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 0.8rem;
        font-weight: 500;
    }
    
    .status-badge.buggy {
        background: rgba(220, 53, 69, 0.1);
        color: #dc3545;
    }
    
    .status-badge.not-buggy {
        background: rgba(40, 167, 69, 0.1);
        color: #28a745;
    }
    
    .risk-badge {
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 0.8rem;
        font-weight: 600;
    }
    
    .risk-high {
        background: rgba(220, 53, 69, 0.1);
        color: #dc3545;
    }
    
    .risk-medium {
        background: rgba(255, 193, 7, 0.1);
        color: #ffc107;
    }
    
    .risk-low {
        background: rgba(40, 167, 69, 0.1);
        color: #28a745;
    }
    
    .progress-bar::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        background: linear-gradient(90deg, #667eea, #764ba2);
        width: var(--progress, 0%);
        transition: width 0.3s ease;
    }
`;
document.head.appendChild(style);
