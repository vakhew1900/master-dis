// Constants for element IDs
const IDS = {
    DETAILS_PANEL: 'details',
    STUDENT_NETWORK: 'student-network',
    REFERENCE_NETWORK: 'reference-network',
    STUDENT_COUNT: 'student-count',
    REFERENCE_COUNT: 'reference-count'
};

let studentNetwork = null;
let referenceNetwork = null;
let comparisonData = null;

/**
 * Initializes the report.
 */
function init() {
    try {
        comparisonData = {{DATA}};
        if (!comparisonData) {
            console.error("No valid comparison data found.");
            return;
        }

        renderNetwork(comparisonData.first_graph, IDS.STUDENT_NETWORK, 'student');
        renderNetwork(comparisonData.second_graph, IDS.REFERENCE_NETWORK, 'reference');
        
        document.getElementById(IDS.STUDENT_COUNT).textContent = `(${comparisonData.first_graph.nodes.length} узлов)`;
        document.getElementById(IDS.REFERENCE_COUNT).textContent = `(${comparisonData.second_graph.nodes.length} узлов)`;
    } catch (e) {
        console.error("Initialization failed:", e);
    }
}

/**
 * Renders a specific graph network.
 */
function renderNetwork(graphDto, containerId, graphType) {
    const data = createNetworkData(graphDto, graphType);
    const container = document.getElementById(containerId);
    
    const options = {
        layout: {
            hierarchical: {
                enabled: true,
                shakeTowards: 'roots',
                treeSpacing: 400,
                direction: 'DU', // Bottom-up direction
                sortMethod: 'directed',
                levelSeparation: 80,
                nodeSpacing: 80,
                edgeMinimization: true,
                parentCentralization: true,
                blockShifting: true
            }
        },
        nodes: {
            size: 16,
            font: {
                color: '#a9b7c6',
                size: 13,
                face: 'Inter, Segoe UI',
                multi: true,
                bold: { color: '#ffffff' }
            },
            borderWidth: 2,
            shadow: {
                enabled: true,
                color: 'rgba(0,0,0,0.3)',
                size: 5,
                x: 2,
                y: 2
            }
        },
        edges: {
            arrows: {
                to: { enabled: true, scaleFactor: 1.0 }
            },
            color: { color: '#555', highlight: '#4b6eaf', hover: '#888' },
            width: 2,
            selectionWidth: 3,
            hoverWidth: 3,
            smooth: {
                enabled: true,
                type: 'curvedCW',
                roundness: 0.2
            }
        },
        physics: { enabled: false },
        interaction: {
            hover: true,
            tooltipDelay: 200,
            hideEdgesOnDrag: false,
            dragNodes: false
        }
    };

    const network = new vis.Network(container, data, options);
    
    if (graphType === 'student') studentNetwork = network;
    else referenceNetwork = network;

    // Prevent view dragging when a drag starts on a node
    network.on("dragStart", function (params) {
        if (params.nodes.length > 0) {
            network.setOptions({ interaction: { dragView: false } });
        }
    });

    // On drag end, re-enable view dragging and handle it as a selection event
    network.on("dragEnd", function (params) {
        network.setOptions({ interaction: { dragView: true } });
        if (params.nodes.length > 0) {
            handleNodeSelection(params.nodes[0], graphType);
        }
    });

    // This handler now only fires for true clicks (no dragging)
    network.on("click", function (params) {
        if (params.nodes.length > 0) {
            handleNodeSelection(params.nodes[0], graphType);
        }
    });

    return network;
}

/**
 * Synchronizes selection between student and reference graphs.
 */
function handleNodeSelection(nodeId, sourceGraphType) {
    const mapping = comparisonData.compare_result.matched_hashes_1_to_2;
    let counterpartId = null;

    if (sourceGraphType === 'student') {
        counterpartId = mapping[nodeId];
        if (counterpartId) referenceNetwork.selectNodes([counterpartId]);
        else referenceNetwork.unselectAll();
    } else {
        counterpartId = Object.keys(mapping).find(key => mapping[key] === nodeId);
        if (counterpartId) studentNetwork.selectNodes([counterpartId]);
        else studentNetwork.unselectAll();
    }

    showDetails(nodeId, counterpartId, sourceGraphType);
}

/**
 * Creates the DataSet for a specific graph.
 */
function createNetworkData(graphDto, graphType) {
    if (!graphDto || !graphDto.nodes) return { nodes: new vis.DataSet([]), edges: new vis.DataSet([]) };
    
    const nodes = graphDto.nodes.map(node => {
        let label = getShortHash(node.hash);
        
        const colorConfig = getSeverityColor(node.severity, graphType);
        console.log(node.severity)
        return {
            id: node.id,
            label: label,
            title: createTooltip(node),
            color: colorConfig,
            shape: (node.severity === 'EXTRA')? 'custom': 'dot',
            ctxRenderer: getCustomRenderer(node.severity)
        };
    });

    const edges = graphDto.links.map(link => ({
        from: nodeToId(link.source, graphDto.nodes),
        to: nodeToId(link.target, graphDto.nodes),
        arrows: 'to'
    }));

    return {
        nodes: new vis.DataSet(nodes),
        edges: new vis.DataSet(edges)
    };
}

/**
 * Displays details for selected node(s).
 */
function showDetails(nodeId, counterpartId, sourceGraphType) {
    const studentNode = sourceGraphType === 'student' ? 
        comparisonData.first_graph.nodes.find(n => n.id === nodeId) :
        (counterpartId ? comparisonData.first_graph.nodes.find(n => n.id === counterpartId) : null);
    
    const referenceNode = sourceGraphType === 'reference' ? 
        comparisonData.second_graph.nodes.find(n => n.id === nodeId) :
        (counterpartId ? comparisonData.second_graph.nodes.find(n => n.id === counterpartId) : null);

    const html = `
        <div class="details-grid">
            <div class="details-column">
                <h4>Текущая версия</h4>
                ${studentNode ? renderNodeInfo(studentNode) : '<div class="empty-state">No matching commit</div>'}
            </div>
            <div class="details-column">
                <h4>Целевая версия</h4>
                ${referenceNode ? renderNodeInfo(referenceNode) : '<div class="empty-state">No matching commit</div>'}
            </div>
        </div>
    `;
    
    document.getElementById(IDS.DETAILS_PANEL).innerHTML = html;
}

/**
 * Renders HTML for a single node's metadata and diffs.
 */
function renderNodeInfo(node) {
    let colorClass = `text-severity-${node.severity}`;
    if (node.severity === 'MOVABLE') {
        // In comparison report, graphType is already known from column, but we keep it safe
        colorClass = 'text-severity-MOVABLE_STUDENT'; // placeholder
    }

    return `
        <h3 style="color:#ffffff; margin: 0 0 5px 0; font-size: 14px; font-family: 'JetBrains Mono', monospace;">[${node.number}] ${node.hash}</h3>
       <div style="margin-bottom: 5px;">
        <!-- Заменили <p> на <div> для лучшей семантики интерфейса -->
        <div style="margin: 2px 0; font-size: 13px; display: flex; align-items: center; gap: 8px;">
        <strong style="color: #888;">Статус:</strong> 
        
        <!-- Заменили <span> на <div>, так как внутри есть блочные элементы и flex -->
        <div class="${colorClass}" style="display:flex; align-items: center; gap: 6px; font-weight: bold;">
            <!-- Внутренний элемент тоже лучше сделать span или div, но div допустим внутри div -->
            <div class="color-box severity-${node.severity}" style="width:10px; height:10px; margin: 0; border-radius: 50%;"></div> 
            ${getSeverityName(node.severity)}
        </div>
    </div>
        </div>
        <button class="commit-metadata-toggle" onclick="toggleMetadata(this)">Commit Details</button>
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
        ${node.diffs && node.diffs.length > 0 ? '<h5 style=\"margin: 10px 0 5px 0; font-size: 12px; color: #888; text-transform: uppercase;\">Changes:</h5>' + renderDiffs(node.diffs) : ''}
    `;
}

if (document.readyState === 'complete') init();
else window.addEventListener('load', init);
