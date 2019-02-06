package EITCIRPD.splithappens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BowlingController {
	
	@Autowired
	private BowlingScorer scorer;
	
	@RequestMapping("/score/{rollInput}")
	Integer score(@PathVariable String rollInput){
		return scorer.score(rollInput);
	}
}
