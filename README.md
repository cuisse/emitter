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
var emitter    = new Emitter<String>();
var subscriber = new Subscriber<>("messages", String.class, (String value, SubscriberContext context) -> {
    if (value.equals("hello")) {
        System.out.println("Did you mean 'Hello World!'?");
    }
});

emitter.subscribe(subscriber);
emitter.emit("messages", "hello");
emitter.unsubscribe(subscriber);
System.out.println("Size: " + emitter.size());

// Output: Did you mean 'Hello World!'?
// Size: 0
```

#### Sharing data between subscribers
```java
var emitter = new Emitter<String>();
emitter.subscribe(new Subscriber<>("messages", String.class, (String value, SubscriberContext context) -> {
    if (value.equals("hello")) {
        context.message().set("Hello World!");
    }
}));
emitter.subscribe(new Subscriber<>("messages", String.class, (String value, SubscriberContext context) -> {
    if (context.message().get() != null) {
        System.out.println("Message received: " + context.message().get());
    }
}));

emitter.emit("messages", "hello");

// Output: Message received: Hello World
```

#### Returning a value from the subscribers
```java
var emitter = new Emitter<String>();
emitter.subscribe(new Subscriber<>("messages", String.class, (String value, SubscriberContext context) -> {
    if (value.equals("hello")) {
        context.output().set("Hello World!");
    }
}));

NotificationReceipt receipt = emitter.emit("messages", "hello");
System.out.println("Output: " + receipt.output());

// Output: Output: Hello World!
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

Todos:
- [ ] Document the project
- [ ] Add more examples
- [ ] Add more tests
- [ ] Upload to Maven Central