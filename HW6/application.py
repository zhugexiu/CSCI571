import string
from flask import Flask, jsonify, request
import requests

app = Flask(__name__, static_url_path='')


@app.route('/home/')
def home():
    return app.send_static_file('html/home.html')


@app.route('/api/search/', methods=['GET'])
def GetInfo():
    q = request.args.get('keyword')
    headers = {
        'Content-Type': 'application/json'
    }
    requestResponse = requests.get("https://api.tiingo.com/api/test?token=61cb5595cf28b8814a5fe8a4e1f016824741f1e9",
                                   headers=headers)
    print(requestResponse.json())








if __name__ == '__main__':
    app.run(debug=True, port=8080, host='127.0.0.1')