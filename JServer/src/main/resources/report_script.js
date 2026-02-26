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

const severityColors = {
    'IDENTICAL': { background: '#e6ffed', border: '#28a745', highlight: { background: '#c6f6d5', border: '#28a745' } },
    'MODIFIED': { background: '#fff9db', border: '#fcc419', highlight: { background: '#fff3bf', border: '#fcc419' } },
    'EXTRA': { background: '#ffeef0', border: '#d73a49', highlight: { background: '#ffdce0', border: '#d73a49' } },
    'MOVABLE': { background: '#e6f7ff', border: '#1890ff', highlight: { background: '#bae7ff', border: '#1890ff' } }
};

let studentNetwork = null;
let referenceNetwork = null;

async function init() {
    console.log("Initializing report...");
    
    // Ждем, пока браузер вычислит размеры контейнеров
    await new Promise(resolve => setTimeout(resolve, 200));

    if (typeof vis === 'undefined') {
        console.warn("Vis library not ready, retrying in 500ms...");
        setTimeout(init, 500);
        return;
    }

    try {
        const options = {
            layout: {
                hierarchical: {
                    direction: 'UD',
                    sortMethod: 'directed',
                    levelSeparation: 100,
                    nodeSpacing: 150,
                    edgeMinimization: true,
                    parentCentralization: true
                }
            },
            physics: {
                enabled: true,
                hierarchicalRepulsion: {
                    nodeDistance: 150
                },
                stabilization: {
                    enabled: true,
                    iterations: 1000,
                    updateInterval: 50
                }
            },
            interaction: {
                hover: true,
                navigationButtons: true,
                keyboard: true
            }
        };

        const studentData = createNetworkData(comparisonData.first_graph);
        const referenceData = createNetworkData(comparisonData.second_graph);

        if (comparisonData.first_graph && comparisonData.first_graph.nodes) {
            document.getElementById(IDS.STUDENT_COUNT).textContent = `(${comparisonData.first_graph.nodes.length} nodes)`;
        }
        if (comparisonData.second_graph && comparisonData.second_graph.nodes) {
            document.getElementById(IDS.REFERENCE_COUNT).textContent = `(${comparisonData.second_graph.nodes.length} nodes)`;
        }

        studentNetwork = new vis.Network(document.getElementById(IDS.STUDENT_NETWORK), studentData, options);
        referenceNetwork = new vis.Network(document.getElementById(IDS.REFERENCE_NETWORK), referenceData, options);

        // Отключаем физику после стабилизации для производительности
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
        color: severityColors[node.severity] || { background: '#ffffff', border: '#ced4da' },
        font: { size: 12 },
        shape: 'box',
        borderWidth: 2,
        margin: 10
    }));

    const edges = (graphDto.links || []).map(link => ({
        from: link.source,
        to: link.target,
        arrows: 'to',
        color: '#adb5bd'
    }));

    return { nodes: new vis.DataSet(nodes), edges: new vis.DataSet(edges) };
}

function setupInteractions(sNet, rNet) {
    sNet.on("click", function (params) {
        if (params.nodes.length > 0) {
            const nodeId = params.nodes[0];
            showCombinedDetails(nodeId, 'student');
            if (comparisonData.compare_result && comparisonData.compare_result.matched_hashes_1_to_2) {
                const matched = comparisonData.compare_result.matched_hashes_1_to_2[nodeId];
                if (matched) rNet.selectNodes([matched]);
                else rNet.unselectAll();
            }
        }
    });

    rNet.on("click", function (params) {
        if (params.nodes.length > 0) {
            const nodeId = params.nodes[0];
            showCombinedDetails(nodeId, 'reference');
            if (comparisonData.compare_result && comparisonData.compare_result.matched_hashes_1_to_2) {
                const mapping = comparisonData.compare_result.matched_hashes_1_to_2;
                const matched = Object.keys(mapping).find(key => mapping[key] === nodeId);
                if (matched) sNet.selectNodes([matched]);
                else sNet.unselectAll();
            }
        }
    });
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
                ${studentNode ? renderNodeInfo(studentNode) : '<p>No matching commit found</p>'}
            </div>
            <div class="details-column">
                <h4>Reference Commit</h4>
                ${referenceNode ? renderNodeInfo(referenceNode) : '<p>No matching commit found</p>'}
            </div>
        </div>
    `;
    document.getElementById(IDS.DETAILS_PANEL).innerHTML = html;
}

function renderNodeInfo(node) {
    return `
        <h3>[${node.number}] ${node.hash.substring(0, 12)}...</h3>
        <p><strong>Severity:</strong> <span class="legend-item"><div class="color-box severity-${node.severity}"></div> ${node.severity}</span></p>
        <p><strong>Message:</strong> ${node.message}</p>
        <p><strong>Author:</strong> ${node.author ? node.author.name : 'N/A'}</p>
        <p><strong>Date:</strong> ${node.commitDate}</p>
        ${node.diffs && node.diffs.length > 0 ? '<h5>Diffs:</h5>' + renderDiffs(node.diffs) : ''}
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

// Запуск
if (document.readyState === 'complete') init();
else window.addEventListener('load', init);
