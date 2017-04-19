# DoodleAppUsTwo

![alt tag](https://github.com/anbuuu/DoodleAppUsTwo/blob/master/Doodle_Default_screen.png)

The primary aim of this App is to follow the User Stories as below 

As a user I want to draw/doodle on my screen so I can kill some time
Acceptance criteria:
	User should be able to pick a background colour
	User should be able to pick a doodle colour

As a user I want to save my doodle to my phone so I can do something with it later on
Acceptance criteria:
It has to be saved in the phone gallery

As a user I want to set my doodle as the wallpaper of my phone so I can show it to my friends
Acceptance criteria:
I want to be notified when the wallpaper has been set

The Top Level Navigation in the App is of the following Order 

1. New Drawing -- Clears the current Drawing and creates a new One 
2. Paint Brush - User can choose the Size of the Brush ( Small, Medium and Large )
3. Erase - User can choose the Eraser Size ( Small, Medium and Large )
4. Fill Background - User can fill the background with the chosen color
5. Set as Wallpaper -- User can choose the current Drawing as their Device Wallpaper
6. Save Drawing - User can choose the drawing in the Device's Gallery Location

The Bottom Level navigation consists of the Color Palette which the user can choose from.

Notes
>> For Simplicity purpose made the App as Portrait Version only 
>> Set background takes a bit of time to do the fill background

# Building the App
- Can use Android Studio to import the project or 
- Can use Gradle Commands to build the App
- Go to App folder
-- ./gradlew clean build
-- ./gradlew assembleRelease
-- Install the Release APK on the Device 
