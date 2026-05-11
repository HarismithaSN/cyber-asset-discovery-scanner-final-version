import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [form, setForm] = useState({ username: "", password: "" });
  const { loginUser } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const res = await axios.post("http://localhost:8080/api/auth/login", form);

      const token = res.data.token || res.data.jwt || res.data.accessToken;

      if (!token) {
        alert("Login success, but token not found in response");
        return;
      }

      loginUser(token);
      navigate("/");
    } catch (err) {
      console.error("Login error:", err);
      alert("Invalid login credentials.");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-100">
      <form
        onSubmit={handleSubmit}
        className="bg-white p-8 rounded-2xl shadow-lg w-96"
      >
        <h2 className="text-3xl font-bold mb-2 text-center text-blue-800">
          Cyber Scanner
        </h2>

        <p className="text-center text-gray-500 mb-6">
          Login to continue
        </p>

        <input
          type="text"
          placeholder="Username"
          value={form.username}
          className="w-full p-3 mb-4 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          onChange={(e) => setForm({ ...form, username: e.target.value })}
        />

        <input
          type="password"
          placeholder="Password"
          value={form.password}
          className="w-full p-3 mb-5 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          onChange={(e) => setForm({ ...form, password: e.target.value })}
        />

        <button
          type="submit"
          className="w-full bg-blue-700 text-white p-3 rounded-lg hover:bg-blue-800 transition font-semibold"
        >
          Login
        </button>

        <p className="text-xs text-center text-gray-400 mt-4">
          Mock token enabled until backend JWT is ready
        </p>
      </form>
    </div>
  );
};

export default Login;