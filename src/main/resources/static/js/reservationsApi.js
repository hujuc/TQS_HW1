const reservationsApi = {
    baseUrl: '/api/reservations',

    async getAll() {
        const response = await fetch(this.baseUrl);
        if (!response.ok) {
            throw new Error('Failed to fetch reservations');
        }
        return await response.json();
    },

    async getById(id) {
        const response = await fetch(`${this.baseUrl}/${id}`);
        if (!response.ok) {
            throw new Error('Failed to fetch reservation');
        }
        return await response.json();
    },

    async getByCode(code) {
        const response = await fetch(`${this.baseUrl}/${code}`);
        if (!response.ok) {
            throw new Error('Failed to fetch reservation');
        }
        return await response.json();
    },

    async getByMeal(mealId) {
        const response = await fetch(`${this.baseUrl}/meal/${mealId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch reservations');
        }
        return await response.json();
    },

    async getByCustomer(email) {
        const response = await fetch(`${this.baseUrl}/customer/${email}`);
        if (!response.ok) {
            throw new Error('Failed to fetch reservations');
        }
        return await response.json();
    },

    async create(reservation) {
        const response = await fetch(this.baseUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reservation)
        });
        if (!response.ok) {
            throw new Error('Failed to create reservation');
        }
        return await response.json();
    },

    async update(id, reservation) {
        const response = await fetch(`${this.baseUrl}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reservation)
        });
        if (!response.ok) {
            throw new Error('Failed to update reservation');
        }
        return await response.json();
    },

    async delete(code) {
        const response = await fetch(`${this.baseUrl}/${code}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error('Failed to delete reservation');
        }
        return true;
    },

    async checkAvailability(mealId) {
        const response = await fetch(`${this.baseUrl}/availability/${mealId}`);
        if (!response.ok) {
            throw new Error('Failed to check availability');
        }
        return await response.json();
    },

    async cancel(code) {
        const response = await fetch(`${this.baseUrl}/${code}/cancel`, {
            method: 'POST'
        });
        if (!response.ok) {
            throw new Error('Failed to cancel reservation');
        }
        return await response.json();
    },

    async markAsUsed(code) {
        const response = await fetch(`${this.baseUrl}/${code}/use`, {
            method: 'POST'
        });
        if (!response.ok) {
            throw new Error('Failed to mark reservation as used');
        }
        return await response.json();
    }
}; 