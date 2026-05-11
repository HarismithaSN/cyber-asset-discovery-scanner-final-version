import { useEffect, useState } from "react";
import api from "../services/api";

const ListPage = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const res = await api.get("/assets"); // change endpoint if needed
      setData(res.data);
    } catch (error) {
      console.error("Error fetching data", error);
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Loading state
  if (loading) {
    return <p className="text-center mt-10">Loading...</p>;
  }

  // 🔹 Empty state
  if (data.length === 0) {
    return <p className="text-center mt-10">No data found</p>;
  }

  return (
    <div className="p-6">
      <h1 className="text-xl font-bold mb-4">Assets List</h1>

      <table className="w-full border border-gray-300">
        <thead>
          <tr className="bg-gray-200">
            <th className="p-2 border">ID</th>
            <th className="p-2 border">Name</th>
            <th className="p-2 border">Type</th>
          </tr>
        </thead>

        <tbody>
          {data.map((item) => (
            <tr key={item.id}>
              <td className="p-2 border">{item.id}</td>
              <td className="p-2 border">{item.name}</td>
              <td className="p-2 border">{item.type}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ListPage;