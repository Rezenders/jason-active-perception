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

public class Directive2B extends DefaultDirective implements Directive {

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
			// System.out.println(t);
			if(t.getOperator()!= Trigger.TEOperator.del){
				String new_label_str = "l__" + String.valueOf(outerContent.getPL().size() + 1);
				Pred new_label = new Pred(createLiteral(new_label_str));

				Trigger t_ap = t.clone();
				Atom ap = createAtom("ap");
				t_ap.getLiteral().addAnnots(ap);
				Plan new_plan = new Plan​(new_label, t_ap, null, new PlanBodyImpl());

				for(Literal b : ap_beliefs_map.getOrDefault(t, new LinkedHashSet<Literal>())){
					Literal update_literal = createLiteral("update").addAnnots(ap);
					// Literal update_literal = createLiteral("update");
					update_literal.addTerm(b.clone());

					PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.achieve, update_literal);
					new_plan.getBody().add(bl);
				}

				Literal g_rp = (Literal)t.getLiteral().clone();
				Atom rp = createAtom("rp");
				PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.achieve, g_rp.addAnnots(rp));
				new_plan.getBody().add(bl);

				try{
					outerContent.getPL().add(new_plan);
				}catch(JasonException je){
					System.out.println("Error adding new plan");
				}
			}
		}

		String update_label_str = "l__" + String.valueOf(outerContent.getPL().size() + 1);
		Pred update_label = new Pred(createLiteral(update_label_str));

		Literal update = createLiteral("update");
		VarTerm x_term = new VarTerm("X");
		update.addTerm(x_term);
		// Trigger update_trigger = new Trigger(TEOperator.add, TEType.achieve, createLiteral("update(X)").addAnnots(ap_atom));
		// Trigger update_trigger = new Trigger(TEOperator.add, TEType.achieve, createLiteral("update(X)"));
		Trigger update_trigger = new Trigger(TEOperator.add, TEType.achieve, update.addAnnots(ap_atom));

		InternalActionLiteral isUpdated = new InternalActionLiteral("active_perception.isUpdated");
		isUpdated.addTerm(x_term);
		LogExpr not_isUpdated = new LogExpr(LogExpr.LogicalOp.not, isUpdated);
		Plan update_plan = new Plan​(update_label, update_trigger, not_isUpdated, new PlanBodyImpl());

		PlanBodyImpl update_body = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.test, x_term);
		update_plan.getBody().add(update_body);

		try{
			outerContent.getPL().add(update_plan);
		}catch(JasonException je){
			System.out.println("Error adding new plan");
		}

		String update2_label_str = "l__" + String.valueOf(outerContent.getPL().size() + 1);
		Pred update2_label = new Pred(createLiteral(update2_label_str));
		Plan update2_plan = new Plan​(update2_label, update_trigger, null, new PlanBodyImpl());

		try{
			outerContent.getPL().add(update2_plan);
		}catch(JasonException je){
			System.out.println("Error adding new plan");
		}

		// System.out.println(update_plan);

		// System.out.println(outerContent.getInitialGoals());
		// for(Plan p: outerContent.getPL()){
		// 	System.out.println(p);
		// 	// System.out.println(p);
		// }
		return null;
	}

}
