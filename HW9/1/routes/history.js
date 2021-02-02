const express = require('express');
const router = express.Router();

router.get('/:keyword', (req, res, next) => {
    const id = req.params.keyword;

    const d = new Date();
    const twoYearsAgo = (d.getFullYear()-2) +"-" + (d.getMonth()+1) +"-"+ d.getDate();
    const request1 = require('request');
    const requestOptions = {
        'url': 'https://api.tiingo.com/tiingo/daily/' + id + '/prices?startDate='+twoYearsAgo+'&token=61cb5595cf28b8814a5fe8a4e1f016824741f1e9',
        'headers': {
            'Content-Type': 'application/json'
        }
    };

    request1(requestOptions,
        function(error, response, body) {
            console.log(body);
            let newResult = [];
            for(let i = 0; i < JSON.parse(body).length; i++) {
                let partRes = {};
                partRes.date = (new Date(JSON.parse(body)[i]['date']) - Number(3600*16*1000*0));
                partRes.open = JSON.parse(body)[i]['open'];
                partRes.high = JSON.parse(body)[i]['high'];
                partRes.low = JSON.parse(body)[i]['low'];
                partRes.close = JSON.parse(body)[i]['close'];
                partRes.volume = JSON.parse(body)[i]['volume'];
                newResult.push(partRes);
            }
            res.status(200).json({
                message: 'Handling GET request to /details',
                description: newResult
            });
        }
    );

})

module.exports = router;