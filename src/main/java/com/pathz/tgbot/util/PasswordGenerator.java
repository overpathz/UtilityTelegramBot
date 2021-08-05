package com.pathz.tgbot.util;

import org.springframework.stereotype.Component;

public final class PasswordGenerator {

    public static String generatePassword(int length) {
        StringBuilder resultRandomPassword = new StringBuilder();
        for(int j = 0; j < length; j++) {
            resultRandomPassword.append(randomCharacter());
        }
        return resultRandomPassword.toString();
    }

    private static char randomCharacter() {

        int rand = (int)(Math.random()*62);

        if(rand <= 9) {
            int number = rand + 48;
            return (char)(number);

        } else if(rand <= 35) {
            int uppercase = rand + 55;
            return (char)(uppercase);

        } else {
            int lowercase = rand + 61;
            return (char)(lowercase);
        }

    }
}
