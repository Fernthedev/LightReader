# LightReader
This is an API for reading simple scripts that manipulates GPIO pins


## Download: 

[![](https://jitpack.io/v/Fernthedev/LightReader.svg)](https://jitpack.io/#Fernthedev/LightReader)

### Maven: 
```xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
    <groupId>com.github.Fernthedev</groupId>
    <artifactId>LightReader</artifactId>
    <version>Tag</version>
</dependency>
  ```
  
  ### Gradle
  ```gradle
repositories {
...
maven { url 'https://jitpack.io' }
}
    
dependencies {
    implementation 'com.github.Fernthedev:LightReader:Tag'
}
  ```
  
  Example: 
  ```
  pin 0 true // pin {WiringPi pin number/all} {state}
  sleep 5 // Wait in time in milliseconds
  pin all true
  pin 0 false
  print Hi! // print messages in system
  ```

## Usage
```java
LightFileFormatter.executeLightFile(File); // Executes the .pia file async
LightFileFormatter.readDirectory(File folder); // Executes all the .pia files in the folder async

LightParser.parseFile(File); // Returns an instance of ILightLine parsed from the file
LightParser.parseFolder(File folder); // Returns a list of LightFile instances, which can be used to get the ILightLine object instances  LightParser.formatLightLineToString(lightLine) // Returns a string representation of the LightLine instance with the arguments required and their corresponding values.
LightParser.formatLightLineToString(LightLine.class) // Returns a string representation of the LightLine instance with the arguments required
```

### To create a custom LightLine command 
```java
import io.github.fernthedev.light.api.annotations.LineArgument;
import io.github.fernthedev.light.api.annotations.LineData;
import io.github.fernthedev.light.api.lines.ILightLine;
import lombok.NonNull;

@LineData(name = "customname") // This is the name used to call the line command in the .pia file
public class CustomLightLine extends ILightLine {

    @LineArgument(name = "argument1name") // The name in the argument that is used in formatString. Not required but recommended to add to all data fields required to construct the LightLine.
    private Object argument;
    
    // Is used to create a "null" instance of the light line command, used only for registering in LightParser
    public CustomLightLine(NullObject nullObject) {
        super(nullObject);
        argument = null;
    }

    public CustomLightLine(@NonNull String line, int lineNumber, @NonNull Object argument) {
        super(line, lineNumber);
        this.argument = argument;
    }

    public CustomLightLine(ILightLine lightLine, @NonNull Object argument) {
        super(lightLine);
        this.argument = argument;
    }

    @Override
    public @NonNull ILightLine constructLightLine(ILightLine lightLine, String[] args) {
        // Create an instance of the class using the lightLine instance and arguments used in the command.
        // Example:
        return new CustomLightLine(lightLine, args[0]);
    }

    @Override
    public void execute() {
        // Execute the code with the arguments
        System.out.println(argument.toString());
    }
}
```
After you have your custom line instance, you can register the Light Line instance for the parser to recognize using the
```java
LightParser.registerLightLine(new CustomLightLine(NullObject.NULL_OBJECT)); // The object is almost an instance with almost no data.
```
