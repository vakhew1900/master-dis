// Constants for element IDs
const IDS = {
    DETAILS_PANEL: 'details',
    NETWORK: 'network',
    NODE_COUNT: 'node-count'
};

let network = null;
let comparisonData = null;

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
            className: `severity-${node.severity}`,
            color: colorConfig,
            shape: 'dot',
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
            smooth: { type: 'cubicBezier', forceDirection: 'vertical', roundness: 0.6 }
        },
        layout: {
            hierarchical: {
                direction: 'DU', // Flipped: Down to Up
                sortMethod: 'directed',
                nodeSpacing: 120,
                levelSeparation: 100,
                edgeMinimization: true,
                parentCentralization: true
            }
        },
        physics: false,
        interaction: { hover: true, selectConnectedEdges: false }
    };

    network = new vis.Network(container, data, options);
    document.getElementById(IDS.NODE_COUNT).textContent = `(${graphDto.nodes.length} узлов)`;

    network.on("click", function (params) {
        // Clear previous movable edges
        const currentEdges = edges.get({ filter: (e) => e.isMovable });
        if (currentEdges.length > 0) {
            edges.remove(currentEdges.map(e => e.id));
        }

        if (params.nodes.length > 0) {
            const nodeId = params.nodes[0];
            const node = graphDto.nodes.find(n => n.id === nodeId);
            
            if (node && node.severity === 'MOVABLE') {
                const mapping = comparisonData.compare_result.matched_hashes_1_to_2;
                let counterpartId = mapping[node.id]; // Try as student
                if (!counterpartId) {
                    // Try as reference
                    counterpartId = Object.keys(mapping).find(key => mapping[key] === node.id);
                }
                
                if (counterpartId) {
                    network.selectNodes([node.id, counterpartId]);
                    
                    // Add temporary dashed edge between moved nodes
                    edges.add({
                        id: `movable-${node.id}-${counterpartId}`,
                        from: node.id,
                        to: counterpartId,
                        dashes: true,
                        color: { color: 'rgba(75, 110, 175, 0.4)' },
                        width: 1,
                        arrows: '',
                        isMovable: true
                    });

                    showMovableDetails(node.id, counterpartId);
                    return;
                }
            }
            
            showDetails(nodeId);
        }
    });
}

/**
 * Displays details for the selected node.
 */
function showDetails(nodeId) {
    const node = comparisonData.merged_graph.nodes.find(n => n.id === nodeId);
    if (!node) return;

    const html = `
        <div class="details-column">
            <h3 style="color:#ffffff; margin: 0 0 5px 0; font-size: 14px; font-family: 'JetBrains Mono', monospace;">[${node.number}] ${node.hash}</h3>
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

            ${node.diffs && node.diffs.length > 0 ? '<h5 style="margin: 10px 0 5px 0; font-size: 12px; color: #888; text-transform: uppercase;">Merged Changes:</h5>' + renderDiffs(node.diffs) : ''}
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
                <h4 style="color:#6b90b2; margin: 0 0 5px 0;">STUDENT (Moved)</h4>
                ${renderNodeSummary(student)}
            </div>
            <div class="details-column" style="border-left: 3px solid #6b90b2; padding-left: 10px; background: rgba(107, 144, 178, 0.05);">
                <h4 style="color:#6b90b2; margin: 0 0 5px 0;">REFERENCE (Moved)</h4>
                ${renderNodeSummary(reference)}
            </div>
        </div>
    `;
    document.getElementById(IDS.DETAILS_PANEL).innerHTML = html;
}

function renderNodeSummary(node) {
    return `
        <h3 style="color:#ffffff; margin: 0 0 5px 0; font-size: 14px; font-family: 'JetBrains Mono', monospace;">[${node.number}] ${node.hash}</h3>
        <div class="metadata-row">
            <div class="metadata-label">Message:</div>
            <div class="metadata-value" style="color: #6a8759;">"${node.message}"</div>
        </div>
        ${node.diffs && node.diffs.length > 0 ? renderDiffs(node.diffs) : ''}
    `;
}

if (document.readyState === 'complete') init();
else window.addEventListener('load', init);
