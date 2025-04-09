const weatherApi = {
    baseUrl: '/api/weather',

    async getForecast(date, location) {
        const response = await fetch(`${this.baseUrl}/forecast?date=${date}&location=${location}`);
        if (!response.ok) {
            throw new Error('Failed to fetch weather forecast');
        }
        return await response.json();
    },

    async getCacheStats() {
        const response = await fetch(`${this.baseUrl}/cache-stats`);
        if (!response.ok) {
            throw new Error('Failed to fetch cache stats');
        }
        return await response.json();
    }
}; 