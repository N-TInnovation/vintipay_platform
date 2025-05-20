// Script simples para converter SVGs em PNGs usando canvas e Node.js
const fs = require('fs');
const path = require('path');
const { createCanvas, loadImage } = require('canvas'); // Requer: npm install canvas

// Diretórios
const iconsDir = path.join(__dirname, 'icons');
const outputDir = path.join(__dirname, '..');

// Tamanhos para cada tipo de ícone
const sizes = {
  android: [36, 48, 72, 96, 144, 192],
  apple: [57, 60, 72, 76, 114, 120, 144, 152, 180],
  favicon: [16, 32, 96],
  ms: [70, 144, 150, 310]
};

// Função para converter SVG para PNG
async function svgToPng(svgPath, pngPath, width, height) {
  try {
    // Criar canvas
    const canvas = createCanvas(width, height);
    const ctx = canvas.getContext('2d');
    
    // Carregar a imagem SVG
    const img = await loadImage('file://' + svgPath);
    
    // Desenhar no canvas
    ctx.drawImage(img, 0, 0, width, height);
    
    // Salvar como PNG
    const out = fs.createWriteStream(pngPath);
    const stream = canvas.createPNGStream();
    stream.pipe(out);
    
    return new Promise((resolve, reject) => {
      out.on('finish', () => {
        console.log(`✓ Criado: ${pngPath}`);
        resolve();
      });
      out.on('error', reject);
    });
  } catch (err) {
    console.error(`Erro ao converter ${svgPath}:`, err);
  }
}

async function main() {
  // Verificar se o módulo canvas está instalado
  try {
    if (!fs.existsSync(path.join(process.cwd(), 'node_modules', 'canvas'))) {
      console.log('Módulo canvas não encontrado. Instalando...');
      console.log('Execute: npm install canvas');
      return;
    }
  } catch (err) {
    console.error('Erro ao verificar módulos:', err);
    return;
  }
  
  // Lista de conversões a fazer
  const conversions = [];
  
  // Encontrar os arquivos SVG na pasta icons
  const svgFiles = fs.readdirSync(iconsDir).filter(file => file.endsWith('.svg'));
  
  // Android icons
  const androidSvg = path.join(iconsDir, 'android-icon-192x192.svg');
  if (fs.existsSync(androidSvg)) {
    for (const size of sizes.android) {
      conversions.push({
        source: androidSvg,
        target: path.join(outputDir, `android-icon-${size}x${size}.png`),
        width: size,
        height: size
      });
    }
  }
  
  // Apple icons
  const appleSvg = path.join(iconsDir, 'apple-icon-180x180.svg');
  if (fs.existsSync(appleSvg)) {
    for (const size of sizes.apple) {
      conversions.push({
        source: appleSvg,
        target: path.join(outputDir, `apple-icon-${size}x${size}.png`),
        width: size,
        height: size
      });
    }
    
    // Apple icon e precomposed
    conversions.push({
      source: appleSvg,
      target: path.join(outputDir, 'apple-icon.png'),
      width: 192,
      height: 192
    });
    conversions.push({
      source: appleSvg,
      target: path.join(outputDir, 'apple-icon-precomposed.png'),
      width: 192,
      height: 192
    });
  }
  
  // Favicon
  const faviconSvg = path.join(iconsDir, 'favicon-32x32.svg');
  if (fs.existsSync(faviconSvg)) {
    for (const size of sizes.favicon) {
      conversions.push({
        source: faviconSvg,
        target: path.join(outputDir, `favicon-${size}x${size}.png`),
        width: size,
        height: size
      });
    }
  }
  
  // MS icons
  const msSvg = path.join(iconsDir, 'android-icon-192x192.svg'); // Reuso do Android para MS
  if (fs.existsSync(msSvg)) {
    for (const size of sizes.ms) {
      conversions.push({
        source: msSvg,
        target: path.join(outputDir, `ms-icon-${size}x${size}.png`),
        width: size,
        height: size
      });
    }
  }
  
  // Executar todas as conversões
  console.log(`Iniciando conversão de ${conversions.length} ícones...`);
  
  for (const conversion of conversions) {
    await svgToPng(
      conversion.source,
      conversion.target,
      conversion.width,
      conversion.height
    );
  }
  
  console.log('Conversão concluída!');
}

// Verifica se há SVGs na pasta icons
if (!fs.existsSync(iconsDir)) {
  console.error(`Diretório ${iconsDir} não encontrado!`);
} else {
  const svgFiles = fs.readdirSync(iconsDir).filter(file => file.endsWith('.svg'));
  if (svgFiles.length === 0) {
    console.error(`Nenhum arquivo SVG encontrado em ${iconsDir}`);
  } else {
    console.log(`Encontrados ${svgFiles.length} arquivos SVG.`);
    console.log('Para executar a conversão, instale o módulo canvas:');
    console.log('npm install canvas');
    console.log('E depois execute:');
    console.log('node convert_to_png.js');
    
    // Executar diretamente se canvas estiver instalado
    // main().catch(console.error);
  }
}
