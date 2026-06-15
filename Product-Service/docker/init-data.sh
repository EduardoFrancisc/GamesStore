#!/bin/sh

#Tempo pra criar o banco(ES)
sleep 15

# 1
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "The Last of Us Part II",
  "description": "Jogo de ação e sobrevivência pós-apocalíptico.",
  "price": 249.99,
  "platform": "PS4",
  "stockQuantity": 100,
  "releaseDate": "2020-06-19T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 2
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Halo Infinite",
  "description": "A mais nova aventura de Master Chief.",
  "price": 299.90,
  "platform": "XBOX_SERIES_X",
  "stockQuantity": 50,
  "releaseDate": "2021-12-08T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 3
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "The Legend of Zelda: Tears of the Kingdom",
  "description": "Aventura épica em mundo aberto por Hyrule.",
  "price": 349.00,
  "platform": "NINTENDO_SWITCH",
  "stockQuantity": 200,
  "releaseDate": "2023-05-12T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 4
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Cyberpunk 2077",
  "description": "RPG de ação em Night City.",
  "price": 199.50,
  "platform": "PC",
  "stockQuantity": 75,
  "releaseDate": "2020-12-10T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 5
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "God of War Ragnarok",
  "description": "Kratos e Atreus enfrentam o fim do mundo nórdico.",
  "price": 349.90,
  "platform": "PS5",
  "stockQuantity": 150,
  "releaseDate": "2022-11-09T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 6
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Red Dead Redemption",
  "description": "A clássica jornada de John Marston.",
  "price": 99.90,
  "platform": "XBOX_360",
  "stockQuantity": 30,
  "releaseDate": "2010-05-18T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 7
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Gears of War 4",
  "description": "O início de uma nova saga para a franquia Gears.",
  "price": 79.90,
  "platform": "XBOX_ONE",
  "stockQuantity": 40,
  "releaseDate": "2016-10-11T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 8
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Demon Souls",
  "description": "O jogo que deu origem ao gênero Soulsborne.",
  "price": 149.90,
  "platform": "PS3",
  "stockQuantity": 20,
  "releaseDate": "2009-02-05T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 9
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Forza Horizon 5",
  "description": "Corridas deslumbrantes pelas paisagens do México.",
  "price": 249.00,
  "platform": "XBOX_SERIES_S",
  "stockQuantity": 90,
  "releaseDate": "2021-11-09T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 10
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Marvel Spider-Man 2",
  "description": "Peter Parker e Miles Morales lutam contra Venom.",
  "price": 349.90,
  "platform": "PS5",
  "stockQuantity": 120,
  "releaseDate": "2023-10-20T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 11
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Super Mario Odyssey",
  "description": "Uma jornada global incrível com o Mario e o Cappy.",
  "price": 299.00,
  "platform": "NINTENDO_SWITCH",
  "stockQuantity": 80,
  "releaseDate": "2017-10-27T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 12
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Elden Ring",
  "description": "O aclamado RPG de ação da FromSoftware.",
  "price": 249.90,
  "platform": "PC",
  "stockQuantity": 300,
  "releaseDate": "2022-02-25T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 13
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Bloodborne",
  "description": "Sobreviva aos horrores da cidade de Yharnam.",
  "price": 99.50,
  "platform": "PS4",
  "stockQuantity": 35,
  "releaseDate": "2015-03-24T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 14
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Animal Crossing: New Horizons",
  "description": "Crie o seu paraíso numa ilha deserta.",
  "price": 299.99,
  "platform": "NINTENDO_SWITCH",
  "stockQuantity": 110,
  "releaseDate": "2020-03-20T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'

# 15
curl -s -X POST "http://product-elasticsearch:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "Starfield",
  "description": "O primeiro novo universo da Bethesda em 25 anos.",
  "price": 299.00,
  "platform": "XBOX_SERIES_X",
  "stockQuantity": 140,
  "releaseDate": "2023-09-06T00:00:00",
  "_class": "br.edu.infnet.product.domain.model.Product"
}'