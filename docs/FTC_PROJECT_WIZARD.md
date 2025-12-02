# FTC Project Initiation Wizard

## Overview

The FTC Project Initiation Wizard is a comprehensive tool for creating new FTC Robot Controller projects. It automates the process of cloning the official FTC SDK, configuring integrations, and generating starter code.

## Features

### 1. Automated SDK Cloning
- Clones the official FIRST Tech Challenge Robot Controller SDK
- Supports both full and shallow clones
- Branch selection (master, or any specific branch)
- Progress monitoring during clone operation
- Automatic validation of cloned repository

### 2. Integration Support

The wizard can automatically configure popular FTC libraries and tools:

#### FTC Dashboard
- Real-time telemetry and configuration
- Live graph plotting
- Camera stream viewing
- Repository: https://github.com/acmerobotics/ftc-dashboard
- Maven: `com.acmerobotics.dashboard:dashboard:0.4.16`

#### Road Runner Quick Start
- Advanced path following and trajectory generation
- Motion profiling for smooth autonomous movement
- Integration with FTC Dashboard
- Repository: https://github.com/acmerobotics/road-runner-quickstart
- Maven: `com.acmerobotics.roadrunner:core:0.5.6`

#### FTCLib
- Command-based programming framework
- Hardware abstraction layer
- Utility classes for common tasks
- Repository: https://github.com/FTCLib/FTCLib
- Maven: `org.ftclib.ftclib:core:2.1.1`

#### Pedro Pathing
- Pure pursuit path following
- Autonomous path generation
- Repository: https://github.com/pedropathing/pedroPathing
- Note: Requires manual source file integration

#### Sloth
- Hot reload for faster iteration
- Automatic code reloading without rebuilding
- Repository: https://github.com/Dairy-Foundation/Sloth
- Maven: `com.github.Dairy-Foundation:Sloth:1.0.0`

### 3. Starter Code Generation

The wizard can generate sample code to help you get started:

#### Sample TeleOp OpMode
- Basic tank drive control
- Gamepad input handling
- Telemetry display
- Available in Java or Kotlin

#### Hardware Configuration Class
- Pre-configured for 4-motor mecanum drive
- Hardware initialization methods
- Motor direction and behavior setup
- Available in Java or Kotlin

## Usage

### Starting the Wizard

1. Open Android Studio
2. Go to **File → New → FTC Project Wizard**
3. The multi-step wizard will open

### Step 1: Project Setup

**Project Location:**
- Choose where to create the project
- Default: `~/FtcRobotController`
- The directory will be created if it doesn't exist

**Team Number (Optional):**
- Your FTC team number
- Used to organize OpModes by group
- Example: `5273`

### Step 2: Clone Options

**Clone Type:**
- **Full Clone:** Includes complete Git history (slower, larger)
- **Shallow Clone:** Single commit only (faster, smaller)

**Branch:**
- Default: `master`
- Can specify any branch name (e.g., `v9.0`, `develop`)

### Step 3: Integrations

Select any combination of integrations:
- Check the boxes for libraries you want to use
- Dependencies will be automatically added to TeamCode/build.gradle
- Maven repositories will be configured as needed

### Step 4: Starter Code

**Generate Sample OpMode:**
- Creates `SampleTeleOp.java` or `SampleTeleOp.kt`
- Basic tank drive implementation
- Ready to customize

**Generate Hardware Configuration Class:**
- Creates `RobotHardware.java` or `RobotHardware.kt`
- Pre-configured for mecanum drive
- Easy to extend with additional hardware

**Language:**
- Choose Java or Kotlin
- Affects generated starter code only

### Completing the Wizard

1. Click **OK** to start the process
2. Monitor progress in the progress indicator
3. Wait for the clone and configuration to complete
4. A success message will appear when finished

## Project Structure

After completion, your project will have this structure:

```
FtcRobotController/
├── TeamCode/
│   ├── src/main/java/org/firstinspires/ftc/teamcode/
│   │   ├── SampleTeleOp.java (if generated)
│   │   └── RobotHardware.java (if generated)
│   └── build.gradle (with selected integrations)
├── FtcRobotController/
├── build.gradle
├── settings.gradle
└── ... (other SDK files)
```

## Troubleshooting

### Clone Fails

**Problem:** Repository clone fails or times out

**Solutions:**
- Check your internet connection
- Try using a shallow clone (faster)
- Verify you're not behind a restrictive firewall
- Try a different branch if the default doesn't work

### TeamCode Directory Not Found

**Problem:** Wizard reports TeamCode directory missing after clone

**Solutions:**
- The SDK structure may have changed
- Verify the branch you selected is correct
- Clone may have been interrupted
- Try cloning manually to verify repository accessibility

### Integration Dependencies Not Added

**Problem:** Selected integrations don't appear in build.gradle

**Solutions:**
- Verify TeamCode/build.gradle exists
- Check file permissions
- Look for error messages in the IDE
- Manually add dependencies if needed

### Generated Code Not Compiling

**Problem:** Starter code has compilation errors

**Solutions:**
- Ensure the FTC SDK version is compatible
- Check that all dependencies are downloaded
- Run Gradle sync in Android Studio
- Verify hardware map names match your configuration

## Manual Integration

If you need to manually add integrations after wizard completion:

### Adding FTC Dashboard

1. Open `TeamCode/build.gradle`
2. Add repository:
   ```groovy
   repositories {
       maven { url 'https://maven.brott.dev/' }
   }
   ```
3. Add dependency:
   ```groovy
   dependencies {
       implementation 'com.acmerobotics.dashboard:dashboard:0.4.16'
   }
   ```

### Adding Road Runner

1. Open `TeamCode/build.gradle`
2. Add repository (if not already present):
   ```groovy
   repositories {
       maven { url 'https://maven.brott.dev/' }
   }
   ```
3. Add dependencies:
   ```groovy
   dependencies {
       implementation 'com.acmerobotics.roadrunner:core:0.5.6'
       implementation 'com.acmerobotics.dashboard:dashboard:0.4.16'
   }
   ```

### Adding FTCLib

1. Open `TeamCode/build.gradle`
2. Add dependency:
   ```groovy
   dependencies {
       implementation 'org.ftclib.ftclib:core:2.1.1'
   }
   ```

## Tips and Best Practices

1. **Start Simple:** Don't enable all integrations at once if you're new
2. **Team Number:** Use your actual FTC team number for organization
3. **Shallow Clone:** Use shallow clone unless you need Git history
4. **Starter Code:** Generate starter code to understand the pattern
5. **Commit Often:** Use Git to track your changes after wizard completion
6. **Sync Gradle:** Always sync Gradle after wizard completion
7. **Test Build:** Run a build immediately to ensure everything works

## Advanced Usage

### Custom Branch Selection

To use a specific SDK version:
1. Go to https://github.com/FIRST-Tech-Challenge/FtcRobotController
2. Find the branch or tag you want (e.g., `v9.0`)
3. Enter that name in the Branch field

### Multiple Projects

To create multiple projects:
1. Change the project location for each wizard run
2. Use different team numbers to distinguish projects
3. Consider using different integration combinations for different robots

### Team Collaboration

After wizard completion:
1. Initialize Git repository if not already done
2. Create a `.gitignore` for Android/Gradle
3. Push to your team's repository
4. Share integration choices with teammates

## Known Limitations

1. **Gradle Format:** Only supports Groovy build.gradle (not Kotlin DSL)
2. **Pedro Pathing:** Requires manual source file integration
3. **Version Updates:** Library versions are fixed in wizard
4. **SDK Structure:** Assumes standard FTC SDK structure
5. **Network Required:** Cannot run offline (clone requires internet)

## Version Information

- **Wizard Version:** 0.0.1-beta
- **JGit Version:** 6.10.0
- **Target SDK:** FTC Robot Controller (all versions)
- **IntelliJ Platform:** 242-252

## Support and Feedback

For issues, suggestions, or contributions:
- GitHub: https://github.com/Andover-Robotics/5273-Android-Studio-Plugin
- Issues: https://github.com/Andover-Robotics/5273-Android-Studio-Plugin/issues

## License

This wizard is part of Mjolnir for FTC, developed by 5273 ARC Thunder.
