package cihw1;

import cihw1.Canvas;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class cihw1 extends Application {

	private Canvas canvasPane;
	private Car car;
	private Label line1Dist = new Label("Red");
	private Label line2Dist = new Label("Blue");
	private Label line3Dist = new Label("Green");
	private Label angleInfo = new Label("");
	private Line sensorLine1;
	private Line sensorLine2;
	private Line sensorLine3;
	private int finalFlag = 0;
	private double initialAngleValue = 90;
	private int ratio = 10;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("A battle with Computational Intelligence.");

		/*
		 * Initial setting
		 */
		BorderPane ciPane = new BorderPane();
		VBox infoBox = new VBox(10);
		Button start = new Button("Start");
		Button restart = new Button("Restart");

		canvasPane = new Canvas();

		ciPane.setRight(canvasPane);
		ciPane.setLeft(infoBox);

		car = new Car(this.canvasPane);
		canvasPane.getChildren().add(car);
		
		Slider slider = new Slider();
		slider.setPrefSize(180, 30);
		slider.setMin(-270);
		slider.setMax(90);
		slider.setValue(90);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(50);
		slider.setMinorTickCount(5);
		slider.setBlockIncrement(10);
		
		Label initialAngle = new Label("Angle value : 90º");
		Label initialAngleSign = new Label("Please slide to start angle :");

		infoBox.setPadding(new Insets(15, 50, 15, 15));
		infoBox.getChildren().addAll(start,restart, line3Dist, line1Dist, line2Dist, angleInfo,initialAngleSign,slider,initialAngle);
		
		line1Dist.setTextFill(Color.DARKRED);
		line2Dist.setTextFill(Color.DARKBLUE);
		line3Dist.setTextFill(Color.DARKGREEN);
		
		/*
		 * Set sensor lines
		 */
		sensorLinesSetting();
		
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				initialAngle.setText("Angle value : "+Math.round((double)newValue * 100.0) / 100.0+"º");
				initialAngleValue = (double) Math.round((double)newValue * 100.0) / 100.0;
				car.angle = (double) newValue;
				car.sliderTuneCar();
				initialSetSensorsLine();
			}
        });

		restart.setOnMouseClicked(event ->{
			this.canvasPane.rePaint();
			this.car = new Car(this.canvasPane);
			canvasPane.getChildren().add(car);
			sensorLinesSetting();
			finalFlag = 0;
			line1Dist.setText("Middle Line");
			line2Dist.setText("Right Line");
			line3Dist.setText("Left Line");
			angleInfo.setText("Angle value : "+Math.round((double) slider.getValue() * 100.0) / 100.0+"º");
			slider.setDisable(false);
			
		});
		
		canvasPane.setOnMouseClicked(event ->{

			car.initialSetCar(event.getX(), event.getY());

			initialSetSensorsLine();

		});

		/*
		 * When start button clicked
		 */
		start.setOnMouseClicked(evnet -> {

			slider.setDisable(true);
			initialAngle.setText("Initial angle value : " + initialAngleValue+"º");
			initialAngleSign.setText("");
			// Open a thread to update GUI
		
			new Thread() {
				public void run() {
					while (true) {
						try {
							// Main thread sleep
							Thread.sleep(500);

							Platform.runLater(new Runnable() {
								// GUI update by javafx thread
								@Override
								public void run() {
									// The function for the final round
									if (finalFlag == 1) {
										line1Dist.setText("done");
										line2Dist.setText("done");
										line3Dist.setText("done");
										angleInfo.setText("done");
										car.tuneCar(canvasPane);
										sensorLine1.setVisible(false);
										sensorLine2.setVisible(false);
										sensorLine3.setVisible(false);
										// Interrupted the thread which just
										// created
										
										// The function for normal round
									} else {
										// Tune car's position and angle
										car.tuneCar(canvasPane);

										initialSetSensorsLine();
									}

								}
							});

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// When goal -> break the while loop
						double checkBreak = car.getCenterY();
						if (checkBreak <= canvasPane.line8.getEndY() + 30) {
							finalFlag = 1;
							break;
						}

					}
				}
			}.start();
		});

		Scene primaryScene = new Scene(ciPane);
		primaryStage.setScene(primaryScene);
		primaryStage.setResizable(false);
		primaryStage.show();

	}
	public void initialSetSensorsLine(){
		sensorLine1.setStartX(transToCanvasX(car.getX()));
		sensorLine1.setStartY(transToCanvasY(car.getY()));
		sensorLine1.setEndX(transToCanvasX(car.sensor1.getX()));
		sensorLine1.setEndY(transToCanvasY(car.sensor1.getY()));
		
		sensorLine2.setStartX(transToCanvasX(car.getX()));
		sensorLine2.setStartY(transToCanvasY(car.getY()));
		sensorLine2.setEndX(transToCanvasX(car.sensor2.getX()));
		sensorLine2.setEndY(transToCanvasY(car.sensor2.getY()));
		
		sensorLine3.setStartX(transToCanvasX(car.getX()));
		sensorLine3.setStartY(transToCanvasY(car.getY()));
		sensorLine3.setEndX(transToCanvasX(car.sensor3.getX()));
		sensorLine3.setEndY(transToCanvasY(car.sensor3.getY()));
		
		// Calculate the distance with walls
		car.sensor1.calDistance(canvasPane);
		car.sensor2.calDistance(canvasPane);
		car.sensor3.calDistance(canvasPane);

		// Set showing information
		line1Dist.setText("Red Line :"+car.sensor1.getDist());
		line2Dist.setText("Blue Line :"+car.sensor2.getDist());
		line3Dist.setText("Green Line :"+car.sensor3.getDist());
		angleInfo.setText("Angle with x-axis : " + Math.round(car.angle * 1000.0) / 1000.0+"º");

		// Set sensor lines
		int sensor1ClosetId = car.sensor1.getClosestLineId();
		int sensor2ClosetId = car.sensor2.getClosestLineId();
		int sensor3ClosetId = car.sensor3.getClosestLineId();
		
		sensorLine1.setEndX(transToCanvasX(car.sensor1.getIntersectionPointX(sensor1ClosetId)));
		sensorLine1.setEndY(transToCanvasY(car.sensor1.getIntersectionPointY(sensor1ClosetId)));
		sensorLine2.setEndX(transToCanvasX(car.sensor2.getIntersectionPointX(sensor2ClosetId)));
		sensorLine2.setEndY(transToCanvasY(car.sensor2.getIntersectionPointY(sensor2ClosetId)));
		sensorLine3.setEndX(transToCanvasX(car.sensor3.getIntersectionPointX(sensor3ClosetId)));
		sensorLine3.setEndY(transToCanvasY(car.sensor3.getIntersectionPointY(sensor3ClosetId)));

	}
	public void printCurrentThread() {
		System.out.println("************************");
		System.out.println(Thread.currentThread());
		System.out.println("************************");

	}
	public void sensorLinesSetting(){
		sensorLine1 = new Line();
		sensorLine1.setStartX(transToCanvasX(car.getX()));
		sensorLine1.setStartY(transToCanvasY(car.getY()));
		sensorLine1.setEndX(transToCanvasX(car.sensor1.getX()));
		sensorLine1.setEndY(transToCanvasY(car.sensor1.getY()));
//		sensorLine1.startXProperty().bind(car.centerXProperty());
//		sensorLine1.startYProperty().bind(car.centerYProperty());
		sensorLine1.setStroke(Color.DARKRED);

		sensorLine2 = new Line();
		sensorLine2.setStartX(transToCanvasX(car.getX()));
		sensorLine2.setStartY(transToCanvasY(car.getY()));
		sensorLine2.setEndX(transToCanvasX(car.sensor2.getX()));
		sensorLine2.setEndY(transToCanvasY(car.sensor2.getY()));
//		sensorLine2.startXProperty().bind(car.centerXProperty());
//		sensorLine2.startYProperty().bind(car.centerYProperty());
		sensorLine2.setStroke(Color.DARKBLUE);

		sensorLine3 = new Line();
		sensorLine3.setStartX(transToCanvasX(car.getX()));
		sensorLine3.setStartY(transToCanvasY(car.getY()));
		sensorLine3.setEndX(transToCanvasX(car.sensor3.getX()));
		sensorLine3.setEndY(transToCanvasY(car.sensor3.getY()));
//		sensorLine3.startXProperty().bind(car.centerXProperty());
//		sensorLine3.startYProperty().bind(car.centerYProperty());
		sensorLine3.setStroke(Color.DARKGREEN);
		
		sensorLine1.setVisible(true);
		sensorLine2.setVisible(true);
		sensorLine3.setVisible(true);
		
		canvasPane.getChildren().addAll(sensorLine1, sensorLine2, sensorLine3);

	}
	public double transToCanvasX(double x) {
		double value = (x + 30) * ratio;
		return value;
	}

	public double transToCanvasY(double y) {
		double value = (-y + 52) * ratio;
		return value;
	}
	public double transBackX(double x){
		double value = (x /ratio)-30;
		return value;
	}
	public double transBackY(double y){
		double value = -1*((y /ratio)-52);
		return value;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
