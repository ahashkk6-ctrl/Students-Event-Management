const fs = require('fs');
const path = require('path');

const htmlPath = path.join(__dirname, '..', 'index.html');
if (!fs.existsSync(htmlPath)) {
  console.error('Error: index.html not found at', htmlPath);
  process.exit(1);
}

const html = fs.readFileSync(htmlPath, 'utf8');
console.log('Verifying Smart Campus Event Management frontend integrity...');

// Extract JS block and check syntax
const scriptMatches = [...html.matchAll(/<script[\s\S]*?>([\s\S]*?)<\/script>/gi)];
let allValid = true;

scriptMatches.forEach((m, idx) => {
  const code = m[1].trim();
  if (code.length > 50) {
    try {
      new Function(code);
      console.log(`✓ Embedded script block ${idx + 1}: Valid syntax`);
    } catch (e) {
      console.error(`✗ Embedded script block ${idx + 1} syntax error:`, e.message);
      allValid = false;
    }
  }
});

const checks = [
  { name: 'HTML DOCTYPE & metadata', pass: html.includes('<!DOCTYPE html>') && html.includes('charset="UTF-8"') },
  { name: 'Chart.js & Plotly CDN scripts', pass: html.includes('chart.umd.min.js') && html.includes('plotly-2.27.0.min.js') },
  { name: 'Bootstrap & FontAwesome CSS', pass: html.includes('bootstrap.min.css') && html.includes('all.min.css') },
  { name: 'Enrolled students table element', pass: html.includes('id="studentTableBody"') },
  { name: 'Cybersecurity department support', pass: html.includes('Cybersecurity') && html.includes('#e11d48') }
];

checks.forEach(c => {
  if (c.pass) {
    console.log(`✓ ${c.name}: OK`);
  } else {
    console.error(`✗ ${c.name}: FAILED`);
    allValid = false;
  }
});

if (allValid) {
  console.log('\nAll project integrity tests passed successfully!');
  process.exit(0);
} else {
  console.error('\nIntegrity checks failed.');
  process.exit(1);
}
