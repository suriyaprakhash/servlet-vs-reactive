# ServletWebApplication

## Test

### Local machine

```
java -jar ./target/ServletWebApplication.jar -javaagent:C:\Users\suriy\main\Softwares\Installed\glowroot-0.14.2-dist-1\glowroot\glowroot.jar -Dglowroot.agent.port=4001
```

## Glowroot

```
http://localhost:4001
```

## Issue

- When using Glowroot in debug mode - https://youtrack.jetbrains.com/issue/IDEA-360896/java.lang.IncompatibleClassChangeError-Class-ch.qos.logback.classic.Level-does-not-implement-the-requested-interface
 