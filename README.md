# Utility telegram bot

## Description:<br>
Telegram bot written in Java for personal use.<br>

✔️ Weather <br>
✔️ Random number in chosen range <br>
✔️ Password generator <br>
✔️ User support system <br>
✔️ Notes manager <br>

(Examples of commands using in "Overview" part in readme)<br><br>

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

3. Create posgres database, name smth like that: util_bot_repo<br>
In this database, you have to create some tables: note, admin (sql queries for creating here: https://pastebin.com/D1XGbzey).<br>

4. Create application.properties file in "src/main/resources" and input there this properties:<br>
(tokens, api keys, and other info you have got earlier)<br>
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
5. Run the application, and start your bot

## Overview
✔ Weather command <br>
![1](https://user-images.githubusercontent.com/72043323/138588309-76c79ed5-b332-4bdc-ae88-8371d15076e7.png)

✔ Password generator <br>
![2](https://user-images.githubusercontent.com/72043323/138588312-44733267-1816-484a-9bed-129feb53786b.png)

✔ Random number in chosen range <br>
![3](https://user-images.githubusercontent.com/72043323/138588313-95808be8-c96a-4c7c-acd1-27db3a661d7d.png)

✔ Notes manager <br>
![4](https://user-images.githubusercontent.com/72043323/138588331-8af5e6bb-6ce6-46ba-b40e-7ec79c6f84a0.png)

✔ Users support system <br><br>
User's chat (ask): <br>
![5](https://user-images.githubusercontent.com/72043323/138588344-b00835a8-c401-481f-ae07-45bdafa49ddf.png)

User's ask in the admin support group: <br>
![6](https://user-images.githubusercontent.com/72043323/138588346-18e05883-74d5-4a8a-8244-ba93ca68e7af.png)

User's chat (response): <br>
![7](https://user-images.githubusercontent.com/72043323/138588349-8f5e1195-4602-4238-a84e-e0c173cab3af.png)
