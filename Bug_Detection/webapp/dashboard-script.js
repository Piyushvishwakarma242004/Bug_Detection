// Real-Time Bug Prediction Dashboard JavaScript
let realtimeChart, riskChart;
let modules = [];
let alerts = [];
let activities = [];
let updateInterval;

// Initialize dashboard
document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
    startRealTimeUpdates();
    initializeCharts();
    generateInitialData();
});

// Initialize dashboard components
function initializeDashboard() {
    // Set initial values
    updateDashboardStats();
    loadModules();
    loadAlerts();
    loadActivities();
    
    // Start real-time updates
    setInterval(updateRealTimeData, 3000);
}

// Initialize charts
function initializeCharts() {
    // Real-time predictions chart
    const ctx1 = document.getElementById('realtimeChart').getContext('2d');
    realtimeChart = new Chart(ctx1, {
        type: 'line',
        data: {
            labels: generateTimeLabels(20),
            datasets: [{
                label: 'Bug Predictions',
                data: generateRandomData(20, 10, 50),
                borderColor: '#4fc3f7',
                backgroundColor: 'rgba(79, 195, 247, 0.1)',
                borderWidth: 2,
                tension: 0.4,
                fill: true
            }, {
                label: 'Actual Bugs',
                data: generateRandomData(20, 8, 45),
                borderColor: '#ff6b6b',
                backgroundColor: 'rgba(244, 67, 107, 0.1)',
                borderWidth: 2,
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    labels: {
                        color: '#ffffff',
                        font: {
                            size: 12
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    },
                    ticks: {
                        color: '#b8c5d6'
                    }
                },
                y: {
                    grid: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    },
                    ticks: {
                        color: '#b8c5d6'
                    }
                }
            }
        }
    });

    // Risk distribution chart
    const ctx2 = document.getElementById('riskChart').getContext('2d');
    riskChart = new Chart(ctx2, {
        type: 'doughnut',
        data: {
            labels: ['High Risk', 'Medium Risk', 'Low Risk'],
            datasets: [{
                data: [25, 45, 30],
                backgroundColor: ['#ff6b6b', '#ffa726', '#4fc3f7'],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });
}

// Generate initial data
function generateInitialData() {
    // Generate sample modules
    const moduleNames = [
        'UserService', 'PaymentGateway', 'AuthController', 'DatabaseManager',
        'APIRouter', 'CacheService', 'Logger', 'ValidationHelper',
        'EmailService', 'FileUploader', 'SecurityFilter', 'SessionManager',
        'ErrorHandler', 'DataMapper', 'QueryBuilder', 'RESTClient',
        'ReportGenerator', 'ConfigLoader', 'UnitTestRunner', 'MetricsCollector'
    ];

    modules = moduleNames.map((name, index) => ({
        id: index + 1,
        name: name,
        riskLevel: getRandomRiskLevel(),
        riskScore: Math.floor(Math.random() * 100),
        predictedBugs: Math.floor(Math.random() * 10),
        complexity: Math.floor(Math.random() * 50) + 10,
        lastUpdated: new Date(Date.now() - Math.random() * 3600000).toLocaleTimeString(),
        status: Math.random() > 0.3 ? 'Active' : 'Inactive'
    }));

    // Generate initial alerts
    alerts = [
        {
            id: 1,
            severity: 'critical',
            title: 'High Risk Module Detected',
            description: 'PaymentGateway module shows 85% risk probability',
            time: '2 minutes ago',
            module: 'PaymentGateway'
        },
        {
            id: 2,
            severity: 'high',
            title: 'Unusual Activity Detected',
            description: 'UserService module showing increased complexity',
            time: '5 minutes ago',
            module: 'UserService'
        },
        {
            id: 3,
            severity: 'medium',
            title: 'Performance Degradation',
            description: 'DatabaseManager response time increased',
            time: '8 minutes ago',
            module: 'DatabaseManager'
        }
    ];

    // Generate initial activities
    activities = [
        {
            type: 'prediction',
            title: 'New Prediction Completed',
            description: 'Analyzed 15 modules for bug risk',
            time: '1 minute ago'
        },
        {
            type: 'alert',
            title: 'Alert Triggered',
            description: 'PaymentGateway module flagged as high risk',
            time: '2 minutes ago'
        },
        {
            type: 'prediction',
            title: 'Batch Analysis Finished',
            description: 'Processed 20 modules successfully',
            time: '5 minutes ago'
        }
    ];

    updateModulesTable();
    updateAlertsPanel();
    updateActivityFeed();
}

// Update dashboard statistics
function updateDashboardStats() {
    document.getElementById('totalModules').textContent = modules.length;
    document.getElementById('highRiskCount').textContent = modules.filter(m => m.riskLevel === 'high').length;
    document.getElementById('predictionCount').textContent = Math.floor(Math.random() * 20) + 10;
    document.getElementById('accuracyRate').textContent = (85 + Math.random() * 10).toFixed(1);
    document.getElementById('activeAlerts').textContent = alerts.filter(a => a.severity === 'critical' || a.severity === 'high').length;
    document.getElementById('modelPerformance').textContent = (90 + Math.random() * 8).toFixed(1);
}

// Load modules
function loadModules() {
    updateModulesTable();
}

// Load alerts
function loadAlerts() {
    updateAlertsPanel();
}

// Load activities
function loadActivities() {
    updateActivityFeed();
}

// Update modules table
function updateModulesTable() {
    const tbody = document.getElementById('modulesTableBody');
    tbody.innerHTML = '';

    modules.forEach(module => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${module.name}</td>
            <td><span class="risk-badge ${module.riskLevel}">${module.riskLevel}</span></td>
            <td>${module.riskScore}%</td>
            <td>${module.predictedBugs}</td>
            <td>${module.complexity}</td>
            <td>${module.lastUpdated}</td>
            <td><span class="status-badge ${module.status.toLowerCase()}">${module.status}</span></td>
            <td>
                <button class="action-btn" onclick="viewModuleDetails(${module.id})">View</button>
                <button class="action-btn" onclick="analyzeModule(${module.id})">Analyze</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Update alerts panel
function updateAlertsPanel() {
    const alertsContent = document.getElementById('alertsContent');
    alertsContent.innerHTML = '';

    alerts.forEach(alert => {
        const alertItem = document.createElement('div');
        alertItem.className = 'alert-item';
        alertItem.innerHTML = `
            <div class="activity-icon alert">
                <i class="fas fa-exclamation-triangle"></i>
            </div>
            <div class="alert-content">
                <div class="alert-severity ${alert.severity}">${alert.severity}</div>
                <div class="activity-title">${alert.title}</div>
                <div class="activity-description">${alert.description}</div>
                <div class="activity-time">${alert.time}</div>
            </div>
        `;
        alertItem.onclick = () => showAlertDetails(alert);
        alertsContent.appendChild(alertItem);
    });

    document.getElementById('alertCount').textContent = alerts.length;
}

// Update activity feed
function updateActivityFeed() {
    const feedContent = document.getElementById('activityFeed');
    feedContent.innerHTML = '';

    activities.forEach(activity => {
        const activityItem = document.createElement('div');
        activityItem.className = 'activity-item';
        activityItem.innerHTML = `
            <div class="activity-icon ${activity.type}">
                <i class="fas fa-${activity.type === 'prediction' ? 'chart-line' : 'exclamation-triangle'}"></i>
            </div>
            <div class="activity-content">
                <div class="activity-title">${activity.title}</div>
                <div class="activity-description">${activity.description}</div>
                <div class="activity-time">${activity.time}</div>
            </div>
        `;
        feedContent.appendChild(activityItem);
    });
}

// Real-time updates
function startRealTimeUpdates() {
    updateInterval = setInterval(() => {
        updateRealTimeData();
        addNewActivity();
        updateCharts();
    }, 3000);
}

// Update real-time data
function updateRealTimeData() {
    // Update random module
    if (modules.length > 0) {
        const randomModule = modules[Math.floor(Math.random() * modules.length)];
        randomModule.riskScore = Math.floor(Math.random() * 100);
        randomModule.riskLevel = getRiskLevel(randomModule.riskScore);
        randomModule.lastUpdated = new Date().toLocaleTimeString();
        randomModule.predictedBugs = Math.floor(Math.random() * 10);
    }

    // Update dashboard stats
    updateDashboardStats();
    
    // Update modules table
    updateModulesTable();

    // Randomly add new alerts
    if (Math.random() > 0.8) {
        addNewAlert();
    }
}

// Update charts
function updateCharts() {
    // Update real-time chart
    if (realtimeChart) {
        realtimeChart.data.labels.shift();
        realtimeChart.data.labels.push(new Date().toLocaleTimeString());
        
        realtimeChart.data.datasets[0].data.shift();
        realtimeChart.data.datasets[0].data.push(Math.floor(Math.random() * 40) + 10);
        
        realtimeChart.data.datasets[1].data.shift();
        realtimeChart.data.datasets[1].data.push(Math.floor(Math.random() * 35) + 8);
        
        realtimeChart.update('none');
    }

    // Update risk chart
    if (riskChart) {
        const highRisk = modules.filter(m => m.riskLevel === 'high').length;
        const mediumRisk = modules.filter(m => m.riskLevel === 'medium').length;
        const lowRisk = modules.filter(m => m.riskLevel === 'low').length;
        
        riskChart.data.datasets[0].data = [highRisk, mediumRisk, lowRisk];
        riskChart.update('none');
    }
}

// Add new activity
function addNewActivity() {
    const activityTypes = ['prediction', 'alert', 'analysis'];
    const type = activityTypes[Math.floor(Math.random() * activityTypes.length)];
    
    const newActivity = {
        type: type,
        title: getRandomActivityTitle(type),
        description: getRandomActivityDescription(type),
        time: 'Just now'
    };

    activities.unshift(newActivity);
    if (activities.length > 10) {
        activities.pop();
    }
    
    updateActivityFeed();
    showNotification(newActivity.title, 'success');
}

// Add new alert
function addNewAlert() {
    const severities = ['critical', 'high', 'medium'];
    const severity = severities[Math.floor(Math.random() * severities.length)];
    
    const newAlert = {
        id: alerts.length + 1,
        severity: severity,
        title: getRandomAlertTitle(severity),
        description: getRandomAlertDescription(),
        time: 'Just now',
        module: modules[Math.floor(Math.random() * modules.length)]?.name || 'Unknown'
    };

    alerts.unshift(newAlert);
    if (alerts.length > 5) {
        alerts.pop();
    }
    
    updateAlertsPanel();
    showNotification(newAlert.title, severity === 'critical' ? 'error' : 'warning');
}

// Utility functions
function generateTimeLabels(count) {
    const labels = [];
    for (let i = count - 1; i >= 0; i--) {
        labels.push(new Date(Date.now() - i * 60000).toLocaleTimeString());
    }
    return labels;
}

function generateRandomData(count, min, max) {
    const data = [];
    for (let i = 0; i < count; i++) {
        data.push(Math.floor(Math.random() * (max - min + 1)) + min);
    }
    return data;
}

function getRandomRiskLevel() {
    const levels = ['high', 'medium', 'low'];
    return levels[Math.floor(Math.random() * levels.length)];
}

function getRiskLevel(score) {
    if (score >= 70) return 'high';
    if (score >= 40) return 'medium';
    return 'low';
}

function getRandomActivityTitle(type) {
    const titles = {
        prediction: ['New Prediction Completed', 'Batch Analysis Finished', 'Model Updated'],
        alert: ['Alert Triggered', 'Threshold Exceeded', 'Anomaly Detected'],
        analysis: ['Analysis Complete', 'Report Generated', 'Metrics Updated']
    };
    const typeTitles = titles[type];
    return typeTitles[Math.floor(Math.random() * typeTitles.length)];
}

function getRandomActivityDescription(type) {
    const descriptions = {
        prediction: ['Analyzed 15 modules for bug risk', 'Processed 20 modules successfully', 'Updated risk scores'],
        alert: ['Module flagged as high risk', 'Unusual activity detected', 'Performance degradation'],
        analysis: ['Generated comprehensive report', 'Updated performance metrics', 'Completed risk assessment']
    };
    const typeDescriptions = descriptions[type];
    return typeDescriptions[Math.floor(Math.random() * typeDescriptions.length)];
}

function getRandomAlertTitle(severity) {
    const titles = {
        critical: ['Critical Risk Detected', 'System Alert', 'Emergency'],
        high: ['High Risk Module', 'Warning Issued', 'Attention Required'],
        medium: ['Moderate Risk', 'Caution Advised', 'Monitor Required']
    };
    const severityTitles = titles[severity];
    return severityTitles[Math.floor(Math.random() * severityTitles.length)];
}

function getRandomAlertDescription() {
    const descriptions = [
        'Module showing unusual patterns',
        'Risk threshold exceeded',
        'Performance metrics degraded',
        'Complexity increased significantly',
        'Bug probability elevated'
    ];
    return descriptions[Math.floor(Math.random() * descriptions.length)];
}

// Filter modules
function filterModules() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const riskFilter = document.getElementById('riskFilter').value;
    
    const filteredModules = modules.filter(module => {
        const matchesSearch = module.name.toLowerCase().includes(searchTerm);
        const matchesRisk = riskFilter === 'all' || module.riskLevel === riskFilter;
        return matchesSearch && matchesRisk;
    });
    
    const tbody = document.getElementById('modulesTableBody');
    tbody.innerHTML = '';
    
    filteredModules.forEach(module => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${module.name}</td>
            <td><span class="risk-badge ${module.riskLevel}">${module.riskLevel}</span></td>
            <td>${module.riskScore}%</td>
            <td>${module.predictedBugs}</td>
            <td>${module.complexity}</td>
            <td>${module.lastUpdated}</td>
            <td><span class="status-badge ${module.status.toLowerCase()}">${module.status}</span></td>
            <td>
                <button class="action-btn" onclick="viewModuleDetails(${module.id})">View</button>
                <button class="action-btn" onclick="analyzeModule(${module.id})">Analyze</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// View module details
function viewModuleDetails(moduleId) {
    const module = modules.find(m => m.id === moduleId);
    if (module) {
        showNotification(`Viewing details for ${module.name}`, 'success');
    }
}

// Analyze module
function analyzeModule(moduleId) {
    const module = modules.find(m => m.id === moduleId);
    if (module) {
        showNotification(`Analyzing ${module.name}...`, 'success');
        // Simulate analysis
        setTimeout(() => {
            module.riskScore = Math.floor(Math.random() * 100);
            module.riskLevel = getRiskLevel(module.riskScore);
            module.lastUpdated = new Date().toLocaleTimeString();
            updateModulesTable();
            showNotification(`Analysis complete for ${module.name}`, 'success');
        }, 2000);
    }
}

// Show alert details
function showAlertDetails(alert) {
    const modal = document.getElementById('alertModal');
    const modalBody = document.getElementById('modalBody');
    
    modalBody.innerHTML = `
        <div class="alert-details">
            <div class="alert-severity ${alert.severity}">${alert.severity.toUpperCase()}</div>
            <h4>${alert.title}</h4>
            <p>${alert.description}</p>
            <div class="alert-meta">
                <p><strong>Module:</strong> ${alert.module}</p>
                <p><strong>Time:</strong> ${alert.time}</p>
                <p><strong>Severity:</strong> ${alert.severity}</p>
            </div>
        </div>
    `;
    
    modal.style.display = 'flex';
}

// Close modal
function closeModal() {
    document.getElementById('alertModal').style.display = 'none';
}

// Take action on alert
function takeAction() {
    showNotification('Action taken successfully', 'success');
    closeModal();
}

// Refresh feed
function refreshFeed() {
    loadActivities();
    showNotification('Activity feed refreshed', 'success');
}

// Update time range
function updateTimeRange(range) {
    // Update button states
    document.querySelectorAll('.chart-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    // Update chart data based on time range
    showNotification(`Time range updated to ${range}`, 'success');
}

// Show notification
function showNotification(message, type = 'success') {
    const container = document.getElementById('notificationContainer');
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'exclamation-triangle'}"></i>
            <span>${message}</span>
        </div>
    `;
    
    container.appendChild(notification);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 3000);
}

// Add status badge styles
const style = document.createElement('style');
style.textContent = `
    .status-badge {
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 0.8rem;
        font-weight: 600;
        text-transform: uppercase;
    }
    
    .status-badge.active {
        background: rgba(79, 195, 247, 0.2);
        color: #4fc3f7;
    }
    
    .status-badge.inactive {
        background: rgba(184, 197, 214, 0.2);
        color: #b8c5d6;
    }
    
    .alert-details {
        color: #ffffff;
    }
    
    .alert-details h4 {
        margin: 15px 0 10px;
        color: #ffffff;
    }
    
    .alert-meta {
        margin-top: 20px;
        padding-top: 15px;
        border-top: 1px solid rgba(255, 255, 255, 0.1);
    }
    
    .alert-meta p {
        margin: 5px 0;
        color: #b8c5d6;
    }
    
    .alert-meta strong {
        color: #ffffff;
    }
`;
document.head.appendChild(style);
