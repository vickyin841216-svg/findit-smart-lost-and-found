/**
 * FindIt – Smart Lost & Found System
 * Client-side dynamic behaviors, Chart.js integrations & verification code handlers
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Initialize Category Chart if on Dashboard
    initCategoryChart();

    // 2. Setup Image URL Preview in Report Form
    initImagePreview();

    // 3. Type Switcher Handler in Report Form
    initTypeSwitcher();
});

function initCategoryChart() {
    const canvas = document.getElementById('categoryDistributionChart');
    if (!canvas) return;

    try {
        const rawLabels = canvas.getAttribute('data-labels') || '';
        const rawValues = canvas.getAttribute('data-values') || '';
        const labels = rawLabels ? rawLabels.split(',') : [];
        const data = rawValues ? rawValues.split(',').map(Number) : [];

        if (labels.length === 0) return;

        const ctx = canvas.getContext('2d');
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: [
                        '#6366f1', '#06b6d4', '#10b981', '#f59e0b',
                        '#ec4899', '#8b5cf6', '#eab308', '#64748b'
                    ],
                    borderWidth: 2,
                    borderColor: '#121826'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            color: '#94a3b8',
                            font: { family: 'Plus Jakarta Sans', size: 12 },
                            padding: 16
                        }
                    }
                },
                cutout: '70%'
            }
        });
    } catch (e) {
        console.warn('Failed to render category chart:', e);
    }
}

function initImagePreview() {
    const input = document.getElementById('imageUrlInput');
    const preview = document.getElementById('imagePreviewContainer');
    const img = document.getElementById('previewImg');

    if (!input || !preview || !img) return;

    const updatePreview = () => {
        const url = input.value.trim();
        if (url) {
            img.src = url;
            preview.classList.remove('d-none');
        } else {
            preview.classList.add('d-none');
        }
    };

    input.addEventListener('input', updatePreview);
    updatePreview();
}

function initTypeSwitcher() {
    const typeRadios = document.querySelectorAll('input[name="type"]');
    const storageLocationWrapper = document.getElementById('storageLocationWrapper');
    const secretMarkLabel = document.getElementById('secretMarkLabel');
    const secretMarkHelp = document.getElementById('secretMarkHelp');

    if (!typeRadios.length) return;

    typeRadios.forEach(radio => {
        radio.addEventListener('change', () => {
            const isFound = radio.value === 'FOUND';
            if (storageLocationWrapper) {
                storageLocationWrapper.style.display = isFound ? 'block' : 'none';
            }
            if (secretMarkLabel) {
                secretMarkLabel.textContent = isFound ? 
                    'Physical Identifier / Serial Number (Stored Securely)' : 
                    'Serial Number / Secret Distinctive Marks';
            }
            if (secretMarkHelp) {
                secretMarkHelp.textContent = isFound ?
                    'Kept confidential to verify authentic claims during handover.' :
                    'Used by our AI algorithm to match with found item records.';
            }
        });
    });
}

function copyToClipboard(text, buttonElement) {
    if (!navigator.clipboard) {
        prompt('Copy code:', text);
        return;
    }
    navigator.clipboard.writeText(text).then(() => {
        const originalHtml = buttonElement.innerHTML;
        buttonElement.innerHTML = '<i class="fa-solid fa-check me-1"></i> Copied!';
        buttonElement.classList.add('btn-success');
        buttonElement.classList.remove('btn-outline-primary', 'btn-outline-light');
        setTimeout(() => {
            buttonElement.innerHTML = originalHtml;
            buttonElement.classList.remove('btn-success');
            buttonElement.classList.add('btn-outline-primary');
        }, 2000);
    }).catch(err => {
        console.error('Copy failed:', err);
    });
}
