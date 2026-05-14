import React from 'react';

const Footer: React.FC = () => {
  return (
    <footer className="main-footer">
      <div className="footer-content">
        <div className="footer-desc">
          <strong>JServer MCTS Analysis Tool</strong><br />
          Experimental system for Maximum Common Transitive Subgraph comparison in Git repositories.
          Designed for automated grading and structural analysis of version control history.
        </div>
        <div className="footer-copy">
          &copy; {new Date().getFullYear()} Chupinin Anton
        </div>
      </div>
    </footer>
  );
};

export default Footer;
