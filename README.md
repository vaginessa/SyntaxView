# SyntaxView
Code beautifier for Android - a wrapper around a WebView running [HighlightJS](https://highlightjs.org/)

#Dependency

Add jitpack.io to your root build.gradle, eg:

```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

then add the dependency to your project build.gradle:

```groovy
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.github.fiskurgit:SyntaxView:1.0.2'
}
```
You can find the latest version in the releases tab above: https://github.com/fiskurgit/SyntaxView/releases

More options at jitpack.io: https://jitpack.io/#fiskurgit/SyntaxView

##Licence

Full licence here: https://github.com/fiskurgit/FloodMonitoring/blob/master/LICENSE.md

In short:

> The MIT License is a permissive license that is short and to the point. It lets people do anything they want with your code as long as they provide attribution back to you and don’t hold you liable.

##Known Issues
* View lifecycle not implemented, doesn't handle orientation changes
* Doesn't like large files

#Usage

If loading source code from file add the READ_EXTERNAL_STORAGE permission to your manifest.xml and add the [runtime permission to your Activity/Fragment](https://developer.android.com/training/permissions/requesting.html):

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

Add to your Android layout xml:
```xml
<eu.fiskur.syntaxview.SyntaxView
    android:id="@+id/syntaxview"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

Initialise as usual:
```java
SyntaxView syntaxView = (SyntaxView) findViewById(R.id.syntaxview);
```

Display code string:
```java
String helloWorld = "private static final String helloWorld = \"HelloWorld!\";";
syntaxView.loadString(helloWorld, "java");
```

Load file:
```java
syntaxView.loadFile(file);
```

#Theming

All [HighlightJS](https://highlightjs.org/) themes are available (the default is a dark theme called 'monokai-sublime'):
```java
String[] themes = syntaxView.themes();
...
syntaxView.setTheme("monokai");
```

You can also set the theme when you pass the code arguments:
```java
syntaxView.loadString(helloWorld, "java", "monokai");

//for a file:
syntaxView.loadFile(file, "monokai");
```

You can also set the ProgressBar loading spinner colour to match whichever theme you use:
```java
syntaxView.setLoadingColor(Color.parseColor("#00ffcc"));
```

