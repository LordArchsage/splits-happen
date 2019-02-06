package EITCIRPD.splithappens;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class ScorerTest {

	private BowlingScorer scorer = new BowlingScorer();
	
	@Test
	public void testPerfect(){
		String perfectRolls = "XXXXXXXXXXXX";
		Assert.assertThat(scorer.score(perfectRolls), Matchers.is(300));
	}
	
	@Test
	public void testNines(){
		String rollNines = "9-9-9-9-9-9-9-9-9-9-";
		Assert.assertThat(scorer.score(rollNines), Matchers.is(90));
	}
	
	@Test
	public void testFives(){
		String rollFives = "5/5/5/5/5/5/5/5/5/5/5";
		Assert.assertThat(scorer.score(rollFives), Matchers.is(150));
	}
	
	@Test
	public void testEndInStrike(){
		String rollComplex = "X7/9-X-88/-6XXX81";
		Assert.assertThat(scorer.score(rollComplex), Matchers.is(167));
	}
	
	@Test
	public void testEndInSpare(){
		String rollComplex = "x729-369/541/9-628/1";
		Assert.assertThat(scorer.score(rollComplex), Matchers.is(117));
	}
	
	@Test
	public void testStrikeInExtra(){
		String rollComplex = "9/-/XX627/8/X9-XX8";
		Assert.assertThat(scorer.score(rollComplex), Matchers.is(176));
	}

	@Test
	public void testComplex(){
		String rollComplex = "x6/x7/x9/8/xxx9-";
		Assert.assertThat(scorer.score(rollComplex), Matchers.is(216));
	}
}
