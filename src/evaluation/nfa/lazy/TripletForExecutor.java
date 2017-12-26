package evaluation.nfa.lazy;

import evaluation.nfa.eager.elements.Instance;

import java.util.ArrayList;
import java.util.List;

public class TripletForExecutor {
    public List<Instance> instancesToAdd;
    public List<Instance> instancesToRemove;
    public  List<Instance>  reclocateInstance;

    public TripletForExecutor(List<Instance> instancesToAdd, List<Instance> instancesToRemove,  List<Instance>  reclocateInstance) {
        this.instancesToAdd = instancesToAdd;
        this.instancesToRemove = instancesToRemove;
        this.reclocateInstance = reclocateInstance;
    }
}
