import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 50 },
        { duration: '1m', target: 100 },
        { duration: '20s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
    },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    // 1. Authenticate
    let loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
        email: "admin@example.com",
        password: "password123"
    }), {
        headers: { 'Content-Type': 'application/json' },
    });
    console.log('POST /auth/login status:', loginRes.status);
    console.log('POST /auth/login body:', loginRes.body);

    check(loginRes, {
        'auth success': (r) => r.status === 200,
    });

    let token = "";
    try {
        token = JSON.parse(loginRes.body).token;
    } catch (e) {
        console.error("Could not extract token from login response");
    }

    // 2. Fetch content with token if available
    let headers = token ? { Authorization: `Bearer ${token}` } : {};
    let res = http.get(`${BASE_URL}/api/content`, { headers });
    console.log('GET /api/content status:', res.status);
    console.log('GET /api/content body:', res.body);

    // 3. Adjust check to match your API's response structure
    check(res, {
        'status 200': (r) => r.status === 200,
        'has content': (r) => {
            try {
                // If the response is an array:
                // return Array.isArray(JSON.parse(r.body)) && JSON.parse(r.body).length > 0;

                // If the response is an object with a 'data' array:
                let obj = JSON.parse(r.body);
                if (Array.isArray(obj)) {
                    return obj.length > 0;
                } else if (Array.isArray(obj.data)) {
                    return obj.data.length > 0;
                }
                return false;
            } catch (e) {
                return false;
            }
        },
    });

    sleep(1);
}
