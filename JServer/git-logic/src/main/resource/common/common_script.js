/**
 * Common JavaScript logic for Git Graph Reports.
 */

const SEVERITY_COLORS = {
    'IDENTICAL': { 
        background: '#365939', 
        border: '#496c4b', 
        highlight: { background: '#496c4b', border: '#5da063' } 
    },
    'MODIFIED': { 
        background: '#5e5339', 
        border: '#80714a', 
        highlight: { background: '#80714a', border: '#a69664' } 
    },
    'EXTRA': { 
        background: '#593939', 
        border: '#804b4b', 
        highlight: { background: '#804b4b', border: '#b26b6b' } 
    },
    'MISSED': { 
        background: '#2b2b2b', 
        border: '#804b4b', 
        highlight: { background: '#3c3f41', border: '#b26b6b' } /* Opaque highlight */
    },
    'MOVABLE': { 
        background: '#384c67', 
        border: '#4b6a8e', 
        highlight: { background: '#4b6a8e', border: '#6b90b2' } 
    },
    'MOVABLE_STUDENT': { 
        background: '#384c67', 
        border: '#4b6a8e', 
        highlight: { background: '#4b6a8e', border: '#6b90b2' } 
    },
    'MOVABLE_REFERENCE': { 
        background: '#4e3867', 
        border: '#6a4b8e', 
        highlight: { background: '#6a4b8e', border: '#906bb2' } 
    }
};

const SEVERITY_NAMES = {
    'IDENTICAL': 'Идентичен',
    'MODIFIED': 'Изменен',
    'EXTRA': 'Лишний (нет в эталоне)',
    'MISSED': 'Пропущен (нет в работе)',
    'MOVABLE': 'Перемещен',
    'MOVABLE_STUDENT': 'Перемещен (студент)',
    'MOVABLE_REFERENCE': 'Перемещен (эталон)'
};

/**
 * Returns the color configuration for a given severity.
 */
function getSeverityColor(severity, graphType) {
    if (severity === 'MOVABLE' && graphType) {
        return SEVERITY_COLORS[graphType === 'student' ? 'MOVABLE_STUDENT' : 'MOVABLE_REFERENCE'];
    }
    return SEVERITY_COLORS[severity] || { background: '#3c3f41', border: '#4b4b4b' };
}

/**
 * Returns the localized name for a given severity.
 */
function getSeverityName(severity) {
    return SEVERITY_NAMES[severity] || severity;
}

/**
 * Returns a shortened hash (max 7 chars).
 */
function getShortHash(hash) {
    if (!hash) return '';
    // For merged nodes, shorten each part to keep the label manageable
    if (hash.includes('/')) {
        return hash.split('/').map(h => h.trim().substring(0, 4)).join(' / ');
    }
    return hash.substring(0, 7);
}

/**
 * Creates a rich HTML tooltip for a node as a DOM element.
 */
function createTooltip(node) {
    const div = document.createElement('div');
    const shortHash = getShortHash(node.hash);
    const msg = node.message.length > 50 ? node.message.substring(0, 47) + '...' : node.message;
    
    // Determine status color class using hash for logical matching against CompareResultDto
    let colorClass = `text-severity-${node.severity}`;
    if (node.severity === 'MOVABLE') {
        const studentHashes = (comparisonData.compare_result && comparisonData.compare_result.matched_hashes_1_to_2) ? Object.keys(comparisonData.compare_result.matched_hashes_1_to_2) : [];
        colorClass = studentHashes.includes(node.hash) ? 'text-severity-MOVABLE_STUDENT' : 'text-severity-MOVABLE_REFERENCE';
    }

    let html = `
        <div class="tooltip-header">${shortHash} | ${msg}</div>
        <div style="margin-bottom: 5px;"><strong>Статус:</strong> <span class="${colorClass}">${getSeverityName(node.severity)}</span></div>
    `;

    if (node.diffs && node.diffs.length > 0) {
        html += `<div class="tooltip-diff">`;
        const limit = 4;
        const displayDiffs = node.diffs.slice(0, limit);
        displayDiffs.forEach(d => {
            const val = d.value.length > 60 ? d.value.substring(0, 57) + '...' : d.value;
            const color = d.value.startsWith('+') ? '#5da063' : (d.value.startsWith('-') ? '#b26b6b' : '#a9b7c6');
            html += `<div style="color: ${color}; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${val.replace(/</g, '&lt;').replace(/>/g, '&gt;')}</div>`;
        });
        if (node.diffs.length > limit) {
            html += `<div style="color: #888; font-style: italic; margin-top: 2px;">... и еще ${node.diffs.length - limit} строк</div>`;
        }
        html += `</div>`;
    }
    
    div.innerHTML = html;
    return div;
}

/**
 * Custom renderer for nodes.
 */
function getCustomRenderer(severity) {
    return function({ ctx, x, y, state: { selected, hover }, style, label }) {
        const {
            color = '#97C2FC',      // заливка узла
            borderColor = '#2B7CE9',
            borderWidth = 1,
            size = 20,
            shadow = false,
            shadowColor = 'rgba(0,0,0,0.3)',
            shadowSize = 5,
            shadowX = 2,
            shadowY = 2
        } = style;

        const currentSeverity = (severity || '').trim().toUpperCase();

        return {
            drawNode() {
                if (typeof x !== 'number' || typeof y !== 'number' || !ctx?.beginPath) return;

                // 🔹 Тень (если включена)
                if (shadow) {
                    ctx.save();
                    ctx.shadowColor = shadowColor;
                    ctx.shadowBlur = shadowSize;
                    ctx.shadowOffsetX = shadowX;
                    ctx.shadowOffsetY = shadowY;
                }

                // 🔹 Основной круг
                ctx.beginPath();
                ctx.arc(x, y, size, 0, 2 * Math.PI, false);
                ctx.fillStyle = color;  // ← правильно: style.color, не background
                ctx.fill();

                // 🔹 Сброс тени для границы (чтобы не размывалась)
                if (shadow) {
                    ctx.shadowBlur = 0;
                    ctx.shadowOffsetX = 0;
                    ctx.shadowOffsetY = 0;
                }

                // 🔹 Граница
                ctx.strokeStyle = borderColor;
                ctx.lineWidth = borderWidth;
                ctx.stroke();

                // 🔹 Восстанавливаем контекст после тени
                if (shadow) ctx.restore();

                // 🔹 Красный крест для EXTRA / MISSED
                if (currentSeverity === 'EXTRA' || currentSeverity === 'MISSED') {
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

            drawExternalLabel() {
                if (label?.text && ctx?.fillText) {
                    ctx.font = style.font || '14px sans-serif';
                    ctx.textAlign = 'center';
                    ctx.textBaseline = 'middle';
                    ctx.fillStyle = style.fontFill || '#000';
                    ctx.fillText(label.text, x, y + size + 18);
                }
            },

            nodeDimensions: {
                width: size * 2,
                height: size * 2
            }
        };
    };
}


function nodeToId(hash, nodeList) {
    const node = nodeList.find(n => n.hash === hash || n.id === hash);
    return node ? node.id : hash;
}

/**
 * Toggles visibility of commit metadata.
 */
function toggleMetadata(button) {
    button.classList.toggle('active');
    const content = button.nextElementSibling;
    content.classList.toggle('show');
}

/**
 * Renders a list of diffs/labels with syntax highlighting.
 */
function renderDiffs(diffs) {
    if (!diffs || diffs.length === 0) return '';
    
    return `
        <ul class="diff-list">
            ${diffs.map(d => {
                const escapedValue = d.value.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
                let className = 'diff-line';
                
                // Add highlighting based on prefix
                if (escapedValue.startsWith('+')) className += ' diff-line-added';
                else if (escapedValue.startsWith('-')) className += ' diff-line-removed';
                
                // Add state class (CORRECT, MISSED, EXTRACT)
                if (d.state) className += ' state-' + d.state;
                
                return `<li class="${className}" title="Status: ${d.state || 'unknown'}">${escapedValue}</li>`;
            }).join('')}
        </ul>
    `;
}
