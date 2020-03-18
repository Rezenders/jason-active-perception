package active_perception;

import jason.asSemantics.Agent;
import jason.asSyntax.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;

public class ActivePerception{

	private Map<Trigger, LinkedHashSet<Literal>> ap_beliefs_map = new HashMap<Trigger, LinkedHashSet<Literal>>();

	public ActivePerception(Agent outerContent){
		findPlansApBel(outerContent);
	}

	///Get beliefs marked with ap
	private List<Literal> findApBeliefs(Literal context){
		List<Literal> beliefs = new ArrayList<Literal>();
		Integer arity = context.getArity();

		if(context.isLiteral() && !context.getAnnots("ap").isEmpty()){
			beliefs.add(context);
		} else if(arity > 0 && context.isLiteral()){
			if(context.getTermsArray()[0].isLiteral()){
				beliefs.addAll(findApBeliefs((Literal)context.getTermsArray()[0]));
			}
			if(context.getTermsArray()[1].isLiteral()){
				beliefs.addAll(findApBeliefs((Literal)context.getTermsArray()[1]));
			}
		}
		return beliefs;
	}

	//Get plans that has any belief marked with ap in context
	public void findPlansApBel(Agent outerContent){
		for(Plan p : outerContent.getPL()){
			Literal context = (Literal)p.getContext();
			if(context != null){
				List<Literal>  ap_beliefs = findApBeliefs(context);
				if(!ap_beliefs.isEmpty()){
					Trigger trigger = p.getTrigger().clone();

					Trigger trigger_del = trigger.clone();
					trigger_del.setTrigOp(Trigger.TEOperator.del);

					ap_beliefs_map.computeIfAbsent(trigger, k -> new LinkedHashSet<Literal>()).addAll(ap_beliefs);
					ap_beliefs_map.computeIfAbsent(trigger_del, k -> new LinkedHashSet<Literal>()).addAll(ap_beliefs);
				}
			}

		}
	}

	public Map<Trigger, LinkedHashSet<Literal>> getPlansApBel(){
		return ap_beliefs_map;
	}
}
