import React from 'react';
import styles from './InputField.module.css';

interface InputFieldProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  type?: string;
  error?: boolean;
  helperText?: string | null;
}

export const InputField: React.FC<InputFieldProps> = ({ 
  label, 
  value, 
  onChange, 
  placeholder, 
  type = 'text',
  error,
  helperText 
}) => {
  return (
    <div className={styles.inputWrapper}>
      <label className={styles.label}>{label}</label>
      <input 
        type={type} 
        value={value} 
        onChange={(e) => onChange(e.target.value)}
        className={`${styles.input} ${error ? styles.inputError : ''}`}
        placeholder={placeholder}
      />
      {helperText && <span className={styles.helperText}>{helperText}</span>}
    </div>
  );
};
