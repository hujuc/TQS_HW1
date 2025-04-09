document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('reservationForm');
    const mealId = document.getElementById('mealId');
    const name = document.getElementById('name');
    const email = document.getElementById('email');
    const phone = document.getElementById('phone');
    const guests = document.getElementById('guests');
    const submitBtn = document.getElementById('submitBtn');
    
    // Form validation
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }
        
        submitBtn.disabled = true;
        submitBtn.textContent = 'Processing...';
        
        try {
            const reservation = {
                mealId: mealId.value,
                name: name.value,
                email: email.value,
                phone: phone.value,
                numberOfGuests: parseInt(guests.value)
            };
            
            const response = await reservationsApi.create(reservation);
            
            if (response) {
                showSuccessMessage('Reservation created successfully!');
                form.reset();
            }
        } catch (error) {
            showErrorMessage(error.message);
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Make Reservation';
        }
    });
    
    // Form validation
    function validateForm() {
        let isValid = true;
        
        if (!name.value.trim()) {
            showError(name, 'Name is required');
            isValid = false;
        } else {
            clearError(name);
        }
        
        if (!email.value.trim()) {
            showError(email, 'Email is required');
            isValid = false;
        } else if (!isValidEmail(email.value)) {
            showError(email, 'Please enter a valid email');
            isValid = false;
        } else {
            clearError(email);
        }
        
        if (!phone.value.trim()) {
            showError(phone, 'Phone is required');
            isValid = false;
        } else {
            clearError(phone);
        }
        
        if (!guests.value || parseInt(guests.value) < 1) {
            showError(guests, 'Number of guests must be at least 1');
            isValid = false;
        } else {
            clearError(guests);
        }
        
        return isValid;
    }
    
    // Helper functions
    function isValidEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }
    
    function showError(element, message) {
        const errorDiv = element.nextElementSibling;
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        element.classList.add('error');
    }
    
    function clearError(element) {
        const errorDiv = element.nextElementSibling;
        errorDiv.textContent = '';
        errorDiv.style.display = 'none';
        element.classList.remove('error');
    }
    
    function showSuccessMessage(message) {
        const successDiv = document.createElement('div');
        successDiv.className = 'success-message';
        successDiv.textContent = message;
        form.insertBefore(successDiv, form.firstChild);
        
        setTimeout(() => {
            successDiv.remove();
        }, 3000);
    }
    
    function showErrorMessage(message) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.textContent = message;
        form.insertBefore(errorDiv, form.firstChild);
        
        setTimeout(() => {
            errorDiv.remove();
        }, 3000);
    }
}); 