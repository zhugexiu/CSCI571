const express = require('express');
const router = express.Router();

router.get('/:keyword', (req, res, next) => {
    const id = req.params.keyword;

    const request1 = require('request');
    const requestOptions = {
        'url': 'https://api.tiingo.com/tiingo/daily/' + id + '?token=61cb5595cf28b8814a5fe8a4e1f016824741f1e9',
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

})

module.exports = router;