package evaluation;

import java.util.List;

import base.Event;
import evaluation.common.Match;
import pattern.Pattern;

public interface IEvaluationMechanism {

	public List<Match> processNewEvent(Event event, boolean canStartInstance);
	public List<Match> validateTimeWindow(long currentTime);
	public void completeCreation(Pattern pattern);
	public List<Match> getLastMatches();
	public long size();
	public String getStructureSummary();
	public void removeConflictingInstances(List<Match> matches);
}
