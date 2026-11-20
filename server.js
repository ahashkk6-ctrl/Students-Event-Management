/**
 * Smart Campus Event Management & Student Participation System
 * Lightweight Static HTTP Server (Zero Dependencies)
 * Department of Information Technology, Velalar College of Engineering & Technology
 */

const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = parseInt(process.env.PORT || '3000', 10);
const HOST = process.env.HOST || 'localhost';

const MIME_TYPES = {
  '.html': 'text/html; charset=UTF-8',
  '.css': 'text/css; charset=UTF-8',
  '.js': 'text/javascript; charset=UTF-8',
  '.json': 'application/json; charset=UTF-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.pdf': 'application/pdf',
  '.csv': 'text/csv; charset=UTF-8'
};

const server = http.createServer((req, res) => {
  // Normalize URL path to prevent directory traversal
  let reqPath = req.url.split('?')[0].split('#')[0];
  if (reqPath === '/' || reqPath === '') reqPath = '/index.html';

  const decoded = decodeURIComponent(reqPath);
  const safeRelative = path.normalize(decoded).replace(/^(\.\.[\/\\])+/, '');
  const filePath = path.join(__dirname, safeRelative);

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      res.writeHead(404, { 'Content-Type': 'text/html; charset=UTF-8' });
      res.end(`
        <!DOCTYPE html>
        <html>
        <head><title>404 Not Found</title></head>
        <body style="font-family:sans-serif;text-align:center;padding:50px;">
          <h2>404 - File Not Found</h2>
          <p>The requested URL <code>${reqPath}</code> was not found on this server.</p>
          <a href="/">Return to Dashboard</a>
        </body>
        </html>
      `);
      return;
    }

    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';

    res.writeHead(200, {
      'Content-Type': contentType,
      'Content-Length': stats.size,
      'Cache-Control': 'no-cache'
    });

    const stream = fs.createReadStream(filePath);
    stream.on('error', (streamErr) => {
      if (!res.headersSent) {
        res.writeHead(500, { 'Content-Type': 'text/plain' });
      }
      res.end('500 Internal Server Error');
    });
    stream.pipe(res);
  });
});

server.listen(PORT, HOST, () => {
  console.log(`\n================================================================`);
  console.log(`  Smart Campus Event Management & Student Participation System`);
  console.log(`  Institution: Velalar College of Engineering and Technology (VCET)`);
  console.log(`  Department : Information Technology`);
  console.log(`----------------------------------------------------------------`);
  console.log(`  Local URL  : http://${HOST}:${PORT}`);
  console.log(`  Web Root   : ${__dirname}`);
  console.log(`  Dashboard  : http://${HOST}:${PORT}/index.html`);
  console.log(`================================================================\n`);
});
