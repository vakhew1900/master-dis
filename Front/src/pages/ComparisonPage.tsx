import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Tabs, Tab, Box, TextField, MenuItem, Button } from '@mui/material';
import { graphService } from '../services/graphService';
import { FileField } from '../components/common/FileField';
import { REPORT_TYPES, USER_ROLES } from '../api/models/constants';
import { getComparisonResultState } from '../api/utils';
import { useAuth } from '../context/AuthContext';
import { taskService } from '../services/taskService';
import type { TaskDto } from '../api/generated/model';
import commonStyles from '../styles/common.module.css';
import styles from './ComparisonPage.module.css';
import ReportTypeSelector from '../components/common/ReportTypeSelector';

const ComparisonPage: React.FC = () => {
  const { user } = useAuth();
  const [tab, setTab] = useState(0);
  const [tasks, setTasks] = useState<TaskDto[]>([]);
  
  // State for standard comparison
  const [studentFile, setStudentFile] = useState<File | null>(null);
  const [referenceFile, setReferenceFile] = useState<File | null>(null);
  
  // State for task-based comparison
  const [taskId, setTaskId] = useState('');
  const [taskFile, setTaskFile] = useState<File | null>(null);
  
  const [reportType, setReportType] = useState<string>(REPORT_TYPES.TWO_GRAPH);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const loadTasks = async () => {
      const allTasks = await taskService.getAllTasks();
      setTasks(allTasks);
  };

  useEffect(() => {
    // Both ADMIN and STUDENT should have access to tasks based on API definition
     // eslint-disable-next-line react-hooks/set-state-in-effect
    loadTasks();
  }, []);



  const handleCompare = async () => {
    setLoading(true);
    try {
      let result;
      if (tab === 0) {
        if (!studentFile || !referenceFile) return;
        result = await graphService.compareFiles(referenceFile, studentFile, { reportType: reportType as any });
      } else {
        if (!taskFile || !taskId) return;
        result = await graphService.checkRepositoryByTaskId(parseInt(taskId), taskFile, { reportType: reportType as any });
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
        {(user?.role === USER_ROLES.ADMIN || user?.role === USER_ROLES.STUDENT) && <Tab label="Проверка по заданию (Демо)" />}
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
        <ReportTypeSelector 
          value={reportType}
          onChange={setReportType}
          onCheck={handleCompare}
          loading={loading}
          buttonText="Запустить сравнение"
        />
      </Box>
    </div>
  );
};

export default ComparisonPage;
