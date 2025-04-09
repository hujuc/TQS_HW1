document.addEventListener('DOMContentLoaded', () => {
    const navToggle = document.getElementById('navToggle');
    const navLinks = document.getElementById('navLinks');
    
    navToggle.addEventListener('click', () => {
        navLinks.classList.toggle('active');
    });
    
    // Close menu when clicking outside
    document.addEventListener('click', (event) => {
        if (!navToggle.contains(event.target) && !navLinks.contains(event.target)) {
            navLinks.classList.remove('active');
        }
    });
    
    // Close menu when clicking a link
    const links = navLinks.querySelectorAll('a');
    links.forEach(link => {
        link.addEventListener('click', () => {
            navLinks.classList.remove('active');
        });
    });
}); 