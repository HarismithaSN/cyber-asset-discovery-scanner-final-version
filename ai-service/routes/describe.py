from flask import Blueprint, request, jsonify
import os
import json

describe_bp = Blueprint('describe', __name__)

@describe_bp.route('/describe', methods=['POST'])
def describe_asset():
    """Describe a cyber asset using AI"""
    try:
        data = request.get_json()
        if not data or 'asset_data' not in data:
            return jsonify({'error': 'asset_data is required'}), 400

        asset_data = data['asset_data']

        # Mock AI response for now - in real implementation, this would call an AI service
        description = f"This is a {asset_data.get('type', 'unknown')} asset named {asset_data.get('name', 'unnamed')}."

        return jsonify({
            'description': description,
            'asset_id': asset_data.get('id')
        })

    except Exception as e:
        return jsonify({'error': str(e)}), 500