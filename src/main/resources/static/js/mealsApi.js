const mealsApi = {
    baseUrl: '/api/meals',

    async getAll() {
        const response = await fetch(this.baseUrl);
        if (!response.ok) {
            throw new Error('Failed to fetch meals');
        }
        return await response.json();
    },

    async getById(id) {
        const response = await fetch(`${this.baseUrl}/${id}`);
        if (!response.ok) {
            throw new Error('Failed to fetch meal');
        }
        return await response.json();
    },

    async getByRestaurantAndDateRange(restaurantId, startDate, endDate) {
        const response = await fetch(`${this.baseUrl}/restaurant/${restaurantId}?startDate=${startDate}&endDate=${endDate}`);
        if (!response.ok) {
            throw new Error('Failed to fetch meals');
        }
        return await response.json();
    },

    async getByRestaurantAndDateAndType(restaurantId, date, type) {
        const response = await fetch(`${this.baseUrl}/restaurant/${restaurantId}/date/${date}/type/${type}`);
        if (!response.ok) {
            throw new Error('Failed to fetch meals');
        }
        return await response.json();
    },

    async create(meal) {
        const response = await fetch(this.baseUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(meal)
        });
        if (!response.ok) {
            throw new Error('Failed to create meal');
        }
        return await response.json();
    },

    async update(id, meal) {
        const response = await fetch(`${this.baseUrl}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(meal)
        });
        if (!response.ok) {
            throw new Error('Failed to update meal');
        }
        return await response.json();
    },

    async delete(id) {
        const response = await fetch(`${this.baseUrl}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error('Failed to delete meal');
        }
    }
}; 