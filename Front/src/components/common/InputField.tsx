import React from 'react';
import styles from './InputField.module.css';

interface InputFieldProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  type?: string;
}

export const InputField: React.FC<InputFieldProps> = ({ label, value, onChange, placeholder, type = 'text' }) => {
  return (
    <div className={styles.inputWrapper}>
      <label className={styles.label}>{label}</label>
      <input 
        type={type} 
        value={value} 
        onChange={(e) => onChange(e.target.value)}
        className={styles.input}
        placeholder={placeholder}
      />
    </div>
  );
};
