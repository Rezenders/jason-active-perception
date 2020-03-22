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
				String new_label_str = "l__" + String.valueOf(outerContent.getPL().size() + 1);
				Pred new_label = new Pred(createLiteral(new_label_str));

				Trigger t_ap = t.clone();
				// Atom ap = createAtom("ap");
				t_ap.getLiteral().addAnnots(ap_atom);
				Plan new_plan = new Plan​(new_label, t_ap, null, new PlanBodyImpl());

				for(Literal b : ap_beliefs_map.getOrDefault(t, new LinkedHashSet<Literal>())){
					InternalActionLiteral update_ia = new InternalActionLiteral("active_perception.update");
					update_ia.addTerm(b);

					PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.internalAction, update_ia);
					new_plan.getBody().add(bl);
				}

				Literal g_rp = (Literal)t.getLiteral().clone();
				// Atom rp = createAtom("rp");
				PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.achieve, g_rp.addAnnots(rp_atom));
				new_plan.getBody().add(bl);

				try{
					outerContent.getPL().add(new_plan);
				}catch(JasonException je){
					System.out.println("Error adding new plan");
				}
			}
		}

		return null;
	}

}
