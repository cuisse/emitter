### Java Event Emitter

#### Basic Usage
```java
var emitter = new Emitter<String>();
emitter.subscribe(new Subscriber<>("messages", String.class, (String value, SubscriberContext context) -> {
    if (value.equals("hello")) {
        System.out.println("Did you mean 'Hello World!'?");
    }
}));

emitter.emit("messages", "hello");

// Output: Did you mean 'Hello World!'?
```


You can also unsubscribe at any time. Lets use the same example as above.
```java
var emitter = new Emitter<String>();
emitter.subscribe(new Subscriber<>("messages", String.class, (String value, SubscriberContext context) -> {
    if (value.equals("hello")) {
        System.out.println("Did you mean 'Hello World!'?");
        context.unsubscribe().set(true); // and see the magic happen
    }
}));

emitter.emit("messages", "hello");
System.out.println("Size: " + emitter.size());

// Output: Did you mean 'Hello World!'?
// Size: 0
```

You can also unsubscribe any subscriber in this way.
```java
var emitter = new Emitter<Integer>();
var subscriber = new Subscriber<>("numbers", Integer.class, (Integer value, SubscriberContext context) -> {
    if (value.equals(5)) {
        System.out.println("5 fingers!");
    }
});

emitter.subscribe(subscriber);
emitter.emit("numbers", 5);
emitter.unsubscribe(subscriber);
System.out.println("Size: " + emitter.size());

// Output: 5 fingers!
// Size: 0
```

#### Sharing data between subscribers
```java
var emitter = new Emitter<String>();
emitter.subscribe(new Subscriber<>("messages", String.class, (String value, SubscriberContext context) -> {
    if (value.equals("hello")) {
        context.message().set(
            List.of("Hello", "World")
        );
    }
}));
emitter.subscribe(new Subscriber<>("messages", String.class, (String value, SubscriberContext context) -> {
    if (context.message().get() != null) {
        System.out.println("Message received: " + context.message().get());
    }
}));

emitter.emit("messages", "hello");

// Output: Message received: [Hello, World] (List<String>)
```

#### Returning a value from the subscribers
```java
var emitter = new Emitter<String>();
emitter.subscribe(new Subscriber<>("messages", String.class, (String value, SubscriberContext context) -> {
    if (value.equals("5 + 5")) {
        context.output().set(10);
    }
}));

NotificationReceipt receipt = emitter.emit("messages", "5 + 5");
System.out.println("Output: " + receipt.output());

// Output: Output: 10 (Integer)
```

#### Handling errors
```java
var emitter = new Emitter<String>();
emitter.subscribe(new Subscriber<>("messages", String.class, (String value, SubscriberContext context) -> {
    if (value.equals("hello") == false) {
        throw new RuntimeException("I need a 'hello' message!");
    }
}));

emitter.emit("messages", "hello", (Throwable error, SubscriberContext context) -> {
    System.out.println("Error: " + error.getMessage());
    return false; // stops the propagation
});

// Output: Error: I need a 'hello' message!
```

You can continue the propagation by returning `true` in the error handler. Also, you can freely modify the context in the error handler.



##### Todos:
- [ ] Document the project
- [ ] Add more examples
- [ ] Add more tests
- [ ] Upload to Maven Central