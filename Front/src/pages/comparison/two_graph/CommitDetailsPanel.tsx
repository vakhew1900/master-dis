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

  return (
    <div className={`${styles.diffLine} ${className}`}>
      {diff.value}
    </div>
  );
};

const NodeInfo: React.FC<{ node: NodeDto; title: string }> = ({ node, title }) => {
  const [showMetadata, setShowMetadata] = useState(false);

  if (!node) {
    return (
      <div className={styles.column}>
        <h4>{title}</h4>
        <div className={styles.empty}>Нет данных (узел отсутствует)</div>
      </div>
    );
  }

  return (
    <div className={styles.column}>
      <h4>{title}</h4>
      <div className={styles.header}>
        <span className={styles.hash}>[{node.number}] {node.hash?.substring(0, 7)}</span>
        <span className={`${styles.statusBadge} ${styles[`severity-${node.severity}`]}`}>
          {node.severity}
        </span>
      </div>

      <button 
        className={styles.toggleBtn}
        onClick={() => setShowMetadata(!showMetadata)}
      >
        {showMetadata ? 'Скрыть детали' : 'Детали коммита'}
      </button>

      {showMetadata && (
        <div className={styles.meta}>
          <div><strong>Сообщение:</strong> <span className={styles.message}>{node.message}</span></div>
          <div><strong>Автор:</strong> {node.author?.name} ({node.author?.email})</div>
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
    return (
      <div className={styles.placeholder}>
        Выберите узел на графе для просмотра деталей
      </div>
    );
  }

  return (
    <div className={styles.panel}>
      <NodeInfo node={studentNode!} title="Репозиторий студента" />
      <NodeInfo node={referenceNode!} title="Эталонный репозиторий" />
    </div>
  );
};
