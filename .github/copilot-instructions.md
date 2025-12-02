# GitHub Copilot Instructions for Mjolnir for FTC

## Project Overview

**Mjolnir for FTC** is an Android Studio plugin designed to streamline FTC (FIRST Tech Challenge) robotics code development. The plugin provides intelligent code generation for OpModes, hardware configuration, finite state machines, and template management.

### Technology Stack

- **Language**: Java 21 (primary), with Kotlin code generation support
- **Platform**: IntelliJ Platform Plugin SDK (Android Studio 242-252)
- **Build System**: Gradle 8.13 with Kotlin DSL
- **Target IDE**: Android Studio 2024.3.2.14
- **Testing Framework**: JUnit with IntelliJ Platform Test Framework

### Key Dependencies

```gradle
- IntelliJ Platform Gradle Plugin
- Android Studio Platform (2024.3.2.14)
- Bundled Plugins: com.intellij.java, org.intellij.groovy
- Kotlin JVM Toolchain 21
```

## Project Structure

```
src/main/
├── java/com/plugin/         # Core plugin implementation
│   ├── MakeOpMode.java      # OpMode creation action
│   ├── OpModeGenerator.java # Code generation logic
│   ├── OpModeDialog.java    # UI dialog for OpMode configuration
│   ├── MakeHardware.java    # Hardware field injection
│   ├── MakeFSM.java         # Finite state machine creation
│   ├── UseFSM.java          # FSM switch-case generation
│   ├── UseTemplate.java     # Template application
│   ├── AddGradleDependency.java # Gradle dependency management
│   └── Utilities.java       # Shared utility functions
└── resources/META-INF/
    └── plugin.xml           # Plugin configuration and action registration
```

## Architecture Patterns

### 1. Action-Based Architecture

All user-facing features extend `com.intellij.openapi.actionSystem.AnAction`:

```java
public class MyAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Implementation
    }
}
```

**Key Pattern**: Actions are registered in `plugin.xml` and integrated into IDE menus/context menus.

### 2. Dialog-Driven Input

User input is collected via `DialogWrapper` subclasses:

```java
public class MyDialog extends DialogWrapper {
    public MyDialog() {
        super(true);
        init();
    }
    
    @Override
    protected JComponent createCenterPanel() {
        // Build Swing UI
        return panel;
    }
}
```

**Pattern Details**:
- Use `JTextField` for text input
- Use `ButtonGroup` with `JRadioButton` for exclusive choices
- Set preferred dimensions for consistent sizing
- Call `showAndGet()` to display and check user acceptance

### 3. Write Command Actions

All PSI modifications MUST be wrapped in `WriteCommandAction`:

```java
WriteCommandAction.runWriteCommandAction(project, () -> {
    // Perform PSI modifications here
});
```

**Critical**: Never modify PSI elements outside a write command action.

### 4. Code Generation Strategy

Code generation uses string templates with `String.join("\n", ...)`:

```java
private static String generateCode(params) {
    return String.join("\n",
        "import statements",
        "class declaration",
        "    method implementation",
        "}"
    );
}
```

**Best Practices**:
- Maintain proper indentation in templates (4 spaces per level)
- Handle optional parameters with conditional logic
- Support both Java and Kotlin generation paths
- Use `List<String>` for building annotation parameters

### 5. PSI Element Creation

Files and code elements are created via PSI factories:

```java
PsiFileFactory factory = PsiFileFactory.getInstance(project);
FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("java");
PsiFile file = factory.createFileFromText("FileName.java", fileType, text);
directory.add(file);
```

**For class modifications**:
```java
PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
PsiField field = elementFactory.createFieldFromText("private int x;", context);
psiClass.add(field);
```

### 6. Import Management

Always ensure imports are added when creating new references:

```java
private void ensureImport(Project project, PsiFile file, String qualifiedName) {
    PsiImportList importList = ((PsiJavaFile) file).getImportList();
    // Check if import already exists
    // Add import if missing using PsiFileFactory
}
```

**Pattern**: Create a dummy file with the import, extract the import statement, and add to target file.

## Coding Standards

### Naming Conventions

- **Classes**: PascalCase (e.g., `OpModeGenerator`, `MakeHardware`)
- **Methods**: camelCase (e.g., `actionPerformed`, `createOpMode`)
- **Fields**: camelCase with `public final` for dialog fields
- **Constants**: UPPER_SNAKE_CASE
- **Packages**: lowercase (e.g., `com.plugin`)

### Code Organization

1. **Field Declaration Order**:
   - Static fields
   - Instance fields
   - Constructor
   - Public methods
   - Private methods

2. **Method Structure**:
   - Keep methods focused and single-purpose
   - Extract complex logic into private helper methods
   - Use descriptive parameter names

3. **Error Handling**:
   - Validate inputs early (null checks, empty strings)
   - Use `Messages.showErrorDialog()` for user-facing errors
   - Return early from invalid states
   - Provide clear error messages

### IntelliJ Platform Conventions

1. **Action Availability**:
   - Check `e.getProject()` for null
   - Validate PSI elements before use
   - Return early if prerequisites aren't met

2. **PSI Navigation**:
   - Use `PsiTreeUtil` for tree traversal
   - Use `Utilities.getClassBase()` for finding enclosing classes
   - Check `instanceof` before casting PSI elements

3. **UI Components**:
   - Use `GridLayout` for form-like dialogs
   - Set explicit preferred sizes for text fields
   - Group related radio buttons with `ButtonGroup`
   - Use descriptive labels

### FTC-Specific Patterns

1. **OpMode Structure**:
   - Support both `LinearOpMode` and `OpMode` base classes
   - Include proper annotations: `@TeleOp` or `@Autonomous`
   - Add `name` and `group` annotation parameters when provided
   - Import from `com.qualcomm.robotcore.eventloop.opmode.*`

2. **Hardware Configuration**:
   - Hardware types: `DcMotor`, `Servo` (from `com.qualcomm.robotcore.hardware`)
   - Hardware accessed via `HardwareMap.get(Class, String)`
   - Initialize hardware in constructor with `HardwareMap` parameter
   - Store hardware references as `private final` fields

3. **Language Support**:
   - Java: public class visibility, explicit types
   - Kotlin: implicit public visibility, type inference
   - Match syntax to language (semicolons, fun vs method, etc.)

## Quality Standards

### Code Quality

- **Maintainability**: Keep methods under 50 lines; extract complex logic
- **Readability**: Use descriptive names; add comments only for non-obvious logic
- **Testability**: Separate UI logic from business logic where possible
- **Performance**: Minimize PSI tree traversals; cache expensive lookups

### Security

- **Input Validation**: Sanitize all user input before PSI operations
- **Null Safety**: Check all nullable returns from IntelliJ Platform APIs
- **Resource Management**: No explicit resource management needed (IDE handles lifecycle)

### Accessibility

- **UI**: Use standard Swing components for accessibility support
- **Keyboard Navigation**: Standard Swing behavior provides keyboard access
- **Error Messages**: Clear, actionable error messages for all failure cases

## Common Patterns and Idioms

### Finding the Current Class

```java
PsiElement element = Utilities.getPsiElement(e);
PsiClass psiClass = Utilities.getClassBase(element);
if (psiClass == null) {
    Messages.showErrorDialog("No class found", "Error");
    return;
}
```

### Adding a Method to a Class

```java
PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
PsiMethod method = factory.createMethodFromText(
    "public void myMethod() { }", 
    psiClass
);
psiClass.add(method);
```

### Getting Selected Radio Button

```java
private boolean getFirstSelected(ButtonGroup group) {
    Enumeration<AbstractButton> buttons = group.getElements();
    while (buttons.hasMoreElements()) {
        if (buttons.nextElement().isSelected()) return true;
    }
    return false;
}
```

### Creating Files in Directory

```java
PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
if (!(element instanceof PsiDirectory dir)) return;

FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("java");
PsiFileFactory factory = PsiFileFactory.getInstance(project);
PsiFile file = factory.createFileFromText("Name.java", fileType, content);
dir.add(file);
```

## Build and Test Commands

### Building the Plugin

```bash
./gradlew build
```

### Running the Plugin in Test IDE

```bash
./gradlew runIde
```

### Running Tests

```bash
./gradlew test
```

### Running UI Tests

```bash
./gradlew runIdeForUiTests
```

### Code Quality Checks

```bash
./gradlew qodana           # Static analysis
./gradlew koverReport      # Coverage report
```

## Plugin Configuration

### Plugin Metadata (gradle.properties)

- **Group**: `com.plugin`
- **Name**: `Mjolnir for FTC`
- **Version**: `0.0.1-beta`
- **Supported Builds**: 242 to 252.*

### Action Registration (plugin.xml)

Actions must be registered in `plugin.xml`:

```xml
<action id="unique.id" 
        class="com.plugin.ClassName" 
        text="Display Name"
        description="Tooltip description">
    <add-to-group group-id="NewGroup" anchor="after" relative-to-action="NewFile"/>
</action>
```

**Available Groups**:
- `NewGroup` - New file menu
- `EditorPopupMenu` - Right-click in editor
- `ProjectViewPopupMenu` - Right-click in project view

## Version Compatibility

### IntelliJ Platform

- **Minimum**: Build 242 (Android Studio 2024.3+)
- **Maximum**: Build 252.* (Android Studio 2025.2+)
- **Current Target**: Android Studio 2024.3.2.14

### Java Version

- **Source/Target**: Java 21
- **JVM Toolchain**: 21
- **Note**: Versions >21 cause crashes with AI 242

### Dependencies

All dependencies managed via Gradle Version Catalog (`libs.versions.toml`):
- IntelliJ Platform Plugin
- Kotlin Plugin
- Changelog Plugin
- Qodana Plugin
- Kover Plugin

## Testing Guidelines

### Test Structure

Tests extend `BasePlatformTestCase` or use `@TestFramework`:

```java
public class MyTest extends BasePlatformTestCase {
    @Test
    public void testFeature() {
        // Arrange
        // Act
        // Assert
    }
}
```

### Test Fixtures

- Use `@TempDir` for temporary test files
- Use `myFixture.configureByText()` for in-memory test files
- Use `myFixture.findClass()` for PSI element lookup

### UI Testing

- Use Robot Server Plugin for UI tests
- Configure via `runIdeForUiTests` task
- Tests run in separate IDE instance

## Extension Points

### Adding New Actions

1. Create class extending `AnAction`
2. Implement `actionPerformed` method
3. Register in `plugin.xml`
4. Add to appropriate action group

### Adding New Dialogs

1. Extend `DialogWrapper`
2. Implement `createCenterPanel()` for UI
3. Add public fields for user input
4. Call `showAndGet()` to display

### Adding New Generators

1. Create static generator methods
2. Use string templates with `String.join`
3. Support multiple language outputs
4. Add unit tests for generated code

## Future Development Guidelines

### Planned Features

1. **One-Button Library Imports**:
   - Support: Pedro Pathing, Roadrunner, FTCLib, NextFTC
   - Pattern: Gradle dependency addition + template application

2. **FSM Enhancement**:
   - Simple Enum + Switch-Case generation
   - Already implemented via `MakeFSM` and `UseFSM`

3. **Constructor Autofill**:
   - Automatic HardwareMap instantiation
   - XML HardwareMap parsing for field names

4. **Open Source Template System**:
   - GitHub integration for template sharing
   - In-IDE template browsing and application
   - Focus on library-specific templates

### Code Review Checklist

- [ ] Extends appropriate base class (`AnAction`, `DialogWrapper`)
- [ ] Uses `WriteCommandAction` for PSI modifications
- [ ] Validates all user inputs and PSI elements
- [ ] Handles null cases gracefully
- [ ] Adds necessary imports via `ensureImport` pattern
- [ ] Follows existing naming conventions
- [ ] Generates syntactically correct Java/Kotlin code
- [ ] Registered in `plugin.xml` if user-facing
- [ ] Tested in development IDE (`./gradlew runIde`)

## Known Issues and Limitations

1. **Build System**: Android Studio version 2024.3.2.14 may have download resolution issues
2. **Java Version**: Must use exactly Java 21 for class file compatibility
3. **Platform API**: Some APIs vary between IntelliJ Platform versions
4. **Testing**: UI tests require Robot Server Plugin configuration

## Additional Resources

- [IntelliJ Platform SDK Documentation](https://plugins.jetbrains.com/docs/intellij/)
- [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- [FTC SDK Documentation](https://github.com/FIRST-Tech-Challenge/FtcRobotController)
- [Gradle IntelliJ Plugin](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html)

## Copilot Generation Guidelines

When generating code for this project:

1. **Follow Existing Patterns**: Match the style and structure of existing actions and generators
2. **Use PSI APIs**: Leverage IntelliJ Platform PSI APIs for code manipulation
3. **Validate Everything**: Check for null and invalid states before operations
4. **Write Command Actions**: Always wrap PSI modifications properly
5. **Support Both Languages**: When generating user code, support both Java and Kotlin
6. **FTC Conventions**: Follow FTC SDK naming and structure conventions
7. **Test in IDE**: All features must be testable via `./gradlew runIde`
8. **Document Patterns**: Add comments for complex PSI operations or non-obvious logic
9. **Error Messages**: Provide clear, actionable error messages for users
10. **Keep It Simple**: Prefer straightforward implementations over clever abstractions

---

*Generated for GitHub Copilot to provide context-aware code suggestions aligned with project standards.*
