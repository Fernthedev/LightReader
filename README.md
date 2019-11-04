# LightReader
This is an API for reading simple scripts that manipulates GPIO pins


## Download: 
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
