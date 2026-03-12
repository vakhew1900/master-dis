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
    const nodes = new vis.DataSet(graphDto.nodes.map(node => ({
        id: node.id,
        label: `[${node.number}] ${node.hash.substring(0, 7)}`,
        title: `${node.message}\nStatus: ${node.severity}`,
        className: `severity-${node.severity}`,
        color: getSeverityColor(node.severity)
    })));

    const edges = new vis.DataSet(graphDto.links.map(link => ({
        from: nodeToId(link.source, graphDto.nodes),
        to: nodeToId(link.target, graphDto.nodes),
        arrows: 'to',
        color: { color: '#555', highlight: '#4b6eaf' }
    })));

    const container = document.getElementById(IDS.NETWORK);
    const data = { nodes, edges };
    const options = {
        nodes: {
            shape: 'dot',
            size: 16,
            font: { color: '#a9b7c6', size: 12, face: 'Inter' },
            borderWidth: 2
        },
        edges: {
            smooth: { type: 'cubicBezier', forceDirection: 'vertical', roundness: 0.4 }
        },
        layout: {
            hierarchical: {
                direction: 'UD',
                sortMethod: 'directed',
                nodeSpacing: 150,
                levelSeparation: 100
            }
        },
        physics: false,
        interaction: { hover: true, selectConnectedEdges: false }
    };

    network = new vis.Network(container, data, options);
    document.getElementById(IDS.NODE_COUNT).textContent = `(${graphDto.nodes.length} узлов)`;

    network.on("click", function (params) {
        if (params.nodes.length > 0) {
            showDetails(params.nodes[0]);
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

            ${node.diffs && node.diffs.length > 0 ? '<h5 style="margin: 10px 0 5px 0; font-size: 12px; color: #888; text-transform: uppercase;">Merged Changes:</h5>' + renderDiffs(node.diffs) : ''}
        </div>
    `;
    document.getElementById(IDS.DETAILS_PANEL).innerHTML = html;
}

if (document.readyState === 'complete') init();
else window.addEventListener('load', init);
