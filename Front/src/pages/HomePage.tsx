import React, { useMemo } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkParse from 'remark-parse';
import { unified } from 'unified';
import { visit } from 'unist-util-visit';
import rehypeSlug from 'rehype-slug';
import content from '../docs/home.md?raw';
import styles from './HomePage.module.css';

interface Heading {
  text: string;
  id: string;
}

const HomePage: React.FC = () => {
  // Автоматический парсинг заголовков для навигации
  const headings = useMemo(() => {
    const ast = unified().use(remarkParse).parse(content);
    const foundHeadings: Heading[] = [];
    
    visit(ast, 'heading', (node: any) => {
      if (node.depth === 2) { // Берем только H2 для навигации
        const text = node.children.map((c: any) => c.value).join('');
        const id = text.toLowerCase().replace(/ /g, '-').replace(/[^\w-]/g, '');
        foundHeadings.push({ text, id });
      }
    });
    return foundHeadings;
  }, []);

  return (
    <div className={styles.container}>
      <aside className={styles.toc}>
        <h3 style={{ marginTop: 0 }}>Содержание</h3>
        <ul>
          {headings.map((h) => (
            <li key={h.id}>
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
