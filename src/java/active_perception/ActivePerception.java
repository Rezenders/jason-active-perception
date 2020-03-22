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


	///Get beliefs marked with ap
	private static List<Literal> findApBeliefs(Literal context){
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
	public static Map<Trigger, LinkedHashSet<Literal>> getPlansApBel(Agent outerContent){
		Map<Trigger, LinkedHashSet<Literal>> ap_beliefs_map = new HashMap<Trigger, LinkedHashSet<Literal>>();
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
		return ap_beliefs_map;
	}

	//Annotating inital goals with annot
	public static void annotInitGoals(Agent outerContent, Map<Trigger, LinkedHashSet<Literal>> ap_beliefs_map, Atom annot){
		for(Literal l: outerContent.getInitialGoals()){
			for(Trigger t: ap_beliefs_map.keySet()){
				if(t.getLiteral().getPredicateIndicator().equals(l.getPredicateIndicator())){
					l.addAnnot(annot);
				}
			}
		}
	}

	//Annotating plans with annot
	public static void annotTriggers(Agent outerContent, Map<Trigger, LinkedHashSet<Literal>> ap_beliefs_map, Atom annot){
		for(Plan p: outerContent.getPL()){
			if(ap_beliefs_map.keySet().contains(p.getTrigger())){
				p.getTrigger().getLiteral().addAnnots(annot);
			}
		}
	}

	//add [annot] to !g inside plans body
	public static void annotPlanBodyAchieve(Agent outerContent, Map<Trigger, LinkedHashSet<Literal>> ap_beliefs_map, Atom annot){
		for(Plan p: outerContent.getPL()){
			PlanBody pb = p.getBody();
			for(int i=0; i<p.getBody().getPlanSize(); i++){
				Literal pb_ap = (Literal)pb.getBodyTerm();
				for(Trigger t: ap_beliefs_map.keySet()){
					if(t.getLiteral().getPredicateIndicator().equals(pb_ap.getPredicateIndicator()) && pb.getBodyType() == jason.asSyntax.PlanBody.BodyType.achieve ){
						pb_ap.addAnnot(annot);
						break;
					}
				}
				pb = pb.getBodyNext();
			}
		}
	}

}
