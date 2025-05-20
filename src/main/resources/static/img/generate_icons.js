// Script para gerar ícones a partir do SVG
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// Caminho para o arquivo SVG
const svgFile = path.join(__dirname, 'favicon.svg');
// Diretório de saída
const outputDir = path.join(__dirname, '..');

// Definindo os tamanhos dos ícones
const androidSizes = [36, 48, 72, 96, 144, 192];
const appleSizes = [57, 60, 72, 76, 114, 120, 144, 152, 180];
const otherSizes = [16, 32, 96];
const msSizes = [70, 144, 150, 310];

// Verifica se o arquivo SVG existe
if (!fs.existsSync(svgFile)) {
  console.error('Arquivo SVG não encontrado:', svgFile);
  process.exit(1);
}

// Lê o conteúdo do SVG
const svgContent = fs.readFileSync(svgFile, 'utf8');

// Função para criar um arquivo HTML temporário para renderizar o SVG
const createHtmlWithSvg = (size) => {
  const htmlContent = `
    <!DOCTYPE html>
    <html>
    <head>
      <title>SVG to PNG</title>
      <style>
        body { margin: 0; padding: 0; }
        svg { width: ${size}px; height: ${size}px; }
      </style>
    </head>
    <body>
      ${svgContent}
      <script>
        // Este script será executado no navegador pelo Puppeteer
        const canvas = document.createElement('canvas');
        canvas.width = ${size};
        canvas.height = ${size};
        const ctx = canvas.getContext('2d');
        
        const svgElement = document.querySelector('svg');
        const serializer = new XMLSerializer();
        const svgString = serializer.serializeToString(svgElement);
        
        const img = new Image();
        img.onload = function() {
          ctx.drawImage(img, 0, 0, ${size}, ${size});
          const dataUrl = canvas.toDataURL('image/png');
          console.log(dataUrl); // Isso será capturado pelo Puppeteer
        };
        img.src = 'data:image/svg+xml;base64,' + btoa(svgString);
      </script>
    </body>
    </html>
  `;
  
  const tempHtmlFile = path.join(__dirname, `temp_${size}.html`);
  fs.writeFileSync(tempHtmlFile, htmlContent);
  return tempHtmlFile;
};

// Função para emular conversão
const svgToPng = (size, outputPath) => {
  console.log(`Gerando ícone de ${size}x${size}px...`);
  
  // Como não temos acesso ao Puppeteer ou outras ferramentas de conversão,
  // vamos apenas criar um HTML que, quando aberto em um navegador,
  // exibirá o SVG no tamanho correto
  const htmlFile = createHtmlWithSvg(size);
  
  console.log(`Arquivo HTML criado em: ${htmlFile}`);
  console.log(`Para gerar o PNG final, abra este arquivo no navegador e use a função de captura de tela.`);
  console.log(`Salve a captura como: ${outputPath}`);
  
  // Nota: Em um ambiente com Node.js + Puppeteer, poderíamos fazer a conversão automática
};

console.log('Iniciando geração de ícones...');

// Gerando ícones para Android
androidSizes.forEach(size => {
  const outputPath = path.join(outputDir, `android-icon-${size}x${size}.png`);
  svgToPng(size, outputPath);
});

// Gerando ícones para Apple
appleSizes.forEach(size => {
  const outputPath = path.join(outputDir, `apple-icon-${size}x${size}.png`);
  svgToPng(size, outputPath);
});

// Gerando ícones padrão de favicon
otherSizes.forEach(size => {
  const outputPath = path.join(outputDir, `favicon-${size}x${size}.png`);
  svgToPng(size, outputPath);
});

// Gerando ícones da Microsoft
msSizes.forEach(size => {
  const outputPath = path.join(outputDir, `ms-icon-${size}x${size}.png`);
  svgToPng(size, outputPath);
});

console.log('Geração de arquivos HTML concluída!');
console.log('Após converter os HTMLs em PNGs, você terá todos os ícones necessários.');
