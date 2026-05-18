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
    <div className={`${commonStyles.detailsPanel} ${styles.container}`}>
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

      <div style={{ marginBottom: '20px' }}>
        <label className={styles.label}>Метод сравнения</label>
        <select 
          value={method} 
          onChange={(e) => setMethod(e.target.value as 'TWO_GRAPH' | 'MERGED_GRAPH')}
          className={styles.select}
        >
          <option value="TWO_GRAPH">Two Graph Comparison (Side-by-side)</option>
          <option value="MERGED_GRAPH">Merged Graph Analysis</option>
        </select>
      </div>

      <button 
        onClick={handleCompare} 
        disabled={loading}
        className={styles.button}
      >
        {loading ? 'Обработка графов...' : 'Запустить сравнение'}
      </button>
    </div>
  );
};

export default ComparisonPage;
