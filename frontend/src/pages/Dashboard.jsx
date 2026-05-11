import { useEffect, useState } from "react";
import API from "../services/api";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from "recharts";
import { Link } from "react-router-dom";

function Dashboard() {
  const [stats, setStats] = useState({
    totalAssets: 0,
    activeAssets: 0,
    inactiveAssets: 0,
    highRiskAssets: 0,
  });

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    let baseStats = { totalAssets: 0, activeAssets: 0, inactiveAssets: 0, highRiskAssets: 0 };
    try {
      const response = await API.get("/assets/stats");
      baseStats = response.data;
    } catch (error) {
      console.error("Stats API error:", error);
    } finally {
      const localAssets = JSON.parse(localStorage.getItem("dummyAssets") || "[]");
      const activeCount = localAssets.filter(a => a.status === "ACTIVE").length;
      const inactiveCount = localAssets.filter(a => a.status === "INACTIVE").length;
      const highRiskCount = localAssets.filter(a => (a.riskScore || 0) >= 70).length;

      setStats({
        totalAssets: baseStats.totalAssets + localAssets.length,
        activeAssets: baseStats.activeAssets + activeCount,
        inactiveAssets: baseStats.inactiveAssets + inactiveCount,
        highRiskAssets: baseStats.highRiskAssets + highRiskCount
      });
      setLoading(false);
    }
  };

  const chartData = [
    { name: "Total", value: stats.totalAssets },
    { name: "Active", value: stats.activeAssets },
    { name: "Inactive", value: stats.inactiveAssets },
    { name: "High Risk", value: stats.highRiskAssets },
  ];

  const cards = [
    {
      title: "Total Assets",
      value: stats.totalAssets,
      icon: "🖥️",
      style: "from-blue-600 to-blue-800",
      text: "All registered assets",
    },
    {
      title: "Active Assets",
      value: stats.activeAssets,
      icon: "✅",
      style: "from-green-500 to-green-700",
      text: "Currently active",
    },
    {
      title: "Inactive Assets",
      value: stats.inactiveAssets,
      icon: "⏸️",
      style: "from-yellow-500 to-orange-600",
      text: "Need attention",
    },
    {
      title: "High Risk",
      value: stats.highRiskAssets,
      icon: "⚠️",
      style: "from-red-500 to-red-700",
      text: "Risk score 70+",
    },
  ];

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-100 flex items-center justify-center">
        <div className="bg-white px-8 py-6 rounded-2xl shadow-lg text-center">
          <div className="animate-spin h-10 w-10 border-4 border-blue-700 border-t-transparent rounded-full mx-auto mb-4"></div>
          <p className="text-lg font-semibold text-slate-700">
            Loading dashboard...
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-100">
      <div className="bg-gradient-to-r from-blue-900 to-blue-700 text-white px-6 py-8 shadow-md">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <h1 className="text-3xl md:text-4xl font-bold">
              Cyber Asset Dashboard
            </h1>
            <p className="text-blue-100 mt-2">
              Monitor assets, activity status, and high-risk systems.
            </p>
          </div>

          <Link
            to="/"
            className="bg-white text-blue-800 font-semibold px-5 py-3 rounded-xl shadow hover:bg-blue-50 transition text-center"
          >
            View Asset List
          </Link>
        </div>
      </div>

      <div className="max-w-7xl mx-auto p-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">
          {cards.map((card) => (
            <div
              key={card.title}
              className={`bg-gradient-to-br ${card.style} text-white rounded-2xl p-6 shadow-lg hover:scale-105 transition-transform duration-200`}
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm opacity-90">{card.title}</p>
                  <h2 className="text-4xl font-bold mt-2">{card.value}</h2>
                </div>

                <div className="text-4xl bg-white/20 h-14 w-14 flex items-center justify-center rounded-full">
                  {card.icon}
                </div>
              </div>

              <p className="text-sm opacity-90 mt-5">{card.text}</p>
            </div>
          ))}
        </div>

        <div className="bg-white rounded-2xl shadow-lg p-6 border border-slate-200">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-6">
            <div>
              <h2 className="text-2xl font-bold text-slate-800">
                Asset Summary Chart
              </h2>
              <p className="text-slate-500 text-sm mt-1">
                Visual overview of total, active, inactive, and high-risk assets.
              </p>
            </div>
          </div>

          <div className="h-80 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="value" fill="#1D4ED8" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="mt-6 bg-white rounded-2xl shadow p-5 border-l-4 border-blue-700">
          <h3 className="font-bold text-slate-800 mb-2">Status</h3>
          <p className="text-slate-600 text-sm">


          </p>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;