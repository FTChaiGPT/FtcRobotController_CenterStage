package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

//All values of the variables target and encoderCounts are reversed due to the encoders

//BluePixel, start at blue pixel position, go to spike mark, drop pixel, go back, turn left, go to backstage


@Autonomous(name="BluePixel", group = "Auto")
public class BluePixel extends LinearOpMode {
    private DcMotor left_front;
    private DcMotor left_back;
    private DcMotor right_front;
    private DcMotor right_back;
    private DcMotor intake;
    private Servo pusher;
    private Servo gate;
    private Servo Front;

    private static final double COUNTS_PER_MOTOR_REV = 750; //Number of encoder counts per motor revolution (1440)
    private static final double WHEEL_DIAMETER_INCHES = 3.75;
    private  static final double GEAR_RATIO = 1.0;
    private static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * GEAR_RATIO)/(WHEEL_DIAMETER_INCHES * Math.PI);

    private static final double ROBOT_WIDTH_INCHES = 18.9; // The distance between the wheels on opposite sides

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

        Initialization();

        waitForStart();

       forward(22, 0.25);
        intake.setPower(0.4);
        //Adheesh's code is working now..yay!
        //sleep(3000);
        backward(20, 0.5);
        strafe_Right(13.5F,0.5);
        forward(40, 0.5);
        turn_Right(90, 0.5);
        backward(73, 0.5);
        gate.setPosition(-0.5);
        sleep(200);
        pusher.setPosition(1);
        sleep(200);
        pusher.setPosition(0.0);
        sleep(200);
        pusher.setPosition(1);
        sleep(200);
        pusher.setPosition(0.0);
        sleep(200);
        gate.setPosition(0.5);
        Front.setPosition(1);
    }


    public void Initialization(){
        left_front = hardwareMap.get(DcMotor.class, "left_front");
        left_back = hardwareMap.get(DcMotor.class, "left_back");
        right_front = hardwareMap.get(DcMotor.class, "right_front");
        right_back = hardwareMap.get(DcMotor.class, "right_back");
        intake = hardwareMap.get(DcMotor.class, "intake");
        pusher = hardwareMap.get(Servo.class, "pusher");
        gate = hardwareMap.get(Servo.class, "gate");
        Front = hardwareMap.get(Servo.class, "front");

        left_front.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left_back.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right_front.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right_back.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        right_front.setDirection(DcMotor.Direction.REVERSE);
        right_back.setDirection(DcMotor.Direction.REVERSE);
        intake.setDirection(DcMotor.Direction.FORWARD);

        left_front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        left_back.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right_front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right_back.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    public void intake(int distance, double power) {
        resetEncoders();

        int target = (int) (distance * COUNTS_PER_INCH);
        intake.setTargetPosition(-target);

        setMotorPower(power);

        stopMotor();


    }
    public void reverse_intake(int distance, double power) {
        resetEncoders();

        int target = (int) (distance * COUNTS_PER_INCH);
        intake.setTargetPosition(-target);

        intake.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setMotorPower(power);


        stopMotor();


    }

    public void slowDownAtEnd(double p) {
        while (opModeIsActive() && left_front.isBusy()) { //Accurate measures
            double remainingDistance = Math.abs(left_front.getTargetPosition() - left_front.getCurrentPosition());
            // Adjust this threshold as needed.
            double rampDownThreshold = 600;  // For example, start ramping down when 300 encoder counts away from target.

            if (remainingDistance < rampDownThreshold) {
                double rampedSpeed = 0.5 * (p * (remainingDistance / rampDownThreshold));
                rampedSpeed = Math.max(rampedSpeed, 0.1); // Ensure you have a minimum speed to prevent the robot from stalling.
                setMotorPower(rampedSpeed);
            }
        }
    }

    public void forward(int distance, double power) {
        resetEncoders();

        int target = (int) (distance * COUNTS_PER_INCH);
        left_front.setTargetPosition(-target);
        left_back.setTargetPosition(-target);
        right_front.setTargetPosition(-target);
        right_back.setTargetPosition(-target);

        left_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addData("Left Motor velocity is", left_front.getPower());
        telemetry.addData("Right Motor velocity is", right_front.getPower());
        telemetry.update();

        setMotorPower(power);


        slowDownAtEnd(power);

        stopMotor();
    }

    private void setMotorPower(double x) {
        left_front.setPower(x);
        left_back.setPower(x);
        right_front.setPower(x);
        right_back.setPower(x);
    }

    public void backward(int distance, double power) {
        resetEncoders();

        int target = (int) (distance * COUNTS_PER_INCH);
        left_front.setTargetPosition(target);
        left_back.setTargetPosition(target);
        right_front.setTargetPosition(target);
        right_back.setTargetPosition(target);

        left_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setMotorPower(power);
        slowDownAtEnd(power);

        stopMotor();

    }

    public void turn_Left(double degrees, double power) {
        resetEncoders();

        double radians = Math.toRadians(degrees);
        double wheelDistance = radians * (ROBOT_WIDTH_INCHES / 2);

        int encoderCounts = (int) (wheelDistance * COUNTS_PER_INCH);

        // Set the target position for a left turn
        left_front.setTargetPosition(encoderCounts);
        left_back.setTargetPosition(encoderCounts);
        right_front.setTargetPosition(-encoderCounts);
        right_back.setTargetPosition(-encoderCounts);

        left_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Power the motors to move towards the target position
        setMotorPower(power);

        slowDownAtEnd(power);

        stopMotor();
    }

    public void turn_Right(int degrees, double power) {
        resetEncoders();

        double radians = Math.toRadians(degrees);
        double wheelDistance = radians * (ROBOT_WIDTH_INCHES / 2);

        int encoderCounts = (int) (wheelDistance * COUNTS_PER_INCH);

        // Set the target position for a left turn
        left_front.setTargetPosition(-encoderCounts);
        left_back.setTargetPosition(-encoderCounts);
        right_front.setTargetPosition(encoderCounts);
        right_back.setTargetPosition(encoderCounts);

        left_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Power the motors to move towards the target position
        setMotorPower(power);

        slowDownAtEnd(power);

        stopMotor();
    }

    public void strafe_Right(float distance, double power) {
        resetEncoders();

        int target = (int) (distance * COUNTS_PER_INCH);
        left_front.setTargetPosition(-target);
        left_back.setTargetPosition(target);
        right_front.setTargetPosition(target);
        right_back.setTargetPosition(-target);

        left_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setMotorPower(power);
        slowDownAtEnd(power);

        stopMotor();

    }

    public void strafe_Left(int distance, double power) {
        resetEncoders();

        int target = (int) (distance * COUNTS_PER_INCH);
        left_front.setTargetPosition(target);
        left_back.setTargetPosition(-target);
        right_front.setTargetPosition(-target);
        right_back.setTargetPosition(target);

        left_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_front.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right_back.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setMotorPower(power);
        slowDownAtEnd(power);

        stopMotor();

    }

    public void stopMotor() {
        setMotorPower(0);
        resetEncoders();
    }

    private void resetEncoders() {
        left_front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left_back.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right_front.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right_back.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //left_front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //left_back.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //right_front.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //right_back.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

}

