# ddth-recipes: Global

_Global recipe added since version `v0.3.0`._

Global recipe provide a global static class to store/access/share data within the application.

## Maven dependency

No extra libs are required.

## Usage examples

**Add a shutdown hook, which to be called right before application's shutdown:**

```java
import com.github.ddth.recipes.global.GlobalRegistry;

void myShutdownHook() {
    try {
        logger.info("Closing closeable...");
        closeable.close();
    } catch (Exception e) {
        logger.warn(e.getMessage(), e);
    }

    try {
        logger.info("Shutting down executorPool...");
        executorPool.shutdown();
    } catch (Exception e) {
        logger.warn(e.getMessage(), e);
    }
}

GlobalRegistry.addShutdownHook(() -> myShutdownHook());

/*
// multiple hooks can be added
GlobalRegistry.addShutdownHook(() -> closeable.close());
GlobalRegistry.addShutdownHook(() -> executorPool.shutdown());
GlobalRegistry.addShutdownHook(() -> myShutdownHook3());
 */
```

**Share data globally:**

```java
import com.github.ddth.recipes.global.GlobalRegistry;

// put a value to to global storage
GlobalRegistry.putToGlobalStorage("key", "a string");

// retrieve a value from global storage
Object value = GlobalRegistry.getFromGlobalStorage("key");

// retrieve a value from global storage with expected type
String valueStr = GlobalRegistry.getFromGlobalStorageOptional("key", String.class).orElse("default value");
```
