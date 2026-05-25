import React, { useEffect, useRef, useState } from 'react';
import  { Network, DataSet} from 'vis-network/standalone';
import type { GitGraphDto, NodeDto } from '../../../api/generated/model';
import { SEVERITY, SEVERITY_COLORS } from '../../../api/models/constants';
import { GraphNodeTooltip } from '../../../components/common/GraphNodeTooltip';
import styles from './GraphCanvas.module.css';

interface GraphCanvasProps {
  data: GitGraphDto;
  title: string;
  onNodeSelect: (nodeId: string | null) => void;
  selectedNodeId: string | null;
}

interface CustomNodeRendererProps {
  ctx: CanvasRenderingContext2D;
  x: number;
  y: number;
  style: {
    size?: number;
    color: string | CanvasGradient | CanvasPattern;
    borderColor: string | CanvasGradient | CanvasPattern;
    borderWidth?: number;
  };
}


const getCustomRenderer = (severity: string) => {
  return ({ ctx, x, y, style }: CustomNodeRendererProps) => {
    const { size = 16, color, borderColor, borderWidth = 2 } = style;
    return {
      drawNode() {
        if (!ctx) return;
        ctx.save();
        ctx.shadowColor = 'rgba(0,0,0,0.4)';
        ctx.shadowBlur = 6;
        ctx.shadowOffsetX = 2;
        ctx.shadowOffsetY = 2;
        ctx.beginPath();
        ctx.arc(x, y, size, 0, 2 * Math.PI, false);
        ctx.fillStyle = color;
        ctx.fill();
        ctx.shadowColor = 'transparent';
        ctx.strokeStyle = borderColor;
        ctx.lineWidth = borderWidth;
        ctx.stroke();
        
        if (severity === SEVERITY.EXTRA || severity === 'MISSED') {
          ctx.beginPath();
          const crossSize = size * 0.7;
          ctx.moveTo(x - crossSize, y - crossSize);
          ctx.lineTo(x + crossSize, y + crossSize);
          ctx.moveTo(x + crossSize, y - crossSize);
          ctx.lineTo(x - crossSize, y + crossSize);
          ctx.strokeStyle = '#ff4d4d';
          ctx.lineWidth = 3;
          ctx.lineCap = 'round';
          ctx.stroke();
        }
        ctx.restore();
      },
      nodeDimensions: { width: size * 2, height: size * 2 }
    };
  };
};

export const GraphCanvas: React.FC<GraphCanvasProps> = ({ data, title, onNodeSelect, selectedNodeId }) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const networkRef = useRef<Network | null>(null);
  const [tooltip, setTooltip] = useState<{ node: NodeDto; x: number; y: number } | null>(null);

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
          color: { background: color.bg, border: color.border, highlight: { background: color.border, border: '#ffffff' } },
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
        zoomView: false,
      },
      autoResize: true
    };

    const network = new Network(containerRef.current, { nodes: visNodes, edges: visEdges }, options);
    networkRef.current = network;

    network.on('hoverNode', (params) => {
      const node = data.nodes?.find(n => n.id === params.node);
      if (node) {
        const pos = network.getPositions([params.node])[params.node];
        const domPos = network.canvasToDOM(pos);
        setTooltip({ node, x: domPos.x, y: domPos.y });
      }
    });

    network.on('blurNode', () => setTooltip(null));

    network.on('click', (params) => {
      onNodeSelect(params.nodes.length > 0 ? params.nodes[0] : null);
    });

    const handleKeyDown = (e: KeyboardEvent) => {
        if (e.key === 'Control' && networkRef.current) {
            networkRef.current.setOptions({ interaction: { zoomView: true } });
        }
    };
    const handleKeyUp = (e: KeyboardEvent) => {
        if (e.key === 'Control' && networkRef.current) {
            networkRef.current.setOptions({ interaction: { zoomView: false } });
        }
    };

    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);

    const timeoutId = setTimeout(() => { if (networkRef.current) networkRef.current.fit(); }, 150);

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('keyup', handleKeyUp);
      clearTimeout(timeoutId);
      network.destroy();
      networkRef.current = null;
    };
  }, [data, onNodeSelect, graphType]);

  useEffect(() => {
    if (networkRef.current) {
      if (selectedNodeId) networkRef.current.selectNodes([selectedNodeId]);
      else networkRef.current.unselectAll();
    }
  }, [selectedNodeId]);

  return (
    <div className={styles.wrapper} style={{ position: 'relative' }}>
      <h3 className={styles.title}>{title}</h3>
      <div ref={containerRef} className={styles.canvas} style={{ minHeight: '400px' }} />
      {tooltip && <GraphNodeTooltip node={tooltip.node} x={tooltip.x} y={tooltip.y} />}
    </div>
  );
};
