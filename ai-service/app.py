import os
from flask import Flask
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

try:
    from sentence_transformers import SentenceTransformer
    print("Pre-loading sentence-transformers at startup...")
    # This will download/load the model into memory
    model = SentenceTransformer('all-MiniLM-L6-v2')
    print("sentence-transformers loaded successfully.")
except ImportError:
    print("sentence-transformers not installed, skipping pre-load...")
    model = None

from routes.describe import describe_bp
from routes.recommend import recommend_bp
from routes.report import report_bp
from routes.health import health_bp

app = Flask(__name__)

limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"]
)

app.register_blueprint(describe_bp)
app.register_blueprint(recommend_bp)
app.register_blueprint(report_bp)
app.register_blueprint(health_bp)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
