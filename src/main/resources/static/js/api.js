const API_BASE_URL = '/api';

async function fetchApi(endpoint, options = {}) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        showError(error.message);
        throw error;
    }
}

// Restaurants API
const restaurantsApi = {
    getAll: () => fetchApi('/restaurants'),
    getById: (id) => fetchApi(`/restaurants/${id}`)
};

// Meals API
const mealsApi = {
    getAll: () => fetchApi('/meals'),
    getById: (id) => fetchApi(`/meals/${id}`),
    getByRestaurantAndDateRange: (restaurantId, startDate, endDate) => 
        fetchApi(`/meals/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`),
    getByRestaurantAndDateAndType: (restaurantId, date, mealType) =>
        fetchApi(`/meals/restaurant/${restaurantId}/date/${date}?mealType=${mealType}`)
};

// Reservations API
const reservationsApi = {
    getByCode: (code) => fetchApi(`/reservations/${code}`),
    getByMeal: (mealId) => fetchApi(`/reservations/meal/${mealId}`),
    getByCustomer: (email) => fetchApi(`/reservations/customer/${email}`),
    create: (reservation) => fetchApi('/reservations', {
        method: 'POST',
        body: JSON.stringify(reservation)
    }),
    cancel: (code) => fetchApi(`/reservations/${code}`, {
        method: 'DELETE'
    }),
    markAsUsed: (code) => fetchApi(`/reservations/${code}/use`, {
        method: 'PUT'
    })
};

// Weather API
const weatherApi = {
    getForecast: (date, location) => fetchApi(`/weather/forecast?date=${date}&location=${location}`)
};

// Utility functions
function showMessage(message) {
    const messageElement = document.getElementById('message');
    messageElement.textContent = message;
    messageElement.classList.remove('d-none');
    setTimeout(() => messageElement.classList.add('d-none'), 5000);
}

function showError(error) {
    const errorElement = document.getElementById('error');
    errorElement.textContent = error;
    errorElement.classList.remove('d-none');
    setTimeout(() => errorElement.classList.add('d-none'), 5000);
}

function formatDate(date) {
    return new Date(date).toLocaleDateString('pt-PT');
}

function formatTime(time) {
    return new Date(time).toLocaleTimeString('pt-PT', { hour: '2-digit', minute: '2-digit' });
} 