# Fifa19Bot

The repository contain AI bot for game Fifa 19.

### How run

###### For run requirements: `java 17+`

###### From root directory run: `gradlew run`

###### Optional args: `-logging` `-replayer`

1. `-logging` - all screenshots will be saved to _**logs/TestImages**_
2. `-replayer` will be run in **_Replayer mode_**

### List modules

`Main` - Entry point for app

`ImageAnalysis` - This class performing base analysis of football field scheme image

`GeometryUtils` - This class provides utils methods for base 2d geometry

`GameInfo` - In the class storing both static information which are time immutable and dynamic data which are actually
for one image screenshot

`GameHistory` - This class represent prev game states and prev targets decision

`GameAction` - This class represent in-game control actions

`DecisionMaker` - This class take responsible for deciding by creating best {@code GameAction} based on {@code GameInfo}
data

`ActionProducer` - This class take responsible for events generation. Now is available only keyboard actions

`ImageLogProducer` - This class create full_game.png image with represent prev game states and log information. It needs
to for debugging


