document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const restaurantId = urlParams.get('id');
    
    if (!restaurantId) {
        showError('Restaurant ID not provided');
        return;
    }

    loadRestaurantDetails(restaurantId);
    loadWeatherForecast(restaurantId);
    loadMeals(restaurantId);
});

async function loadRestaurantDetails(restaurantId) {
    try {
        const response = await fetch(`/api/restaurants/${restaurantId}`);
        if (!response.ok) {
            throw new Error('Failed to load restaurant details');
        }
        const restaurant = await response.json();
        displayRestaurantDetails(restaurant);
    } catch (error) {
        showError('Failed to load restaurant details: ' + error.message);
    }
}

function displayRestaurantDetails(restaurant) {
    document.getElementById('restaurant-name').textContent = restaurant.name;
    document.getElementById('restaurant-address').textContent = restaurant.location;
    document.getElementById('restaurant-phone').textContent = restaurant.phone || 'Not available';
    document.getElementById('restaurant-email').textContent = restaurant.email || 'Not available';
    document.getElementById('restaurant-details').style.display = 'block';
    document.getElementById('loading').style.display = 'none';
}

async function loadWeatherForecast(restaurantId) {
    try {
        const response = await fetch(`/api/restaurants/${restaurantId}`);
        if (!response.ok) {
            throw new Error('Failed to load restaurant location');
        }
        const restaurant = await response.json();
        
        const weatherResponse = await fetch(`/api/weather/forecast?location=${encodeURIComponent(restaurant.location)}`);
        if (!weatherResponse.ok) {
            throw new Error('Failed to load weather forecast');
        }
        const forecast = await weatherResponse.json();
        updateWeatherInfo(forecast);
    } catch (error) {
        console.error('Error loading weather forecast:', error);
        document.getElementById('weather-description').textContent = 'Weather forecast not available';
    }
}

function updateWeatherInfo(forecast) {
    document.getElementById('weather-description').textContent = forecast.description;
    document.getElementById('weather-temp').textContent = `${forecast.temperature}°C`;
    document.getElementById('weather-humidity').textContent = `${forecast.humidity}%`;
    document.getElementById('weather-wind').textContent = `${forecast.windSpeed} m/s`;
}

async function loadMeals(restaurantId) {
    try {
        const today = new Date().toISOString().split('T')[0];
        const response = await fetch(`/api/meals/restaurant/${restaurantId}/date/${today}`);
        if (!response.ok) {
            throw new Error('Failed to load meals');
        }
        const meals = await response.json();
        displayMeals(meals);
    } catch (error) {
        showError('Failed to load meals: ' + error.message);
    }
}

function displayMeals(meals) {
    const container = document.getElementById('meals-container');
    container.innerHTML = '';
    
    if (meals.length === 0) {
        container.innerHTML = '<p class="no-meals">No meals available for today</p>';
        return;
    }
    
    meals.forEach(meal => {
        const mealCard = createMealCard(meal);
        container.appendChild(mealCard);
    });
}

function createMealCard(meal) {
    const card = document.createElement('div');
    card.className = 'meal-card';
    
    card.innerHTML = `
        <h3>${meal.name}</h3>
        <p class="meal-description">${meal.description}</p>
        <div class="meal-details">
            <span class="meal-price">€${meal.price.toFixed(2)}</span>
            <span class="meal-type" style="display: none;">${meal.mealType}</span>
        </div>
        <button class="btn btn-primary" onclick="makeReservation(${meal.id})">Make Reservation</button>
    `;
    
    return card;
}

function showError(message) {
    const errorDiv = document.getElementById('error-message');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    document.getElementById('loading').style.display = 'none';
}

// Add event listeners for meal type filters
document.querySelectorAll('.meal-filters button').forEach(button => {
    button.addEventListener('click', function() {
        // Remove active class from all buttons
        document.querySelectorAll('.meal-filters button').forEach(btn => {
            btn.classList.remove('active');
        });
        
        // Add active class to clicked button
        this.classList.add('active');
        
        // Filter meals
        const type = this.dataset.type;
        filterMeals(type);
    });
});

function filterMeals(type) {
    const meals = document.querySelectorAll('.meal-card');
    meals.forEach(meal => {
        if (type === 'all') {
            meal.style.display = 'block';
        } else {
            const mealType = meal.querySelector('.meal-type').textContent.toLowerCase();
            meal.style.display = mealType === type ? 'block' : 'none';
        }
    });
} 