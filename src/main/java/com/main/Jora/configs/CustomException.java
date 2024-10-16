package com.main.Jora.configs;

public class CustomException{
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
    public static class LargeSizeException extends Exception{
        public LargeSizeException(String message) {super(message);}
    }
    public static class ObjectExistsException extends Exception{
        public ObjectExistsException(String message){super(message);}
    }
    public static class UserNotFoundException extends Exception{
        public UserNotFoundException(String message){super(message);}
    }
}
