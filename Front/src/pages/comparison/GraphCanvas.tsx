import React, { useEffect, useRef } from 'react';
import { Network, DataSet } from 'vis-network/standalone';
import type { components } from '../../api/models/schema';
import { SEVERITY, SEVERITY_COLORS } from '../../api/models/constants';
import styles from './GraphCanvas.module.css';

type GitGraphDto = components["schemas"]["GitGraphDto"];
type NodeDto = components["schemas"]["NodeDto"];

interface GraphCanvasProps {
  data: GitGraphDto;
  title: string;
  onNodeSelect: (nodeId: string | null) => void;
  selectedNodeId: string | null;
}

/**
 * Custom renderer to draw a cross over EXTRA/MISSED nodes.
 */
const getCustomRenderer = (severity: string) => {
  return ({ ctx, x, y, style }: any) => {
    const { size = 16, color, borderColor, borderWidth = 2 } = style;
    
    return {
      drawNode() {
        if (!ctx) return;
        
        // Draw the base circle
        ctx.beginPath();
        ctx.arc(x, y, size, 0, 2 * Math.PI, false);
        ctx.fillStyle = color;
        ctx.fill();
        ctx.strokeStyle = borderColor;
        ctx.lineWidth = borderWidth;
        ctx.stroke();

        // Draw the cross for EXTRA/MISSED
        if (severity === SEVERITY.EXTRA || severity === 'MISSED') {
          ctx.beginPath();
          const crossSize = size * 0.8;
          ctx.moveTo(x - crossSize, y - crossSize);
          ctx.lineTo(x + crossSize, y + crossSize);
          ctx.moveTo(x + crossSize, y - crossSize);
          ctx.lineTo(x - crossSize, y + crossSize);
          ctx.strokeStyle = '#f44336';
          ctx.lineWidth = 3;
          ctx.lineCap = 'round';
          ctx.stroke();
        }
      },
      nodeDimensions: { width: size * 2, height: size * 2 }
    };
  };
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
        if (severityKey === SEVERITY.MOVABLE) {
            severityKey = graphType === 'student' ? SEVERITY.MOVABLE_STUDENT : SEVERITY.MOVABLE_REFERENCE;
        }
        
        const color = SEVERITY_COLORS[severityKey as keyof typeof SEVERITY_COLORS] || SEVERITY_COLORS.DEFAULT;
        
        return {
          id: node.id,
          label: node.hash?.substring(0, 7),
          color: {
            background: color.bg,
            border: color.border,
            highlight: { background: color.border, border: '#ffffff' }
          },
          font: { color: '#a9b7c6', size: 12 },
          shape: 'custom',
          ctxRenderer: getCustomRenderer(node.severity || ''),
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
          levelSeparation: 60,
          nodeSpacing: 60,
        }
      },
      physics: { enabled: false },
      interaction: { 
        hover: true,
        dragNodes: false,
        multiselect: false,
        zoomView: true, // Включаем зум обратно
      },
      autoResize: true
    };

    const network = new Network(containerRef.current, { nodes: visNodes, edges: visEdges }, options);
    networkRef.current = network;

    network.on('click', (params) => {
      onNodeSelect(params.nodes.length > 0 ? params.nodes[0] : null);
    });

    const timeoutId = setTimeout(() => {
        if (networkRef.current) networkRef.current.fit();
    }, 100);

    return () => {
      clearTimeout(timeoutId);
      if (networkRef.current) {
          networkRef.current.destroy();
          networkRef.current = null;
      }
    };
  }, [data, onNodeSelect, graphType]);

  useEffect(() => {
    if (networkRef.current) {
      if (selectedNodeId) networkRef.current.selectNodes([selectedNodeId]);
      else networkRef.current.unselectAll();
    }
  }, [selectedNodeId]);

  return (
    <div className={styles.wrapper}>
      <h3 className={styles.title}>{title}</h3>
      <div ref={containerRef} className={styles.canvas} style={{ minHeight: '400px' }} />
    </div>
  );
};
