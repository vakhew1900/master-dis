import React, { useState } from 'react';
import type { components } from '../../../api/models/schema';
import { SEVERITY } from '../../../api/models/constants';
import { MergedGraphLegend } from './MergedGraphLegend';
import { GraphCanvas } from './GraphCanvas';
import { CommitDetailsPanel } from './CommitDetailsPanel';
import styles from './MergedComparisonView.module.css';

type MergedGraphResult = components["schemas"]["MergedGraphComparisonResultDto"];
type NodeDto = components["schemas"]["NodeDto"];

interface MergedComparisonViewProps {
  result: MergedGraphResult;
}

export const MergedComparisonView: React.FC<MergedComparisonViewProps> = ({ result }) => {
  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null);
  const [activeMovablePair, setActiveMovablePair] = useState<{ from: string; to: string } | null>(null);

  if (!result.mergedGraph) return null;

  const handleNodeSelect = (nodeId: string | null) => {
    setActiveMovablePair(null);
    if (!nodeId) {
        setSelectedNodeId(null);
        return;
    }

    const node = result.mergedGraph?.nodes?.find(n => n.id === nodeId);
    if (node?.severity === SEVERITY.MOVABLE) {
        const mapping = result.compareResult?.matchedHashes1To2 || {};
        let counterpartId = mapping[nodeId];
        if (!counterpartId) {
            counterpartId = Object.keys(mapping).find(key => mapping[key] === nodeId) || '';
        }

        if (counterpartId) {
            setSelectedNodeId(nodeId);
            setActiveMovablePair({ from: nodeId, to: counterpartId });
            return;
        }
    }

    setSelectedNodeId(nodeId);
  };

  // Find nodes for details
  let studentNode: NodeDto | null = null;
  let referenceNode: NodeDto | null = null;

  if (activeMovablePair) {
      studentNode = result.mergedGraph.nodes?.find(n => n.id === activeMovablePair.from || n.id === activeMovablePair.to) || null;
      referenceNode = result.mergedGraph.nodes?.find(n => n.id !== studentNode?.id && (n.id === activeMovablePair.from || n.id === activeMovablePair.to)) || null;
  } else if (selectedNodeId) {
      studentNode = result.mergedGraph.nodes?.find(n => n.id === selectedNodeId) || null;
  }

  return (
    <div className={styles.viewWrapper}>
      <MergedGraphLegend />
      
      <div className={styles.mainLayout}>
        <div className={styles.graphColumn}>
          <GraphCanvas 
            data={result.mergedGraph} 
            title="Объединенный граф (Merged Graph)" 
            onNodeSelect={handleNodeSelect}
            selectedNodeId={selectedNodeId}
            activeMovablePair={activeMovablePair}
          />
        </div>
        
        <div className={styles.detailsColumn}>
          <CommitDetailsPanel 
            studentNode={studentNode} 
            referenceNode={referenceNode} 
          />
        </div>
      </div>
    </div>
  );
};
