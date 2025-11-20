// Dashboard JavaScript

const API_BASE = '';

// Get token from localStorage (set after login)
let authToken = localStorage.getItem('token');
const currentUsername = typeof username !== 'undefined' ? username : localStorage.getItem('username');

// Redirect to login if no token
if (!authToken) {
    window.location.href = '/login';
}

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('shortenForm');
    if (form) {
        form.addEventListener('submit', handleShortenUrl);
    }
    
    // Load user's URLs (if we had an endpoint for this, we'd call it here)
    // For now, we'll just handle the shorten form
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
    
    if (!authToken) {
        showError('Please login to shorten URLs');
        window.location.href = '/login';
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/api/shorten`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ url: url })
        });
        
        if (!response.ok) {
            if (response.status === 401) {
                // Token expired or invalid
                localStorage.removeItem('token');
                window.location.href = '/login';
                return;
            }
            const error = await response.text();
            throw new Error(error || 'Failed to shorten URL');
        }
        
        const data = await response.json();
        displayResult(data);
        urlInput.value = '';
        
        // Optionally reload the page to show the new URL in the list
        // For now, we'll just show the result
    } catch (error) {
        showError(error.message || 'An error occurred while shortening the URL');
    }
}

function displayResult(data) {
    const resultContainer = document.getElementById('resultContainer');
    const shortUrlOutput = document.getElementById('shortUrlOutput');
    
    shortUrlOutput.value = data.shortUrl;
    resultContainer.style.display = 'block';
    
    // Scroll to result
    resultContainer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function copyToClipboard() {
    const shortUrlOutput = document.getElementById('shortUrlOutput');
    shortUrlOutput.select();
    shortUrlOutput.setSelectionRange(0, 99999);
    
    try {
        document.execCommand('copy');
        
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

