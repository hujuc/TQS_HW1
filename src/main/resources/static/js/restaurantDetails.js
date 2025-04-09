document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const restaurantId = urlParams.get('id');
    
    if (!restaurantId) {
        showError('Restaurant ID not provided');
        return;
    }

    // Get restaurant details
    fetch(`/api/restaurants/${restaurantId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Restaurant not found');
            }
            return response.json();
        })
        .then(restaurant => {
            displayRestaurantDetails(restaurant);
            // Use restaurant location if available, otherwise use Aveiro as default
            const location = restaurant.location || 'Aveiro,PT';
            return fetch(`/api/weather/forecast?location=${encodeURIComponent(location)}`);
        })
        .then(response => {
            if (!response.ok) {
                // If weather forecast fails, show a default message
                displayDefaultWeatherInfo();
                return null;
            }
            return response.json();
        })
        .then(weather => {
            if (weather) {
                displayWeatherInfo(weather);
            }
            return fetch(`/api/meals/restaurant/${restaurantId}/date/${new Date().toISOString().split('T')[0]}`);
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Meals not available');
            }
            return response.json();
        })
        .then(meals => {
            displayMeals(meals);
            document.getElementById('loading').style.display = 'none';
            document.getElementById('restaurant-details').style.display = 'block';
        })
        .catch(error => {
            showError(error.message);
            document.getElementById('loading').style.display = 'none';
        });

    // Setup meal type filters
    const filterButtons = document.querySelectorAll('.meal-filters button');
    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Remove active class from all buttons
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // Add active class to clicked button
            button.classList.add('active');
            
            const type = button.dataset.type;
            filterMeals(type);
        });
    });
});

function displayRestaurantDetails(restaurant) {
    document.getElementById('restaurant-name').textContent = restaurant.name;
    document.getElementById('restaurant-address').textContent = restaurant.address;
    document.getElementById('restaurant-phone').textContent = restaurant.phone;
    document.getElementById('restaurant-email').textContent = restaurant.email;
}

function displayWeatherInfo(weather) {
    const icon = document.getElementById('weather-icon');
    const temperature = document.getElementById('temperature');
    const description = document.getElementById('weather-description');

    // Set weather icon based on description
    const weatherIcon = getWeatherIcon(weather.description);
    icon.src = `/images/weather/${weatherIcon}.png`;
    icon.alt = weather.description;

    temperature.textContent = weather.temperature;
    description.textContent = weather.description;
}

function displayDefaultWeatherInfo() {
    const icon = document.getElementById('weather-icon');
    const temperature = document.getElementById('temperature');
    const description = document.getElementById('weather-description');

    icon.src = '/images/weather/default.png';
    icon.alt = 'Weather information not available';
    temperature.textContent = '--';
    description.textContent = 'Weather information not available';
}

function displayMeals(meals) {
    const container = document.getElementById('meals-container');
    container.innerHTML = '';

    if (meals.length === 0) {
        container.innerHTML = '<p class="no-meals">No meals available for today.</p>';
        return;
    }

    meals.forEach(meal => {
        const mealCard = createMealCard(meal);
        container.appendChild(mealCard);
    });
}

function createMealCard(meal) {
    const card = document.createElement('div');
    card.className = 'card meal-card';
    card.dataset.type = meal.mealType.toLowerCase();

    const content = `
        <div class="meal-card-badge ${meal.mealType.toLowerCase()}">${meal.mealType}</div>
        <h3>${meal.name}</h3>
        <p>${meal.description}</p>
        <div class="meal-card-details">
            <p><strong>Price:</strong> €${meal.price.toFixed(2)}</p>
        </div>
        <div class="meal-card-actions">
            <button class="btn btn-primary" onclick="makeReservation(${meal.id})">Make Reservation</button>
        </div>
    `;

    card.innerHTML = content;
    return card;
}

function filterMeals(type) {
    const meals = document.querySelectorAll('.meal-card');
    meals.forEach(meal => {
        if (type === 'all' || meal.dataset.type === type) {
            meal.style.display = 'block';
        } else {
            meal.style.display = 'none';
        }
    });
}

function getWeatherIcon(description) {
    const desc = description.toLowerCase();
    if (desc.includes('rain')) return 'rain';
    if (desc.includes('cloud')) return 'clouds';
    if (desc.includes('sun') || desc.includes('clear')) return 'sun';
    if (desc.includes('snow')) return 'snow';
    return 'default';
}

function makeReservation(mealId) {
    window.location.href = `/reservation-form.html?mealId=${mealId}`;
}

function showError(message) {
    const errorDiv = document.getElementById('error-message');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
} 