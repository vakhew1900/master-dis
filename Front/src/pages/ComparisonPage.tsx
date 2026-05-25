import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Tabs, Tab, Box, TextField, MenuItem, Button } from '@mui/material';
import { graphService } from '../services/graphService';
import { FileField } from '../components/common/FileField';
import { REPORT_TYPES, USER_ROLES } from '../api/models/constants';
import { getComparisonResultState } from '../api/utils';
import { useAuth } from '../context/AuthContext';
import { labService } from '../services/labService';
import type { AdminTaskDto } from '../api/generated/model';
import commonStyles from '../styles/common.module.css';
import styles from './ComparisonPage.module.css';

const ComparisonPage: React.FC = () => {
  const { user } = useAuth();
  const [tab, setTab] = useState(0);
  const [tasks, setTasks] = useState<AdminTaskDto[]>([]);
  
  // State for standard comparison
  const [studentFile, setStudentFile] = useState<File | null>(null);
  const [referenceFile, setReferenceFile] = useState<File | null>(null);
  
  // State for task-based comparison
  const [taskId, setTaskId] = useState('');
  const [taskFile, setTaskFile] = useState<File | null>(null);
  
  const [method, setMethod] = useState<"TWO_GRAPH" | "MERGED_GRAPH">(REPORT_TYPES.TWO_GRAPH as any);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (user?.role === USER_ROLES.ADMIN) {
        loadTasks();
    }
  }, [user]);

  const loadTasks = async () => {
      // Assuming we need a way to fetch all tasks. 
      // If no direct API, we might need to iterate labs.
      // For now let's mock or use a known fetch method if exists.
      // Based on available labService, let's list labs first then tasks.
      const labs = await labService.getAllLabs();
      const allTasks = labs.flatMap(l => l.tasks || []);
      setTasks(allTasks);
  };

  const handleCompare = async () => {
    setLoading(true);
    try {
      let result;
      if (tab === 0) {
        if (!studentFile || !referenceFile) return;
        result = await graphService.compareFiles(referenceFile, studentFile, { reportType: method });
      } else {
        if (!taskFile || !taskId) return;
        result = await graphService.checkRepositoryByTaskId(parseInt(taskId), taskFile, { reportType: method });
      }
      
      navigate('/comparison-result', { state: getComparisonResultState(result) });
    } catch (error) {
        console.error('Comparison failed:', error);
    } finally {
        setLoading(false);
    }
  };

  return (
    <div className={`${commonStyles.detailsPanel} ${styles.container}`}>
      <h1 className={commonStyles.pageTitle}>Сравнение Git-репозиториев</h1>
      
      <Tabs value={tab} onChange={(_, newValue) => setTab(newValue)} sx={{ mb: 3 }}>
        <Tab label="Сравнение двух ZIP" />
        {user?.role === USER_ROLES.ADMIN && <Tab label="Проверка по заданию (Admin)" />}
      </Tabs>

      {tab === 0 ? (
        <Box className={styles.comparisonGrid}>
            <div className={styles.column}>
                <h3>Студенческий репозиторий (ZIP)</h3>
                <FileField label={studentFile?.name || "Выберите файл"} onChange={setStudentFile} />
            </div>
            <div className={styles.column}>
                <h3>Эталонный репозиторий (ZIP)</h3>
                <FileField label={referenceFile?.name || "Выберите файл"} onChange={setReferenceFile} />
            </div>
        </Box>
      ) : (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField select label="Задание" value={taskId} onChange={(e) => setTaskId(e.target.value)} fullWidth>
                {tasks.map(t => <MenuItem key={t.id} value={t.id}>{t.number} - {t.description}</MenuItem>)}
            </TextField>
            <FileField label={taskFile?.name || "Выберите решение студента (ZIP)"} onChange={setTaskFile} />
        </Box>
      )}

      <Box sx={{ mt: 3 }}>
        <label>Метод сравнения</label>
        <select value={method} onChange={(e) => setMethod(e.target.value as any)} className={styles.select}>
            <option value="TWO_GRAPH">Two Graph (Side-by-side)</option>
            <option value="MERGED_GRAPH">Merged Graph</option>
        </select>
      </Box>

      <Button variant="contained" onClick={handleCompare} disabled={loading} sx={{ mt: 3 }}>
        {loading ? 'Обработка...' : 'Запустить сравнение'}
      </Button>
    </div>
  );
};

export default ComparisonPage;
