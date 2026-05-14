import React from 'react';
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
    { severity: 'IDENTICAL', label: 'IDENTICAL', description: 'Полное совпадение' },
    { severity: 'MODIFIED', label: 'MODIFIED', description: 'Изменен (diff)' },
    { severity: 'EXTRA', label: 'EXTRA', description: 'Лишний узел' },
    { severity: 'MOVABLE_STUDENT', label: 'MOVABLE (S)', description: 'Перемещен (Студент)' },
    { severity: 'MOVABLE_REFERENCE', label: 'MOVABLE (R)', description: 'Перемещен (Эталон)' },
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
