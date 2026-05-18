import React from 'react';
import type { NodeDto } from '../../api/generated/model';
import styles from './GraphNodeTooltip.module.css';

interface GraphNodeTooltipProps {
  node: NodeDto;
  x: number;
  y: number;
}

export const GraphNodeTooltip: React.FC<GraphNodeTooltipProps> = ({ node, x, y }) => {
  return (
    <div className={styles.tooltip} style={{ top: y + 20, left: x + 20 }}>
      <div className={styles.hash}>hash: {node.hash?.substring(0, 7)}</div>
      <div className={styles.message}>{node.message}</div>
      <div className={styles.meta}>
        <div><strong>Автор:</strong> {node.author?.name}</div>
        <div><strong>Дата:</strong> {node.commitDate}</div>
      </div>
    </div>
  );
};
