import React from "react";

/**
 * ErrorBoundary Component
 * Catches errors in child components and displays a fallback UI
 * Prevents the entire app from crashing due to a single component error
 */
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    this.setState({
      error: error,
      errorInfo: errorInfo,
    });

    // Log error for debugging (Optional: send to error tracking service)
    console.error("Error caught by ErrorBoundary:", error, errorInfo);
  }

  handleReset = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
    });
  };

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-slate-100 flex items-center justify-center px-4">
          <div className="bg-white rounded-2xl shadow-lg p-8 max-w-md w-full border-l-4 border-red-600">
            <div className="mb-4">
              <h1 className="text-3xl font-bold text-red-600">⚠️ Oops!</h1>
              <p className="text-gray-600 mt-2">Something went wrong</p>
            </div>

            <details className="mb-6 bg-red-50 border border-red-200 rounded p-4 cursor-pointer">
              <summary className="font-semibold text-red-800 hover:text-red-900">
                Error Details
              </summary>
              <pre className="mt-3 text-xs bg-red-100 text-red-800 p-3 rounded overflow-auto max-h-40 font-mono">
                {this.state.error && this.state.error.toString()}
                {"\n"}
                {this.state.errorInfo && this.state.errorInfo.componentStack}
              </pre>
            </details>

            <div className="space-y-3">
              <button
                onClick={this.handleReset}
                className="w-full bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition font-semibold"
              >
                Try Again
              </button>

              <a
                href="/"
                className="block w-full bg-slate-600 text-white px-4 py-2 rounded-lg hover:bg-slate-700 transition font-semibold text-center"
              >
                Go to Home
              </a>
            </div>

            <p className="text-xs text-gray-500 mt-4 text-center">
              If the problem persists, please contact support.
            </p>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;