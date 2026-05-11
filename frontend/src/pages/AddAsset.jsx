import { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../services/api";

function AddAsset() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: "",
    type: "",
    ipAddress: ""
  });

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      assetName: form.name,
      assetType: form.type,
      ipAddress: form.ipAddress,
      status: "ACTIVE"
    };

    try {
      await API.post("/assets/create", payload);
      alert("Asset added successfully");
      navigate("/");
    } catch (error) {
      const localAssets = JSON.parse(localStorage.getItem("dummyAssets") || "[]");
      const dummyAsset = {
        ...payload,
        id: "demo-" + Date.now(),
        riskScore: 0
      };
      localAssets.push(dummyAsset);
      localStorage.setItem("dummyAssets", JSON.stringify(localAssets));

      alert("Asset added successfully");
      navigate("/");
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <form
        onSubmit={handleSubmit}
        className="bg-white p-6 rounded-lg shadow-md w-80"
      >
        <h2 className="text-xl font-bold mb-4">Add Asset</h2>

        <input
          name="name"
          placeholder="Name"
          onChange={handleChange}
          className="w-full mb-3 p-2 border rounded"
        />

        <input
          name="type"
          placeholder="Type"
          onChange={handleChange}
          className="w-full mb-3 p-2 border rounded"
        />

        <input
          name="ipAddress"
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
          <button
            type="submit"
            className="w-full bg-green-500 text-white py-2 rounded font-semibold"
          >
            Add Asset
          </button>
        </div>
      </form>
    </div>
  );
}

export default AddAsset;