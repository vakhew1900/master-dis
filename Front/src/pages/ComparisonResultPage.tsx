import React from 'react';
import { useLocation } from 'react-router-dom';
import { ComparisonLegend as TwoGraphLegend } from './comparison/two_graph/ComparisonLegend';
import { GraphCanvas as TwoGraphCanvas } from './comparison/two_graph/GraphCanvas';
import { CommitDetailsPanel as TwoGraphDetailsPanel } from './comparison/two_graph/CommitDetailsPanel';
import { MergedComparisonView } from './comparison/merged_graph/MergedComparisonView';
import type { NodeDto } from '../api/generated/model';
import styles from './ComparisonResultPage.module.css';

const ComparisonResultPage: React.FC = () => {
  const location = useLocation();
  const result = location.state?.result;

  if (!result) {
    return <div className="container">Нет данных для отображения.</div>;
  }

  // Two Graph mode
  if (result.type === 'TwoGraphComparisonResultDto') {
      const { firstGraph, secondGraph, compareResult } = result;
      
      // Логика синхронизации для двух графов (дублируем из старой версии для стабильности)
      const [selectedNodeId, setSelectedNodeId] = React.useState<string | null>(null);
      const handleNodeSelect = (id: string | null) => setSelectedNodeId(id);

      const getCounterpartId = (nodeId: string | null) => {
        if (!nodeId) return null;
        const mapping: Record<string, string> = (compareResult?.matchedHashes1To2 as any) || {};
        if (mapping[nodeId]) return mapping[nodeId];
        const reverseMatch = Object.keys(mapping).find((key: string) => mapping[key] === nodeId);
        return reverseMatch || null;
      };

      const counterpartId = getCounterpartId(selectedNodeId);
      const studentNode = firstGraph.nodes?.find((n: NodeDto) => n.id === selectedNodeId) 
                       || firstGraph.nodes?.find((n: NodeDto) => n.id === counterpartId) || null;
      const referenceNode = secondGraph.nodes?.find((n: NodeDto) => n.id === selectedNodeId) 
                         || secondGraph.nodes?.find((n: NodeDto) => n.id === counterpartId) || null;

      return (
        <div className="container">
          <TwoGraphLegend />
          <div className={styles.graphsWrapper}>
            <TwoGraphCanvas 
              data={firstGraph} 
              title="Репозиторий студента" 
              onNodeSelect={handleNodeSelect}
              selectedNodeId={selectedNodeId === studentNode?.id ? selectedNodeId : counterpartId}
            />
            <TwoGraphCanvas 
              data={secondGraph} 
              title="Эталонный репозиторий" 
              onNodeSelect={handleNodeSelect}
              selectedNodeId={selectedNodeId === referenceNode?.id ? selectedNodeId : counterpartId}
            />
          </div>
          <TwoGraphDetailsPanel studentNode={studentNode} referenceNode={referenceNode} />
        </div>
      );
  }

  // Merged Graph mode
  if (result.type === 'MergedGraphComparisonResultDto') {
      return (
        <div className="container">
          <MergedComparisonView result={result} />
        </div>
      );
  }

  return <div className="container">Неизвестный тип отчета.</div>;
};

export default ComparisonResultPage;
