## Telegram
**Token**: 7340987230:AAEgslTT6jjFdmObq0YF5EdcJXX5wn07jSw

**chatId** : 8123671151

# Resulats:
**Requête HTTP**:

**Envoyer un message au bot**
POST http://localhost:9090/sendMessage
{
    "chat_id": 8123671151,
    "text":"Bonjour my friend"
}

**requête avec API météo**
GET http://localhost:9090/meteo/forecast?ville=Paris&days=3

**une blague aléatoire**
GET http://localhost:9090/api/v1/joke/random

**requête ajout d'une blague**
POST http://localhost:9090/api/v1/joke
{
    "titre":"blague nulle",
    "texte" : "Toto rentre dans un bar",
    "note" : 3.1
}
