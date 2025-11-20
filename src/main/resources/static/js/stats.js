// Statistics page JavaScript

const API_BASE = '';

// Get token from localStorage and shortCode from page (set by Thymeleaf)
let authToken = localStorage.getItem('token');
const shortCodeParam = typeof shortCode !== 'undefined' ? shortCode : '';

// Redirect to login if no token
if (!authToken) {
    window.location.href = '/login';
}

document.addEventListener('DOMContentLoaded', function() {
    if (shortCodeParam) {
        loadStatistics(shortCodeParam);
    } else {
        showError('No short code provided');
    }
});

async function loadStatistics(shortCode) {
    const statsContainer = document.getElementById('statsContainer');
    const errorMessage = document.getElementById('errorMessage');
    
    errorMessage.style.display = 'none';
    statsContainer.innerHTML = '<div class="loading">Loading statistics...</div>';
    
    try {
        const response = await fetch(`${API_BASE}/api/stats/${shortCode}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (!response.ok) {
            if (response.status === 401) {
                localStorage.removeItem('token');
                window.location.href = '/login';
                return;
            }
            if (response.status === 404) {
                throw new Error('URL not found');
            }
            throw new Error('Failed to load statistics');
        }
        
        const data = await response.json();
        displayStatistics(data);
    } catch (error) {
        showError(error.message || 'Failed to load statistics');
        statsContainer.innerHTML = '';
    }
}

function displayStatistics(data) {
    const statsContainer = document.getElementById('statsContainer');
    
    const createdAt = new Date(data.createdAt).toLocaleString();
    const expiresAt = data.expiresAt ? new Date(data.expiresAt).toLocaleString() : 'Never';
    
    statsContainer.innerHTML = `
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-label">Short Code</div>
                <div class="stat-value">${data.shortCode}</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Click Count</div>
                <div class="stat-value">${data.clickCount}</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Created At</div>
                <div class="stat-value" style="font-size: 1.25rem;">${createdAt}</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Expires At</div>
                <div class="stat-value" style="font-size: 1.25rem;">${expiresAt}</div>
            </div>
        </div>
        <div style="margin-top: 2rem;">
            <h3 style="margin-bottom: 1rem;">URL Details</h3>
            <div class="result-box">
                <div class="form-group" style="margin-bottom: 1rem;">
                    <label>Short URL</label>
                    <div class="result-url">
                        <input type="text" value="${data.shortUrl}" readonly class="url-output">
                        <button onclick="copyToClipboard('${data.shortUrl}')" class="btn btn-secondary">Copy</button>
                    </div>
                </div>
                <div class="form-group">
                    <label>Original URL</label>
                    <input type="text" value="${data.originalUrl}" readonly class="url-output">
                </div>
            </div>
        </div>
    `;
}

function copyToClipboard(text) {
    const input = document.createElement('input');
    input.value = text;
    document.body.appendChild(input);
    input.select();
    input.setSelectionRange(0, 99999);
    
    try {
        document.execCommand('copy');
        document.body.removeChild(input);
        
        const btn = event.target;
        const originalText = btn.textContent;
        btn.textContent = 'Copied!';
        btn.style.backgroundColor = '#10b981';
        
        setTimeout(() => {
            btn.textContent = originalText;
            btn.style.backgroundColor = '';
        }, 2000);
    } catch (err) {
        showError('Failed to copy to clipboard');
    }
}

function showError(message) {
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.textContent = message;
    errorMessage.style.display = 'block';
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    window.location.href = '/';
}

