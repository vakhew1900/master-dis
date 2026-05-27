import React, { useState } from 'react';
import { Box, Typography, Paper, IconButton } from '@mui/material';
import { CloudUpload as CloudUploadIcon, Close as CloseIcon } from '@mui/icons-material';

interface FileFieldProps {
  label: string;
  onChange: (file: File | null) => void;
  accept?: string;
  fileName?: string;
}

export const FileField: React.FC<FileFieldProps> = ({ label, onChange, accept = ".zip", fileName }) => {
  const [dragActive, setDragActive] = useState(false);

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      onChange(e.dataTransfer.files[0]);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    onChange(file);
  };

  return (
    <Box sx={{ mb: 2 }}>
      <Typography variant="caption" sx={{ mb: 0.5, fontWeight: 'bold', color: 'text.secondary', display: 'block', textTransform: 'uppercase', letterSpacing: 1 }}>
        {label}
      </Typography>
      
      <Paper
        variant="outlined"
        onDragEnter={handleDrag}
        onDragLeave={handleDrag}
        onDragOver={handleDrag}
        onDrop={handleDrop}
        sx={{
          border: '1px dashed',
          borderColor: dragActive ? 'primary.main' : 'divider',
          backgroundColor: dragActive ? 'action.hover' : 'background.paper',
          p: 1.5,
          textAlign: 'center',
          cursor: 'pointer',
          transition: 'all 0.2s ease-in-out',
          '&:hover': {
            borderColor: 'primary.main',
            backgroundColor: 'action.hover',
          },
          display: 'flex',
          flexDirection: 'row',
          alignItems: 'center',
          justifyContent: 'center',
          gap: 1.5,
          minHeight: '56px'
        }}
        component="label"
      >
        <input
          type="file"
          hidden
          accept={accept}
          onChange={handleChange}
        />
        
        <CloudUploadIcon sx={{ fontSize: 24, color: dragActive ? 'primary.main' : 'action.active' }} />
        
        <Box sx={{ display: 'flex', alignItems: 'center', flexGrow: 1, minWidth: 0 }}>
          {fileName ? (
            <>
              <Typography 
                variant="body2" 
                sx={{ 
                  fontWeight: 'medium', 
                  color: 'primary.main',
                  textDecoration: 'underline',
                  cursor: 'pointer',
                  overflow: 'hidden', 
                  textOverflow: 'ellipsis', 
                  whiteSpace: 'nowrap',
                  '&:hover': {
                    color: 'primary.dark',
                    textDecoration: 'none',
                    bgcolor: 'action.hover',
                    borderRadius: '4px',
                    px: 0.5,
                    mx: -0.5,
                  },
                  transition: 'all 0.1s'
                }}
                onClick={(e) => {
                  e.preventDefault(); // Предотвращаем открытие диалога выбора файла
                  e.stopPropagation(); // Останавливаем всплытие события к Paper (label)
                  
                  // Здесь в будущем будет логика скачивания
                  console.log("Download file:", fileName);
                  alert(`Запрос на скачивание файла: ${fileName}`);
                }}
              >
                {fileName}
              </Typography>
              <IconButton 
                size="small" 
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  onChange(null);
                }}
                sx={{ ml: 0.5 }}
              >
                <CloseIcon fontSize="small" />
              </IconButton>
            </>
          ) : (
            <Typography variant="body2" color="text.secondary">
              Перетащите файл или нажмите для выбора
            </Typography>
          )}
        </Box>

        {!fileName && (
          <Typography variant="caption" color="text.disabled" sx={{ whiteSpace: 'nowrap' }}>
            ({accept})
          </Typography>
        )}
      </Paper>
    </Box>
  );
};
