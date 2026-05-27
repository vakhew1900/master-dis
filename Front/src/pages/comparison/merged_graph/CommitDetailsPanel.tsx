import React, { useState } from 'react';
import type { NodeDto, DiffDto } from '../../../api/generated/model';
import styles from './CommitDetailsPanel.module.css';

interface CommitDetailsPanelProps {
  studentNode: NodeDto | null;
  referenceNode: NodeDto | null;
}

const DiffLine: React.FC<{ diff: DiffDto }> = ({ diff }) => {
  const isAdded = diff.value?.startsWith('+');
  const isRemoved = diff.value?.startsWith('-');
  const className = isAdded ? styles.added : (isRemoved ? styles.removed : '');
  return <div className={`${styles.diffLine} ${className}`}>{diff.value}</div>;
};

const NodeInfo: React.FC<{ node: NodeDto; title: string; isMovable?: boolean }> = ({ node, title, isMovable }) => {
  const [showMetadata, setShowMetadata] = useState(false);

  return (
    <div className={`${styles.column} ${isMovable ? styles.movableColumn : ''}`}>
      <h4 className={styles.columnTitle}>{title}</h4>
      <div className={styles.header}>
        <span className={styles.hash}>[{node.number}] {node.hash}</span>
        <span className={`${styles.statusBadge} severity-${node.severity}`}>
          {node.severity}
        </span>
      </div>

      <button className={styles.toggleBtn} onClick={() => setShowMetadata(!showMetadata)}>
        {showMetadata ? 'Скрыть детали' : 'Детали коммита'}
      </button>

      {showMetadata && (
        <div className={styles.meta}>
          <div><strong>Сообщение:</strong> <span className={styles.message}>{node.message}</span></div>
          <div><strong>Автор:</strong> {node.author?.name}</div>
          <div><strong>Дата:</strong> {node.commitDate}</div>
        </div>
      )}

      {node.diffs && node.diffs.length > 0 && (
        <div className={styles.diffSection}>
          <div className={styles.diffTitle}>Изменения:</div>
          <div className={styles.diffList}>
            {node.diffs.map((d, i) => <DiffLine key={i} diff={d} />)}
          </div>
        </div>
      )}
    </div>
  );
};

export const CommitDetailsPanel: React.FC<CommitDetailsPanelProps> = ({ studentNode, referenceNode }) => {
  if (!studentNode && !referenceNode) {
    return <div className={styles.placeholder}>Выберите узел на графе</div>;
  }

  // If both nodes are present, it's a MOVABLE pair comparison
  if (studentNode && referenceNode) {
    return (
      <div className={styles.panel}>
        <div className={styles.movableGrid}>
            <NodeInfo node={studentNode} title="Студент (Перемещен)" isMovable />
            <NodeInfo node={referenceNode} title="Эталон (Перемещен)" isMovable />
        </div>
      </div>
    );
  }

  return (
    <div className={styles.panel}>
      <NodeInfo node={(studentNode || referenceNode)!} title="Информация о коммите" />
    </div>
  );
};
