document.addEventListener('DOMContentLoaded', () => {
    loadRestaurants();
    setupEventListeners();
});

let editingRestaurantId = null;

function setupEventListeners() {
    // Add Restaurant Button
    document.getElementById('addRestaurantBtn').addEventListener('click', () => {
        editingRestaurantId = null;
        document.getElementById('modalTitle').textContent = 'Add Restaurant';
        document.getElementById('restaurantForm').reset();
        document.getElementById('restaurantModal').style.display = 'block';
    });

    // Close Modal Button
    document.querySelector('.close').addEventListener('click', () => {
        document.getElementById('restaurantModal').style.display = 'none';
    });

    // Form Submission
    document.getElementById('restaurantForm').addEventListener('submit', handleRestaurantSubmit);

    // Close modal when clicking outside
    window.addEventListener('click', (event) => {
        const modal = document.getElementById('restaurantModal');
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
}

async function loadRestaurants() {
    try {
        const restaurants = await restaurantsApi.getAll();
        displayRestaurants(restaurants);
    } catch (error) {
        showError('Failed to load restaurants: ' + error.message);
    }
}

function displayRestaurants(restaurants) {
    const container = document.getElementById('restaurantsList');
    container.innerHTML = '';

    restaurants.forEach(restaurant => {
        const card = createRestaurantCard(restaurant);
        container.appendChild(card);
    });
}

function createRestaurantCard(restaurant) {
    const card = document.createElement('div');
    card.className = 'restaurant-card';
    
    const content = document.createElement('div');
    content.className = 'restaurant-card-content';
    
    content.innerHTML = `
        <h3>${restaurant.name}</h3>
        <p><i class="fas fa-map-marker-alt"></i> ${restaurant.location}</p>
        <p><i class="fas fa-users"></i> Capacity: ${restaurant.capacity}</p>
        <p><i class="fas fa-clock"></i> ${restaurant.operatingHours}</p>
    `;
    
    const actions = document.createElement('div');
    actions.className = 'restaurant-card-actions';
    
    const detailsBtn = document.createElement('button');
    detailsBtn.className = 'btn btn-details';
    detailsBtn.innerHTML = '<i class="fas fa-info-circle"></i> Details';
    detailsBtn.addEventListener('click', () => window.location.href = `/restaurant-details.html?id=${restaurant.id}`);
    
    const editBtn = document.createElement('button');
    editBtn.className = 'btn btn-primary';
    editBtn.innerHTML = '<i class="fas fa-edit"></i> Edit';
    editBtn.addEventListener('click', () => editRestaurant(restaurant));
    
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'btn btn-secondary';
    deleteBtn.innerHTML = '<i class="fas fa-trash"></i> Delete';
    deleteBtn.addEventListener('click', () => deleteRestaurant(restaurant.id));
    
    actions.appendChild(detailsBtn);
    actions.appendChild(editBtn);
    actions.appendChild(deleteBtn);
    
    card.appendChild(content);
    card.appendChild(actions);
    
    return card;
}

function editRestaurant(restaurant) {
    editingRestaurantId = restaurant.id;
    document.getElementById('modalTitle').textContent = 'Edit Restaurant';
    document.getElementById('name').value = restaurant.name;
    document.getElementById('location').value = restaurant.location;
    document.getElementById('capacity').value = restaurant.capacity;
    document.getElementById('operatingHours').value = restaurant.operatingHours;
    document.getElementById('restaurantModal').style.display = 'block';
}

async function deleteRestaurant(id) {
    if (!confirm('Are you sure you want to delete this restaurant?')) {
        return;
    }

    try {
        await restaurantsApi.delete(id);
        showMessage('Restaurant deleted successfully');
        loadRestaurants();
    } catch (error) {
        showError('Failed to delete restaurant: ' + error.message);
    }
}

async function handleRestaurantSubmit(event) {
    event.preventDefault();
    
    const restaurant = {
        name: document.getElementById('name').value,
        location: document.getElementById('location').value,
        capacity: parseInt(document.getElementById('capacity').value),
        operatingHours: document.getElementById('operatingHours').value
    };

    try {
        if (editingRestaurantId) {
            await restaurantsApi.update(editingRestaurantId, restaurant);
            showMessage('Restaurant updated successfully');
        } else {
            await restaurantsApi.create(restaurant);
            showMessage('Restaurant created successfully');
        }
        
        document.getElementById('restaurantModal').style.display = 'none';
        loadRestaurants();
    } catch (error) {
        showError('Failed to save restaurant: ' + error.message);
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

async function loadRestaurantMeals(restaurantId) {
    try {
        const today = new Date().toISOString().split('T')[0];
        const nextWeek = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
        
        const meals = await mealsApi.getByRestaurantAndDateRange(restaurantId, today, nextWeek);
        const restaurant = await restaurantsApi.getById(restaurantId);
        
        const content = document.getElementById('content');
        content.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h1>Refeições - ${restaurant.name}</h1>
                <div id="weather-info" class="weather-info">
                    <!-- Weather info will be loaded here -->
                </div>
            </div>
            <div class="row">
                ${meals.map(meal => `
                    <div class="col-md-4 mb-4">
                        <div class="card h-100">
                            <div class="card-body">
                                <h5 class="card-title">${meal.name}</h5>
                                <p class="card-text">
                                    <i class="bi bi-calendar"></i> ${formatDate(meal.date)}<br>
                                    <i class="bi bi-clock"></i> ${meal.type}<br>
                                    <i class="bi bi-currency-euro"></i> ${meal.price}
                                </p>
                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="badge ${meal.available ? 'bg-success' : 'bg-danger'}">
                                        ${meal.available ? 'Disponível' : 'Esgotado'}
                                    </span>
                                    <button class="btn btn-primary" 
                                            onclick="showReservationForm(${meal.id})"
                                            ${!meal.available ? 'disabled' : ''}>
                                        Reservar
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;

        // Load weather info
        const weatherInfo = document.getElementById('weather-info');
        const weather = await weatherApi.getForecast(today, 'Aveiro');
        weatherInfo.innerHTML = `
            <div class="d-flex align-items-center">
                <img src="${weather.iconUrl}" alt="Weather icon" class="weather-icon">
                <div>
                    <span class="temperature">${weather.temperature}°C</span><br>
                    <small class="description">${weather.description}</small>
                </div>
            </div>
        `;
    } catch (error) {
        showError('Erro ao carregar refeições do restaurante');
    }
} 