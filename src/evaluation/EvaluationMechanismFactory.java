package evaluation;

import evaluation.nfa.eager.AND_NFA;
import evaluation.nfa.eager.AND_SEQ_NFA;
import evaluation.nfa.eager.OR_AND_SEQ_NFA;
import evaluation.nfa.eager.SEQ_NFA;
import evaluation.nfa.lazy.LazyChainNFA;
import evaluation.nfa.lazy.LazyMultiChainNFA;
import evaluation.tree.MultiTreeEvaluationMechanism;
import evaluation.tree.TreeEvaluationMechanism;
import pattern.Pattern;
import simulator.Environment;
import specification.EvaluationSpecification;

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
						return new TreeEvaluationMechanism(pattern, evaluationPlan);
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
						return new MultiTreeEvaluationMechanism(pattern, evaluationPlan);
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
				throw new RuntimeException(String.format("Cannot create an evaluation mechanism for main pattern type %s", 
										   pattern.getType()));
			}
	}
}
