import express from "express";
import fs from 'fs';

const files = fs.readdirSync("./",  { withFileTypes: true })
    .filter(file => file.isFile())
    .map(file => file.name);

const app = express();

app.use('/file', express.static('./'))

app.get('/files', (req, res) => res.json(files).status(200));

app.listen(736, () => console.log('Server Started at port 736.'));
