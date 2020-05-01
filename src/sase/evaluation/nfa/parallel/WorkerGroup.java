package sase.evaluation.nfa.parallel;

public enum WorkerGroup {
    EVENT_WORKER {
        @Override
        public WorkerGroup getOpposite() {
            return MATCH_WORKER;
        }
    },
    MATCH_WORKER {
        @Override
        public WorkerGroup getOpposite() {
            return EVENT_WORKER;
        }
    };

    public abstract WorkerGroup getOpposite();
}

