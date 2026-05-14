import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { graphService } from '../services/graphService';
import { InputField } from '../components/common/InputField';
import commonStyles from '../styles/common.module.css';
import styles from './ComparisonPage.module.css';

const ComparisonPage: React.FC = () => {
  const [repo1, setRepo1] = useState('');
  const [repo2, setRepo2] = useState('');
  const [method, setMethod] = useState<'two_graph' | 'merged'>('two_graph');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleCompare = async () => {
    setLoading(true);
    try {
      const response: any = await graphService.compareGraphs(repo1, repo2, method);
      
      navigate('/comparison-result', { state: { result: response.data } });
    } catch (error) {
      console.error('Comparison failed', error);
      alert('Ошибка при выполнении сравнения');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={`${commonStyles.detailsPanel} ${styles.container}`}>
      <h2 style={{ marginTop: 0 }}>Сравнение Git графов</h2>
      <InputField label="Репозиторий 1 (Студент)" value={repo1} onChange={setRepo1} placeholder="Путь к репозиторию 1" />
      <InputField label="Репозиторий 2 (Эталон)" value={repo2} onChange={setRepo2} placeholder="Путь к репозиторию 2" />
      
      <div style={{ marginBottom: '20px' }}>
        <label style={{ display: 'block', marginBottom: '8px', fontSize: '14px', fontWeight: 500 }}>Метод сравнения</label>
        <select 
          value={method} 
          onChange={(e) => setMethod(e.target.value as 'two_graph' | 'merged')}
          className={styles.select}
        >
          <option value="two_graph">Two Graph Comparison</option>
          <option value="merged">Merged Graph Analysis</option>
        </select>
      </div>

      <button 
        onClick={handleCompare} 
        disabled={loading}
        className={styles.button}
      >
        {loading ? 'Загрузка...' : 'Сравнить'}
      </button>
    </div>
  );
};

export default ComparisonPage;
