package models;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
//import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.ColorDetector;
import lejos.robotics.ColorIdentifier;
import lejos.utility.Delay;

public class ColorSensor implements ColorDetector, ColorIdentifier {

	// Comment
	EV3ColorSensor sensor = new EV3ColorSensor(SensorPort.S4);
	float black;
	float white;
	float[] sample; // Hier komt rood, groen en blauw in
	float gray;

	public ColorSensor() {
		super();
		setRGBMode();
	}

	// kleurcalibratie
	public float[] calibrateColor(String colorName) {
		final EV3 ev3 = (EV3) BrickFinder.getLocal();
	
		LCD.drawString("Place me on a", 0, 1);
		LCD.drawString(colorName.toUpperCase(), 0, 2);
		LCD.drawString("surface.", 0, 3);
	
		while (Button.ENTER.isUp()) {
			LCD.clear(4);
			LCD.clear(5);
			LCD.clear(6);
			LCD.drawString("" + getRed(), 0, 4);
			LCD.drawString("" + getGreen(), 0, 5);
			LCD.drawString("" + getBlue(), 0, 6);
			Delay.msDelay(500);
		}
		Delay.msDelay(500);
	
		LCD.clear();
		LCD.drawString("Confirm", 0, 1);
		LCD.drawString(colorName.toUpperCase(), 0, 2);
		LCD.drawString("values?", 0, 3);
	
		while (Button.ENTER.isUp()) {
			LCD.clear(4);
			LCD.clear(5);
			LCD.clear(6);
			LCD.drawString("" + getRed(), 0, 4);
			LCD.drawString("" + getGreen(), 0, 5);
			LCD.drawString("" + getBlue(), 0, 6);
			Delay.msDelay(500);
		}
		Delay.msDelay(500);
	
		LCD.clear();
		return getColorRGB();
	}

	public void close() {
		sensor.close();
	}

	public float getBlue() {
		sensor.fetchSample(sample, 0);
		return sample[2];
	}

	// een functie die een grijsschaal maakt van de drie kleuren
	public float getBrightness(float RGBColor[]) {
		// gemiddelde van de drie waardes om grijs te krijgen (max 255).
		float grijsWaarde = (RGBColor[0] + RGBColor[1] + RGBColor[2]) / 3;
		return grijsWaarde;
	}

	// grey = 0.5
	public float getCurrentNormalisedBrightness() {
		float[] currentSample = getColorRGB();
		float currentBrightness = getBrightness(currentSample);
		float normalisedBrightness = (currentBrightness - black) / white;
		return normalisedBrightness;
	}

	@Override
	public Color getColor() {
		return null;
	}

	@Override
	public int getColorID() {
		sensor.fetchSample(sample, 0);
		return (int) sample[0];
	}

	public float[] getColorRGB() {
		sensor.fetchSample(sample, 0);
		return sample;
	}

	public float getGreen() {
		sensor.fetchSample(sample, 0);
		return sample[1];
	}

	public float getRed() {
		sensor.fetchSample(sample, 0);
		return sample[0];
	}

	// Calibreer wit en zwart
	public void setBlackWhiteFromCalibration() {
		this.black = getBrightness(calibrateColor("black"));
		this.white = getBrightness(calibrateColor("white"));
	}

	public void setColorIdMode() {
		sensor.setCurrentMode("ColorID");
		this.sample = new float[sensor.sampleSize()];
	}

	public void setFloodLight(boolean on) {
		sensor.setFloodlight(on);
	}

	public void setFloodLight(int color) {
		sensor.setFloodlight(color);
	}

	public void setRedMode() {
		sensor.setCurrentMode("Red");
		this.sample = new float[sensor.sampleSize()];
	}

	public void setRGBMode() {
		sensor.setCurrentMode("RGB");
		this.sample = new float[sensor.sampleSize()];
	}

}
