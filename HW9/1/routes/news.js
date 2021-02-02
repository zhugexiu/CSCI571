const express = require('express');
const fetch = require('node-fetch');
const router = express.Router();

router.get('/:keyword', (req, res, next) => {
    const id = req.params.keyword;
    const url = "https://newsapi.org/v2/everything?q=" + id + "&apiKey=980c50f23cfc4667a34594b779561f8d";
    let Result = [];
    fetch(url)
        .then(function(response){
            return response.json();
        })
        .then(function (myJson){
            const Raw = myJson.articles;
            for (let i = 0; i < Raw.length; i++){
                let newResult = {};
                if (Raw[i].hasOwnProperty("url" ) && Raw[i].url !== "" ){
                    newResult.url=Raw[i].url;
                }else
                    continue;
                if (Raw[i].hasOwnProperty("title" ) && Raw[i].title !== "" ){
                    newResult.title=Raw[i].title;
                }else
                    continue;
                if (Raw[i].hasOwnProperty("description" ) && Raw[i].description !== "" ){
                    newResult.description=Raw[i].description;
                }else
                    continue;
                if (Raw[i].hasOwnProperty("source" ) && Raw[i].source !== "" ){
                    newResult.source=Raw[i].source;
                }else
                    continue;
                if (Raw[i].hasOwnProperty("urlToImage" ) && Raw[i].urlToImage !== ""&& Raw[i].urlToImage !== null){
                    newResult.urlToImage=Raw[i].urlToImage;
                }else
                    continue;
                if (Raw[i].hasOwnProperty("publishedAt") && Raw[i].publishedAt !== ""){
                    newResult.publishedAt=getFormatDate(Raw[i].publishedAt);
                }else
                    continue;
                Result.push(newResult);
                if(i>=19) break;
            }
            res.status(200).json(Result);
        })
});

function getFormatDate(day) {
    const almonds = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
    const date = new Date(day);
    const year = date.getFullYear();
    let month = date.getMonth() + 1;
    let strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = '0' + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = '0' + strDate;
    }
    return almonds[month] + ' ' + strDate +','+year;
}

module.exports = router;