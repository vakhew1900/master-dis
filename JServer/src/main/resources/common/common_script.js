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
        background: '#482c2c', 
        border: '#804b4b', 
        highlight: { background: '#804b4b', border: '#b26b6b' } 
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
        background: '#2b3a4f', 
        border: '#3a4e66', 
        highlight: { background: '#3a4e66', border: '#4b6a8e' } 
    }
};

const SEVERITY_NAMES = {
    'IDENTICAL': 'Идентичен',
    'MODIFIED': 'Изменен',
    'EXTRA': 'Лишний (у студента)',
    'MISSED': 'Пропущен (в эталоне)',
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
