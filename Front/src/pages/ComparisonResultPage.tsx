import React from 'react';
import { useLocation } from 'react-router-dom';
import { ComparisonLegend as TwoGraphLegend } from './comparison/two_graph/ComparisonLegend';
import { GraphCanvas as TwoGraphCanvas } from './comparison/two_graph/GraphCanvas';
import { CommitDetailsPanel as TwoGraphDetailsPanel } from './comparison/two_graph/CommitDetailsPanel';
import { MergedComparisonView } from './comparison/merged_graph/MergedComparisonView';
import type { NodeDto } from '../api/generated/model';
import type {CompareResultDtoMatchedHashes1To2} from '../api/generated/model';
import styles from './ComparisonResultPage.module.css';
import { isMergedResult, isTwoGraphResult } from '../api/utils';

const ComparisonResultPage: React.FC = () => {
  const location = useLocation();
  const result = location.state?.result;

  console.log(location)
  const [selectedNodeId, setSelectedNodeId] = React.useState<string | null>(null);

  if (!result) {
    return <div className="container">Нет данных для отображения.</div>;
  }

  // Two Graph mode
  if (isTwoGraphResult(result)) {
      const { first_graph, second_graph, compare_result } = result;
      
      // Логика синхронизации для двух графов (дублируем из старой версии для стабильности
      const handleNodeSelect = (id: string | null) => setSelectedNodeId(id);

      const getCounterpartId = (nodeId: string | null) => {
        if (!nodeId) return null;
        const mapping: Record<string, string> = (compare_result?.matched_hashes_1_to_2 as CompareResultDtoMatchedHashes1To2) || {};
        if (mapping[nodeId]) return mapping[nodeId];
        const reverseMatch = Object.keys(mapping).find((key: string) => mapping[key] === nodeId);
        return reverseMatch || null;
      };

      const counterpartId = getCounterpartId(selectedNodeId);
      const studentNode = first_graph?.nodes?.find((n: NodeDto) => n.id === selectedNodeId) 
                       || first_graph?.nodes?.find((n: NodeDto) => n.id === counterpartId) || null;
      const referenceNode = second_graph?.nodes?.find((n: NodeDto) => n.id === selectedNodeId) 
                         || second_graph?.nodes?.find((n: NodeDto) => n.id === counterpartId) || null;

      return (
        <div className="container">
          <TwoGraphLegend />
          <div className={styles.graphsWrapper}>
            <TwoGraphCanvas 
              data={first_graph} 
              title="Репозиторий студента" 
              onNodeSelect={handleNodeSelect}
              selectedNodeId={selectedNodeId === studentNode?.id ? selectedNodeId : counterpartId}
            />
            <TwoGraphCanvas 
              data={second_graph} 
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
  if (isMergedResult(result)) {
      return (
        <div className="container">
          <MergedComparisonView result={result} />
        </div>
      );
  }

  return <div className="container">Неизвестный тип отчета.</div>;
};

export default ComparisonResultPage;
