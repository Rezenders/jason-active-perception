// 2 & Beliefs & Batch & All & Time & Natural
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

import active_perception.ActivePerception;

public class Directive2 extends DefaultDirective implements Directive {

	public Agent process(Pred directive, Agent outerContent, Agent innerContent){
		if (outerContent == null)
            return null;

		Map<Trigger, LinkedHashSet<Literal>> ap_beliefs_map = ActivePerception.getPlansApBel(outerContent);

		Atom ap_atom = createAtom("ap");
		Atom rp_atom = createAtom("rp");

		//Annotating inital goals with ap
		ActivePerception.annotInitGoals(outerContent, ap_beliefs_map, ap_atom);

		//Annotating plans with rp
		ActivePerception.annotTriggers(outerContent, ap_beliefs_map, rp_atom);

		//add [ap] to !g inside plans body
		ActivePerception.annotPlanBodyAchieve(outerContent, ap_beliefs_map, ap_atom);

		//Adding new plans +!g[ap] -> .update(...); !g[rp].
		for(Trigger t : ap_beliefs_map.keySet()){
			if(t.getOperator()!= Trigger.TEOperator.del){
				Trigger t_ap = t.clone();
				t_ap.getLiteral().addAnnots(ap_atom);

				List<PlanBodyImpl> pb_list = new ArrayList<PlanBodyImpl>();

				for(Literal b : ap_beliefs_map.getOrDefault(t, new LinkedHashSet<Literal>())){
					InternalActionLiteral update_ia = new InternalActionLiteral("active_perception.update");
					update_ia.addTerm(b);

					PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.internalAction, update_ia);
					pb_list.add(bl);
				}

				Literal g_rp = (Literal)t.getLiteral().clone();
				PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.achieve, g_rp.addAnnots(rp_atom));
				pb_list.add(bl);

				ActivePerception.addNewPlan(outerContent, t_ap, null, pb_list);
			}
		}

		return null;
	}

}
