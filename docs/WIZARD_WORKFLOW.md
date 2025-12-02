# FTC Project Wizard Workflow

## User Journey

```
┌─────────────────────────────────────────────────────────────┐
│                    Start Wizard                              │
│              File → New → FTC Project Wizard                 │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  Step 1: Project Setup                       │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ Project Location: [~/FtcRobotController] [Browse...] │  │
│  │ Team Number:      [5273]                              │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                 Step 2: Clone Options                        │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ Clone Type:  ⦿ Full clone (includes history)         │  │
│  │              ○ Shallow clone (faster, no history)    │  │
│  │ Branch:      [master]                                 │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                Step 3: Integrations                          │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ ☐ FTC Dashboard (real-time telemetry)                │  │
│  │ ☐ Road Runner Quick Start (path following)           │  │
│  │ ☐ FTCLib (command-based framework)                   │  │
│  │ ☐ Pedro Pathing (autonomous paths)                   │  │
│  │ ☐ Sloth (hot reload)                                 │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                 Step 4: Starter Code                         │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ ☑ Generate sample OpMode                             │  │
│  │ ☑ Generate hardware configuration class              │  │
│  │ Language:  ⦿ Java  ○ Kotlin                          │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼ [Click OK]
┌─────────────────────────────────────────────────────────────┐
│                 Execution Phase                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ Progress: Cloning FTC Robot Controller SDK...        │  │
│  │ [████████████████████──────────────] 65%             │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                  Success!                                    │
│  FTC project has been created successfully at:               │
│  ~/FtcRobotController                                        │
└─────────────────────────────────────────────────────────────┘
```

## Technical Workflow

```
User Action
    │
    ▼
┌────────────────────────┐
│ FTCProjectWizardAction │ ◄─── Registered in plugin.xml
└───────────┬────────────┘
            │ actionPerformed()
            ▼
┌────────────────────────┐
│ FTCProjectWizardDialog │ ◄─── Multi-tab UI (JTabbedPane)
│  - Step 1: Location    │
│  - Step 2: Clone opts  │
│  - Step 3: Integrations│
│  - Step 4: Starter code│
└───────────┬────────────┘
            │ showAndGet()
            ▼
┌─────────────────────────┐
│FTCProjectWizardExecutor│ ◄─── Background task orchestrator
└───────────┬─────────────┘
            │ execute()
            │
            ├─────────────► Step 1: Clone Repository
            │               ┌────────────────┐
            │               │ FTCGitCloner   │
            │               │  - Uses JGit   │
            │               │  - Progress    │
            │               └────────────────┘
            │
            ├─────────────► Step 2: Configure Integrations
            │               ┌──────────────────┐
            │               │ FTCGradleEditor  │
            │               │  - Edit build.   │
            │               │    gradle        │
            │               │  - Add repos     │
            │               │  - Add deps      │
            │               └──────────────────┘
            │
            └─────────────► Step 3: Generate Code
                            ┌───────────────────────┐
                            │FTCStarterCodeGenerator│
                            │  - SampleTeleOp       │
                            │  - RobotHardware      │
                            │  - Java/Kotlin        │
                            └───────────────────────┘
```

## Data Flow

```
User Input (Dialog)
    │
    ├─ projectLocation ────────┐
    ├─ teamNumber ──────────────┤
    ├─ branch ──────────────────┤
    ├─ isShallow ───────────────┤
    ├─ integrations[] ──────────┤
    ├─ generateOpMode ──────────┤
    ├─ generateHardware ────────┤
    └─ isJava ──────────────────┤
                                │
                                ▼
                        ┌───────────────┐
                        │   Executor    │
                        └───────┬───────┘
                                │
            ┌───────────────────┼───────────────────┐
            │                   │                   │
            ▼                   ▼                   ▼
    ┌───────────┐       ┌───────────┐      ┌──────────────┐
    │ Git Clone │       │  Gradle   │      │ Code Gen     │
    │           │       │  Config   │      │              │
    │ FTC SDK   │──────►│           │─────►│ SampleTeleOp │
    │ Repo      │       │ + Deps    │      │ + Hardware   │
    └───────────┘       └───────────┘      └──────────────┘
            │                   │                   │
            └───────────────────┴───────────────────┘
                                │
                                ▼
                        Project Ready!
```

## Integration Flow

When user selects integrations:

```
FTC Dashboard Selected
    │
    ▼
┌────────────────────────────────────┐
│ Add Maven Repository               │
│ maven { url 'maven.brott.dev/' }   │
└──────────────┬─────────────────────┘
               │
               ▼
┌────────────────────────────────────┐
│ Add Dependency                     │
│ implementation 'dashboard:0.4.16'  │
└────────────────────────────────────┘

Road Runner Selected
    │
    ▼
┌────────────────────────────────────┐
│ Add Maven Repository (if needed)   │
│ maven { url 'maven.brott.dev/' }   │
└──────────────┬─────────────────────┘
               │
               ▼
┌────────────────────────────────────┐
│ Add Dependencies                   │
│ - roadrunner:core:0.5.6            │
│ - dashboard:0.4.16                 │
└────────────────────────────────────┘

FTCLib Selected
    │
    ▼
┌────────────────────────────────────┐
│ Add Dependency (Maven Central)     │
│ implementation 'ftclib:core:2.1.1' │
└────────────────────────────────────┘
```

## Error Handling Flow

```
User Input
    │
    ▼
┌────────────────┐
│ Validation     │
│  - Location OK?│
│  - Branch OK?  │
│  - Team # OK?  │
└────┬───────────┘
     │ Valid ✓
     ▼
┌────────────────┐
│ Clone Attempt  │
└────┬───────────┘
     │
     ├─ Success ──────────────► Continue
     │
     └─ Failure
         │
         ├─ Network Error ────► Show: "Check internet connection"
         ├─ Invalid Branch ───► Show: "Branch not found"
         ├─ Dir Not Empty ────► Show: "Directory not empty"
         └─ Permission ───────► Show: "Permission denied"
```

## File Structure After Completion

```
~/FtcRobotController/
│
├── .git/                          # Git repository
├── build.gradle                   # Root build config
├── settings.gradle                # Project structure
│
├── FtcRobotController/            # SDK module
│   ├── src/
│   └── build.gradle
│
└── TeamCode/                      # Your code here!
    ├── build.gradle               # ◄─ Modified with integrations
    │   ├── repositories {
    │   │   maven { url 'maven.brott.dev/' }  # If needed
    │   ├── }
    │   └── dependencies {
    │       implementation 'dashboard:0.4.16'  # If selected
    │       implementation 'roadrunner:0.5.6'  # If selected
    │       # ... other integrations
    │   }
    │
    └── src/main/java/org/firstinspires/ftc/teamcode/
        ├── SampleTeleOp.java      # ◄─ Generated if selected
        └── RobotHardware.java     # ◄─ Generated if selected
```

## Key Design Decisions

1. **JGit over Git CLI**
   - Cross-platform compatibility
   - No external dependencies
   - Progress monitoring built-in

2. **Text-based Gradle Editing**
   - Preserves user comments
   - Maintains formatting
   - Simple and reliable

3. **Multi-step Wizard (Tabs)**
   - Clear progression
   - Easy to understand
   - All options visible

4. **Background Task Execution**
   - UI remains responsive
   - Progress feedback
   - Cancellable operation

5. **Validation at Multiple Levels**
   - Dialog validation (UI)
   - Executor validation (logic)
   - Component validation (operations)

## Extension Points

Future developers can extend:

1. **Add New Integrations**
   - Add method to FTCGradleEditor
   - Add checkbox to dialog Step 3
   - Update documentation

2. **Add New Code Templates**
   - Add method to FTCStarterCodeGenerator
   - Add checkbox to dialog Step 4
   - Support Java and Kotlin

3. **Customize Clone Behavior**
   - Modify FTCGitCloner
   - Add options to dialog Step 2

4. **Change UI Flow**
   - Modify FTCProjectWizardDialog
   - Adjust tab structure or layout
