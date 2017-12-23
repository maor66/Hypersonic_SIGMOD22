package evaluation.nfa.lazy;

import evaluation.common.Match;
import evaluation.nfa.eager.elements.Instance;

import java.util.List;

public class NFArunResult {
    List<Instance> instancesToAdd;
    List<Instance> instancesToRemove;
    List<Match> matches;

    public NFArunResult(List<Instance> instancesToAdd, List<Instance> instancesToRemove, List<Match> matches) {
        this.instancesToAdd = instancesToAdd;
        this.instancesToRemove = instancesToRemove;
        this.matches = matches;
    }
}
