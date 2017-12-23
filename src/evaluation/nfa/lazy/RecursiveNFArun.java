package evaluation.nfa.lazy;

import base.Event;
import evaluation.common.Match;
import evaluation.nfa.NFA;
import evaluation.nfa.eager.elements.Instance;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static statistics.Statistics.matches;

public class RecursiveNFArun extends RecursiveTask<NFArunResult> {

    LazyNFA lazyNFA;
    Event event;
    List<List<Instance>> instancesToCheck;
    ArrayList<Instance> instancesToAdd;
    ArrayList<Instance> instancesToRemove;
    LinkedList<Match> matches;

    public RecursiveNFArun(LazyNFA lazyNFA, Event event, List<List<Instance>> instancesForEvent, ArrayList<Instance> instances, ArrayList<Instance> instances1, LinkedList<Match> matches) {
        this.lazyNFA = lazyNFA;
        this.event = event;
        this.instancesToCheck = instancesForEvent;
        this.instancesToAdd = instances;
        this.instancesToRemove = instances1;
        this.matches = matches;
    }

    @Override
    protected NFArunResult compute() {
        List<Instance> firstInstanceInList = (instancesToCheck.size() > 0)  ? instancesToCheck.get(0) : null;
        if (null == firstInstanceInList || (instancesToAdd.size() + instancesToRemove.size() + firstInstanceInList.size() < 100000))
        {
            if (lazyNFA.shouldActivateUnboundedIterativeMode()) {
                lazyNFA.performInstanceLoopWithUnboundedIterativeEvents(event, instancesToCheck, instancesToAdd,
                        instancesToRemove, matches);
            }
            else {
                lazyNFA.performRegularInstanceLoop(event, instancesToCheck, instancesToAdd, instancesToRemove, matches);
            }
            return new NFArunResult(instancesToAdd,instancesToRemove,matches);
        }
        else {
            /*
            List<List<Instance>> instancesToCheckLeft = new ArrayList<List<Instance>>();
            List<List<Instance>> instancesToCheckRight = new ArrayList<List<Instance>>();
            List<Instance> listToAdd;
            listToAdd = new ArrayList<>(firstInstanceInList.subList(0, firstInstanceInList.size()/2));
            instancesToCheckLeft.add(listToAdd);
            listToAdd = new ArrayList<>(firstInstanceInList.subList(firstInstanceInList.size()/2, firstInstanceInList.size()));
            instancesToCheckRight.add(listToAdd);
             */
            List<List<Instance>> instancesToCheckLeft = new ArrayList<List<Instance>>(instancesToCheck);
            instancesToCheckLeft.set(0, new ArrayList<>(firstInstanceInList.subList(0, firstInstanceInList.size()/2)));
            List<List<Instance>> instancesToCheckRight = new ArrayList<List<Instance>>(instancesToCheck);
            instancesToCheckRight.set(0, new ArrayList<>(firstInstanceInList.subList(firstInstanceInList.size()/2, firstInstanceInList.size())));
            RecursiveNFArun r1 = new RecursiveNFArun(lazyNFA, new Event(event),instancesToCheckLeft, new ArrayList<Instance>(), new ArrayList<Instance>(),  new LinkedList<Match>());
            RecursiveNFArun r2 = new RecursiveNFArun(lazyNFA, new Event(event),instancesToCheckRight, new ArrayList<Instance>(), new ArrayList<Instance>(),  new LinkedList<Match>());
            r1.fork();
            NFArunResult result1 = r1.join();
            NFArunResult result2 = r2.compute();
            result1.instancesToAdd.addAll(result2.instancesToAdd);
            result1.instancesToRemove.addAll(result2.instancesToRemove);
            result1.matches.addAll(result2.matches);
            return new  NFArunResult(result1.instancesToAdd, result1.instancesToRemove, result1.matches);

            /*
            invokeAll(new RecursiveNFArun(lazyNFA, new Event(event),instancesToCheckLeft, new ArrayList<Instance>(), new ArrayList<Instance>(),  new LinkedList<Match>()),
                      new RecursiveNFArun(lazyNFA, new Event(event),instancesToCheckRight, new ArrayList<Instance>(), new ArrayList<Instance>(),  new LinkedList<Match>()));
                      */

        }
    }
}
