package sase.pattern.workload;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sase.multi.MultiPlan;
import sase.pattern.CompositePattern;
import sase.pattern.Pattern;

public class DynamicMultiPatternWorkloadManager extends MultiPatternWorkloadManager implements IWorkloadManager {

	private List<Pattern> currentWorkload;
	private List<Pattern> reservedWorkload;
	private Double workloadModificationProbability;
	
	public DynamicMultiPatternWorkloadManager(List<Pattern> patterns, 
											  Double initialCurrentToReservedRatio,
											  Double workloadModificationProbability) {
		super(patterns);
		currentWorkload = new ArrayList<Pattern>();
		reservedWorkload = new ArrayList<Pattern>();
		this.workloadModificationProbability = workloadModificationProbability;
		Integer initialNumberOfPatterns = new Double(this.patterns.size() * initialCurrentToReservedRatio).intValue();
		for (int i = 0; i < this.patterns.size(); ++i) {
			if (i < initialNumberOfPatterns) {
				currentWorkload.add(this.patterns.get(i));
			}
			else {
				reservedWorkload.add(this.patterns.get(i));
			}
		}
	}
	
	@Override
	public List<Pattern> getCurrentWorkload() {
		return currentWorkload;
	}

	@Override
	public boolean tryAlterWorkload(MultiPlan currentMultiPlan) {
		Random random = new Random();
		if (random.nextDouble() > workloadModificationProbability) {
			return false;
		}
		if (random.nextBoolean() && !reservedWorkload.isEmpty()) {
			insertRandomPattern(currentMultiPlan, random);
		}
		else {
			removeRandomPattern(currentMultiPlan, random);
		}
		return true;
	}

	private void insertRandomPattern(MultiPlan multiPlan, Random random) {
		CompositePattern randomPattern = (CompositePattern)moveRandomPattern(reservedWorkload, currentWorkload, random);
		multiPlan.addPatternPlan(randomPattern);
		multiPlan.getGraph().addPattern(randomPattern);
	}

	private void removeRandomPattern(MultiPlan multiPlan, Random random) {
		CompositePattern randomPattern = (CompositePattern)moveRandomPattern(currentWorkload, reservedWorkload, random);
		multiPlan.removePatternPlan(randomPattern);
		multiPlan.getGraph().removePattern(randomPattern);
	}

	private Pattern moveRandomPattern(List<Pattern> source, List<Pattern> destination, Random random) {
		Pattern randomPattern = source.remove(random.nextInt(source.size()));
		destination.add(randomPattern);
		return randomPattern;
	}

}
