# Utility telegram bot

## Description:<br>
Telegram bot written in Java for personal use.<br>

✔️ Weather <br>
✔️ Random number in chosen range <br>
✔️ Password generator <br>
✔️ User support system <br>
✔️ Notes manager <br>

(Examples of commands in "Overview" part in readme)<br><br>

## Features
- ##### Quite good functionality
- ##### User support system
- ##### In addition to Telegram API, others are used here

## Technologies
- ##### Java 16
- ##### Spring Boot (Data JPA)
- ##### Lombok
- ##### Maven
- ##### Git
- ##### PostgreSQL
- ##### Telegram bots API

## Set Up
1. Get the Telegram token<br>
@BotFather - start this bot<br>
Type /newbot command. There are will be further instructions.<br>
After creating the bot, you will be have the username and the token.<br><br>

2. Get the OpenWeatherMap API key<br>
https://openweathermap.org/guide<br>
Your API keys can always be found on your account page,<br>where you can also generate additional API keys if needed.<br>

3. Create posgres database, smth like that: util_bot_repo<br>
In this database, you have to create some tables: note, admin (sql queries for creating here: https://pastebin.com/D1XGbzey).<br>

4. Create application.properties file in "src/main/resources" and input there this properties:<br>
(tokens, api keys, and other info ou)<br>
```
spring.datasource.url=jdbc:postgresql://localhost:5432/YOUR_DB_NAME
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
 
telegram.bot.username=YOUR_BOT_USERNAME
telegram.bot.token=YOUR_BOT_TOKEN
open.weather.api.token=YOUR_OPENWEATHERMAP_API_KEY
 
bot.owner.admin.id=YOUR_TELEGRAM_IDENTIFIER
user.support.chat.id=TG_GROUP_SUPPORT_CHAT_ID
```
5. Run the application, and start you bot

## Overview
✔ Weather command <br><br>
<a href="https://ibb.co/rwxqcMT"><img src="https://i.ibb.co/6WHG1bV/Screenshot-41.png" alt="Screenshot-41" border="0"></a> <br><br>

✔ Weather command <br><br>

