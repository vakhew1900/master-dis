import React from 'react';
import { useLocation } from 'react-router-dom';

const ComparisonResultPage: React.FC = () => {
  const location = useLocation();
  const { result } = location.state || {};

  if (!result) {
    return <div className="container">Нет данных для отображения. Вернитесь к форме сравнения.</div>;
  }

  return (
    <div className="container">
      <div className="details-panel">
        <h2>Результат сравнения</h2>
        <pre>{JSON.stringify(result, null, 2)}</pre>
      </div>
    </div>
  );
};

export default ComparisonResultPage;
