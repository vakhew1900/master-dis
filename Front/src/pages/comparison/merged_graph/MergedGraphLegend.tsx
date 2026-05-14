import React from 'react';
import { SEVERITY } from '../../../api/models/constants';
import styles from './MergedGraphLegend.module.css';

interface LegendItemProps {
  severity: string;
  label: string;
  description: string;
}

const LegendItem: React.FC<LegendItemProps> = ({ severity, label, description }) => {
  return (
    <div className={styles.legendItem}>
      <div className={`${styles.colorBox} severity-${severity}`}></div>
      <div>
        <span className={styles.label}>{label}</span>
        <span className={styles.description}>{description}</span>
      </div>
    </div>
  );
};

export const MergedGraphLegend: React.FC = () => {
  const items = [
    { severity: SEVERITY.IDENTICAL, label: 'IDENTICAL', description: 'Общий узел' },
    { severity: SEVERITY.MODIFIED, label: 'MODIFIED', description: 'Общий, но измененный' },
    { severity: SEVERITY.EXTRA, label: 'EXTRA', description: 'Только у студента' },
    { severity: 'MISSED', label: 'MISSED', description: 'Только в эталоне' },
    { severity: SEVERITY.MOVABLE_STUDENT, label: 'MOVABLE (S)', description: 'Перемещен (Студент)' },
    { severity: SEVERITY.MOVABLE_REFERENCE, label: 'MOVABLE (R)', description: 'Перемещен (Эталон)' },
  ];

  return (
    <details className={styles.legendDetails} open>
      <summary className={styles.legendSummary}>Легенда статусов (Объединенный граф)</summary>
      <div className={styles.legendContent}>
        {items.map(item => <LegendItem key={item.severity} {...item} />)}
      </div>
    </details>
  );
};
