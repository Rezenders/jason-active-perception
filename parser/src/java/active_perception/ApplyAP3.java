// 3 & Beliefs & Individual & All & Time & Natural
package active_perception;

import jason.asSemantics.Agent;
import jason.asSyntax.*;
import jason.asSyntax.directives.*;
import jason.asSyntax.BodyLiteral.BodyType;
import static jason.asSyntax.ASSyntax.*;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import jason.asSyntax.Trigger.TEType;
import jason.asSyntax.Trigger.TEOperator;
import jason.JasonException;
import jason.architecture.AgArch;

public class ApplyAP3 extends DefaultDirective implements Directive {
	Map<Trigger, LinkedHashSet<Literal>> ap_beliefs_map = new HashMap<Trigger, LinkedHashSet<Literal>>();

	public Agent process(Pred directive, Agent outerContent, Agent innerContent){
		if (outerContent == null)
            return null;

		Set<Trigger> triggers_ap = new LinkedHashSet<Trigger>();

		//Get plans that has any belief marked with ap in context
		for(Plan p : outerContent.getPL()){
			Literal context = (Literal)p.getContext();
			if(context != null){
				//Get beliefs annotated with ap
				List<Literal>  ap_beliefs = getApBeliefs(context);

				//If it has any beliefs that are annotated
				if(!ap_beliefs.isEmpty()){
					//Get trigger. e.g: +!g
					Trigger trigger = p.getTrigger().clone();
					triggers_ap.add(trigger);
					// Mapping trigger with its respective beliefs marked with
					// ap. e.g. (+!g, [b[ap(1000)], c[ap(2000)]])
					ap_beliefs_map.computeIfAbsent(trigger, k -> new LinkedHashSet<Literal>()).addAll(ap_beliefs);
				}
			}
		}

		//Annotating plans with ap and label. e.g +!g[ap,l_3]
		for(Plan p: outerContent.getPL()){
			if(triggers_ap.contains(p.getTrigger())){
				Atom ap = createAtom("ap");
				Term label = p.getLabel().clone();
				p.getTrigger().getLiteral().addAnnots(ap, label);
			}
			// System.out.println(p);
		}
		return null;
	}

	List<Literal> getApBeliefs(Literal context){
		List<Literal> beliefs = new ArrayList<Literal>();
		Integer arity = context.getArity();

		if(arity==0 && !context.getAnnots("ap").isEmpty()){
			beliefs.add(context);
		}else if(arity > 0){
			beliefs.addAll(getApBeliefs((Literal)context.getTermsArray()[0]));
			beliefs.addAll(getApBeliefs((Literal)context.getTermsArray()[1]));
		}
		return beliefs;
	}

}
