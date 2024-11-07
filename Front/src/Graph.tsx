import React, { useRef, useEffect } from 'react';
import cytoscape from 'cytoscape';
import colaLayout from 'cytoscape-cola';
import dagreLayout from 'cytoscape-dagre';


cytoscape.use(dagreLayout as any);

export const MyGraph = () => {
    const cyRef = useRef(null);

    useEffect(() => {
        const cy = cytoscape({
            container: cyRef.current,
            elements: [
                // Main branch
                { data: { id: 'commit1', label: 'Initial commit' } },
                { data: { id: 'commit2', label: 'Feature A' } },
                { data: { id: 'commit3', label: 'Bug fix' } },
                { data: { id: 'commit4', label: 'Feature B' } },

                // Branch 1
                { data: { id: 'commit5', label: 'Feature C' } },
                { data: { id: 'commit6', label: 'Feature D' } },

                // Branch 2
                { data: { id: 'commit7', label: 'Feature E' } },

                // Merge points
                { data: { id: 'merge1', label: 'Merge' } },
                { data: { id: 'merge2', label: 'Merge' } },

                // Edges
                { data: { source: 'commit1', target: 'commit2' } },
                { data: { source: 'commit2', target: 'commit3' } },
                { data: { source: 'commit3', target: 'commit4' } },
                { data: { source: 'commit4', target: 'merge1' } },
                { data: { source: 'commit3', target: 'commit5' } },
                { data: { source: 'commit5', target: 'commit6' } },
                { data: { source: 'commit6', target: 'merge1' } },
                { data: { source: 'commit3', target: 'commit7' } },
                { data: { source: 'commit7', target: 'merge2' } },
                { data: { source: 'merge1', target: 'merge2' } },
            ],
            style: [
                {
                    selector: 'node',
                    style: {
                        'background-color': '#666',
                        'label': 'data(label)',
                        'width': '10px',
                        'height': '10px',
                        'text-valign': 'center',
                        'text-halign': 'center',
                        'shape': 'roundrectangle',
                        'font-size': '10px'
                    }
                },
                {
                    selector: 'edge',
                    style: {
                        'width': 3,
                        'line-color': '#ccc',
                        'target-arrow-shape': 'triangle',
                        'target-arrow-color': '#ccc'
                    }
                }
            ],
           
            // userPanningEnabled: false
            userZoomingEnabled: false,
        });

        // cy.on('zoom', function(evt) {
        //     evt.preventDefault();
        //   });
      
        //   cy.on('userzooming', function(evt) {
        //     evt.preventDefault();
        //   });

        // Запускаем  алгоритм  укладки  `cose-bilkent` 
        cy.layout({ name: 'dagre',  rankDir: 'BT', }).run(); 
    }, []);

    return (
        <div ref={cyRef} style={{ height: '800px', width: '50%'}}/>
    );
};