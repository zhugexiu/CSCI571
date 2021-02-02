 'use strict';
const express = require('express');
const app = express();
const morgan = require('morgan');

//const watchlist =  require('./routes/product');
//const portfolio =  require('./routes/portfolio');
const detail = require('./routes/details');
const price = require('./routes/price');
const history = require('./routes/history');
const dailychart = require('./routes/dailychart');
const autocomplete = require('./routes/auto');
const news = require('./routes/news');

app.use(morgan('dev'));
app.use((req,res,next) =>{
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Headers', '*');
    next();
});

app.use('/details', detail);
app.use('/price', price);
app.use('/history',history);
app.use('/dailychart',dailychart);
app.use('/auto',autocomplete);
app.use('/news',news);

app.get('/', (req, res) => {
    res
        .status(200)
        .send('Hello, world!')
        .end();
});

// Start the server
const PORT = process.env.PORT || 8081;
app.listen(PORT, () => {
    console.log(`App listening on port ${PORT}`);
    console.log('Press Ctrl+C to quit.');
});
// [END gae_node_request_example]

