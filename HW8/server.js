
var path = require('path');
var express = require('express');

const app = express();

app.use('' ,express.static(path.join(__dirname, 'dist/untitled2')));


app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist/untitled2/index.html'));
})

const PORT = process.env.PORT || 8081;
app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
  console.log('Press Ctrl+C to quit.');
});
