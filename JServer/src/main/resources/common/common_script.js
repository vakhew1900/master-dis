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
        background: 'rgba(0,0,0,0)', 
        border: '#804b4b', 
        highlight: { background: 'rgba(128, 75, 75, 0.2)', border: '#b26b6b' } 
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
    'EXTRA': 'Лишний (отсутствует в эталоне)',
    'MISSED': 'Пропущен (отсутствует в работе)',
    'MOVABLE': 'Перемещен',
    'MOVABLE_STUDENT': 'Перемещен (у студента)',
    'MOVABLE_REFERENCE': 'Перемещен (в эталоне)'
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
    // Handle multiple hashes separated by '/' or ' / '
    if (hash.includes('/')) {
        return hash.split('/').map(h => h.trim().substring(0, 7)).join(' / ');
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
    
    let html = `
        <div class="tooltip-header">${shortHash} | ${msg}</div>
        <div style="margin-bottom: 5px;"><strong>Статус:</strong> ${getSeverityName(node.severity)}</div>
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
 * Custom renderer for nodes that handles ALL nodes correctly.
 */
function getCustomRenderer(severity) {
    return function({ ctx, x, y, state: { selected, hover }, style }) {
        const size = style.size;
        
        // 1. Draw base circle
        ctx.beginPath();
        ctx.arc(x, y, size, 0, 2 * Math.PI, false);
        
        if (severity === 'MISSED') {
            ctx.fillStyle = 'rgba(0,0,0,0)'; // Hollow
        } else {
            ctx.fillStyle = style.color; // Filled with status color
        }
        ctx.fill();
        
        // 2. Draw border
        ctx.strokeStyle = style.borderColor;
        ctx.lineWidth = style.borderWidth;
        if (selected || hover) {
            ctx.lineWidth = style.borderWidth * 2.5;
            ctx.strokeStyle = style.highlightColor || style.borderColor;
        }
        ctx.stroke();
        
        // 3. Draw Red Cross for EXTRA nodes
        if (severity === 'EXTRA') {
            ctx.beginPath();
            const crossSize = size * 0.7;
            ctx.moveTo(x - crossSize, y - crossSize);
            ctx.lineTo(x + crossSize, y + crossSize);
            ctx.moveTo(x + crossSize, y - crossSize);
            ctx.lineTo(x - crossSize, y + crossSize);
            ctx.strokeStyle = '#ff4444';
            ctx.lineWidth = 2.5;
            ctx.stroke();
        }
    };
}

/**
 * Maps a node hash or ID to the actual node ID in vis.network.
 */
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
