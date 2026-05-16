import React, { useEffect, useRef } from 'react';
import { Network, DataSet } from 'vis-network/standalone';
import type { components } from '../../api/models/schema';
import styles from './GraphCanvas.module.css';

type GitGraphDto = components["schemas"]["GitGraphDto"];
type NodeDto = components["schemas"]["NodeDto"];

interface GraphCanvasProps {
  data: GitGraphDto;
  title: string;
  onNodeSelect: (nodeId: string | null) => void;
  selectedNodeId: string | null;
}

// Vis-network renders to Canvas, so it needs real hex/rgb colors, not CSS var()
const COLORS = {
  IDENTICAL: { bg: '#365939', border: '#496c4b' },
  MODIFIED: { bg: '#5e5339', border: '#80714a' },
  EXTRA: { bg: '#593939', border: '#804b4b' },
  MOVABLE_STUDENT: { bg: '#384c67', border: '#4b6a8e' },
  MOVABLE_REFERENCE: { bg: '#4e3867', border: '#6a4b8e' },
  DEFAULT: { bg: '#2b2b2b', border: '#3f3f3f' }
};

export const GraphCanvas: React.FC<GraphCanvasProps> = ({ data, title, onNodeSelect, selectedNodeId }) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const networkRef = useRef<Network | null>(null);

  const graphType = title.toLowerCase().includes('эталон') ? 'reference' : 'student';

  useEffect(() => {
    if (!containerRef.current || !data.nodes) return;

    const visNodes = new DataSet(
      data.nodes.map((node: NodeDto) => {
        let severityKey = node.severity || 'DEFAULT';
        if (severityKey === 'MOVABLE') {
            severityKey = graphType === 'student' ? 'MOVABLE_STUDENT' : 'MOVABLE_REFERENCE';
        }
        
        const color = COLORS[severityKey as keyof typeof COLORS] || COLORS.DEFAULT;
        
        return {
          id: node.id,
          label: node.hash?.substring(0, 7),
          color: {
            background: color.bg,
            border: color.border,
            highlight: { background: color.border, border: '#ffffff' }
          },
          font: { color: '#a9b7c6', size: 12 },
          shape: 'dot',
          size: 16
        };
      })
    );

    const visEdges = new DataSet(
      data.links?.map(link => ({
        from: link.source,
        to: link.target,
        arrows: { to: { enabled: true, scaleFactor: 0.5 } },
        color: { color: '#555', highlight: '#4b6eaf' }
      })) || []
    );

    const options = {
      layout: {
        hierarchical: {
          enabled: true,
          direction: 'DU',
          sortMethod: 'directed',
          levelSeparation: 80,
          nodeSpacing: 100,
        }
      },
      physics: { enabled: false },
      interaction: { 
        hover: true,
        dragNodes: false,
        multiselect: false
      },
      autoResize: true
    };

    const network = new Network(containerRef.current, { nodes: visNodes, edges: visEdges }, options);
    networkRef.current = network;

    network.on('click', (params) => {
      onNodeSelect(params.nodes.length > 0 ? params.nodes[0] : null);
    });

    // Гарантируем отрисовку после Layout браузера
    const timeoutId = setTimeout(() => {
        if (networkRef.current) {
            networkRef.current.fit();
        }
    }, 100);

    return () => {
      clearTimeout(timeoutId);
      if (networkRef.current) {
          networkRef.current.destroy();
          networkRef.current = null;
      }
    };
  }, [data, onNodeSelect]); // title убран из зависимостей, так как он константен

  useEffect(() => {
    if (networkRef.current) {
      if (selectedNodeId) {
        networkRef.current.selectNodes([selectedNodeId]);
      } else {
        networkRef.current.unselectAll();
      }
    }
  }, [selectedNodeId]);

  return (
    <div className={styles.wrapper}>
      <h3 className={styles.title}>{title}</h3>
      <div ref={containerRef} className={styles.canvas} style={{ minHeight: '400px' }} />
    </div>
  );
};
