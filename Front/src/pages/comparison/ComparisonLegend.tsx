import React from 'react';
import { SEVERITY } from '../../api/models/constants';
import styles from './ComparisonLegend.module.css';

interface LegendItemProps {
  severity: string;
  label: string;
  description: string;
}

const LegendItem: React.FC<LegendItemProps> = ({ severity, label, description }) => {
  return (
    <div className={styles.legendItem}>
      <div className={`${styles.colorBox} ${styles[`severity-${severity}`]}`}></div>
      <div>
        <span className={styles.label}>{label}</span>
        <span className={styles.description}>{description}</span>
      </div>
    </div>
  );
};

export const ComparisonLegend: React.FC = () => {
  const items = [
    { severity: SEVERITY.IDENTICAL, label: 'IDENTICAL', description: 'Полное совпадение' },
    { severity: SEVERITY.MODIFIED, label: 'MODIFIED', description: 'Изменен (diff)' },
    { severity: SEVERITY.EXTRA, label: 'EXTRA', description: 'Лишний узел' },
    { severity: SEVERITY.MOVABLE_STUDENT, label: 'MOVABLE (S)', description: 'Перемещен (Студент)' },
    { severity: SEVERITY.MOVABLE_REFERENCE, label: 'MOVABLE (R)', description: 'Перемещен (Эталон)' },
  ];

  return (
    <details className={styles.legendDetails} open>
      <summary className={styles.legendSummary}>Легенда статусов</summary>
      <div className={styles.legendContent}>
        {items.map(item => <LegendItem key={item.severity} {...item} />)}
      </div>
    </details>
  );
};
