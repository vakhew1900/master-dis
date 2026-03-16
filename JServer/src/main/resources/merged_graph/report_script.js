// Constants for element IDs
const IDS = {
    DETAILS_PANEL: 'details',
    NETWORK: 'network',
    NODE_COUNT: 'node-count'
};

let network = null;
let comparisonData = null;
let activeMovablePair = null; // Store pair for canvas drawing

/**
 * Initializes the report.
 */
function init() {
    try {
        comparisonData = {{DATA}};
        if (!comparisonData || !comparisonData.merged_graph) {
            console.error("No valid comparison data found.");
            return;
        }

        renderNetwork(comparisonData.merged_graph);
    } catch (e) {
        console.error("Initialization failed:", e);
    }
}

/**
 * Renders the merged graph network.
 */
function renderNetwork(graphDto) {
    const studentHashes = Object.keys(comparisonData.compare_result.matched_hashes_1_to_2);
    
    const nodes = new vis.DataSet(graphDto.nodes.map(node => {
        let label = getShortHash(node.hash);
        
        let graphType = null;
        if (node.severity === 'MOVABLE') {
            graphType = studentHashes.includes(node.id) ? 'student' : 'reference';
        }

        const colorConfig = getSeverityColor(node.severity, graphType);

        return {
            id: node.id,
            label: label,
            title: createTooltip(node),
            color: colorConfig,
            shape: (node.severity === 'EXTRA')? 'custom': 'dot',
            ctxRenderer: getCustomRenderer(node.severity)
        };
    }));

    const edges = new vis.DataSet(graphDto.links.map(link => ({
        from: nodeToId(link.source, graphDto.nodes),
        to: nodeToId(link.target, graphDto.nodes),
        arrows: 'to',
        color: { color: '#555', highlight: '#4b6eaf' },
        width: 2
    })));

    const container = document.getElementById(IDS.NETWORK);
    const data = { nodes, edges };
    const options = {
        nodes: {
            size: 13,
            font: { color: '#a9b7c6', size: 12, face: 'Inter' },
            borderWidth: 2
        },
        edges: {
            smooth: { 
                type: 'curvedCW', 
                roundness: 0.2 
            }
        },
        layout: {
            hierarchical: {
                direction: 'DU', // Flipped: Down to Up
                sortMethod: 'directed',
                nodeSpacing: 80,
                levelSeparation: 80,
                edgeMinimization: true,
                parentCentralization: true,
                blockShifting: true
            }
        },
        physics: {
            enabled: false
        },
        interaction: { 
            hover: true, 
            selectConnectedEdges: false,
            dragNodes: false
        }
    };

    network = new vis.Network(container, data, options);
    document.getElementById(IDS.NODE_COUNT).textContent = `(${graphDto.nodes.length} узлов)`;

    // Prevent view dragging when a drag starts on a node
    network.on("dragStart", function (params) {
        if (params.nodes.length > 0) {
            network.setOptions({ interaction: { dragView: false } });
        }
    });
    network.on("dragEnd", function () {
        network.setOptions({ interaction: { dragView: true } });
    });

    // DRAW dashed line manually on canvas to avoid layout shifts
    network.on("afterDrawing", function (ctx) {
        if (activeMovablePair) {
            const pos1 = network.getPositions([activeMovablePair.from])[activeMovablePair.from];
            const pos2 = network.getPositions([activeMovablePair.to])[activeMovablePair.to];
            if (pos1 && pos2) {
                ctx.strokeStyle = 'rgba(75, 110, 175, 0.7)';
                ctx.lineWidth = 1.5;
                ctx.setLineDash([5, 5]);
                ctx.beginPath();
                ctx.moveTo(pos1.x, pos1.y);
                ctx.lineTo(pos2.x, pos2.y);
                ctx.stroke();
                ctx.setLineDash([]); // reset for other drawings
            }
        }
    });

    network.on("dragEnd", function (params) {
        network.setOptions({ interaction: { dragView: true } });
        // If we just finished dragging a node (and didn't drag it far), treat it as a click
        if (params.nodes.length > 0) {
            handleNodeClick(params.nodes[0]);
        }
    });

    // This handler now only fires for true clicks, not drags
    network.on("click", function (params) {
        if (params.nodes.length > 0) {
            handleNodeClick(params.nodes[0]);
        }
    });
}

/**
 * Handles all logic for when a node is clicked or selected via drag-release.
 * @param {string} nodeId The ID of the selected node.
 */
function handleNodeClick(nodeId) {
    // Reset any active movable pair drawings
    activeMovablePair = null;
    
    const node = comparisonData.merged_graph.nodes.find(n => n.id === nodeId);
    if (!node) return;

    // Special handling for MOVABLE nodes to select and detail both
    if (node.severity === 'MOVABLE') {
        const mapping = comparisonData.compare_result.matched_hashes_1_to_2;
        let counterpartId = mapping[node.id]; // Try as student
        if (!counterpartId) {
            // Try as reference
            counterpartId = Object.keys(mapping).find(key => mapping[key] === node.id);
        }
        
        if (counterpartId) {
            network.selectNodes([node.id, counterpartId]);
            activeMovablePair = { from: node.id, to: counterpartId };
            showMovableDetails(node.id, counterpartId);
            network.redraw(); // Trigger redraw for the dashed line
            return;
        }
    }
    
    // Default action: select the single node and show its details
    network.selectNodes([nodeId]);
    showDetails(nodeId);
    network.redraw();
}

/**
 * Displays details for the selected node.
 */
function showDetails(nodeId) {
    const node = comparisonData.merged_graph.nodes.find(n => n.id === nodeId);
    if (!node) return;

    let colorClass = `text-severity-${node.severity}`;
    if (node.severity === 'MOVABLE') {
        const studentHashes = Object.keys(comparisonData.compare_result.matched_hashes_1_to_2);
        colorClass = studentHashes.includes(node.id) ? 'text-severity-MOVABLE_STUDENT' : 'text-severity-MOVABLE_REFERENCE';
    }

    const html = `
        <div class="details-column">
            <h3 style="color:#ffffff; margin: 0 0 5px 0; font-size: 14px; font-family: 'JetBrains Mono', monospace;">[${node.number}] ${node.hash}</h3>
            <div style="margin-bottom: 5px;">
                <p style="margin: 2px 0; font-size: 13px; display: flex; align-items: center; gap: 8px;">
                    <strong style="color: #888;">Статус:</strong> 
                    <span class="${colorClass}" style="display:inline-flex; align-items: center; gap: 6px; font-weight: bold;">
                        <div class="color-box severity-${node.severity}" style="width:10px; height:10px; margin: 0;"></div> 
                        ${getSeverityName(node.severity)}
                    </span>
                </p>
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

            ${node.diffs && node.diffs.length > 0 ? '<h5 style="margin: 10px 0 5px 0; font-size: 12px; color: #888; text-transform: uppercase;">Объединенные изменения:</h5>' + renderDiffs(node.diffs) : ''}
        </div>
    `;
    document.getElementById(IDS.DETAILS_PANEL).innerHTML = html;
}

/**
 * Displays details for two movable nodes side-by-side.
 */
function showMovableDetails(id1, id2) {
    const node1 = comparisonData.merged_graph.nodes.find(n => n.id === id1);
    const node2 = comparisonData.merged_graph.nodes.find(n => n.id === id2);
    if (!node1 || !node2) return;

    // Ensure student is first (optional, but consistent)
    const mapping = comparisonData.compare_result.matched_hashes_1_to_2;
    const student = mapping[node1.id] ? node1 : node2;
    const reference = student === node1 ? node2 : node1;

    const html = `
        <div style="display: flex; flex-direction: column; gap: 20px;">
            <div class="details-column" style="border-left: 3px solid #6b90b2; padding-left: 10px; background: rgba(107, 144, 178, 0.05);">
                <h4 style="color:#6b90b2; margin: 0 0 5px 0;">СТУДЕНТ (Перемещен)</h4>
                ${renderNodeSummary(student)}
            </div>
            <div class="details-column" style="border-left: 3px solid #4e3867; padding-left: 10px; background: rgba(78, 56, 103, 0.05);">
                <h4 style="color:#906bb2; margin: 0 0 5px 0;">ЭТАЛОН (Перемещен)</h4>
                ${renderNodeSummary(reference)}
            </div>
        </div>
    `;
    document.getElementById(IDS.DETAILS_PANEL).innerHTML = html;
}

function renderNodeSummary(node) {
    let colorClass = `text-severity-${node.severity}`;
    if (node.severity === 'MOVABLE') {
        const studentHashes = Object.keys(comparisonData.compare_result.matched_hashes_1_to_2);
        colorClass = studentHashes.includes(node.id) ? 'text-severity-MOVABLE_STUDENT' : 'text-severity-MOVABLE_REFERENCE';
    }

    return `
        <h3 style="color:#ffffff; margin: 0 0 5px 0; font-size: 14px; font-family: 'JetBrains Mono', monospace;">[${node.number}] ${node.hash}</h3>
        <div style="margin-bottom: 5px;">
            <p style="margin: 2px 0; font-size: 13px; display: flex; align-items: center; gap: 8px;">
                <strong style="color: #888;">Статус:</strong> 
                <span class="${colorClass}" style="display:inline-flex; align-items: center; gap: 6px; font-weight: bold;">
                    <div class="color-box severity-${node.severity}" style="width:10px; height:10px; margin: 0;"></div> 
                    ${getSeverityName(node.severity)}
                </span>
            </p>
        </div>
        <div class="metadata-row">
            <div class="metadata-label">Message:</div>
            <div class="metadata-value" style="color: #6a8759;">"${node.message}"</div>
        </div>
        <div class="metadata-row">
            <div class="metadata-label">Author:</div>
            <div class="metadata-value">${node.author ? node.author.name : 'N/A'}</div>
        </div>
        <div class="metadata-row">
            <div class="metadata-label">Date:</div>
            <div class="metadata-value" style="color: #cc7832">${node.commitDate}</div>
        </div>
        ${node.diffs && node.diffs.length > 0 ? renderDiffs(node.diffs) : ''}
    `;
}

if (document.readyState === 'complete') init();
else window.addEventListener('load', init);
