import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { graphService } from '../services/graphService';
import { FileField } from '../components/common/FileField';
import commonStyles from '../styles/common.module.css';
import styles from './ComparisonPage.module.css';

const ComparisonPage: React.FC = () => {
  const [studentFile, setStudentFile] = useState<File | null>(null);
  const [referenceFile, setReferenceFile] = useState<File | null>(null);
  const [method, setMethod] = useState<'TWO_GRAPH' | 'MERGED_GRAPH'>('TWO_GRAPH');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleCompare = async () => {
    // Временно отключено для тестов
    /*
    if (!studentFile || !referenceFile) {
        alert('Пожалуйста, выберите оба файла для сравнения');
        return;
    }
    */

    setLoading(true);
    try {
      const result = await graphService.compareGraphs(studentFile, referenceFile, method);
      
      // Переход на страницу результата с передачей данных в state
      navigate('/comparison-result', { state: { result } });
    } catch (error) {
      console.error('Comparison failed', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={`${commonStyles.detailsPanel} ${styles.container}`}>
      <h2 style={{ marginTop: 0 }}>Сравнение Git графов</h2>
      <p style={{ fontSize: '14px', color: '#8b949e', marginBottom: '24px' }}>
        Загрузите ZIP-архивы репозиториев для проведения MCTS анализа.
      </p>

      <FileField 
        label="Репозиторий студента" 
        onChange={setStudentFile} 
        fileName={studentFile?.name}
      />
      
      <FileField 
        label="Эталонный репозиторий" 
        onChange={setReferenceFile} 
        fileName={referenceFile?.name}
      />
      
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
