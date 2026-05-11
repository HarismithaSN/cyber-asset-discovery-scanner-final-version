import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/api",
});

API.interceptors.request.use((config) => {
  const savedUser = localStorage.getItem("user");
  let token = null;
  if (savedUser) {
    try {
      const user = JSON.parse(savedUser);
      token = user.token;
    } catch (e) { }
  }

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default API;