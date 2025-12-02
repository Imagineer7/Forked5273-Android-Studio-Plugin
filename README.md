# 5273-Android-Studio-Plugin
An [Android Studio](https://developer.android.com/studio) plugin intended to make writing [FTC](https://www.firstinspires.org/robotics/ftc) code easier. Made by Team **5273 ARC Thunder**

<!-- this is used to build the plugin, don't remove these comments -->
<!-- Plugin description -->
## Features (Implemented)

**FTC Project Initiation Wizard**
- Automated cloning of FTC Robot Controller SDK
- Support for full and shallow clones
- One-click integration setup:
  - FTC Dashboard (real-time telemetry)
  - Road Runner (advanced path following)
  - FTCLib (command-based framework)
  - Pedro Pathing (autonomous paths)
  - Sloth (hot reload)
- Starter code generation (Java & Kotlin)
- Team number customization
- [Full Documentation](docs/FTC_PROJECT_WIZARD.md)

Easy **Opmode Creation**
- Support for **Java** & **Kotlin**
- Selector for **LinearOpMode** vs **Iterative (Regular) Opmode**
- Selector for **Autonomous** vs **TeleOp**

![Demo gif](https://github.com/user-attachments/assets/63721ec3-4e31-4f57-bb91-496abb123d77)
## Features (Planned)

Quick **FSM Creation**
- Simple Enum and Switch-Case (partially implemented)

**Autofilling Constructors**
- Automatic hardware map instantiation with class names
- Potentially using XML HardwareMap for autofilling

System to contribute **Open Source Templates directly through Android Studio**
- Teams get easy outreach!
- No fiddling around with github required - all in IDE
- Focus particularly on templates for specific libraries :D
<!-- Plugin description end -->