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
		List<Character> rolls = rollInput.chars().mapToObj(e -> (char) e).collect(Collectors.toList());

		for (Character roll : rolls) {
			switch (roll) {
			case '-':
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
			case 'x':
				handleStrike(game, rollsLeft);
				break;
			case '/':
				handleSpare(game, rollsLeft);
				break;

			}
		}

		return game.values().stream().mapToInt(i -> i.intValue()).sum();
	}

	private void addRoll(int rollValue, Multimap<Integer, Integer> game, int round, Map<Integer, Integer> rollsLeft,
			boolean topLevelCall) {
		System.out.println(
				"adding roll " + rollValue + " to round " + round + "rolls left for round " + rollsLeft.get(round));
		int rollsLeftPrior = rollsLeft.get(round);
		if (round < 11)
			game.put(round, rollValue);
		rollsLeft.put(round, --rollsLeftPrior);
		if (topLevelCall && rollsLeft.get(round - 1) != null && rollsLeft.get(round - 1) > 0) {
			addRoll(rollValue, game, round - 1, rollsLeft, false);
		}
		if (topLevelCall && rollsLeft.get(round - 2) != null && rollsLeft.get(round - 2) > 0) {
			addRoll(rollValue, game, round - 2, rollsLeft, false);
		}
		handleFrameChange(rollsLeft, round, topLevelCall);
	}

	private void handleFrameChange(Map<Integer, Integer> rollsLeft, int round, boolean topLevelCall) {
		if (topLevelCall && rollsLeft.get(round) == 0)
			this.round++;
	}

	private Map<Integer, Integer> prepareGame() {
		Map<Integer, Integer> rollsLeft = new HashMap<>();
		for (int i = 1; i <= 10; i++) {
			rollsLeft.put(i, 2);
		}
		return rollsLeft;
	}

	private void handleStrike(Multimap<Integer, Integer> game, Map<Integer, Integer> rollsLeft) {
		addRoll(10, game, round, rollsLeft, true);
		rollsLeft.put(round, 2);
		if (round == 10) {
			rollsLeft.put(11, 2);
		} else if (round == 11) {
			rollsLeft.put(12, 1);
		}
		round++;
	}

	private void handleSpare(Multimap<Integer, Integer> game, Map<Integer, Integer> rollsLeft) {
		int pinsDroppedPreviouslyThisRound = game.get(round).stream().mapToInt(i -> i.intValue()).sum();
		int pinsDroppedThisRoll = 10 - pinsDroppedPreviouslyThisRound;
		addRoll(pinsDroppedThisRoll, game, round, rollsLeft, true);
		rollsLeft.put(round - 1, 1);
		if (round == 11)
			rollsLeft.put(11, 1);
	}
}
