import { useEffect, useState } from "react";
import API from "../services/api";
import { Link } from "react-router-dom";
import { SkeletonAssetList } from "../components/SkeletonLoader";

function AssetList() {
  const [assets, setAssets] = useState([]);

  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [status, setStatus] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(1);

  const size = 2;

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
      setPage(0);
    }, 500);

    return () => clearTimeout(timer);
  }, [search]);

  const fetchAssets = async () => {
    try {
      setLoading(true);

      const res = await API.get("/assets/search", {
        params: {
          q: debouncedSearch,
          status,
          startDate,
          endDate,
          page,
          size,
        },
      });

      const serverAssets = res.data.content || [];
      const localAssets = JSON.parse(localStorage.getItem("dummyAssets") || "[]");

      const filteredLocal = localAssets.filter(asset => {
        if (debouncedSearch && !asset.assetName.toLowerCase().includes(debouncedSearch.toLowerCase())) return false;
        if (status && asset.status !== status) return false;
        return true;
      });

      setAssets([...serverAssets, ...filteredLocal]);
      setTotalPages(res.data.totalPages || 1);
    } catch (err) {
      console.error("Error fetching assets:", err);
      const localAssets = JSON.parse(localStorage.getItem("dummyAssets") || "[]");
      const filteredLocal = localAssets.filter(asset => {
        if (debouncedSearch && !asset.assetName.toLowerCase().includes(debouncedSearch.toLowerCase())) return false;
        if (status && asset.status !== status) return false;
        return true;
      });
      setAssets(filteredLocal);
      setTotalPages(1);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAssets();
  }, [debouncedSearch, status, startDate, endDate, page]);

  const resetFilters = () => {
    setSearch("");
    setDebouncedSearch("");
    setStatus("");
    setStartDate("");
    setEndDate("");
    setPage(0);
  };

  const deleteAsset = async (id) => {
    const confirmDelete = window.confirm("Are you sure you want to delete this asset?");
    if (!confirmDelete) return;

    if (String(id).startsWith("demo-")) {
      const localAssets = JSON.parse(localStorage.getItem("dummyAssets") || "[]");
      const updated = localAssets.filter(a => a.id !== id);
      localStorage.setItem("dummyAssets", JSON.stringify(updated));
      alert("Asset deleted! (Demo Mode)");
      fetchAssets();
      return;
    }

    try {
      await API.delete(`/assets/${id}`);
      alert("Asset deleted!");

      if (assets.length === 1 && page > 0) {
        setPage(page - 1);
      } else {
        fetchAssets();
      }
    } catch (err) {
      console.error("Delete error:", err);
      alert("Delete failed!");
    }
  };

  const getStatusBadge = (assetStatus) => {
    if (assetStatus === "ACTIVE") {
      return "bg-green-100 text-green-700 border-green-200";
    }

    if (assetStatus === "INACTIVE") {
      return "bg-gray-100 text-gray-700 border-gray-200";
    }

    if (assetStatus === "RISKY") {
      return "bg-red-100 text-red-700 border-red-200";
    }

    return "bg-blue-100 text-blue-700 border-blue-200";
  };

  const getRiskBadge = (score) => {
    if (score >= 70) return "bg-red-100 text-red-700";
    if (score >= 40) return "bg-yellow-100 text-yellow-700";
    return "bg-green-100 text-green-700";
  };

  return (
    <div className="min-h-screen bg-slate-100">
      <div className="bg-gradient-to-r from-primary to-blue-900 text-white px-6 py-8 shadow"
        style={{ backgroundColor: '#1B4F8A' }}>
        <div className="max-w-6xl mx-auto flex flex-col md:flex-row justify-between md:items-center gap-4">
          <div>
            <h1 className="text-3xl font-bold font-sans">Asset List</h1>
            <p className="text-blue-100 mt-1">
              Search, filter and manage discovered cyber assets
            </p>
          </div>

          <div className="flex flex-wrap gap-3">
            <Link
              to="/dashboard"
              className="bg-purple-600 text-white px-4 py-2 rounded-lg hover:bg-purple-700 shadow min-h-touch"
            >
              Dashboard
            </Link>

            <Link
              to="/add"
              className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 min-h-touch"
            >
              + Add Asset
            </Link>
          </div>
        </div>
      </div>

      <div className="max-w-6xl mx-auto p-[8px]">
        <div className="bg-white rounded-2xl shadow p-5 mb-6 border border-slate-200">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-3 mb-4">
            <div>
              <h2 className="text-xl font-bold text-primary" style={{ color: '#1B4F8A' }}>
                Search & Filters
              </h2>
              <p className="text-sm text-gray-500">
                Debounced search, status filter and date range filter
              </p>
            </div>

            <button
              onClick={resetFilters}
              className="bg-slate-800 text-white px-4 py-2 rounded-lg hover:bg-slate-900 min-h-touch"
            >
              Reset Filters
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-[8px]">
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">
                Search
              </label>
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search name, type, IP..."
                className="w-full border border-slate-300 rounded-lg px-4 py-2 outline-none focus:ring-2 focus:ring-blue-500 min-h-touch"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">
                Status
              </label>
              <select
                value={status}
                onChange={(e) => {
                  setStatus(e.target.value);
                  setPage(0);
                }}
                className="w-full border border-slate-300 rounded-lg px-4 py-2 outline-none focus:ring-2 focus:ring-blue-500 min-h-touch"
              >
                <option value="">All Status</option>
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
                <option value="RISKY">RISKY</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">
                Start Date
              </label>
              <input
                type="date"
                value={startDate}
                onChange={(e) => {
                  setStartDate(e.target.value);
                  setPage(0);
                }}
                className="w-full border border-slate-300 rounded-lg px-4 py-2 outline-none focus:ring-2 focus:ring-blue-500 min-h-touch"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">
                End Date
              </label>
              <input
                type="date"
                value={endDate}
                onChange={(e) => {
                  setEndDate(e.target.value);
                  setPage(0);
                }}
                className="w-full border border-slate-300 rounded-lg px-4 py-2 outline-none focus:ring-2 focus:ring-blue-500 min-h-touch"
              />
            </div>
          </div>
        </div>

        {loading ? (
          <div className="bg-white rounded-xl shadow p-8">
            <SkeletonAssetList />
          </div>
        ) : assets.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {assets.map((asset) => (
              <div
                key={asset.id}
                className="bg-white rounded-2xl shadow p-6 border border-slate-200 hover:shadow-lg transition"
              >
                <div className="flex justify-between items-start gap-3 mb-4">
                  <div>
                    <h2 className="text-xl font-bold text-blue-900">
                      {asset.assetName || asset.name}
                    </h2>
                    <p className="text-sm text-gray-500">Asset ID: {asset.id}</p>
                  </div>

                  <span
                    className={`px-3 py-1 rounded-full text-xs font-bold border ${getStatusBadge(
                      asset.status
                    )}`}
                  >
                    {asset.status || "N/A"}
                  </span>
                </div>

                <div className="space-y-2">
                  <p className="text-gray-700">
                    <strong>Type:</strong> {asset.assetType || asset.type}
                  </p>

                  <p className="text-gray-700">
                    <strong>IP Address:</strong> {asset.ipAddress}
                  </p>

                  <p className="text-gray-700">
                    <strong>Risk Score:</strong>{" "}
                    <span
                      className={`px-2 py-1 rounded font-bold ${getRiskBadge(
                        asset.riskScore ?? 0
                      )}`}
                    >
                      {asset.riskScore ?? 0}
                    </span>
                  </p>
                </div>

                <div className="mt-5 flex flex-wrap gap-[8px]">
                  <Link
                    to={`/assets/${asset.id}`}
                    className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 min-h-touch"
                  >
                    View
                  </Link>

                  <Link
                    to={`/edit/${asset.id}`}
                    className="bg-yellow-500 text-white px-4 py-2 rounded-lg hover:bg-yellow-600 min-h-touch"
                  >
                    Edit
                  </Link>

                  <button
                    onClick={() => deleteAsset(asset.id)}
                    className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 min-h-touch"
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center p-10 bg-white rounded-xl shadow border border-slate-200">
            <div className="mb-4">
              <p className="text-6xl mb-3">📦</p>
              <p className="text-gray-700 font-semibold text-lg mb-2">No assets found</p>
              <p className="text-gray-500 text-sm">
                {debouncedSearch || status || startDate || endDate
                  ? "Try adjusting your filters"
                  : "Create your first asset to get started"}
              </p>
            </div>
            <Link
              to="/add"
              className="bg-green-600 text-white px-5 py-2 rounded-lg hover:bg-green-700 inline-block mt-4"
            >
              + Add First Asset
            </Link>
          </div>
        )}

        <div className="mt-8 flex justify-center items-center gap-4">
          <button
            onClick={() => setPage(page - 1)}
            disabled={page === 0}
            className="px-5 py-2 rounded-lg bg-gray-300 hover:bg-gray-400 disabled:opacity-50 min-h-touch"
          >
            Prev
          </button>

          <span className="font-semibold text-gray-700">
            Page {page + 1} of {totalPages}
          </span>

          <button
            onClick={() => setPage(page + 1)}
            disabled={page + 1 >= totalPages}
            className="px-5 py-2 rounded-lg bg-gray-300 hover:bg-gray-400 disabled:opacity-50 min-h-touch"
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
}

export default AssetList;