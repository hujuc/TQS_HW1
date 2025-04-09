document.addEventListener('DOMContentLoaded', () => {
    // Load initial data
    loadMeals();
    loadReservations();

    // Event listeners
    document.getElementById('addReservationBtn').addEventListener('click', () => {
        document.getElementById('modalTitle').textContent = 'New Reservation';
        document.getElementById('reservationForm').reset();
        document.getElementById('reservationId').value = '';
        document.getElementById('reservationModal').style.display = 'block';
    });

    document.querySelector('.close').addEventListener('click', () => {
        document.getElementById('reservationModal').style.display = 'none';
    });

    document.getElementById('cancelBtn').addEventListener('click', () => {
        document.getElementById('reservationModal').style.display = 'none';
    });

    document.getElementById('reservationForm').addEventListener('submit', handleReservationSubmit);

    // Filter event listeners
    document.getElementById('mealFilter').addEventListener('change', loadReservations);
    document.getElementById('dateFilter').addEventListener('change', loadReservations);
    document.getElementById('statusFilter').addEventListener('change', loadReservations);

    // Close modal when clicking outside
    window.addEventListener('click', (event) => {
        const modal = document.getElementById('reservationModal');
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
});

async function loadMeals() {
    try {
        const meals = await mealsApi.getAll();
        const mealSelect = document.getElementById('mealSelect');
        const mealFilter = document.getElementById('mealFilter');

        // Clear existing options
        mealSelect.innerHTML = '<option value="">Select a meal</option>';
        mealFilter.innerHTML = '<option value="">All Meals</option>';

        // Add meals to both selects
        meals.forEach(meal => {
            const option = document.createElement('option');
            option.value = meal.id;
            option.textContent = `${meal.name} - ${meal.restaurant.name} (${meal.date})`;
            mealSelect.appendChild(option.cloneNode(true));
            mealFilter.appendChild(option.cloneNode(true));
        });
    } catch (error) {
        showError('Failed to load meals: ' + error.message);
    }
}

async function loadReservations() {
    try {
        const mealId = document.getElementById('mealFilter').value;
        const date = document.getElementById('dateFilter').value;
        const status = document.getElementById('statusFilter').value;

        let reservations;
        if (mealId) {
            reservations = await reservationsApi.getByMeal(mealId);
        } else {
            reservations = await reservationsApi.getAll();
        }

        // Apply client-side filtering
        if (date) {
            reservations = reservations.filter(r => r.meal.date === date);
        }
        if (status) {
            reservations = reservations.filter(r => {
                if (status === 'active') return !r.isUsed;
                if (status === 'used') return r.isUsed;
                if (status === 'cancelled') return r.isCancelled;
                return true;
            });
        }

        displayReservations(reservations);
    } catch (error) {
        showError('Failed to load reservations: ' + error.message);
    }
}

function displayReservations(reservations) {
    const grid = document.getElementById('reservationsGrid');
    grid.innerHTML = '';

    if (reservations.length === 0) {
        grid.innerHTML = '<div class="no-results">No reservations found</div>';
        return;
    }

    reservations.forEach(reservation => {
        const card = document.createElement('div');
        card.className = 'reservation-card';
        card.innerHTML = `
            <div class="reservation-card-content">
                <div class="reservation-header">
                    <h3>${reservation.meal.name}</h3>
                    <span class="status ${reservation.status.toLowerCase()}">${reservation.status}</span>
                </div>
                <p><i class="fas fa-utensils"></i> ${reservation.meal.restaurant.name}</p>
                <p><i class="fas fa-user"></i> ${reservation.customerName}</p>
                <p><i class="fas fa-envelope"></i> ${reservation.customerEmail}</p>
                <p><i class="fas fa-users"></i> ${reservation.numberOfPeople} people</p>
                <p><i class="fas fa-calendar"></i> ${reservation.meal.date}</p>
                <p><i class="fas fa-clock"></i> ${reservation.reservationTime}</p>
                <p><i class="fas fa-tag"></i> Code: ${reservation.reservationCode}</p>
            </div>
            <div class="reservation-card-actions">
                ${!reservation.isUsed && reservation.status !== 'CANCELED' ? `
                    <button class="btn btn-primary" onclick="markAsUsed('${reservation.reservationCode}')">
                        <i class="fas fa-check"></i> Check In
                    </button>
                    <button class="btn btn-secondary" onclick="cancelReservation('${reservation.reservationCode}')">
                        <i class="fas fa-times"></i> Cancel
                    </button>
                ` : ''}
                <button class="btn btn-danger" onclick="deleteReservation('${reservation.reservationCode}')">
                    <i class="fas fa-trash"></i> Delete
                </button>
            </div>
        `;
        grid.appendChild(card);
    });
}

async function handleReservationSubmit(event) {
    event.preventDefault();

    const reservation = {
        meal: {
            id: document.getElementById('mealSelect').value
        },
        customerName: document.getElementById('customerName').value,
        customerEmail: document.getElementById('customerEmail').value,
        numberOfPeople: parseInt(document.getElementById('numberOfPeople').value)
    };

    try {
        await reservationsApi.create(reservation);
        document.getElementById('reservationModal').style.display = 'none';
        loadReservations();
        showMessage('Reservation created successfully');
    } catch (error) {
        showError('Failed to create reservation: ' + error.message);
    }
}

async function cancelReservation(code) {
    if (!confirm('Are you sure you want to cancel this reservation?')) {
        return;
    }

    try {
        await reservationsApi.cancel(code);
        loadReservations();
        showMessage('Reservation cancelled successfully');
    } catch (error) {
        showError('Failed to cancel reservation: ' + error.message);
    }
}

async function markAsUsed(code) {
    if (!confirm('Are you sure you want to check in this reservation?')) {
        return;
    }

    try {
        await checkInReservation(code);
    } catch (error) {
        showError('Failed to check in reservation: ' + error.message);
    }
}

async function checkInReservation(code) {
    try {
        const response = await fetch(`/api/reservations/${code}/use`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) {
            throw new Error('Failed to check in reservation');
        }
        const updatedReservation = await response.json();
        showMessage('Reservation checked in successfully', 'success');
        loadReservations(); // Refresh the list
    } catch (error) {
        console.error('Error checking in reservation:', error);
        showMessage('Failed to check in reservation: ' + error.message, 'error');
    }
}

async function deleteReservation(code) {
    if (!confirm('Are you sure you want to delete this reservation?')) {
        return;
    }

    try {
        await reservationsApi.delete(code);
        // Force a complete reload of the reservations list
        await loadReservations();
        showMessage('Reservation deleted successfully');
    } catch (error) {
        showError('Failed to delete reservation: ' + error.message);
    }
}

function showMessage(message, type = 'info') {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    messageDiv.textContent = message;
    document.querySelector('main').insertBefore(messageDiv, document.querySelector('.filters'));
    setTimeout(() => messageDiv.remove(), 3000);
}

function showError(message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error';
    errorDiv.textContent = message;
    document.querySelector('main').insertBefore(errorDiv, document.querySelector('.filters'));
    setTimeout(() => errorDiv.remove(), 3000);
} 