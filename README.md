This is an project made by myself that can be useed in chess games as a digital clock.

Introduction =

Chess Clock is a practical and user-friendly Android chess timer app designed for chess players of all levels. It features a simple interface that makes setting up and managing game timers straightforward and efficient. The app supports various time controls, including rapid, blitz, and custom settings, and allows for adjustments like increment and delay. With multiple presets and the ability to save your preferred configurations, Chess Clock caters to both casual and competitive players. Its reliable performance ensures accurate timekeeping, helping you focus on your game without worrying about the clock. Chess Clock is the perfect tool for anyone looking to improve their chess experience on an Android device.

How it works =

The MainActivity class in this chess timer program is the primary controller for a variety of functions like  timer initialization, player turn management, and timer modes such as Blitz, Rapid, and Custom. When the program launches, it establishes default timer settings (10 minutes for Blitz mode. The Handler object is important for controlling the countdown in one-second intervals. It refreshes the timer display for each player and checks for timeouts. If a player's time runs out, the program will pause the timer, display a message declaring the winner, and provide a window for picking the next game mode. I also added various.
Functions to meet the given qualities. 

Key features =

Key features include automatic keyboard dismissal after custom time entry, error handling with Toast notifications, and dynamic control of button visibility. The app also incorporates sound alerts for time warnings and smooth performance, even with simultaneous timers under 20 seconds. Additionally, a radio button within a winning dialog updates the spinner based on the selected game mode, providing a seamless user experience.
