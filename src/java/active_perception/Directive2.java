// 2 & Beliefs & Batch & All & Time & Natural
package active_perception;

import jason.asSemantics.Agent;
import jason.asSyntax.*;
import jason.asSyntax.directives.*;
import jason.asSyntax.BodyLiteral.BodyType;
import static jason.asSyntax.ASSyntax.*;
import jason.asSyntax.Trigger.TEType;
import jason.asSyntax.Trigger.TEOperator;
import jason.JasonException;
import jason.architecture.AgArch;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import active_perception.ActivePerception;
import active_perception.PlansApBel;
import active_perception.ApBels;

public class Directive2 extends DefaultDirective implements Directive {

	public Agent process(Pred directive, Agent outerContent, Agent innerContent){
		if (outerContent == null)
            return null;

		//Get plans that has any belief marked with ap in context
		PlansApBel plans_ap_bel = ActivePerception.getPlansApBel(outerContent);

		Atom ap_atom = createAtom("ap");
		Atom rp_atom = createAtom("rp");

		//Annotating inital goals with ap
		// ActivePerception.annotInitGoals(outerContent, plans_ap_bel.getTriggerSet(), ap_atom);
		ActivePerception.annotInitGoals(outerContent, plans_ap_bel.getTriggerSet(), ap_atom);

		//Annotating plans with rp
		ActivePerception.annotTriggers(outerContent, plans_ap_bel.getTriggerSet(), rp_atom);

		//add [ap] to !g inside plans body
		// ActivePerception.annotPlanBodyAchieve(outerContent, plans_ap_bel.getTriggerSet(), ap_atom);
		ActivePerception.annotPlanBodyAchieve(outerContent, plans_ap_bel.getTriggerSet(), ap_atom);

		//Adding new plans +!g[ap] -> .update(...); !g[rp].
		for(Trigger t : plans_ap_bel.getTriggerSet()){
			if(t.getOperator()!= Trigger.TEOperator.del){
				Trigger t_ap = t.clone();
				t_ap.getLiteral().addAnnots(ap_atom);

				List<PlanBodyImpl> pb_list = new ArrayList<PlanBodyImpl>();

				Set<Literal> added_bels = new LinkedHashSet<Literal>();
				// for(LinkedHashSet<Literal> bel_set : ap_beliefs_map.getOrDefault(t, new LinkedList<LinkedHashSet<Literal>>())){
				for(ApBels ap_bels : plans_ap_bel.getApBels(t)){
					for(Literal b : ap_bels.ap_set){
						if(!added_bels.contains(b)){
							Literal update_literal = createLiteral("update").addAnnots(ap_atom);
							update_literal.addTerm(b.clone());

							PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.achieve, update_literal);
							pb_list.add(bl);
							added_bels.add(b);
						}
					}
				}

				Literal g_rp = (Literal)t.getLiteral().clone();
				PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.achieve, g_rp.addAnnots(rp_atom));
				pb_list.add(bl);

				ActivePerception.addNewPlan(outerContent, t_ap, null, pb_list.toArray(new PlanBodyImpl[pb_list.size()]));

			}
		}

		//Adding new plans +!update(X): not active_perception.isUpdated(X) <- ?X.
		//and +!update(X).
		ActivePerception.addUpdatePlan(outerContent);

		// for(Plan p: outerContent.getPL()){
		// 	System.out.println(p);
		// }
		return null;
	}

}
