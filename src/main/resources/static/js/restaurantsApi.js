const restaurantsApi = {
    baseUrl: '/api/restaurants',

    async getAll() {
        const response = await fetch(this.baseUrl);
        if (!response.ok) {
            throw new Error('Failed to fetch restaurants');
        }
        return await response.json();
    },

    async getById(id) {
        const response = await fetch(`${this.baseUrl}/${id}`);
        if (!response.ok) {
            throw new Error('Failed to fetch restaurant');
        }
        return await response.json();
    },

    async create(restaurant) {
        const response = await fetch(this.baseUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(restaurant)
        });
        if (!response.ok) {
            throw new Error('Failed to create restaurant');
        }
        return await response.json();
    },

    async update(id, restaurant) {
        const response = await fetch(`${this.baseUrl}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(restaurant)
        });
        if (!response.ok) {
            throw new Error('Failed to update restaurant');
        }
        return await response.json();
    },

    async delete(id) {
        const response = await fetch(`${this.baseUrl}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error('Failed to delete restaurant');
        }
    }
}; 