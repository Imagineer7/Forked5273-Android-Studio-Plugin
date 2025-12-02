package com.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Generator for FTC starter code (OpModes and hardware classes).
 */
public class FTCStarterCodeGenerator {
    private final File projectDir;
    private final boolean isJava;
    private final String teamNumber;
    private final File teamCodeDir;
    
    public FTCStarterCodeGenerator(File projectDir, boolean isJava, String teamNumber) {
        this.projectDir = projectDir;
        this.isJava = isJava;
        this.teamNumber = teamNumber.isEmpty() ? "TeamCode" : teamNumber;
        this.teamCodeDir = new File(projectDir, "TeamCode/src/main/java/org/firstinspires/ftc/teamcode");
    }
    
    /**
     * Generate a sample TeleOp OpMode.
     */
    public void generateSampleOpMode() throws IOException {
        if (!teamCodeDir.exists()) {
            teamCodeDir.mkdirs();
        }
        
        String fileName = isJava ? "SampleTeleOp.java" : "SampleTeleOp.kt";
        File opModeFile = new File(teamCodeDir, fileName);
        
        String content = isJava ? generateJavaSampleOpMode() : generateKotlinSampleOpMode();
        Files.write(opModeFile.toPath(), content.getBytes());
    }
    
    /**
     * Generate a hardware configuration class.
     */
    public void generateHardwareClass() throws IOException {
        if (!teamCodeDir.exists()) {
            teamCodeDir.mkdirs();
        }
        
        String fileName = isJava ? "RobotHardware.java" : "RobotHardware.kt";
        File hardwareFile = new File(teamCodeDir, fileName);
        
        String content = isJava ? generateJavaHardwareClass() : generateKotlinHardwareClass();
        Files.write(hardwareFile.toPath(), content.getBytes());
    }
    
    private String generateJavaSampleOpMode() {
        return String.join("\n",
            "package org.firstinspires.ftc.teamcode;",
            "",
            "import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;",
            "import com.qualcomm.robotcore.eventloop.opmode.TeleOp;",
            "import com.qualcomm.robotcore.hardware.DcMotor;",
            "",
            "@TeleOp(name = \"Sample TeleOp\", group = \"" + teamNumber + "\")",
            "public class SampleTeleOp extends LinearOpMode {",
            "    ",
            "    private DcMotor leftDrive = null;",
            "    private DcMotor rightDrive = null;",
            "    ",
            "    @Override",
            "    public void runOpMode() {",
            "        // Initialize hardware",
            "        leftDrive = hardwareMap.get(DcMotor.class, \"left_drive\");",
            "        rightDrive = hardwareMap.get(DcMotor.class, \"right_drive\");",
            "        ",
            "        // Set motor directions",
            "        leftDrive.setDirection(DcMotor.Direction.FORWARD);",
            "        rightDrive.setDirection(DcMotor.Direction.REVERSE);",
            "        ",
            "        // Wait for start",
            "        telemetry.addData(\"Status\", \"Initialized\");",
            "        telemetry.update();",
            "        waitForStart();",
            "        ",
            "        // Run until stop is pressed",
            "        while (opModeIsActive()) {",
            "            // Get gamepad input",
            "            double leftPower = -gamepad1.left_stick_y;",
            "            double rightPower = -gamepad1.right_stick_y;",
            "            ",
            "            // Set motor power",
            "            leftDrive.setPower(leftPower);",
            "            rightDrive.setPower(rightPower);",
            "            ",
            "            // Display telemetry",
            "            telemetry.addData(\"Left Power\", leftPower);",
            "            telemetry.addData(\"Right Power\", rightPower);",
            "            telemetry.update();",
            "        }",
            "    }",
            "}"
        );
    }
    
    private String generateKotlinSampleOpMode() {
        return String.join("\n",
            "package org.firstinspires.ftc.teamcode",
            "",
            "import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode",
            "import com.qualcomm.robotcore.eventloop.opmode.TeleOp",
            "import com.qualcomm.robotcore.hardware.DcMotor",
            "",
            "@TeleOp(name = \"Sample TeleOp\", group = \"" + teamNumber + "\")",
            "class SampleTeleOp : LinearOpMode() {",
            "    ",
            "    private lateinit var leftDrive: DcMotor",
            "    private lateinit var rightDrive: DcMotor",
            "    ",
            "    override fun runOpMode() {",
            "        // Initialize hardware",
            "        leftDrive = hardwareMap.get(DcMotor::class.java, \"left_drive\")",
            "        rightDrive = hardwareMap.get(DcMotor::class.java, \"right_drive\")",
            "        ",
            "        // Set motor directions",
            "        leftDrive.direction = DcMotor.Direction.FORWARD",
            "        rightDrive.direction = DcMotor.Direction.REVERSE",
            "        ",
            "        // Wait for start",
            "        telemetry.addData(\"Status\", \"Initialized\")",
            "        telemetry.update()",
            "        waitForStart()",
            "        ",
            "        // Run until stop is pressed",
            "        while (opModeIsActive()) {",
            "            // Get gamepad input",
            "            val leftPower = -gamepad1.left_stick_y.toDouble()",
            "            val rightPower = -gamepad1.right_stick_y.toDouble()",
            "            ",
            "            // Set motor power",
            "            leftDrive.power = leftPower",
            "            rightDrive.power = rightPower",
            "            ",
            "            // Display telemetry",
            "            telemetry.addData(\"Left Power\", leftPower)",
            "            telemetry.addData(\"Right Power\", rightPower)",
            "            telemetry.update()",
            "        }",
            "    }",
            "}"
        );
    }
    
    private String generateJavaHardwareClass() {
        return String.join("\n",
            "package org.firstinspires.ftc.teamcode;",
            "",
            "import com.qualcomm.robotcore.hardware.DcMotor;",
            "import com.qualcomm.robotcore.hardware.HardwareMap;",
            "import com.qualcomm.robotcore.hardware.Servo;",
            "",
            "/**",
            " * Hardware configuration class for the robot.",
            " * Contains all motor and servo declarations.",
            " */",
            "public class RobotHardware {",
            "    ",
            "    // Drive motors",
            "    public DcMotor leftFrontDrive = null;",
            "    public DcMotor rightFrontDrive = null;",
            "    public DcMotor leftBackDrive = null;",
            "    public DcMotor rightBackDrive = null;",
            "    ",
            "    // Additional motors",
            "    // public DcMotor armMotor = null;",
            "    ",
            "    // Servos",
            "    // public Servo clawServo = null;",
            "    ",
            "    private HardwareMap hwMap = null;",
            "    ",
            "    /**",
            "     * Initialize all hardware components.",
            "     */",
            "    public void init(HardwareMap ahwMap) {",
            "        hwMap = ahwMap;",
            "        ",
            "        // Initialize drive motors",
            "        leftFrontDrive = hwMap.get(DcMotor.class, \"left_front_drive\");",
            "        rightFrontDrive = hwMap.get(DcMotor.class, \"right_front_drive\");",
            "        leftBackDrive = hwMap.get(DcMotor.class, \"left_back_drive\");",
            "        rightBackDrive = hwMap.get(DcMotor.class, \"right_back_drive\");",
            "        ",
            "        // Set motor directions",
            "        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);",
            "        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);",
            "        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);",
            "        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);",
            "        ",
            "        // Set motor behavior",
            "        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);",
            "        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);",
            "        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);",
            "        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);",
            "        ",
            "        // Stop all motors",
            "        stopMotors();",
            "    }",
            "    ",
            "    /**",
            "     * Stop all drive motors.",
            "     */",
            "    public void stopMotors() {",
            "        leftFrontDrive.setPower(0);",
            "        rightFrontDrive.setPower(0);",
            "        leftBackDrive.setPower(0);",
            "        rightBackDrive.setPower(0);",
            "    }",
            "}"
        );
    }
    
    private String generateKotlinHardwareClass() {
        return String.join("\n",
            "package org.firstinspires.ftc.teamcode",
            "",
            "import com.qualcomm.robotcore.hardware.DcMotor",
            "import com.qualcomm.robotcore.hardware.HardwareMap",
            "import com.qualcomm.robotcore.hardware.Servo",
            "",
            "/**",
            " * Hardware configuration class for the robot.",
            " * Contains all motor and servo declarations.",
            " */",
            "class RobotHardware {",
            "    ",
            "    // Drive motors",
            "    lateinit var leftFrontDrive: DcMotor",
            "    lateinit var rightFrontDrive: DcMotor",
            "    lateinit var leftBackDrive: DcMotor",
            "    lateinit var rightBackDrive: DcMotor",
            "    ",
            "    // Additional motors",
            "    // lateinit var armMotor: DcMotor",
            "    ",
            "    // Servos",
            "    // lateinit var clawServo: Servo",
            "    ",
            "    private lateinit var hwMap: HardwareMap",
            "    ",
            "    /**",
            "     * Initialize all hardware components.",
            "     */",
            "    fun init(ahwMap: HardwareMap) {",
            "        hwMap = ahwMap",
            "        ",
            "        // Initialize drive motors",
            "        leftFrontDrive = hwMap.get(DcMotor::class.java, \"left_front_drive\")",
            "        rightFrontDrive = hwMap.get(DcMotor::class.java, \"right_front_drive\")",
            "        leftBackDrive = hwMap.get(DcMotor::class.java, \"left_back_drive\")",
            "        rightBackDrive = hwMap.get(DcMotor::class.java, \"right_back_drive\")",
            "        ",
            "        // Set motor directions",
            "        leftFrontDrive.direction = DcMotor.Direction.FORWARD",
            "        rightFrontDrive.direction = DcMotor.Direction.REVERSE",
            "        leftBackDrive.direction = DcMotor.Direction.FORWARD",
            "        rightBackDrive.direction = DcMotor.Direction.REVERSE",
            "        ",
            "        // Set motor behavior",
            "        leftFrontDrive.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE",
            "        rightFrontDrive.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE",
            "        leftBackDrive.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE",
            "        rightBackDrive.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE",
            "        ",
            "        // Stop all motors",
            "        stopMotors()",
            "    }",
            "    ",
            "    /**",
            "     * Stop all drive motors.",
            "     */",
            "    fun stopMotors() {",
            "        leftFrontDrive.power = 0.0",
            "        rightFrontDrive.power = 0.0",
            "        leftBackDrive.power = 0.0",
            "        rightBackDrive.power = 0.0",
            "    }",
            "}"
        );
    }
}
