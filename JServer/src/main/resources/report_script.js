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

// Darcula Palette for Graph Nodes
const severityColors = {
    'IDENTICAL': { 
        background: '#365939', 
        border: '#496c4b', 
        highlight: { background: '#496c4b', border: '#5da063' } 
    },
    'MODIFIED': { 
        background: '#5e5339', 
        border: '#80714a', 
        highlight: { background: '#80714a', border: '#a69664' } 
    },
    'EXTRA': { 
        background: '#593939', 
        border: '#804b4b', 
        highlight: { background: '#804b4b', border: '#b26b6b' } 
    },
    'MOVABLE': { 
        background: '#384c67', 
        border: '#4b6a8e', 
        highlight: { background: '#4b6a8e', border: '#6b90b2' } 
    }
};

let studentNetwork = null;
let referenceNetwork = null;

async function init() {
    console.log("Initializing Darcula report...");
    
    await new Promise(resolve => setTimeout(resolve, 200));

    if (typeof vis === 'undefined') {
        setTimeout(init, 500);
        return;
    }

    try {
        const options = {
            layout: {
                hierarchical: {
                    direction: 'UD',
                    sortMethod: 'directed',
                    levelSeparation: 120,
                    nodeSpacing: 180,
                    edgeMinimization: true,
                    parentCentralization: true
                }
            },
            nodes: {
                shape: 'dot',
                size: 20,
                font: {
                    color: '#a9b7c6',
                    size: 14,
                    face: 'Segoe UI'
                },
                borderWidth: 2,
                shadow: true
            },
            edges: {
                arrows: 'to',
                color: { color: '#555555', highlight: '#888888' },
                width: 2,
                smooth: {
                    type: 'cubicBezier',
                    forceDirection: 'vertical',
                    roundness: 0.4
                }
            },
            physics: {
                enabled: true,
                hierarchicalRepulsion: {
                    nodeDistance: 200
                },
                stabilization: {
                    enabled: true,
                    iterations: 1000
                }
            },
            interaction: {
                hover: true,
                navigationButtons: true,
                keyboard: true,
                tooltipDelay: 200
            }
        };

        const studentData = createNetworkData(comparisonData.first_graph);
        const referenceData = createNetworkData(comparisonData.second_graph);

        document.getElementById(IDS.STUDENT_COUNT).textContent = `(${comparisonData.first_graph.nodes.length} nodes)`;
        document.getElementById(IDS.REFERENCE_COUNT).textContent = `(${comparisonData.second_graph.nodes.length} nodes)`;

        studentNetwork = new vis.Network(document.getElementById(IDS.STUDENT_NETWORK), studentData, options);
        referenceNetwork = new vis.Network(document.getElementById(IDS.REFERENCE_NETWORK), referenceData, options);

        studentNetwork.on("stabilizationIterationsDone", () => studentNetwork.setOptions({ physics: false }));
        referenceNetwork.on("stabilizationIterationsDone", () => referenceNetwork.setOptions({ physics: false }));

        setupInteractions(studentNetwork, referenceNetwork);
        
    } catch (e) {
        console.error("Critical Init Error:", e);
        document.getElementById(IDS.DETAILS_PANEL).innerHTML = `<h3 style="color:red">Render Error</h3><pre>${e.stack}</pre>`;
    }
}

function createNetworkData(graphDto) {
    if (!graphDto || !graphDto.nodes) return { nodes: new vis.DataSet([]), edges: new vis.DataSet([]) };
    
    const nodes = graphDto.nodes.map(node => ({
        id: node.id,
        label: `[${node.number}]\n${node.id.substring(0, 7)}`,
        title: node.message,
        color: severityColors[node.severity] || { background: '#3c3f41', border: '#4b4b4b' }
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
                <h4>Student Commit</h4>
                ${studentNode ? renderNodeInfo(studentNode) : '<p style="opacity:0.5">No matching commit found</p>'}
            </div>
            <div class="details-column">
                <h4>Reference Commit</h4>
                ${referenceNode ? renderNodeInfo(referenceNode) : '<p style="opacity:0.5">No matching commit found</p>'}
            </div>
        </div>
    `;
    document.getElementById(IDS.DETAILS_PANEL).innerHTML = html;
}

function renderNodeInfo(node) {
    return `
        <h3 style="color:#bbbbbb">[${node.number}] ${node.hash.substring(0, 12)}...</h3>
        <p><strong>Severity:</strong> <span class="legend-item"><div class="color-box severity-${node.severity}"></div> ${node.severity}</span></p>
        <p><strong>Message:</strong> <span style="color: #6a8759">"${node.message}"</span></p>
        <p><strong>Author:</strong> ${node.author ? node.author.name : 'N/A'}</p>
        <p><strong>Date:</strong> <span style="color: #cc7832">${node.commitDate}</span></p>
        ${node.diffs && node.diffs.length > 0 ? '<h5>Changes (Diffs):</h5>' + renderDiffs(node.diffs) : ''}
    `;
}

function renderDiffs(diffs) {
    return `
        <ul class="diff-list">
            ${diffs.map(d => {
                const value = d.value.replace(/</g, '&lt;').replace(/>/g, '&gt;');
                let className = 'diff-line';
                if (value.startsWith('+')) className += ' diff-line-added';
                else className += ' diff-line-removed';
                return `<li class="${className}">${value}</li>`;
            }).join('')}
        </ul>
    `;
}

if (document.readyState === 'complete') init();
else window.addEventListener('load', init);
