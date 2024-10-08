package com.main.Jora.configs;

public class CustomException{
    //Исключения, выбрасываемые при присоединении пользователя к проекту
    public static class UserAlreadyJoinedException extends Exception{
        public UserAlreadyJoinedException(String message) {
            super(message);
        }
    }
    public static class UserBannedException extends Exception{
        public UserBannedException(String message){
            super(message);
        }
    }
}
