## Overview
A simple library for working with environment-variables in Java.
It is licensed under the **MIT License**.


It has no runtime dependencies and runs on Java 8 or higher. The primary use-case is to map and bind
environment variables to an instance of a POJO type. It supports binding normal classes and records using reflection.

---

## Getting Started
Java Version: 8 or higher

### Adding Dependency
#### Maven
```xml
  <dependency>
    <groupId>com.madimadica</groupId>
    <artifactId>better-env</artifactId>
    <version>0.0.1</version>
  </dependency>
```

#### Gradle
```groovy
implementation 'com.madimadica:better-env:0.0.1'
```


## Quick Start Tutorial
A detailed tutorial can be found below, but this section covers the most common use-cases to get started with.

The important classes to know about are 
* `@Env` field annotation to specify which environment variable to bind
* `BetterEnv` static methods to load POJOs or environment variables

Fields annotated with `@Env` will have the environment variable's value coerced into the declared type,
so `@Env("FIZZ_BUZZ") int fizzbuzz` will convert `System.getenv("FIZZ_BUZZ")` to an `int`.

### Defining a POJO `record`
The most straightforward case is with a `record` type and the default values
```java
public record DatabaseEnv (
  @Env("DB_USER") String user,
  @Env("DB_PASS") String password,
  @Env("DB_SERVER") String server,
  @Env("DB_PORT") int port
) {}
```
### Defining a POJO `class`
Nearly as straightforward as with records, here is the same thing as a normal class.
```java
public class DatabaseEnv {
    @Env("DB_USER")
    private String user;
    
    @Env("DB_PASS")
    private String password;
    
    @Env("DB_SERVER")
    private String server;

    @Env("DB_PORT")
    private int port;
    
    // Using default constructor
    
    // Getters
}
```

### Loading a POJO
Whether you have a `record` or `class`, loading is the same:
```java
public class Main {
    public static void main(String[] args) {
        DatabaseEnv dbEnv = BetterEnv.load(DatabaseEnv.class);
    }
}
```

### Defaults
The default behavior with `@Env` in the examples above is `required = true` and `allowBlank = false`.
This means that if an environment variable cannot be found, or is empty, an exception will be thrown
when you try to load it.

Avoiding blanks is helpful incase with certain situations, such as with GitHub Actions.
When a workflow defines a key under `env:`, if the variable/secret cannot be resolved,
it will default to an empty string, but they key still exists.

---

## Advanced Tutorial
### Multiple Environment Variable Names
It could be useful to try multiple options when resolving an environment variable.
You can do this by providing an array for the `@Env#value`, such as in the following example.
```java
public class MultipleNamesExample {
    @Env({"DB_USER", "DB_USERNAME", "DATABASE_USER"})
    private String user;
}
```
In this example, we first try to find `DB_USER`, then `DB_USERNAME`, and finally `DATABASE_USER`.
The first valid one to be found is used, and the rest are ignored.

---

### Hardcoded Fallbacks (`@Env.Fallback`)
If having multiple environment variables to try isn't enough, you can add a hardcoded value with
`@Env.Fallback`, such as in
```java
public class MultipleNamesExample {
    @Env({"DB_USER", "DB_USERNAME", "DATABASE_USER"})
    @Env.Fallback("admin")
    private String user;
}
```
In this example, if `DB_USER`, `DB_USERNAME`, and `DATABASE_USER` are all invalid, then we
will instead use the hardcoded fallback value of `admin`.

---

### `required` attribute
By default, `@Env` annotations will have `required = true`. Setting this to `false` will bind
the variable to `null` instead of throwing an exception. In the case of primitive types not resolving,
an exception will still be thrown (as `null` cannot be assigned).

### `allowBlank` attribute
By default, `@Env` annotations will have `allowBlank = false`. Setting this to `true` will allow
an empty String or String of whitespaces to be a valid value. Note, this will still fail if the
value cannot be coerced into to declared type, such as a blank string to an `int`. Thus, `allowBlank`
is most useful with `String` declared types.

---

### Type Coercion
Based on the declared type, the String literal from the environment will be coerced into
the declared type. Below is a list of supported types for coercion, and validation requirements.
Note that primitive types *cannot* be `null`. Reference types can be `null`,
which may be invalid based on the `required` attribute.

| Declared Type | Mapped By                                                     |
|---------------|---------------------------------------------------------------|
| String        | N/A                                                           |
| byte          | `Byte.parseByte`                                              |
| Byte          | `Byte.parseByte`                                              |
| short         | `Short.parseShort`                                            |
| Short         | `Short.parseShort`                                            |
| int           | `Integer.parseInt`                                            |
| Integer       | `Integer.parseInt`                                            |
| long          | `Long.parseLong`                                              |
| Long          | `Long.parseLong`                                              |
| float         | `Float.parseFloat`                                            |
| Float         | `Float.parseFloat`                                            |
| double        | `Double.parseDouble`                                          |
| Double        | `Double.parseDouble`                                          |
| char          | `s.charAt(0)` and `s.length() == 1`                           |
| Character     | `s.charAt(0)` and `s.length() == 1`                           |
| boolean       | `"true".equalsIgnoreCase(s)` or `"false".equalsIgnoreCase(s)` |
| Boolean       | `"true".equalsIgnoreCase(s)` or `"false".equalsIgnoreCase(s)` |
| BigInteger    | `new BigInteger(s)`                                           |
| BigDecimal    | `new BigDecimal(s)`                                           |

All other types are unsupported. 

Note that for the byte/short/int/long/float/double, these are only considered invalid
if their corresponding static `parse` method throws a `NumberFormatException`. Booleans
are considered invalid if they are neither `true` nor `false` (case-insensitive).
Characters throw an exception if they are not exactly a length of `1`.

---

### Constructors
The first thing the loader does is check for a constructor matching all the `@Env` declared fields
in their encounter order. If such a constructor exists, it will be used. For records, this
will always be the canonical constructor.

If no matching constructor for all the `@Env` fields is found, the no-args constructor
will be used.

If both an overloaded and no-args constructor are provided, only the overloaded constructor
is tried, and the no-args is ignored.

If neither of these constructors are provided, an exception is thrown.

---

### Non-`Env` fields
For records, all fields *must* have an `@Env` annotation. As for classes, as many or as few 
as you want can have `@Env` annotations.

### `static` fields
All `static` fields are ignored.

### `final` fields
Class fields may be `final` if an overloaded constructor matching all the `@Env` fields is provided,
otherwise an exception will be thrown when trying to set a final field.

---

### Errors
If any of the above requirements are not met, an exception will be thrown. None of the exceptions thrown
will ever contain environment variable values, and standard exceptions like `NumberFormatException` will exclude
the message for the input string and be masked into a `InvalidEnvironmentException`.

Constructor or field declaration errors should result in `InvalidEnvTypeException` and have simple error messages.

Environment loading errors will have a more elaborate message pointing out which fields
and environment variables could not be loaded into a valid state, and the issue associated with each.
For example, you might receive this error message:
```
Failed to load env data for type "com.example.Example":
	Field "foo":
		"FOO1": Missing environment variable
		"FOO2": Cannot be blank
	Field "bar":
		"BAR1": Failed to coerce type to "java.lang.Long": NumberFormatException
	Field "baz":
		"BAZ1": Failed to coerce type to "java.lang.Boolean": Expected 'true' or 'false' (case-insensitive)
```
Only the fields which are invalid are included in the message. Additionally, a field is only 
considered invalid if *none* of the variables could be resolved to a valid value, however if you tried `DB_USER`
and it was invalid, but the next option for `DB_USERNAME` was valid, then no exception/error information
will be logged for that field.

---

### Extra Methods
There are 3 additional static methods on `BetterEnv` to help with loading environment variables
without binding it to a POJO. They are
* `Optional<String> get(String name)`
* `Optional<Integer> getInt(String name)`
* `Optional<Long> getLong(String name)`

If the given environment variable name does not exist, then an `Optional.empty()` is returned.
Additionally, for `getInt` and `getLong`, if `parseInt` and `parseLong` throw, then an `Optional.empty()`
is thrown.

Here is an example of using these utility methods:
```java
public static void main(String[] args) {
    Optional<String> javaHome = BetterEnv.get("JAVA_HOME");
    Optional<Integer> processors = BetterEnv.getInt("NUMBER_OF_PROCESSORS");
}
```

---

## Contact / Support
For bug reports and feature requests, please start by creating a new GitHub Issue.

