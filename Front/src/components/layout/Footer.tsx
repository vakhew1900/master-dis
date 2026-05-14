import React from 'react';
import styles from './Footer.module.css';

const Footer: React.FC = () => {
  return (
    <footer className={styles.mainFooter}>
      <div className={styles.footerContent}>
        <div className={styles.footerDesc}>
          <strong>CompareRepositoryTutor MCTS Analysis Tool</strong><br />
          Experimental system for Maximum Common Transitive Subgraph comparison in Git repositories.
          Designed for automated grading and structural analysis of version control history.
        </div>
        <div className={styles.footerCopy}>
          &copy; {new Date().getFullYear()} Chupinin Anton
        </div>
      </div>
    </footer>
  );
};

export default Footer;
