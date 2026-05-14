import React from 'react';
import styles from './FileField.module.css';

interface FileFieldProps {
  label: string;
  onChange: (file: File | null) => void;
  accept?: string;
  fileName?: string;
}

export const FileField: React.FC<FileFieldProps> = ({ label, onChange, accept = ".zip", fileName }) => {
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    onChange(file);
  };

  return (
    <div className={styles.wrapper}>
      <label className={styles.label}>{label}</label>
      <div className={styles.inputContainer}>
        <input 
          type="file" 
          id={`file-upload-${label}`}
          onChange={handleFileChange}
          accept={accept}
          className={styles.hiddenInput}
        />
        <label htmlFor={`file-upload-${label}`} className={styles.customButton}>
          {fileName ? 'Файл выбран' : 'Выберите ZIP архив'}
        </label>
        {fileName && <span className={styles.fileName}>{fileName}</span>}
      </div>
    </div>
  );
};
