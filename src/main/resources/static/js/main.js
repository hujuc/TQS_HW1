// Common utility functions
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString();
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'EUR'
    }).format(amount);
}

// Error handling
window.onerror = function(message, source, lineno, colno, error) {
    console.error('Error:', message, 'at', source, 'line', lineno, 'column', colno);
    showError(message);
    return true;
};

// Navigation
document.addEventListener('DOMContentLoaded', () => {
    // Set active link in navigation
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-links a');
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });
});

// Theme handling
const themeToggle = document.getElementById('themeToggle');
if (themeToggle) {
    themeToggle.addEventListener('click', () => {
        document.body.classList.toggle('dark-theme');
        localStorage.setItem('theme', document.body.classList.contains('dark-theme') ? 'dark' : 'light');
    });

    // Load saved theme
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-theme');
    }
}

// Responsive navigation
const navToggle = document.getElementById('navToggle');
if (navToggle) {
    navToggle.addEventListener('click', () => {
        const navLinks = document.querySelector('.nav-links');
        navLinks.classList.toggle('active');
    });
}

// Form validation
function validateForm(form) {
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;

    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('error');
            isValid = false;
        } else {
            input.classList.remove('error');
        }
    });

    return isValid;
}

// Add error class to invalid inputs
document.addEventListener('input', (e) => {
    if (e.target.hasAttribute('required')) {
        if (!e.target.value.trim()) {
            e.target.classList.add('error');
        } else {
            e.target.classList.remove('error');
        }
    }
}); 