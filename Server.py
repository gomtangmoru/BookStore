import flask, hashlib, os
from flask import request, jsonify
from python_dotenv import load_dotenv

load_dotenv()

app = flask.Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URL'] = 'mysql+pymysql://{}:{}@{}/{}'.format(
    os.getenv('SERVERDBUSER'),
    os.getenv('SERVERDBPASSWORD'),
    os.getenv('SERVERDBHOST'),
    os.getenv('SERVERDBNAME')
)
