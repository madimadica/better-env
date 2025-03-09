package com.madimadica.betterenv;

/**
 * <p>
 *     When a POJO used with {@link BetterEnv#load(Class)} does not have a valid set of environment variables
 *     to extract values from, then this exception is thrown.
 * </p>
 * <p>
 *     The exception message will not contain any environment variable values, only general information on what was wrong with the variable.
 * </p>
 * <p>
 *     The exception message will contain a summary of each invalid <em>field</em>, and within the field summary a summary of each attempted key
 *     and error will be added. Consider this example.
 * </p>
 * <pre>
 * Error binding type "com.example.Example":
 *   Field "foo":
 *     "FOO": Missing environment variable
 *     "FOO2": Cannot be blank
 *   Field "bar":
 *     "BAR": Failed to coerce type to "java.lang.Long": NumberFormatException
 * </pre>
 * <p>
 *     If using a {@link Env.Fallback} annotation with an invalid binding, the field key will be logged as {@value EnvMetadata#FALLBACK_KEY}.
 * </p>
 */
public class InvalidEnvironmentException extends RuntimeException {
    /**
     * Construct an exception with the given error message details
     * @param message String exception message
     */
    public InvalidEnvironmentException(String message) {
        super(message);
    }
}
