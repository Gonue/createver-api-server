import axios from 'axios';

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
  }

const server = axios.create({
  baseURL: process.env.BASE_URL,
});

server.interceptors.request.use((config) => {
  const token = getCookie("codongs");
  if (token) {
    config.headers['Authorization'] = token;
  }
  return config;
});

export default server;