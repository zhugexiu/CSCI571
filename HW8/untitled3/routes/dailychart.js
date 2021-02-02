const express = require('express');
const router = express.Router();

router.get('/:keyword', (req, res, next) => {
    const id = req.params.keyword;
    let nowdate = new Date();
    let closedate = new Date();
    if(nowdate.getDay()===6){
        const date = +new Date();
        const before = date-1000*60*60*24;
        closedate = dateToStrSlash1(new Date(before));
    }else if(nowdate.getDay()===7){
        const date2 = +new Date();
        const before2 = date2 - 2 * 1000 * 60 * 60 * 24;
        closedate = dateToStrSlash1(new Date(before2));
    }else if(nowdate.getDay() === 1){
        if(nowdate.getHours()<6||(nowdate.getHours()===6&&nowdate.getMinutes()<30)){
            const date3 = +new Date();
            const before3 = date3 - 3 * 1000 * 60 * 60 * 24;
            closedate = dateToStrSlash1(new Date(before3));
        }else{
            closedate = dateToStrSlash1(closedate);
        }
    }else {
        if(nowdate.getHours()<6){
            const date4 = +new Date();
            const before4 = date4 - 1000 * 60 * 60 * 24;
            closedate = dateToStrSlash1(new Date(before4));
        }else if(nowdate.getHours()===6&&nowdate.getMinutes()<30){
            const date4 = +new Date();
            const before4 = date4 - 1000 * 60 * 60 * 24;
            closedate = dateToStrSlash1(new Date(before4));
        }else{
            closedate = dateToStrSlash1(closedate);
        }
    }


    const request1 = require('request');
    const requestOptions = {
        'url': 'https://api.tiingo.com/iex/' + id + '/prices?startDate='+closedate+'&resampleFreq=4min&columns=open,high,low,close,volume&token=61cb5595cf28b8814a5fe8a4e1f016824741f1e9',
        'headers': {
            'Content-Type': 'application/json'
        }
    };

    request1(requestOptions,
        function(error, response, body) {
            console.log(body);
            res.status(200).json({
                message: 'Handling GET request to /details',
                description: JSON.parse(body)
            });
        }
    );

    function dateToStrSlash1(date) {
        const Y = date.getFullYear() + '-';
        const M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
        const D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate());
        return Y + M + D;
    }

})

module.exports = router;
