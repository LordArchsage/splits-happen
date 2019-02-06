package EITCIRPD.splithappens;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

@Component
public class BowlingScorer {

	int round = 0;

	public int score(String rollInput) {
		round = 1;
		Multimap<Integer, Integer> game = ArrayListMultimap.create();
		Map<Integer, Integer> rollsLeft = prepareGame();
		List<Character> rolls = rollInput.trim().chars().mapToObj(e -> (char) e).collect(Collectors.toList()); //parse input to list of rolls

		for (Character roll : rolls) {
			switch (roll) {
			case '-': //counting - as zero
				addRoll(0, game, round, rollsLeft, true);
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				int rollValue = Integer.parseInt(roll + "");
				addRoll(rollValue, game, round, rollsLeft, true);
				break;
			case 'X':
			case 'x': //making sure it's case insensitive
				handleStrike(game, rollsLeft);
				break;
			case '/':
				handleSpare(game, rollsLeft);
				break;
			default : //in case of non-valid char do nothing
				break;
			}
		}

		return game.values().stream().mapToInt(i -> i.intValue()).sum();
	}

	private void addRoll(int rollValue, Multimap<Integer, Integer> game, int round, Map<Integer, Integer> rollsLeft,
			boolean topLevelCall) {
		int rollsLeftPrior = rollsLeft.get(round);
		if (round < 11) //only count endgame rolls for the frames from normal game, they don't count extra
			game.put(round, rollValue);
		rollsLeft.put(round, --rollsLeftPrior);//reduce the number of rolls left to make for current round
		if (topLevelCall && rollsLeft.get(round - 1) != null && rollsLeft.get(round - 1) > 0) { //check if there was a spare or strike previously that still needs rolls
			addRoll(rollValue, game, round - 1, rollsLeft, false);
		}
		if (topLevelCall && rollsLeft.get(round - 2) != null && rollsLeft.get(round - 2) > 0) { // check for strike previously that still needs another roll
			addRoll(rollValue, game, round - 2, rollsLeft, false);
		}
		handleFrameChange(rollsLeft, round, topLevelCall); //check to see if should increment round number
	}

	private void handleFrameChange(Map<Integer, Integer> rollsLeft, int round, boolean topLevelCall) {
		//topLevelCall makes sure that only addRoll calls from the roll itself count to increment roundNumber, not adding points to previous round due to strike/spare
		if (topLevelCall && rollsLeft.get(round) == 0)
			this.round++;
	}

	//Create basic game where there are 2 rolls per frame
	private Map<Integer, Integer> prepareGame() { 
		Map<Integer, Integer> rollsLeft = new HashMap<>();
		for (int i = 1; i <= 10; i++) {
			rollsLeft.put(i, 2);
		}
		return rollsLeft;
	}

	//round isn't relative here so doesn't need to be passed in
	private void handleStrike(Multimap<Integer, Integer> game, Map<Integer, Integer> rollsLeft) {
		addRoll(10, game, round, rollsLeft, true);
		rollsLeft.put(round, 2);
		if (round == 10) { //handle endgame
			rollsLeft.put(11, 2);
		} else if (round == 11) {
			rollsLeft.put(12, 1);
		}
		round++;
	}

	//round isn't relative here so doesn't need to be passed in
	private void handleSpare(Multimap<Integer, Integer> game, Map<Integer, Integer> rollsLeft) {
		int pinsDroppedPreviouslyThisRound = game.get(round).stream().mapToInt(i -> i.intValue()).sum();
		int pinsDroppedThisRoll = 10 - pinsDroppedPreviouslyThisRound;
		addRoll(pinsDroppedThisRoll, game, round, rollsLeft, true);
		rollsLeft.put(round - 1, 1); //assume that the handleFrameChange has already incremented the round number
		if (round == 11) //handle endgame
			rollsLeft.put(11, 1);
	}
}
