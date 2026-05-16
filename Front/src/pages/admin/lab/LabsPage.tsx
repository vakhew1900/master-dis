import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Menu,
  MenuItem,
  Box,
  CircularProgress
} from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import AddIcon from '@mui/icons-material/Add';
import { labService } from '../../../services/labService';
import type { LaboratoryWork } from '../../../services/labService';
import styles from './LabsPage.module.css';

const AdminLabsPage: React.FC = () => {
  const [labs, setLabs] = useState<LaboratoryWork[]>([]);
  const [loading, setLoading] = useState(true);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [selectedLabId, setSelectedLabId] = useState<number | null>(null);
  const navigate = useNavigate();

  const loadLabs = async () => {
    setLoading(true);
    try {
      const data = await labService.getAllLabs();
      setLabs(data);
    } catch (error) {
      console.error('Failed to load labs:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLabs();
  }, []);

  const handleMenuOpen = (event: React.MouseEvent<HTMLButtonElement>, id: number) => {
    event.stopPropagation();
    setAnchorEl(event.currentTarget);
    setSelectedLabId(id);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedLabId(null);
  };

  const handleEdit = () => {
    if (selectedLabId) {
      navigate(`/admin/labs/${selectedLabId}`);
    }
    handleMenuClose();
  };

  const handleDelete = async () => {
    if (selectedLabId) {
      try {
        await labService.deleteLab(selectedLabId);
        setLabs(labs.filter(lab => lab.id !== selectedLabId));
      } catch (error) {
        console.error('Failed to delete lab:', error);
      }
    }
    handleMenuClose();
  };

  const handleRowClick = (id: number) => {
    navigate(`/admin/labs/${id}`);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <Typography variant="h4" component="h1" gutterBottom>
          Лабораторные работы
        </Typography>

        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={() => navigate('/admin/labs/new')}
        >
          Создать работу
        </Button>
      </div>

      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 650 }} aria-label="таблица лабораторных работ">
          <TableHead>
            <TableRow>
              <TableCell width={80}>№</TableCell>
              <TableCell>Тема</TableCell>
              <TableCell>Описание</TableCell>
              <TableCell align="right" width={120}>Задания</TableCell>
              <TableCell align="right" width={80}>Действия</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {labs.map((lab) => (
              <TableRow
                key={lab.id}
                className={styles.tableRow}
                onClick={() => handleRowClick(lab.id!)}
              >
                <TableCell component="th" scope="row">
                  {lab.number}
                </TableCell>
                <TableCell>{lab.topic}</TableCell>
                <TableCell className={styles.descriptionCell}>
                  {lab.description}
                </TableCell>
                <TableCell align="right">{lab.tasks?.length || 0}</TableCell>
                <TableCell align="right">
                  <IconButton
                    className={styles.actionButton}
                    onClick={(e) => handleMenuOpen(e, lab.id!)}
                  >
                    <MoreVertIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={handleEdit}>Изменить</MenuItem>
        <MenuItem onClick={handleDelete} sx={{ color: 'error.main' }}>Удалить</MenuItem>
      </Menu>
    </div>
  );
};

export default AdminLabsPage;
