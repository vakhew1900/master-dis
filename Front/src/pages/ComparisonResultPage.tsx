import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import type { components } from '../api/models/schema';
import { ComparisonLegend } from './comparison/ComparisonLegend';
import { GraphCanvas } from './comparison/GraphCanvas';
import { CommitDetailsPanel } from './comparison/CommitDetailsPanel';
import styles from './ComparisonResultPage.module.css';

type TwoGraphResult = components["schemas"]["TwoGraphComparisonResultDto"];
type NodeDto = components["schemas"]["NodeDto"];

const ComparisonResultPage: React.FC = () => {
  const location = useLocation();
  const result = location.state?.result as TwoGraphResult;
  
  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null);

  if (!result || !result.firstGraph || !result.secondGraph) {
    return <div className="container">Нет данных для отображения.</div>;
  }

  // Логика синхронизации: поиск соответствия
  const getCounterpartId = (nodeId: string | null) => {
    if (!nodeId) return null;
    const mapping = result.compareResult?.matchedHashes1To2 || {};
    
    // Если выбрали в первом графе, ищем во втором
    if (mapping[nodeId]) return mapping[nodeId];
    
    // Если выбрали во втором графе, ищем в первом (реверсивный поиск)
    const reverseMatch = Object.keys(mapping).find(key => mapping[key] === nodeId);
    return reverseMatch || null;
  };

  const handleNodeSelect = (nodeId: string | null) => {
    setSelectedNodeId(nodeId);
  };

  const counterpartId = getCounterpartId(selectedNodeId);

  // Находим сами объекты нод для панели деталей
  const studentNode = result.firstGraph.nodes?.find(n => n.id === selectedNodeId) 
                   || result.firstGraph.nodes?.find(n => n.id === counterpartId) || null;
                   
  const referenceNode = result.secondGraph.nodes?.find(n => n.id === selectedNodeId) 
                     || result.secondGraph.nodes?.find(n => n.id === counterpartId) || null;

  return (
    <div className="container">
      <ComparisonLegend />
      
      <div className={styles.graphsWrapper}>
        <GraphCanvas 
          data={result.firstGraph} 
          title="Репозиторий студента" 
          onNodeSelect={handleNodeSelect}
          selectedNodeId={selectedNodeId === studentNode?.id ? selectedNodeId : counterpartId}
        />
        <GraphCanvas 
          data={result.secondGraph} 
          title="Эталонный репозиторий" 
          onNodeSelect={handleNodeSelect}
          selectedNodeId={selectedNodeId === referenceNode?.id ? selectedNodeId : counterpartId}
        />
      </div>

      <CommitDetailsPanel 
        studentNode={studentNode} 
        referenceNode={referenceNode} 
      />
    </div>
  );
};

export default ComparisonResultPage;
