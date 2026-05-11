import { BrowserRouter, Routes, Route } from "react-router-dom";
import AssetList from "./pages/AssetList";
import AddAsset from "./pages/AddAsset";
import EditAsset from "./pages/EditAsset";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import AssetDetail from "./pages/AssetDetail";
import { AuthProvider } from "./context/AuthContext";
import ErrorBoundary from "./components/ErrorBoundary";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ErrorBoundary>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
            <Route path="/" element={<ProtectedRoute><AssetList /></ProtectedRoute>} />
            <Route path="/assets/:id" element={<ProtectedRoute><AssetDetail /></ProtectedRoute>} />
            <Route path="/add" element={<ProtectedRoute><AddAsset /></ProtectedRoute>} />
            <Route path="/edit/:id" element={<ProtectedRoute><EditAsset /></ProtectedRoute>} />
          </Routes>
        </ErrorBoundary>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;