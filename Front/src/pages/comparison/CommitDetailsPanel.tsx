import React from 'react';
import type { components } from '../../api/models/schema';
import styles from './CommitDetailsPanel.module.css';

type NodeDto = components["schemas"]["NodeDto"];
type DiffDto = components["schemas"]["DiffDto"];

interface CommitDetailsPanelProps {
  studentNode: NodeDto | null;
  referenceNode: NodeDto | null;
}

const DiffLine: React.FC<{ diff: DiffDto }> = ({ diff }) => {
  const isAdded = diff.value?.startsWith('+');
  const isRemoved = diff.value?.startsWith('-');
  
  const className = isAdded ? styles.added : (isRemoved ? styles.removed : '');

  return (
    <div className={`${styles.diffLine} ${className}`}>
      {diff.value}
    </div>
  );
};

const NodeInfo: React.FC<{ node: NodeDto; title: string }> = ({ node, title }) => {
  return (
    <div className={styles.column}>
      <h4>{title}</h4>
      {node ? (
        <>
          <div className={styles.meta}>
            <div><strong>Hash:</strong> {node.hash?.substring(0, 10)}...</div>
            <div><strong>Author:</strong> {node.author?.name}</div>
            <div><strong>Date:</strong> {node.commitDate}</div>
            <div className={styles.message}>"{node.message}"</div>
          </div>
          <div className={styles.diffList}>
            {node.diffs?.map((d, i) => <DiffLine key={i} diff={d} />)}
          </div>
        </>
      ) : (
        <div className={styles.empty}>Нет данных (узел отсутствует)</div>
      )}
    </div>
  );
};

export const CommitDetailsPanel: React.FC<CommitDetailsPanelProps> = ({ studentNode, referenceNode }) => {
  if (!studentNode && !referenceNode) {
    return (
      <div className={styles.placeholder}>
        Выберите узел на графе для просмотра деталей
      </div>
    );
  }

  return (
    <div className={styles.panel}>
      <NodeInfo node={studentNode!} title="Студент" />
      <NodeInfo node={referenceNode!} title="Эталон" />
    </div>
  );
};
