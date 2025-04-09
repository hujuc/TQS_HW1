import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

export const options = {
  stages: [
    // ramp up from 0 to 120 VUs over the next 30s
    { duration: '30s', target: 120 },
    // run 120 VUs over the next 30 seconds
    { duration: '30s', target: 120 },
    // ramp down from 120 to 0 VUs over the next 30 seconds
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<1100'], // 95% of requests should be below 1.1s
    http_req_failed: ['rate<0.01'],    // Less than 1% of requests should fail
    checks: ['rate>0.98'],             // 98% of checks should pass
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

export function handleSummary(data) {
  return {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
  };
} 