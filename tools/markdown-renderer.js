#!/usr/bin/env node

import { readFileSync, writeFileSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join, resolve } from 'path';
import MarkdownIt from 'markdown-it';
import { exec } from 'child_process';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// Template HTML con estilos y scripts para Mermaid
const HTML_TEMPLATE = `<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Markdown Render</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1, h2, h3, h4, h5, h6 {
            color: #2c3e50;
            margin-top: 1.5em;
            margin-bottom: 0.5em;
        }
        h1 { border-bottom: 2px solid #3498db; padding-bottom: 10px; }
        h2 { border-bottom: 1px solid #bdc3c7; padding-bottom: 5px; }
        code {
            background: #f8f9fa;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
            font-size: 0.9em;
        }
        pre {
            background: #2c3e50;
            color: #ecf0f1;
            padding: 15px;
            border-radius: 5px;
            overflow-x: auto;
            margin: 1em 0;
        }
        pre code {
            background: none;
            padding: 0;
        }
        blockquote {
            border-left: 4px solid #3498db;
            margin: 1em 0;
            padding-left: 1em;
            color: #555;
        }
        table {
            border-collapse: collapse;
            width: 100%;
            margin: 1em 0;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px 12px;
            text-align: left;
        }
        th {
            background-color: #f8f9fa;
            font-weight: 600;
        }
        tr:nth-child(even) {
            background-color: #f8f9fa;
        }
        img {
            max-width: 100%;
            height: auto;
            border-radius: 4px;
        }
        .mermaid {
            text-align: center;
            margin: 2em 0;
            position: relative;
            overflow: hidden;
        }
        .pz-root { position: relative; overflow: hidden; background: transparent; }
        .pz-viewport { transform-origin: 0 0; will-change: transform; display: inline-block; }
        .pz-wrapper { display: inline-block; }
        .zoom-controls {
            position: absolute;
            top: 10px;
            right: 10px;
            display: flex;
            gap: 8px;
            z-index: 1000;
            background: rgba(255, 255, 255, 0.95);
            padding: 6px;
            border-radius: 6px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
        }
        .zoom-controls button {
            background: #2c3e50;
            color: #ffffff;
            border: 0;
            border-radius: 4px;
            width: 32px;
            height: 32px;
            cursor: pointer;
            font-weight: 700;
            font-size: 16px;
            line-height: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: background 0.2s;
        }
        .zoom-controls button:hover { 
            background: #34495e; 
        }
        .zoom-controls button:active { 
            background: #1a252f; 
            transform: scale(0.95);
        }
        ul, ol {
            margin: 1em 0;
            padding-left: 2em;
        }
        li {
            margin: 0.5em 0;
        }
        a {
            color: #3498db;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        hr {
            border: none;
            border-top: 1px solid #bdc3c7;
            margin: 2em 0;
        }
    </style>
</head>
<body>
    <div class="container">
        {{CONTENT}}
    </div>

    <!-- Mermaid.js + svg-pan-zoom -->
    <script type="module">
        import mermaid from 'https://cdn.skypack.dev/mermaid@10.6.1';

        mermaid.initialize({
            startOnLoad: true,
            theme: 'default',
            securityLevel: 'loose',
            fontFamily: 'arial',
            fontSize: 14
        });

        // Renderizar todos los diagramas Mermaid y habilitar pan/zoom con svg-pan-zoom
        document.addEventListener('DOMContentLoaded', function() {
            mermaid.init(undefined, document.querySelectorAll('.mermaid'));

            // Cargar svg-pan-zoom (UMD) desde CDN
            function loadSvgPanZoom() {
                return new Promise(function(resolve, reject) {
                    if (window.svgPanZoom) return resolve();
                    const s = document.createElement('script');
                    s.src = 'https://cdn.jsdelivr.net/npm/svg-pan-zoom@3.6.1/dist/svg-pan-zoom.min.js';
                    s.onload = () => resolve();
                    s.onerror = reject;
                    document.head.appendChild(s);
                });
            }

            // Función para añadir controles DESPUÉS de que Mermaid termine completamente
            function setupPanZoomOnce() {
                document.querySelectorAll('.mermaid svg').forEach(function(svg) {
                    // Verificar si ya tiene controles o ya fue procesado
                    var container = svg.closest('.mermaid');
                    if (!container) return;
                    
                    if (svg.dataset.panZoomEnabled === 'complete') return;
                    
                    // Verificar que el SVG tenga contenido completo (no solo placeholder)
                    if (svg.querySelector('g.root') || svg.querySelector('g.nodes')) {
                        loadSvgPanZoom().then(function() {
                            if (svg.dataset.panZoomEnabled === 'complete') return;
                            try {
                                // Ajustes básicos de tamaño
                                svg.removeAttribute('width');
                                svg.removeAttribute('height');
                                svg.style.width = '100%';
                                svg.style.minHeight = '400px';
                                svg.style.height = 'auto';

                                // Inicializar svg-pan-zoom (sin controles nativos para evitar duplicados)
                                var panZoomInstance = window.svgPanZoom(svg, {
                                    zoomEnabled: true,
                                    controlIconsEnabled: false,
                                    fit: true,
                                    center: true,
                                    minZoom: 0.2,
                                    maxZoom: 10,
                                    dblClickZoomEnabled: true,
                                });
                                // Guardar la instancia en el SVG para acceso posterior
                                svg._panZoomInstance = panZoomInstance;
                                svg.dataset.panZoomEnabled = 'complete';
                                ensureControls(container, svg); // añade nuestros +/-/reset por consistencia
                            } catch (e) {
                                console.error('svg-pan-zoom init error:', e);
                            }
                        });
                    }
                });
            }
            
            // Usar MutationObserver para detectar cuando Mermaid termina de renderizar
            var processedContainers = new Set();
            var observer = new MutationObserver(function(mutations) {
                mutations.forEach(function(mutation) {
                    if (mutation.type === 'childList') {
                        mutation.addedNodes.forEach(function(node) {
                            if (node.nodeType === 1 && node.tagName === 'SVG') {
                                var container = node.closest('.mermaid');
                                if (container && !processedContainers.has(container)) {
                                    processedContainers.add(container);
                                    setTimeout(function() {
                                        if (!node.dataset.panZoomEnabled || node.dataset.panZoomEnabled !== 'complete') {
                                            loadSvgPanZoom().then(function(){
                                                try {
                                                    node.removeAttribute('width');
                                                    node.removeAttribute('height');
                                                    node.style.width = '100%';
                                                    node.style.minHeight = '400px';
                                                    node.style.height = 'auto';
                                                    var panZoomInstance = window.svgPanZoom(node, {
                                                        zoomEnabled: true,
                                                        controlIconsEnabled: false,
                                                        fit: true,
                                                        center: true,
                                                        minZoom: 0.2,
                                                        maxZoom: 10,
                                                        dblClickZoomEnabled: true
                                                    });
                                                    // Guardar la instancia en el SVG para acceso posterior
                                                    node._panZoomInstance = panZoomInstance;
                                                    node.dataset.panZoomEnabled = 'complete';
                                                    ensureControls(node.closest('.mermaid'), node);
                                                } catch (e) {
                                                    console.error('svg-pan-zoom init error:', e);
                                                }
                                            });
                                        }
                                    }, 200);
                                }
                            }
                        });
                    }
                });
                setupPanZoomOnce();
            });
            
            // Observar todos los contenedores
            document.querySelectorAll('.mermaid').forEach(function(container) {
                observer.observe(container, { 
                    childList: true, 
                    subtree: true,
                    attributes: true,
                    attributeFilter: ['data-processed']
                });
            });
            
            // Intentos periódicos para casos donde el observer no detecte
            var attempts = 0;
            var interval = setInterval(function() {
                attempts++;
                setupPanZoomOnce();
                if (attempts > 20 || document.querySelectorAll('.mermaid svg').length > 0) {
                    clearInterval(interval);
                }
            }, 200);
            
            // Limpiar después de 5 segundos
            setTimeout(function() {
                clearInterval(interval);
                observer.disconnect();
                setupPanZoomOnce();
            }, 5000);
        });

        // Función para crear/actualizar controles (usará svg-pan-zoom internamente)
        function ensureControls(container, svg) {
                var existingControls = container.querySelector('.zoom-controls');
                if (existingControls) return existingControls;
                
                var controls = document.createElement('div');
                controls.className = 'zoom-controls';
                controls.setAttribute('data-zoom-controls', 'true');
                controls.innerHTML = '<button data-act="in" title="Zoom In">+</button><button data-act="out" title="Zoom Out">−</button><button data-act="reset" title="Reset Zoom">⟲</button>';
                container.style.position = 'relative';
                container.appendChild(controls);

                controls.addEventListener('click', function(ev) {
                    var act = ev.target.getAttribute('data-act');
                    if (!act) return;
                    // Usar la instancia guardada en el SVG
                    var api = svg && svg._panZoomInstance ? svg._panZoomInstance : null;
                    if (!api) {
                        console.warn('PanZoom API no disponible');
                        return;
                    }
                    if (act === 'in') api.zoomIn();
                    if (act === 'out') api.zoomOut();
                    if (act === 'reset') { 
                        api.resetZoom();
                        api.center();
                        api.fit();
                    }
                });
                
                return controls;
            }
            
            // Nota: los controles se añaden durante la inicialización del pan/zoom
            // (sin acciones adicionales)
    </script>
</body>
</html>`;

// Función principal
async function renderMarkdown(filePath) {
    try {
        // Resolver la ruta absoluta
        const absolutePath = resolve(filePath);

        // Leer el archivo markdown
        const markdownContent = readFileSync(absolutePath, 'utf-8');

        // Configurar MarkdownIt con soporte para Mermaid
        const md = new MarkdownIt({
            html: true,
            linkify: true,
            typographer: true
        });

        // Agregar soporte para bloques de código Mermaid
        md.renderer.rules.fence = function(tokens, idx, options, env, slf) {
            const token = tokens[idx];
            const code = token.content.trim();

            if (token.info === 'mermaid') {
                return '<div class="mermaid">' + code + '</div>';
            }

            return '<pre><code class="' + token.info + '">' +
                   md.utils.escapeHtml(code) +
                   '</code></pre>';
        };

        // Convertir markdown a HTML
        const htmlContent = md.render(markdownContent);

        // Insertar el contenido en la plantilla
        const finalHtml = HTML_TEMPLATE.replace('{{CONTENT}}', htmlContent);

        // Crear archivo temporal
        const tempFile = join(__dirname, '..', 'temp_render.html');
        writeFileSync(tempFile, finalHtml);

        console.log('✅ Markdown convertido exitosamente: ' + absolutePath);
        console.log('📄 Archivo HTML temporal creado: ' + tempFile);

        // Abrir en el navegador
        const command = process.platform === 'win32'
            ? 'start "" "' + tempFile + '"'
            : process.platform === 'darwin'
            ? 'open "' + tempFile + '"'
            : 'xdg-open "' + tempFile + '"';

        exec(command, (error) => {
            if (error) {
                console.error('❌ Error al abrir el navegador:', error);
            } else {
                console.log('🌐 Archivo abierto en el navegador');
            }
        });

    } catch (error) {
        console.error('❌ Error:', error.message);
        process.exit(1);
    }
}

// Verificar argumentos
const args = process.argv.slice(2);
if (args.length === 0) {
    console.log('Uso: node tools/markdown-renderer.js <archivo-markdown>');
    console.log('Ejemplo: node tools/markdown-renderer.js README.md');
    process.exit(1);
}

const markdownFile = args[0];
renderMarkdown(markdownFile);
