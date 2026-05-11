import { useState, useEffect } from "react";
import API from "../services/api";
import { useParams, useNavigate } from "react-router-dom";

function EditAsset() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [asset, setAsset] = useState({
    assetName: "",
    assetType: "",
    ipAddress: ""
  });

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAsset();
  }, [id]);

  const fetchAsset = async () => {
    try {
      const response = await API.get(`/assets/${id}`);
      const data = response.data;
      setAsset({
        assetName: data.assetName || data.name || "",
        assetType: data.assetType || data.type || "",
        ipAddress: data.ipAddress || ""
      });
    } catch (error) {
      console.error("Asset fetch error:", error);
      alert("Asset not found");
      navigate("/");
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setAsset({ ...asset, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        assetName: asset.assetName,
        assetType: asset.assetType,
        ipAddress: asset.ipAddress,
        status: "ACTIVE"
      };
      await API.put(`/assets/${id}`, payload);
      alert("Asset Updated!");
      navigate("/");
    } catch (error) {
      console.error(error);
      alert("Error updating asset");
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen bg-gray-100">
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-md w-80">
        <h2 className="text-xl font-bold mb-4">Edit Asset</h2>

        <input
          name="assetName"
          value={asset.assetName}
          placeholder="Name"
          onChange={handleChange}
          className="w-full mb-3 p-2 border rounded"
        />
        <input
          name="assetType"
          value={asset.assetType}
          placeholder="Type"
          onChange={handleChange}
          className="w-full mb-3 p-2 border rounded"
        />
        <input
          name="ipAddress"
          value={asset.ipAddress}
          placeholder="IP Address"
          onChange={handleChange}
          className="w-full mb-3 p-2 border rounded"
        />

        <div className="flex gap-4">
          <button
            type="button"
            onClick={() => navigate("/")}
            className="w-full bg-gray-500 text-white py-2 rounded font-semibold"
          >
            Back
          </button>
          <button type="submit" className="w-full bg-yellow-500 text-white py-2 rounded font-semibold">
            Update
          </button>
        </div>
      </form>
    </div>
  );
}

export default EditAsset;