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
            document.getElementById('student-count').textContent = `(${comparisonData.first_graph.nodes.length} nodes)`;
        }
        if (comparisonData.second_graph && comparisonData.second_graph.nodes) {
            document.getElementById('reference-count').textContent = `(${comparisonData.second_graph.nodes.length} nodes)`;
        }

        studentNetwork = new vis.Network(document.getElementById('student-network'), studentData, options);
        referenceNetwork = new vis.Network(document.getElementById('reference-network'), referenceData, options);

        // Отключаем физику после стабилизации для производительности
        studentNetwork.on("stabilizationIterationsDone", () => studentNetwork.setOptions({ physics: false }));
        referenceNetwork.on("stabilizationIterationsDone", () => referenceNetwork.setOptions({ physics: false }));

        setupInteractions(studentNetwork, referenceNetwork);
        
    } catch (e) {
        console.error("Critical Init Error:", e);
        document.getElementById('details').innerHTML = `<h3 style="color:red">Render Error</h3><pre>${e.stack}</pre>`;
    }
}

function createNetworkData(graphDto) {
    if (!graphDto || !graphDto.nodes) return { nodes: new vis.DataSet([]), edges: new vis.DataSet([]) };
    
    const nodes = graphDto.nodes.map(node => ({
        id: node.id,
        label: `[${node.number}]
${node.id.substring(0, 7)}`,
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
            showDetails(nodeId, 'student');
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
            showDetails(nodeId, 'reference');
            if (comparisonData.compare_result && comparisonData.compare_result.matched_hashes_1_to_2) {
                const mapping = comparisonData.compare_result.matched_hashes_1_to_2;
                const matched = Object.keys(mapping).find(key => mapping[key] === nodeId);
                if (matched) sNet.selectNodes([matched]);
                else sNet.unselectAll();
            }
        }
    });
}

function showDetails(nodeId, graphType) {
    const graph = graphType === 'student' ? comparisonData.first_graph : comparisonData.second_graph;
    const node = graph.nodes.find(n => n.id === nodeId);
    if (!node) return;

    const detailsHtml = `
        <h3>[${node.number}] ${node.hash}</h3>
        <p><strong>Severity:</strong> <span class="legend-item"><div class="color-box severity-${node.severity}"></div> ${node.severity}</span></p>
        <p><strong>Message:</strong> ${node.message}</p>
        <p><strong>Author:</strong> ${node.author ? node.author.name + ' <' + node.author.email + '>' : 'N/A'}</p>
        <p><strong>Date:</strong> ${node.commitDate}</p>
        ${node.diffs && node.diffs.length > 0 ? '<h4>Diffs:</h4><ul style="max-height: 200px; overflow-y: auto; background: #f8f9fa; padding: 10px; border-radius: 4px;">' + node.diffs.map(d => `<li style="font-family: monospace; border-bottom: 1px solid #eee; margin-bottom: 5px; list-style: none;">${d.value.replace(/</g, '&lt;').replace(/>/g, '&gt;')}</li>`).join('') + '</ul>' : ''}
    `;
    document.getElementById('details').innerHTML = detailsHtml;
}

// Запуск
if (document.readyState === 'complete') init();
else window.addEventListener('load', init);
