package org.example;

public class CustomExceptions {
    public static class NotImplementedException extends RuntimeException {
        public NotImplementedException() {
            super("This functionality is not implemented yet. Come back soon");
        }
    }
}
