#!/usr/bin/env node

import { readFileSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join, resolve } from 'path';
import http from 'http';
import chokidar from 'chokidar';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const PORT = 3001;
const tasksPath = resolve('docs/tasks.json');

// Global state
let currentTasksData = {};
let clients = new Set(); // SSE clients
let server;

// Load tasks data
function loadTasksData() {
    try {
        const rawData = readFileSync(tasksPath, 'utf-8');
        const data = JSON.parse(rawData);
        currentTasksData = data;
        return data;
    } catch (error) {
        console.error('❌ Error loading tasks:', error.message);
        return { tasks: [], feature_title: 'Error Loading Tasks', currentTaskId: '' };
    }
}

// Initial load
currentTasksData = loadTasksData();

// HTML template with SSE support
const HTML_TEMPLATE = `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Task Visualizer - Live Updates</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 16px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            overflow: hidden;
            backdrop-filter: blur(10px);
        }
        header {
            background: linear-gradient(135deg, #2c3e50, #3498db);
            color: white;
            padding: 30px;
            text-align: center;
        }
        header h1 { font-size: 2.5em; margin-bottom: 10px; }
        header p { opacity: 0.9; font-size: 1.1em; }
        .status-bar {
            background: rgba(0,0,0,0.1);
            padding: 15px 20px;
            color: white;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 10px;
        }
        .connection-status {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .status-dot {
            width: 12px; height: 12px; border-radius: 50%;
            background: #27ae60;
            animation: pulse 2s infinite;
        }
        .status-dot.disconnected { background: #e74c3c; animation: none; }
        .stats {
            display: flex;
            justify-content: space-around;
            padding: 20px;
            background: rgba(0,0,0,0.1);
            flex-wrap: wrap;
            gap: 15px;
        }
        .stat {
            text-align: center;
            color: white;
            min-width: 80px;
        }
        .stat-number { font-size: 2em; font-weight: bold; display: block; }
        .stat-label { font-size: 0.9em; opacity: 0.8; }
        .tasks-section { padding: 30px; }
        .section-title {
            font-size: 1.8em;
            color: #2c3e50;
            margin-bottom: 20px;
            border-bottom: 3px solid #3498db;
            padding-bottom: 10px;
        }
        .task-card {
            background: white;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 15px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            transition: all 0.3s ease;
            border-left: 5px solid #95a5a6;
            animation: slideIn 0.5s ease-out;
            opacity: 0;
            transform: translateY(20px);
        }
        .task-card.show {
            opacity: 1;
            transform: translateY(0);
        }
        .task-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        .task-card.completed {
            border-left-color: #27ae60;
            background: linear-gradient(135deg, #d5f4e6, #ffffff);
        }
        .task-card.in-progress {
            border-left-color: #f39c12;
            background: linear-gradient(135deg, #fef5e7, #ffffff);
        }
        .task-card.todo {
            border-left-color: #e74c3c;
            background: linear-gradient(135deg, #fdf2f2, #ffffff);
        }
        .task-card.current-task {
            border-left-color: #3498db;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.3);
            animation: highlightPulse 2s ease-in-out;
        }
        .task-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 15px;
        }
        .task-id {
            background: #34495e;
            color: white;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 0.8em;
            font-weight: bold;
        }
        .task-title {
            font-size: 1.3em;
            color: #2c3e50;
            font-weight: 600;
            flex: 1;
            margin-left: 15px;
        }
        .task-status {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: bold;
            text-transform: uppercase;
        }
        .status-todo { background: #e74c3c; color: white; }
        .status-completed { background: #27ae60; color: white; }
        .status-in_progress { background: #f39c12; color: white; }
        .task-description {
            color: #555;
            line-height: 1.6;
            margin-bottom: 15px;
            font-size: 0.95em;
        }
        .task-details {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            font-size: 0.9em;
            color: #666;
            margin-top: 15px;
        }
        .task-details dt {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 5px;
            margin-top: 10px;
        }
        .task-details dd { margin-bottom: 10px; }
        .task-details pre {
            background: #2c3e50;
            color: #ecf0f1;
            padding: 10px;
            border-radius: 4px;
            overflow-x: auto;
            font-size: 0.85em;
        }
        .progress-bar {
            width: 100%;
            height: 8px;
            background: #ecf0f1;
            border-radius: 4px;
            overflow: hidden;
            margin: 20px 0;
        }
        .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #3498db, #2ecc71);
            transition: width 0.8s ease;
            border-radius: 4px;
        }
        .last-updated {
            text-align: center;
            padding: 20px;
            color: #7f8c8d;
            font-style: italic;
            border-top: 1px solid #ecf0f1;
        }
        .live-notice {
            background: #d4edda;
            border: 1px solid #28a745;
            color: #155724;
            padding: 15px;
            border-radius: 8px;
            margin: 20px 0;
            text-align: center;
            font-weight: 500;
        }
        .update-notification {
            position: fixed;
            top: 20px;
            right: 20px;
            background: #27ae60;
            color: white;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            transform: translateX(400px);
            transition: transform 0.3s ease;
            z-index: 1000;
            max-width: 300px;
        }
        .update-notification.show { transform: translateX(0); }
        .update-notification h4 { margin-bottom: 5px; font-size: 1.1em; }
        .update-notification p { font-size: 0.9em; opacity: 0.9; }
        @keyframes slideIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes highlightPulse {
            0%, 100% { box-shadow: 0 0 20px rgba(52, 152, 219, 0.5); }
            50% { box-shadow: 0 0 30px rgba(52, 152, 219, 0.8); }
        }
        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.7; }
        }
        .loading { text-align: center; padding: 40px; color: #7f8c8d; }
        .error {
            background: #fee;
            color: #c33;
            padding: 20px;
            border-radius: 8px;
            margin: 20px 0;
            border-left: 4px solid #e74c3c;
        }
        @media (max-width: 768px) {
            .container { margin: 10px; }
            header { padding: 20px; }
            .tasks-section { padding: 20px; }
            .task-header { flex-direction: column; align-items: flex-start; }
            .task-title { margin-left: 0; margin-top: 10px; }
            .stats { flex-direction: column; gap: 10px; }
            .status-bar { flex-direction: column; gap: 10px; text-align: center; }
            .update-notification { 
                right: 10px; left: 10px; max-width: none;
                transform: translateY(-100px);
            }
            .update-notification.show { transform: translateY(0); }
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1 id="page-title">🚀 Task Visualizer</h1>
            <p>Real-time progress tracking for Becoming AI Native project</p>
        </header>
        
        <div class="status-bar">
            <div class="connection-status">
                <div class="status-dot" id="connection-dot"></div>
                <span id="connection-text">Connecting to Live Server</span>
            </div>
            <div style="font-size: 0.9em; opacity: 0.8;">
                Server: http://localhost:${PORT} | <a href="#" onclick="location.reload()" style="color: white; text-decoration: underline;">Refresh</a>
            </div>
        </div>
        
        <div class="stats" id="stats">
            <!-- Stats will be populated by JS -->
        </div>
        
        <div class="tasks-section">
            <h2 class="section-title">Current Tasks</h2>
            <div class="live-notice">
                <strong>🔴 LIVE MODE ACTIVE</strong> - Changes to tasks.json update automatically (~1s delay)
            </div>
            <div id="progress-bar">
                <div class="progress-bar">
                    <div class="progress-fill" id="progress-fill" style="width: 0%"></div>
                </div>
            </div>
            <div id="tasks-container" class="loading">
                <p>🔄 Connecting to live server...</p>
            </div>
            <div id="error-container" class="error" style="display: none;"></div>
        </div>
        
        <div class="last-updated" id="last-updated">
            Last updated: <span id="last-update-time"></span>
        </div>
    </div>

    <!-- Update notification -->
    <div id="update-notification" class="update-notification">
        <h4>📊 Tasks Updated!</h4>
        <p>New data loaded automatically. <strong id="update-details"></strong></p>
    </div>

    <script>
        let tasks = [];
        let currentTaskId = '';
        let connectionStatus = 'connecting';
        let reconnectAttempts = 0;
        const MAX_RECONNECTS = 10;
        let eventSource = null;
        let lastUpdateTime = new Date();

        // Status colors and animations
        const STATUS_CONFIG = {
            todo: { class: 'status-todo', label: 'To Do', icon: '📋' },
            completed: { class: 'status-completed', label: 'Completed', icon: '✅' },
            in_progress: { class: 'status-in_progress', label: 'In Progress', icon: '⚡' }
        };

        // Update connection status
        function updateConnectionStatus(status, message = '') {
            connectionStatus = status;
            const dot = document.getElementById('connection-dot');
            const text = document.getElementById('connection-text');
            
            if (status === 'connected') {
                dot.className = 'status-dot';
                text.textContent = '🟢 Live Updates Active';
            } else if (status === 'connecting') {
                dot.className = 'status-dot';
                text.textContent = '🟡 Connecting...';
            } else if (status === 'disconnected') {
                dot.className = 'status-dot disconnected';
                text.textContent = '🔴 Disconnected - Reconnecting...';
            } else if (status === 'error') {
                dot.className = 'status-dot disconnected';
                text.textContent = \`🔴 Error: \${message}\`;
            }
        }

        // Show update notification
        function showUpdateNotification(completedCount, totalCount, changedTasks = []) {
            const notification = document.getElementById('update-notification');
            const details = document.getElementById('update-details');
            
            // Simplified notification - no specific task details
            details.textContent = 'Progress updated automatically';
            notification.classList.add('show');
            
            setTimeout(() => {
                notification.classList.remove('show');
            }, 3000); // Shorter duration for generic message
        }

        function updateStats(total, completed, inProgress) {
            const statsContainer = document.getElementById('stats');
            const percentage = total > 0 ? Math.round((completed / total) * 100) : 0;
            
            statsContainer.innerHTML = \`
                <div class="stat">
                    <span class="stat-number">\${total}</span>
                    <span class="stat-label">Total Tasks</span>
                </div>
                <div class="stat">
                    <span class="stat-number">\${completed}</span>
                    <span class="stat-label">Completed</span>
                </div>
                <div class="stat">
                    <span class="stat-number">\${inProgress}</span>
                    <span class="stat-label">In Progress</span>
                </div>
                <div class="stat">
                    <span class="stat-number">\${percentage}%</span>
                    <span class="stat-label">Progress</span>
                </div>
            \`;
            
            // Update progress bar
            const progressFill = document.getElementById('progress-fill');
            progressFill.style.width = \`\${percentage}%\`;
            progressFill.style.transition = 'width 0.8s ease';
        }

        function renderTasks(data) {
            tasks = data.tasks || [];
            currentTaskId = data.currentTaskId || '';
            
            const container = document.getElementById('tasks-container');
            const errorContainer = document.getElementById('error-container');
            
            if (!tasks.length) {
                container.innerHTML = '<p>No tasks found.</p>';
                return;
            }

            errorContainer.style.display = 'none';
            container.classList.remove('loading');
            
            // Sort tasks by ID to maintain order
            tasks.sort((a, b) => a.id.localeCompare(b.id));
            
            // Group tasks by status
            const statusGroups = {
                todo: [],
                completed: [],
                in_progress: []
            };
            
            tasks.forEach(task => {
                const status = task.status || 'todo';
                if (statusGroups[status]) {
                    statusGroups[status].push(task);
                } else {
                    statusGroups.todo.push(task);
                }
            });

            let html = '';
            
            // Track changed tasks for notification
            let changedTasks = [];
            
            // Render completed tasks (reversed)
            if (statusGroups.completed.length > 0) {
                html += '<h3 style="color: #27ae60; margin: 20px 0 10px 0; font-size: 1.4em;">✅ Completed Tasks (' + statusGroups.completed.length + ')</h3>';
                statusGroups.completed.slice().reverse().forEach(task => {
                    const card = createTaskCard(task, true);
                    html += card;
                    if (task.status === 'completed') changedTasks.push(task);
                });
            }
            
            // Render in_progress tasks
            if (statusGroups.in_progress.length > 0) {
                html += '<h3 style="color: #f39c12; margin: 20px 0 10px 0; font-size: 1.4em;">⚡ In Progress (' + statusGroups.in_progress.length + ')</h3>';
                statusGroups.in_progress.forEach(task => {
                    html += createTaskCard(task, false, true);
                    if (task.status === 'in_progress') changedTasks.push(task);
                });
            }
            
            // Render todo tasks
            html += '<h3 style="color: #e74c3c; margin: 20px 0 10px 0; font-size: 1.4em;">📋 Pending Tasks (' + statusGroups.todo.length + ')</h3>';
            statusGroups.todo.forEach(task => {
                html += createTaskCard(task, false);
            });

            // Animate new cards
            container.innerHTML = html;
            
            // Add slide-in animation to all cards
            setTimeout(() => {
                document.querySelectorAll('.task-card').forEach((card, index) => {
                    setTimeout(() => {
                        card.classList.add('show');
                    }, index * 50);
                });
            }, 100);
            
            // Update stats
            const total = tasks.length;
            const completed = statusGroups.completed.length;
            const inProgress = statusGroups.in_progress.length;
            updateStats(total, completed, inProgress);
            
            // Highlight current task
            highlightCurrentTask();
            
            // Update timestamp
            updateTimestamp();
            
            // Show notification if there are changes
            if (changedTasks.length > 0) {
                showUpdateNotification(completed, total, changedTasks);
            }
        }

        function createTaskCard(task, isCompleted = false, isInProgress = false) {
            const statusConfig = STATUS_CONFIG[task.status || 'todo'];
            const isCurrent = task.id === currentTaskId;
            const cardClass = isCompleted ? 'completed' : (isInProgress ? 'in-progress' : 'todo');
            
            const command = task.command ? \`
                <dt>Command:</dt>
                <dd>\${task.command}</dd>
            \` : '';
            
            const params = task.params ? \`
                <dt>Parameters:</dt>
                <dd><pre>\${JSON.stringify(task.params, null, 2)}</pre></dd>
            \` : '';
            
            const acceptance = task.acceptance_criteria ? \`
                <dt>Acceptance Criteria:</dt>
                <dd>
                    <ul style="margin: 0; padding-left: 20px;">
                        \${task.acceptance_criteria.map(crit => \`<li>\${crit}</li>\`).join('')}
                    </ul>
                </dd>
            \` : '';

            return \`
                <div class="task-card \${cardClass} \${isCurrent ? 'current-task' : ''}" data-task-id="\${task.id}">
                    <div class="task-header">
                        <span class="task-id">\${task.id}</span>
                        <h3 class="task-title">\${task.title}</h3>
                        <span class="task-status \${statusConfig.class}">
                            \${statusConfig.icon} \${statusConfig.label}
                        </span>
                    </div>
                    <div class="task-description">\${task.description}</div>
                    <dl class="task-details">
                        \${command}
                        \${params}
                        \${acceptance}
                    </dl>
                </div>
            \`;
        }

        function highlightCurrentTask() {
            // Remove previous highlights
            document.querySelectorAll('.current-task').forEach(el => {
                el.classList.remove('current-task');
            });
            
            // Add highlight to current task
            const currentTaskEl = document.querySelector(\`[data-task-id="\${currentTaskId}"]\`);
            if (currentTaskEl) {
                currentTaskEl.classList.add('current-task');
                currentTaskEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }

        function updateTimestamp() {
            const now = new Date();
            document.getElementById('last-update-time').textContent = now.toLocaleString();
        }

        // SSE Connection
        function connectSSE() {
            if (eventSource) {
                eventSource.close();
            }

            eventSource = new EventSource('/events');
            
            updateConnectionStatus('connecting');

            eventSource.onopen = function() {
                updateConnectionStatus('connected');
                reconnectAttempts = 0;
                console.log('🟢 SSE Connected');
            };

            eventSource.onmessage = function(event) {
                try {
                    const data = JSON.parse(event.data);
                    if (data.type === 'tasks-update') {
                        renderTasks(data.tasksData);
                        console.log('📊 Live update received:', data.tasksData.tasks.length, 'tasks');
                    }
                } catch (error) {
                    console.error('Error parsing SSE data:', error);
                }
            };

            eventSource.onerror = function(event) {
                updateConnectionStatus('disconnected');
                console.log('🔴 SSE Error, attempting reconnect...');
                
                if (reconnectAttempts < MAX_RECONNECTS) {
                    setTimeout(() => {
                        reconnectAttempts++;
                        connectSSE();
                    }, Math.min(1000 * Math.pow(2, reconnectAttempts), 30000));
                } else {
                    updateConnectionStatus('error', 'Max reconnect attempts reached');
                }
            };
        }

        // Initialize
        document.addEventListener('DOMContentLoaded', function() {
            updateConnectionStatus('connecting');
            connectSSE();
            
            // Initial load via fetch
            fetch('/api/tasks')
                .then(response => response.json())
                .then(data => {
                    renderTasks(data);
                    if (data.feature_title) {
                        document.getElementById('page-title').textContent = 
                            \`🚀 \${data.feature_title} - Live Task Visualizer\`;
                    }
                })
                .catch(error => {
                    console.error('Initial load error:', error);
                    updateConnectionStatus('error', error.message);
                });
        });

        // Handle page visibility
        document.addEventListener('visibilitychange', function() {
            if (!document.hidden && connectionStatus !== 'connected') {
                connectSSE();
            }
        });

        // Graceful cleanup
        window.addEventListener('beforeunload', function() {
            if (eventSource) {
                eventSource.close();
            }
        });
    </script>
</body>
</html>`;

// HTTP Server
function createServer() {
    const server = http.createServer((req, res) => {
        // Set CORS headers
        res.setHeader('Access-Control-Allow-Origin', '*');
        res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
        res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
        
        // Handle preflight
        if (req.method === 'OPTIONS') {
            res.writeHead(200);
            res.end();
            return;
        }

        // API endpoints
        if (req.url === '/api/tasks' && req.method === 'GET') {
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify(currentTasksData));
            return;
        }

        // SSE endpoint
        if (req.url === '/events' && req.method === 'GET') {
            res.writeHead(200, {
                'Content-Type': 'text/event-stream',
                'Cache-Control': 'no-cache',
                'Connection': 'keep-alive',
                'Access-Control-Allow-Origin': '*'
            });

            const clientId = Date.now() + Math.random();
            clients.add({ id: clientId, res });

            req.on('close', () => {
                clients.delete({ id: clientId, res });
            });

            // Keep connection alive
            const heartbeat = setInterval(() => {
                if (!res.writableEnded) {
                    res.write('data: {"type": "heartbeat"}\n\n');
                }
            }, 15000);

            res.on('close', () => {
                clearInterval(heartbeat);
                clients.delete({ id: clientId, res });
            });

            return;
        }

        // Serve HTML
        if (req.url === '/' || req.url === '/index.html') {
            const html = HTML_TEMPLATE;
            res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
            res.end(html);
            return;
        }

        // 404
        res.writeHead(404);
        res.end('Not Found');
    });

    return server;
}

// Broadcast update to all SSE clients
function broadcastUpdate() {
    const updateData = {
        type: 'tasks-update',
        tasksData: currentTasksData,
        timestamp: Date.now()
    };

    const message = `data: ${JSON.stringify(updateData)}\n\n`;

    for (const client of clients) {
        if (!client.res.writableEnded) {
            client.res.write(message);
        }
    }

    console.log(`📡 Broadcasted update to ${clients.size} clients`);
}

// Watch for file changes
function setupWatcher() {
    const watcher = chokidar.watch(tasksPath, {
        persistent: true,
        ignoreInitial: true,
        awaitWriteFinish: {
            stabilityThreshold: 300,
            pollInterval: 100
        }
    });

    watcher.on('change', (path) => {
        console.log('\n🔔 Cambio detectado en:', path);
        
        // Reload data
        currentTasksData = loadTasksData();
        
        // Count changes
        const completedCount = currentTasksData.tasks.filter(t => t.status === 'completed').length;
        const totalCount = currentTasksData.tasks.length;
        
        console.log(`📊 Nuevos datos: ${totalCount} total, ${completedCount} completadas`);
        
        // Broadcast to clients
        broadcastUpdate();
    });

    watcher.on('error', (error) => {
        console.error('❌ Error en watcher:', error);
    });

    return watcher;
}

// Main initialization
function initializeLiveVisualizer() {
    try {
        // Initial load
        currentTasksData = loadTasksData();
        
        console.log('✅ Task Visualizer Live Server inicializado');
        console.log('📊 Tareas iniciales:', currentTasksData.tasks.length, 'total');
        console.log(`🌐 Servidor corriendo en: http://localhost:${PORT}`);
        console.log('🔍 Watcher activo para docs/tasks.json');
        console.log('\n🎉 LIVE MODE ACTIVADO');
        console.log('📝 Modifica docs/tasks.json y los cambios aparecerán automáticamente');
        console.log('⏹️  Presiona Ctrl+C para detener el servidor');
        console.log('🔗 Abre: http://localhost:3001 en tu navegador\n');

        // Create and start server
        server = createServer();
        const watcher = setupWatcher();

        server.listen(PORT, 'localhost', () => {
            console.log(`🚀 Servidor HTTP escuchando en puerto ${PORT}`);
        });

        // Graceful shutdown
        process.on('SIGINT', () => {
            console.log('\n\n👋 Cerrando Task Visualizer Live Server...');
            watcher.close();
            clients.forEach(client => {
                if (!client.res.writableEnded) {
                    client.res.end();
                }
            });
            clients.clear();
            if (server) {
                server.close(() => {
                    console.log('📊 Servidor cerrado correctamente');
                    console.log('👋 ¡Gracias por usar Task Visualizer Live!');
                    process.exit(0);
                });
            }
        });

        // Handle uncaught errors
        process.on('uncaughtException', (error) => {
            console.error('❌ Error no capturado:', error);
            process.exit(1);
        });

    } catch (error) {
        console.error('❌ Error inicializando servidor:', error.message);
        process.exit(1);
    }
}

// Handle arguments
const args = process.argv.slice(2);
if (args.length > 0) {
    console.log('Uso: npm run view-tasks');
    console.log('Inicia el visualizador live con actualizaciones automáticas');
    process.exit(1);
}

initializeLiveVisualizer();
