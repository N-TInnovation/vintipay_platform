#!/bin/bash
# Script para gerar ícones em diferentes tamanhos a partir do favicon.svg
# Requer: ImageMagick ou librsvg

SVG_SOURCE="favicon.svg"
OUTPUT_DIR="../"

# Tamanhos para Android
ANDROID_SIZES=(36 48 72 96 144 192)
# Tamanhos para Apple
APPLE_SIZES=(57 60 72 76 114 120 144 152 180)
# Outros tamanhos
OTHER_SIZES=(16 32 96)

echo "Gerando ícones para Android..."
for size in "${ANDROID_SIZES[@]}"; do
  if command -v convert &> /dev/null; then
    convert -background none -resize ${size}x${size} $SVG_SOURCE "${OUTPUT_DIR}/android-icon-${size}x${size}.png"
  elif command -v rsvg-convert &> /dev/null; then
    rsvg-convert -w $size -h $size $SVG_SOURCE > "${OUTPUT_DIR}/android-icon-${size}x${size}.png"
  else
    echo "Erro: ImageMagick ou librsvg não encontrados!"
    exit 1
  fi
  echo "Criado: android-icon-${size}x${size}.png"
done

echo "Gerando ícones para Apple..."
for size in "${APPLE_SIZES[@]}"; do
  if command -v convert &> /dev/null; then
    convert -background none -resize ${size}x${size} $SVG_SOURCE "${OUTPUT_DIR}/apple-icon-${size}x${size}.png"
  elif command -v rsvg-convert &> /dev/null; then
    rsvg-convert -w $size -h $size $SVG_SOURCE > "${OUTPUT_DIR}/apple-icon-${size}x${size}.png"
  else
    echo "Erro: ImageMagick ou librsvg não encontrados!"
    exit 1
  fi
  echo "Criado: apple-icon-${size}x${size}.png"
done

echo "Gerando ícones para favicon..."
for size in "${OTHER_SIZES[@]}"; do
  if command -v convert &> /dev/null; then
    convert -background none -resize ${size}x${size} $SVG_SOURCE "${OUTPUT_DIR}/favicon-${size}x${size}.png"
  elif command -v rsvg-convert &> /dev/null; then
    rsvg-convert -w $size -h $size $SVG_SOURCE > "${OUTPUT_DIR}/favicon-${size}x${size}.png"
  else
    echo "Erro: ImageMagick ou librsvg não encontrados!"
    exit 1
  fi
  echo "Criado: favicon-${size}x${size}.png"
done

# Gerar favicon.ico
if command -v convert &> /dev/null; then
  convert -background none -resize 16x16 $SVG_SOURCE "${OUTPUT_DIR}/favicon.ico"
  echo "Criado: favicon.ico"
else
  echo "Aviso: Não foi possível criar favicon.ico (requer ImageMagick)"
fi

echo "Criando apple-icon.png e apple-icon-precomposed.png..."
if command -v convert &> /dev/null; then
  convert -background none -resize 192x192 $SVG_SOURCE "${OUTPUT_DIR}/apple-icon.png"
  cp "${OUTPUT_DIR}/apple-icon.png" "${OUTPUT_DIR}/apple-icon-precomposed.png"
elif command -v rsvg-convert &> /dev/null; then
  rsvg-convert -w 192 -h 192 $SVG_SOURCE > "${OUTPUT_DIR}/apple-icon.png"
  cp "${OUTPUT_DIR}/apple-icon.png" "${OUTPUT_DIR}/apple-icon-precomposed.png"
fi

# Gerar MS icon
for size in 70 144 150 310; do
  if command -v convert &> /dev/null; then
    convert -background none -resize ${size}x${size} $SVG_SOURCE "${OUTPUT_DIR}/ms-icon-${size}x${size}.png"
  elif command -v rsvg-convert &> /dev/null; then
    rsvg-convert -w $size -h $size $SVG_SOURCE > "${OUTPUT_DIR}/ms-icon-${size}x${size}.png"
  fi
  echo "Criado: ms-icon-${size}x${size}.png"
done

echo "Concluído!"
