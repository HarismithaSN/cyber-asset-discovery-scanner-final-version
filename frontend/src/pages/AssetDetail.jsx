import { useEffect, useState } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";
import API from "../services/api";

function AssetDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [asset, setAsset] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAsset();
  }, [id]);

  const fetchAsset = async () => {
    try {
      if (String(id).startsWith("demo-")) {
        const localAssets = JSON.parse(localStorage.getItem("dummyAssets") || "[]");
        const found = localAssets.find(a => a.id === id);
        if (found) {
          setAsset(found);
        } else {
          alert("Demo asset not found");
          navigate("/");
        }
      } else {
        const response = await API.get(`/assets/${id}`);
        setAsset(response.data);
      }
    } catch (error) {
      console.error("Asset detail error:", error);
      alert("Asset not found");
      navigate("/");
    } finally {
      setLoading(false);
    }
  };

  const getScoreBadge = (score) => {
    if (score >= 70) {
      return "bg-red-100 text-red-800 border-red-400";
    }
    if (score >= 40) {
      return "bg-yellow-100 text-yellow-800 border-yellow-400";
    }
    return "bg-green-100 text-green-800 border-green-400";
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-100 flex items-center justify-center">
        <div className="bg-white px-8 py-6 rounded-2xl shadow-lg text-center">
          <div className="animate-spin h-10 w-10 border-4 border-blue-700 border-t-transparent rounded-full mx-auto mb-4"></div>
          <p className="text-lg font-semibold text-slate-700">
            Loading asset details...
          </p>
        </div>
      </div>
    );
  }

  if (!asset) return null;

  const riskScore = asset.riskScore ?? asset.score ?? 0;

  return (
    <div className="min-h-screen bg-slate-100 p-6">
      <div className="max-w-4xl mx-auto">

        {/* Header */}
        <div className="bg-gradient-to-r from-blue-900 to-blue-700 text-white p-6 rounded-2xl shadow mb-6">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold">Asset Details</h1>
              <p className="text-blue-100 text-sm mt-1">
                View complete information of selected asset
              </p>
            </div>

            <Link
              to="/"
              className="bg-white text-blue-800 px-4 py-2 rounded-lg font-semibold hover:bg-blue-50 transition"
            >
              Back
            </Link>
          </div>
        </div>

        {/* Content Card */}
        <div className="bg-white rounded-2xl shadow-lg p-6 border border-slate-200">

          {/* Info Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

            <div>
              <p className="text-sm text-gray-500">Asset Name</p>
              <p className="text-lg font-semibold text-gray-800">
                {asset.name || asset.assetName}
              </p>
            </div>

            <div>
              <p className="text-sm text-gray-500">IP Address</p>
              <p className="text-lg font-semibold text-gray-800">
                {asset.ipAddress}
              </p>
            </div>

            <div>
              <p className="text-sm text-gray-500">Type</p>
              <p className="text-lg font-semibold text-gray-800">
                {asset.type || asset.assetType}
              </p>
            </div>

            <div>
              <p className="text-sm text-gray-500">Status</p>
              <p className="text-lg font-semibold text-gray-800">
                {asset.status || "N/A"}
              </p>
            </div>

            <div>
              <p className="text-sm text-gray-500">Risk Score</p>
              <span
                className={`inline-block px-4 py-1 mt-1 rounded-full border font-bold ${getScoreBadge(
                  riskScore
                )}`}
              >
                {riskScore}
              </span>
            </div>

            {asset.description && (
              <div className="md:col-span-2">
                <p className="text-sm text-gray-500">Description</p>
                <p className="text-gray-700 mt-1">{asset.description}</p>
              </div>
            )}
          </div>

          {/* Action Button */}
          <div className="flex justify-end mt-8">
            <Link
              to={`/edit/${asset.id}`}
              className="bg-yellow-500 text-white px-6 py-2 rounded-lg font-semibold hover:bg-yellow-600 transition shadow"
            >
              Edit Asset
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AssetDetail;