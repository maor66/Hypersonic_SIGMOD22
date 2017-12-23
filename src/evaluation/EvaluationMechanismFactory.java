package sase.evaluation;

import sase.evaluation.nfa.eager.AND_NFA;
import sase.evaluation.nfa.eager.AND_SEQ_NFA;
import sase.evaluation.nfa.eager.OR_AND_SEQ_NFA;
import sase.evaluation.nfa.eager.SEQ_NFA;
import sase.evaluation.nfa.lazy.LazyChainNFA;
import sase.evaluation.nfa.lazy.LazyMultiChainNFA;
import sase.evaluation.tree.TreeEvaluationMechanism;
import sase.pattern.Pattern;
import sase.simulator.Environment;
import sase.specification.EvaluationSpecification;

public class EvaluationMechanismFactory {

	public static Object createEvaluationMechanism(Pattern pattern) {
		EvaluationPlanCreator evaluationPlanCreator = Environment.getEnvironment().getEvaluationPlanCreator();
		EvaluationPlan evaluationPlan = evaluationPlanCreator.createEvaluationPlan(pattern);
		EvaluationSpecification specification = evaluationPlanCreator.getSpecification();
		switch (pattern.getType()) {
			case SEQ:
				switch(specification.type) {
					case EAGER:
						return new AND_SEQ_NFA(pattern);
					case LAZY_CHAIN:
						return new LazyChainNFA(pattern, evaluationPlan, specification.negationType);
					case LAZY_TREE:
						return null;
					case TREE:
						return new TreeEvaluationMechanism(pattern, evaluationPlan);
					default:
						return null;
			}
			case AND_SEQ:
				switch(specification.type) {
					case EAGER:
						return new AND_SEQ_NFA(pattern);
					case LAZY_CHAIN:
						return new LazyChainNFA(pattern, evaluationPlan, specification.negationType);
					case LAZY_TREE:
						return null;
					case TREE:
						return null;
					default:
						return null;
			}
			case OR:
				switch(specification.type) {
					case EAGER:
						return new OR_AND_SEQ_NFA(pattern);
					case LAZY_CHAIN:
						return new LazyMultiChainNFA(pattern, evaluationPlan, specification.negationType);
					case LAZY_TREE:
						return null;
					case TREE:
						return null;
					default:
						return null;
			}
			case OLD_SEQ:
				switch(specification.type) {
					case EAGER:
						return new SEQ_NFA(pattern);
					case LAZY_CHAIN:
					case LAZY_TREE:
					case TREE:
					default:
						return null;
				}
			case OLD_AND:
				switch(specification.type) {
					case EAGER:
						return new AND_NFA(pattern);
					case LAZY_CHAIN:
					case LAZY_TREE:
					case TREE:
					default:
						return null;
				}
			case ITER:
			case NEG:
			case NOP:
			default:
				throw new RuntimeException(String.format("Cannot create NFA for main pattern type %s", 
										   pattern.getType()));
			}
	}
}
