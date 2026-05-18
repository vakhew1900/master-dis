import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { graphService } from '../services/graphService';
import { FileField } from '../components/common/FileField';
import { REPORT_TYPES } from '../api/models/constants';
import commonStyles from '../styles/common.module.css';
import styles from './ComparisonPage.module.css';

const ComparisonPage: React.FC = () => {
  const [studentFile, setStudentFile] = useState<File | null>(null);
  const [referenceFile, setReferenceFile] = useState<File | null>(null);
  const [method, setMethod] = useState<"TWO_GRAPH" | "MERGED_GRAPH">(REPORT_TYPES.TWO_GRAPH as any);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleCompare = async () => {
    if (!studentFile || !referenceFile) {
        alert('Пожалуйста, выберите оба файла для сравнения');
        return;
    }

    setLoading(true);
    try {
      const result = await graphService.compareFiles(referenceFile, studentFile, { reportType: method });
      
      // Переход на страницу результата с передачей данных в state
      navigate('/comparison-result', { 
        state: { 
            result, 
            type: result.type === 'TwoGraphComparisonResultDto' ? 'two' : 'merged' 
        } 
      });
    } catch (error) {
        console.error('Comparison failed:', error);
    } finally {
        setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <h1 className={commonStyles.pageTitle}>Сравнение Git-репозиториев</h1>
      
      <div className={styles.comparisonGrid}>
        <div className={styles.column}>
          <h3>Студенческий репозиторий (ZIP)</h3>
          <FileField 
            label={studentFile ? studentFile.name : "Выберите файл студента"} 
            onChange={(file) => setStudentFile(file)} 
          />
        </div>

        <div className={styles.column}>
          <h3>Эталонный репозиторий (ZIP)</h3>
          <FileField 
            label={referenceFile ? referenceFile.name : "Выберите эталонный файл"} 
            onChange={(file) => setReferenceFile(file)} 
          />
        </div>
      </div>

      <div className={styles.options}>
        <h3>Тип отчета</h3>
        <div className={styles.methodToggle}>
          <button 
            className={method === 'TWO_GRAPH' ? styles.active : ''} 
            onClick={() => setMethod('TWO_GRAPH')}
          >
            Два графа
          </button>
          <button 
            className={method === 'MERGED_GRAPH' ? styles.active : ''} 
            onClick={() => setMethod('MERGED_GRAPH')}
          >
            Объединенный граф
          </button>
        </div>
      </div>

      <div className={styles.actions}>
        <button 
          className={commonStyles.primaryButton} 
          onClick={handleCompare}
          disabled={loading || !studentFile || !referenceFile}
        >
          {loading ? 'Обработка...' : 'Начать сравнение'}
        </button>
      </div>
    </div>
  );
};

export default ComparisonPage;
