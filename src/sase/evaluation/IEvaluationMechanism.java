package sase.evaluation;

import java.util.ArrayList;
import java.util.List;

import sase.base.Event;
import sase.evaluation.common.Match;
import sase.pattern.Pattern;

public interface IEvaluationMechanism {

	public List<Match> processNewEvent(Event event, boolean canStartInstance);
	public List<Match> validateTimeWindow(long currentTime);
	public void completeCreation(List<Pattern> patterns);
	public List<Match> getLastMatches();
	public long size();
	public String getStructureSummary();
	public void removeConflictingInstances(List<Match> matches);

	public ArrayList<Match> waitForGroupToFinish();
}
