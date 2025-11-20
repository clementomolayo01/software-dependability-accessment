// Main application JavaScript for index page

const API_BASE = '';

// Get token from localStorage (if user is logged in)
let authToken = localStorage.getItem('token');

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('shortenForm');
    if (form) {
        form.addEventListener('submit', handleShortenUrl);
    }
});

async function handleShortenUrl(e) {
    e.preventDefault();
    
    const urlInput = document.getElementById('urlInput');
    const url = urlInput.value.trim();
    const resultContainer = document.getElementById('resultContainer');
    const errorMessage = document.getElementById('errorMessage');
    
    // Hide previous messages
    errorMessage.style.display = 'none';
    resultContainer.style.display = 'none';
    
    if (!url) {
        showError('Please enter a URL');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/api/shorten`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(authToken && { 'Authorization': `Bearer ${authToken}` })
            },
            body: JSON.stringify({ url: url })
        });
        
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Failed to shorten URL');
        }
        
        const data = await response.json();
        displayResult(data);
        urlInput.value = '';
    } catch (error) {
        showError(error.message || 'An error occurred while shortening the URL');
    }
}

function displayResult(data) {
    const resultContainer = document.getElementById('resultContainer');
    const shortUrlOutput = document.getElementById('shortUrlOutput');
    const originalUrl = document.getElementById('originalUrl');
    
    shortUrlOutput.value = data.shortUrl;
    originalUrl.textContent = data.originalUrl;
    resultContainer.style.display = 'block';
    
    // Scroll to result
    resultContainer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function copyToClipboard() {
    const shortUrlOutput = document.getElementById('shortUrlOutput');
    shortUrlOutput.select();
    shortUrlOutput.setSelectionRange(0, 99999); // For mobile devices
    
    try {
        document.execCommand('copy');
        
        // Show feedback
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

