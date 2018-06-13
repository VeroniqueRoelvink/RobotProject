package Programmas;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.Sound;
import lejos.robotics.Color;
import lejos.utility.Delay;
import models.ColorSensor;
import models.MotionController;

public class MasterMind {
	// 0 = rood, 1 = groen, 2 = blauw, 3 = geel, 6 = wit, 7 = zwart
	private int[] guess = new int[4];
	private int[] randomToNumber = { 0, 1, 2, 3, 6, 7 };
	private String[] numberToColor = { "rood", "groen", "blauw", "geel", "", "", "wit", "zwart" };
	private ColorSensor CS;
	private MotionController BW;

	// Constructor
	public MasterMind(ColorSensor CS, MotionController BW) {
		super();
		this.CS = CS;
		this.BW = BW;
	}
	
	// TODO MISSCHIEN WAT MOOIER WEERGEVEN OP DE BRICK!!
	

	// Cre�er een random raadsel
	public int[] createRiddle() { // Naamgeving kan anders
		int[] riddle = new int[4];
		for (int i = 0; i < riddle.length; i++) {
			int randomNumber = ((int) (Math.random() * 6));
			riddle[i] = randomToNumber[randomNumber];
			System.out.println(numberToColor[riddle[i]]);// TEST***
		}
		return riddle;
	}

	// doe 1 keer 4 enkele gokjes (dus 1 beurt als het ware)
	public int[] takeAGuess() {
		// Plaats op de stip
		System.out.println("Positioneer de Robot op de zwarte stip en druk op ENTER");
		waitForKey(Button.ENTER);
		
		/* Lees kleuren in en rij verder */
		for (int i = 0; i < guess.length; i++) {
			if(i == 0) {
				// Beweeg naar eerste meetpunt
				BW.setRotations(BW.getRotationDegreesFromLength(4.5));
				BW.waitComplete();
				// Lees de kleur in
				guess[i] = CS.getColorID();
				
				// TODO IS NIET HEEL NETJES! (Kan eventueel ook een muziekje of toontje??)
				// Heel soms wordt geel aangegeven als 13 (bruin) daarom hebben we deze failsafe ingebouwd
				if(guess[i] == 13) {
					guess[i] = 3;
				}
				
				int test = guess[i];
				// Schrijf op display wat er gemeten is
				System.out.println(numberToColor[test]);
			} else if(i > 0 && i < guess.length) {
				BW.setRotations(BW.getRotationDegreesFromLength(6.5));
				BW.waitComplete();
				// Lees de kleur in
				guess[i] = CS.getColorID();
				
				// TODO IS NIET HEEL NETJES!
				// Heel soms wordt geel aangegeven als 13 (bruin) daarom hebben we deze failsafe ingebouwd
				if(guess[i] == 13) {
					guess[i] = 3;
				}
				
				int test = guess[i];
				// Schrijf op display wat er gemeten is
				System.out.println(numberToColor[test]);
			}
		}
		// Rij terug en geef de guess terug
		BW.setRotations(BW.getRotationDegreesFromLength(-(4.5+(3*6.5))));
		BW.waitComplete();
		return guess;
	}

	// geeft het aantal exact goed geraden pinnetjes (goede kleur en op juiste
	// positie)
	public int[] numbersGuessed(int[] riddle, int[] guess) {
		int numberFullyCorrect = 0;
		for (int i = 0; i < riddle.length; i++) {
			if (guess[i] == riddle[i]) {
				numberFullyCorrect++;
				riddle[i] = 66; // TODO
				guess[i] = 99; // TODO
			}
		}
		// Roep de numberColorsGuessed methode aan
		int numberColorCorrect = numberColorsGuessed(riddle, guess);
		int[] correctGuesses = { numberFullyCorrect, numberColorCorrect };

		// // TEST***
		// System.out.println("Riddle: ");
		// for (int i = 0; i < riddle.length; i++) {
		// System.out.printf("%d ", riddle[i]);
		// }
		// System.out.println("\nGuess: ");
		// for (int i = 0; i < riddle.length; i++) {
		// System.out.printf("%d ", guess[i]);
		// }
		return correctGuesses;
	}

	// geeft het aantal goed geraden kleuren (goede kleur, onjuiste positie)
	public int numberColorsGuessed(int[] tempRiddle, int[] guess) {
		int numberColorCorrect = 0;
		for (int i = 0; i < guess.length; i++) {
			for (int j = 0; j < tempRiddle.length; j++) {
				if (i != j) {
					if (guess[i] == tempRiddle[j]) {
						numberColorCorrect++;
						tempRiddle[j] = 66; // OP het moment dat deze kleur al gevonden is, wordt de waarde binnen deze
						// methode op 66 gezet
						break;
					}
				} else {
					continue;
				}
			}
		}
		return numberColorCorrect;
	}	

	public void playMasterMind() {
		System.out.println("Wil je mastermind spelen? Druk ENTER voor JA en ESCAPE voor NEE");
		waitForKey(Button.ENTER);

		CS.setFloodLight(true);
		CS.setFloodLight(6); // TODO NOG FF AAN SLEUTELEN
		CS.setColorIdMode();

		// cre�ert een raadsel
		int[] riddle = createRiddle();

		// Men kan 12 keer raden
		for (int i = 0; i < 12; i++) {
			// creeer eerst een tijdelijke array
			int[] tempRiddle = new int[4];
			for (int j = 0; j < riddle.length; j++) {
				tempRiddle[j] = riddle[j];
			}
			System.out.printf("Ronde #%d\n", i); // TODO kan ook van 12 naar 1
			int[] guess = takeAGuess();
			int[] numberGuessedCorrectly = numbersGuessed(tempRiddle, guess);
			if (numberGuessedCorrectly[0] == 4) {
				System.out.println("Gefeliciteerd, je hebt gewonnen!!");
				Sound.systemSound(true, 2); // Geeft een descending arpeggio
				Sound.systemSound(true, 2); // Geeft een descending arpeggio
				Delay.msDelay(5000); // Laat eerst 5 sec zien dat je gewonnen hebt
				break;
			} else if (i == 11 && numberGuessedCorrectly[0] != 4) {
				System.out.println("Helaas, je hebt de kleurencode niet kunnen raden. \nDe kleurencode was: \n");
				for (int j = 0; j < riddle.length; j++) {
					System.out.printf("%s ", numberToColor[riddle[j]]);
					
				}
				Sound.systemSound(true, 3); // Geeft een ascending arpeggio
				Delay.msDelay(5000); // Laat eerst 5 sec zien dat je gewonnen hebt
				break;
			} else {
				System.out.printf(
						"\nJe hebt %d kleuren goed op je juiste plaats \npJe hebt %d kleuren goed, maar niet op de juiste plaats",
						numberGuessedCorrectly[0], numberGuessedCorrectly[1]);
			}
		}
		System.out.println("Wil je nog een keer spelen? \nENTER = JA, een andere knop = NEE");
		Button.waitForAnyPress();
		if (Button.ENTER.isDown()) {
			playMasterMind();
		}
	}
	
	

	// TODO HIER KUNNEN WE EEN INTERFACE VOOR MAKEN
	public void waitForKey(Key key) {
		while (key.isUp()) {
			Delay.msDelay(100);
		}
		while (key.isDown()) {
			Delay.msDelay(100);
		}
	}

}