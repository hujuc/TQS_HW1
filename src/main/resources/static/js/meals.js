document.addEventListener('DOMContentLoaded', () => {
    loadRestaurants();
    loadMeals();
    setupEventListeners();
});

let editingMealId = null;

function setupEventListeners() {
    // Add Meal Button
    document.getElementById('addMealBtn').addEventListener('click', () => {
        editingMealId = null;
        document.getElementById('modalTitle').textContent = 'Add Meal';
        document.getElementById('mealForm').reset();
        document.getElementById('mealModal').style.display = 'block';
    });

    // Close Modal Buttons
    document.querySelectorAll('.close').forEach(closeBtn => {
        closeBtn.addEventListener('click', () => {
            document.getElementById('mealModal').style.display = 'none';
            document.getElementById('weatherModal').style.display = 'none';
        });
    });

    // Form Submission
    document.getElementById('mealForm').addEventListener('submit', handleMealSubmit);

    // Filters
    document.getElementById('restaurantFilter').addEventListener('change', loadMeals);
    document.getElementById('dateFilter').addEventListener('change', loadMeals);
    document.getElementById('typeFilter').addEventListener('change', loadMeals);

    // Close modals when clicking outside
    window.addEventListener('click', (event) => {
        const mealModal = document.getElementById('mealModal');
        const weatherModal = document.getElementById('weatherModal');
        if (event.target === mealModal) {
            mealModal.style.display = 'none';
        }
        if (event.target === weatherModal) {
            weatherModal.style.display = 'none';
        }
    });
}

async function loadRestaurants() {
    try {
        const restaurants = await restaurantsApi.getAll();
        const restaurantSelects = document.querySelectorAll('select[id="restaurant"], select[id="restaurantFilter"]');
        
        restaurantSelects.forEach(select => {
            select.innerHTML = '<option value="">All Restaurants</option>' +
                restaurants.map(restaurant => 
                    `<option value="${restaurant.id}">${restaurant.name}</option>`
                ).join('');
        });
    } catch (error) {
        showError('Failed to load restaurants: ' + error.message);
    }
}

async function loadMeals() {
    try {
        const restaurantId = document.getElementById('restaurantFilter').value;
        const date = document.getElementById('dateFilter').value;
        const type = document.getElementById('typeFilter').value;

        let meals;
        if (restaurantId && date && type) {
            meals = await mealsApi.getByRestaurantAndDateAndType(restaurantId, date, type);
        } else if (restaurantId && date) {
            const endDate = new Date(date);
            endDate.setDate(endDate.getDate() + 7);
            meals = await mealsApi.getByRestaurantAndDateRange(restaurantId, date, endDate.toISOString().split('T')[0]);
        } else {
            meals = await mealsApi.getAll();
        }

        // Apply client-side filtering for remaining filters
        if (meals && meals.length > 0) {
            if (type && !restaurantId && !date) {
                meals = meals.filter(meal => meal.mealType === type);
            }
            if (date && !restaurantId && !type) {
                meals = meals.filter(meal => meal.date === date);
            }
            if (restaurantId && !date && !type) {
                meals = meals.filter(meal => meal.restaurant.id === parseInt(restaurantId));
            }
        }

        displayMeals(meals);
    } catch (error) {
        showError('Failed to load meals: ' + error.message);
    }
}

function displayMeals(meals) {
    const container = document.getElementById('mealsList');
    container.innerHTML = '';

    meals.forEach(meal => {
        const card = createMealCard(meal);
        container.appendChild(card);
    });
}

function createMealCard(meal) {
    const card = document.createElement('div');
    card.className = 'meal-card';
    
    const content = document.createElement('div');
    content.className = 'meal-card-content';
    
    content.innerHTML = `
        <h3>${meal.name}</h3>
        <p class="restaurant-name"><i class="fas fa-utensils"></i> ${meal.restaurant.name}</p>
        <p class="description">${meal.description}</p>
        <p class="price"><i class="fas fa-euro-sign"></i> ${meal.price.toFixed(2)}</p>
        <p class="date"><i class="fas fa-calendar"></i> ${new Date(meal.date).toLocaleDateString()}</p>
        <p class="type"><i class="fas fa-clock"></i> ${meal.mealType.charAt(0).toUpperCase() + meal.mealType.slice(1)}</p>
    `;
    
    const actions = document.createElement('div');
    actions.className = 'meal-card-actions';
    
    const weatherBtn = document.createElement('button');
    weatherBtn.className = 'btn btn-info';
    weatherBtn.innerHTML = '<i class="fas fa-cloud-sun"></i> Weather';
    weatherBtn.addEventListener('click', () => showWeatherInfo(meal));
    
    const editBtn = document.createElement('button');
    editBtn.className = 'btn btn-primary';
    editBtn.innerHTML = '<i class="fas fa-edit"></i> Edit';
    editBtn.addEventListener('click', () => editMeal(meal));
    
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'btn btn-secondary';
    deleteBtn.innerHTML = '<i class="fas fa-trash"></i> Delete';
    deleteBtn.addEventListener('click', () => deleteMeal(meal.id));
    
    actions.appendChild(weatherBtn);
    actions.appendChild(editBtn);
    actions.appendChild(deleteBtn);
    
    card.appendChild(content);
    card.appendChild(actions);
    
    return card;
}

function editMeal(meal) {
    editingMealId = meal.id;
    document.getElementById('modalTitle').textContent = 'Edit Meal';
    
    document.getElementById('restaurant').value = meal.restaurant.id;
    document.getElementById('name').value = meal.name;
    document.getElementById('description').value = meal.description;
    document.getElementById('price').value = meal.price;
    document.getElementById('date').value = meal.date;
    document.getElementById('mealType').value = meal.mealType;
    
    document.getElementById('mealModal').style.display = 'block';
}

async function deleteMeal(id) {
    if (!confirm('Are you sure you want to delete this meal?')) {
        return;
    }

    try {
        await mealsApi.delete(id);
        showMessage('Meal deleted successfully');
        loadMeals();
    } catch (error) {
        showError('Failed to delete meal: ' + error.message);
    }
}

async function handleMealSubmit(event) {
    event.preventDefault();
    
    const meal = {
        restaurant: {
            id: parseInt(document.getElementById('restaurant').value)
        },
        name: document.getElementById('name').value,
        description: document.getElementById('description').value,
        price: parseFloat(document.getElementById('price').value),
        date: document.getElementById('date').value,
        mealType: document.getElementById('mealType').value
    };

    try {
        if (editingMealId) {
            await mealsApi.update(editingMealId, meal);
            showMessage('Meal updated successfully');
        } else {
            await mealsApi.create(meal);
            showMessage('Meal created successfully');
        }
        
        document.getElementById('mealModal').style.display = 'none';
        loadMeals();
    } catch (error) {
        showError('Failed to save meal: ' + error.message);
    }
}

function showMessage(message) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = message;
    messageDiv.style.display = 'block';
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 3000);
}

function showError(error) {
    const errorDiv = document.getElementById('error');
    errorDiv.textContent = error;
    errorDiv.style.display = 'block';
    setTimeout(() => {
        errorDiv.style.display = 'none';
    }, 3000);
}

async function showWeatherInfo(meal) {
    try {
        const weather = await weatherApi.getForecast(meal.date, meal.restaurant.location);
        const weatherInfo = document.getElementById('weatherInfo');
        
        weatherInfo.innerHTML = `
            <div class="weather-details">
                <p><i class="fas fa-map-marker-alt"></i> Location: ${weather.location}</p>
                <p><i class="fas fa-calendar"></i> Date: ${new Date(weather.date).toLocaleDateString()}</p>
                <p><i class="fas fa-temperature-high"></i> Temperature: ${weather.temperature}°C</p>
                <p><i class="fas fa-cloud"></i> Description: ${weather.description}</p>
                <p><i class="fas fa-tint"></i> Humidity: ${weather.humidity}%</p>
                <p><i class="fas fa-wind"></i> Wind Speed: ${weather.windSpeed} m/s</p>
            </div>
        `;
        
        document.getElementById('weatherModal').style.display = 'block';
    } catch (error) {
        showError('Failed to load weather information: ' + error.message);
    }
} 