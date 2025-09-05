const canvas = document.getElementById('myCanvas');
const ctx = canvas.getContext('2d');

let drawing = false;
let brushColor = '#ff6b6b'; // default bright color
let brushSize = 5;

// Start drawing
canvas.addEventListener('mousedown', () => drawing = true);
canvas.addEventListener('mouseup', () => drawing = false);
canvas.addEventListener('mouseout', () => drawing = false);

// Draw on canvas
canvas.addEventListener('mousemove', (e) => {
    if (!drawing) return;
    ctx.fillStyle = brushColor;
    ctx.beginPath();
    ctx.arc(e.offsetX, e.offsetY, brushSize, 0, Math.PI * 2);
    ctx.fill();
});
const brushColors = ['#ff6b6b', '#4d9eff', '#ffe066', '#4aff91', '#ff9f1c'];

brushColors.forEach(color => {
    const btn = document.createElement('button');
    btn.style.backgroundColor = color;
    btn.style.width = '30px';
    btn.style.height = '30px';
    btn.style.margin = '5px';
    btn.addEventListener('click', () => brushColor = color);
    document.body.insertBefore(btn, canvas);
});
