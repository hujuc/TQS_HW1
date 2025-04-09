import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    // ramp up from 0 to 20 VUs over the next 5 seconds
    { duration: '5s', target: 20 },
    // run 20 VUs over the next 10 seconds
    { duration: '10s', target: 20 },
    // ramp down from 20 to 0 VUs over the next 5 seconds
    { duration: '5s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<1100'], // 95% of requests should be below 1.1s
    http_req_failed: ['rate<0.01'],    // Less than 1% of requests should fail
  },
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
      [`${endpoint} response time < 1.1s`]: (r) => r.timings.duration < 1100,
      [`${endpoint} body size < 1KB`]: (r) => r.body.length < 1024,
    });

    sleep(1);
  });
} 