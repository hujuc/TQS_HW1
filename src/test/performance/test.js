import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 1,
  duration: '30s',
};

export default function () {
  const BASE_URL = 'http://localhost:8080';

  // Test multiple endpoints
  const endpoints = [
    '/api/meals',
    '/api/restaurants',
    '/api/weather/cache-stats',
  ];

  endpoints.forEach(endpoint => {
    const response = http.get(`${BASE_URL}${endpoint}`);
    
    check(response, {
      [`${endpoint} status is 200`]: (r) => r.status === 200,
      [`${endpoint} response time < 500ms`]: (r) => r.timings.duration < 500,
      [`${endpoint} body size < 1KB`]: (r) => r.body.length < 1024,
    });

    sleep(1);
  });
} 