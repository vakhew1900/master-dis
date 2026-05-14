import React, { useMemo } from 'react';
import ReactMarkdown from 'react-markdown';
import rehypeSlug from 'rehype-slug';
import content from '../docs/home.md?raw';
import styles from './HomePage.module.css';

// Упрощенная версия без unified для локализации ошибки
const HomePage: React.FC = () => {
  const headings = useMemo(() => {
    // Временный ручной парсинг через Regex, чтобы избежать конфликтов с unified в браузере
    const matches = Array.from(content.matchAll(/^## (.*$)/gm));
    return matches.map((m, index) => ({
      text: m[1],
      id: m[1].toLowerCase().replace(/ /g, '-').replace(/[^\w-]/g, '')
    }));
  }, []);

  return (
    <div className={styles.container}>
      <aside className={styles.toc}>
        <h3 style={{ marginTop: 0 }}>Содержание</h3>
        <ul>
          {headings.map((h, i) => (
            <li key={i}>
              <a href={`#${h.id}`}>{h.text}</a>
            </li>
          ))}
        </ul>
      </aside>
      
      <div className={styles.markdownBody}>
        <ReactMarkdown rehypePlugins={[rehypeSlug]}>
          {content}
        </ReactMarkdown>
      </div>
    </div>
  );
};

export default HomePage;
