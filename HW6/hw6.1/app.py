from flask import Flask, jsonify, request
import requests
import datetime
from dateutil.relativedelta import relativedelta

font_6_time = datetime.date.today() - relativedelta(months=6)

app = Flask(__name__, static_url_path='')


@app.route('/')
def home():
    return app.send_static_file('home.html')


@app.route('/api/search/', methods=['GET'])
def info():
    q = str(request.args.get('keyword'))
    headers = {
        'Content-Type': 'application/json'
    }
    response1 = requests.get("https://api.tiingo.com/tiingo/daily/"+q+"?token=61cb5595cf28b8814a5fe8a4e1f016824741f1e9",
                                   headers=headers)
    response2 = requests.get("https://api.tiingo.com/iex/"+q+"?token=61cb5595cf28b8814a5fe8a4e1f016824741f1e9",
                           headers=headers)
    response3 = requests.get(
        "https://api.tiingo.com/iex/"+q+"/prices?startDate="+str(font_6_time)+"&resampleFreq=12hour&columns="
                                        "open,high,low,close,volume&token=61cb5595cf28b8814a5fe8a4e1f016824741f1e9",
        headers=headers)
    url = ('http://newsapi.org/v2/everything?'
           'q='+q+'&'
           'from=2020-09-26&'
           'sortBy=popularity&'
           'apiKey=980c50f23cfc4667a34594b779561f8d')

    response4 = requests.get(url).json()

    dict = {}
    if "detail" in response1.json():
        dict['message'] = "error"
    else:
        dict['outlook'] = response1.json()

    if len(response2.json()) == 0:
        dict['message'] = "error"
    else:
        dict['summary'] = response2.json()[0]

    if len(response3.json()) == 0:
        dict['message'] = "error"
    else:

        dict['chart'] = response3.json()


    articles = response4['articles']
    validarticles = []
    count = 0
    for article in articles:
        if article['publishedAt'] == "" or article['publishedAt'] is None:
            continue
        if article['title'] == "" or article['title'] is None:
            continue
        if article['url'] == "" or article['url'] is None:
            continue
        if article['urlToImage'] == "" or article['urlToImage'] is None:
            continue
        count += 1
        validarticles.append(article)
        if count == 5:
            break

    dict['news'] = validarticles
    return jsonify(dict)


if __name__ == '__main__':
    app.run(debug=True, port=5000, host='127.0.0.1')
