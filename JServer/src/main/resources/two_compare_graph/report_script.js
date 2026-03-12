// Constants for element IDs
const IDS = {
    DETAILS_PANEL: 'details',
    STUDENT_NETWORK: 'student-network',
    REFERENCE_NETWORK: 'reference-network',
    STUDENT_COUNT: 'student-count',
    REFERENCE_COUNT: 'reference-count'
};

// Placeholder for injected JSON data
const comparisonData = {{DATA}};

let studentNetwork = null;
let referenceNetwork = null;

async function init() {
    console.log("Initializing Enhanced Git Comparison Report...");
    
    await new Promise(resolve => setTimeout(resolve, 200));

    if (typeof vis === 'undefined') {
        setTimeout(init, 500);
        return;
    }

    try {
        const options = {
            layout: {
                hierarchical: {
                    enabled: true,
                    direction: 'DU', // Bottom-up direction
                    sortMethod: 'directed',
                    levelSeparation: 150,
                    nodeSpacing: 200,
                    edgeMinimization: true,
                    parentCentralization: true
                }
            },
            nodes: {
                shape: 'dot',
                size: 24,
                font: {
                    color: '#a9b7c6',
                    size: 14,
                    face: 'Inter, Segoe UI',
                    multi: true,
                    bold: { color: '#ffffff' }
                },
                borderWidth: 2,
                shadow: {
                    enabled: true,
                    color: 'rgba(0,0,0,0.3)',
                    size: 10,
                    x: 5,
                    y: 5
                }
            },
            edges: {
                arrows: {
                    to: { enabled: true, scaleFactor: 1.2 }
                },
                color: { color: '#444444', highlight: '#888888', hover: '#666666' },
                width: 2,
                selectionWidth: 3,
                hoverWidth: 3,
                smooth: {
                    enabled: true,
                    type: 'cubicBezier',
                    forceDirection: 'vertical',
                    roundness: 0.5
                }
            },
            physics: {
                enabled: false // Hierarchical layout works better without physics once stabilized
            },
            interaction: {
                hover: true,
                navigationButtons: false, // Buttons removed per request
                keyboard: true,
                tooltipDelay: 200,
                hideEdgesOnDrag: true
            }
        };

        const studentData = createNetworkData(comparisonData.first_graph);
        const referenceData = createNetworkData(comparisonData.second_graph);

        document.getElementById(IDS.STUDENT_COUNT).textContent = `(${comparisonData.first_graph.nodes.length} узлов)`;
        document.getElementById(IDS.REFERENCE_COUNT).textContent = `(${comparisonData.second_graph.nodes.length} узлов)`;

        studentNetwork = new vis.Network(document.getElementById(IDS.STUDENT_NETWORK), studentData, options);
        referenceNetwork = new vis.Network(document.getElementById(IDS.REFERENCE_NETWORK), referenceData, options);

        setupInteractions(studentNetwork, referenceNetwork);
        
    } catch (e) {
        console.error("Critical Init Error:", e);
        document.getElementById(IDS.DETAILS_PANEL).innerHTML = `<h3 style="color:#d73a49">Render Error</h3><pre style="color:#a9b7c6">${e.stack}</pre>`;
    }
}

function createNetworkData(graphDto) {
    if (!graphDto || !graphDto.nodes) return { nodes: new vis.DataSet([]), edges: new vis.DataSet([]) };
    
    const nodes = graphDto.nodes.map(node => ({
        id: node.id,
        label: `<b>[${node.number}]</b>\n${node.id.substring(0, 7)}`,
        title: node.message,
        color: getSeverityColor(node.severity)
    }));

    const edges = (graphDto.links || []).map(link => ({
        from: link.source,
        to: link.target
    }));

    return { nodes: new vis.DataSet(nodes), edges: new vis.DataSet(edges) };
}

function setupInteractions(sNet, rNet) {
    sNet.on("click", (params) => {
        if (params.nodes.length > 0) {
            const nodeId = params.nodes[0];
            showCombinedDetails(nodeId, 'student');
            syncSelection(nodeId, 'student', rNet);
        }
    });

    rNet.on("click", (params) => {
        if (params.nodes.length > 0) {
            const nodeId = params.nodes[0];
            showCombinedDetails(nodeId, 'reference');
            syncSelection(nodeId, 'reference', sNet);
        }
    });
}

function syncSelection(nodeId, sourceGraphType, targetNetwork) {
    const mapping = comparisonData.compare_result ? comparisonData.compare_result.matched_hashes_1_to_2 : {};
    if (sourceGraphType === 'student') {
        const matched = mapping[nodeId];
        if (matched) targetNetwork.selectNodes([matched]);
        else targetNetwork.unselectAll();
    } else {
        const matched = Object.keys(mapping).find(key => mapping[key] === nodeId);
        if (matched) targetNetwork.selectNodes([matched]);
        else targetNetwork.unselectAll();
    }
}

function showCombinedDetails(nodeId, clickedGraphType) {
    let studentNode, referenceNode;
    const mapping = comparisonData.compare_result ? comparisonData.compare_result.matched_hashes_1_to_2 : {};

    if (clickedGraphType === 'student') {
        studentNode = comparisonData.first_graph.nodes.find(n => n.id === nodeId);
        const matchedHash = mapping[nodeId];
        if (matchedHash) {
            referenceNode = comparisonData.second_graph.nodes.find(n => n.id === matchedHash);
        }
    } else {
        referenceNode = comparisonData.second_graph.nodes.find(n => n.id === nodeId);
        const matchedHash = Object.keys(mapping).find(key => mapping[key] === nodeId);
        if (matchedHash) {
            studentNode = comparisonData.first_graph.nodes.find(n => n.id === matchedHash);
        }
    }

    const html = `
        <div class="details-grid">
            <div class="details-column">
                <h4>Коммит студента</h4>
                ${studentNode ? renderNodeInfo(studentNode) : '<p style="opacity:0.3">Соответствующий коммит не найден</p>'}
            </div>
            <div class="details-column">
                <h4>Ожидаемый коммит</h4>
                ${referenceNode ? renderNodeInfo(referenceNode) : '<p style="opacity:0.3">Соответствующий коммит не найден</p>'}
            </div>
        </div>
    `;
    document.getElementById(IDS.DETAILS_PANEL).innerHTML = html;
}

function renderNodeInfo(node) {
    return `
        <h3 style="color:#ffffff; margin: 0 0 5px 0; font-size: 14px; font-family: 'JetBrains Mono', monospace;">[${node.number}] ${node.hash.substring(0, 12)}...</h3>
        <div style="margin-bottom: 5px;">
            <p style="margin: 2px 0; font-size: 13px;"><strong>Severity:</strong> <span class="legend-item" style="display:inline-flex; vertical-align: middle; gap: 5px;"><div class="color-box severity-${node.severity}" style="width:10px; height:10px;"></div> ${getSeverityName(node.severity)}</span></p>
        </div>
        
        <button class="commit-metadata-toggle" onclick="toggleMetadata(this)">
            Commit Details
        </button>
        <div class="commit-metadata-content">
            <div class="metadata-row">
                <div class="metadata-label">Message:</div>
                <div class="metadata-value" style="color: #6a8759; font-style: italic;">"${node.message}"</div>
            </div>
            <div class="metadata-row">
                <div class="metadata-label">Author:</div>
                <div class="metadata-value">${node.author ? node.author.name : 'N/A'}</div>
            </div>
            <div class="metadata-row">
                <div class="metadata-label">Date:</div>
                <div class="metadata-value" style="color: #cc7832">${node.commitDate}</div>
            </div>
        </div>

        ${node.diffs && node.diffs.length > 0 ? '<h5 style="margin: 10px 0 5px 0; font-size: 12px; color: #888; text-transform: uppercase;">Changes:</h5>' + renderDiffs(node.diffs) : ''}
    `;
}

if (document.readyState === 'complete') init();
else window.addEventListener('load', init);
