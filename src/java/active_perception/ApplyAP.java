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

public class ApplyAP extends DefaultDirective implements Directive {

	Map<Trigger, LinkedHashSet<Literal>> ap_beliefs_map = new HashMap<Trigger, LinkedHashSet<Literal>>();

	public Agent process(Pred directive, Agent outerContent, Agent innerContent){
		if (outerContent == null)
            return null;

		Set<Trigger> triggers_ap = new LinkedHashSet<Trigger>();

		//Get plans that has any belief marked with ap in context
		for(Plan p : outerContent.getPL()){
			Literal context = (Literal)p.getContext();
			if(context != null){
				List<Literal>  ap_beliefs = ActivePerception.getApBeliefs(context);
				if(!ap_beliefs.isEmpty()){
					Trigger trigger = p.getTrigger().clone();
					triggers_ap.add(trigger);

					Trigger trigger_del = trigger.clone();
					trigger_del.setTrigOp(Trigger.TEOperator.del);
					triggers_ap.add(trigger_del);

					ap_beliefs_map.computeIfAbsent(trigger, k -> new LinkedHashSet<Literal>()).addAll(ap_beliefs);
				}
			}
		}

		//Annotating plans with rp
		for(Plan p: outerContent.getPL()){
			if(triggers_ap.contains(p.getTrigger())){
				Atom rp = createAtom("rp");
				p.getTrigger().getLiteral().addAnnots(rp);
			}

			//add [ap] to !g inside plans body
			PlanBody pb = p.getBody();
			for(int i=0; i<p.getBody().getPlanSize(); i++){
				for(Trigger t: triggers_ap){
					if(t.getLiteral().equals(pb.getBodyTerm()) && pb.getBodyType() == jason.asSyntax.PlanBody.BodyType.achieve ){
						Atom ap = createAtom("ap");
						Literal pb_ap = (Literal)pb.getBodyTerm();
						pb_ap.addAnnot(ap);
						break;
					}
				}
				pb = pb.getBodyNext();
			}
		}

		//Annotating inital goals with ap
		for(Literal l: outerContent.getInitialGoals()){
			for(Trigger t: triggers_ap){
				if(t.getLiteral().equals(l)){
					Atom rp = createAtom("ap");
					l.addAnnot(rp);
				}
			}
		}

		//Adding new plans +!g[ap] -> .update(...); !g[rp].
		for(Trigger t : triggers_ap){
			String new_label_str = "l__" + String.valueOf(outerContent.getPL().size() + 1);
			Pred new_label = new Pred(createLiteral(new_label_str));

			Atom ap = createAtom("ap");
			t.getLiteral().addAnnots(ap);
			Plan new_plan = new Plan​(new_label, t, null, new PlanBodyImpl());

			for(Literal b : ap_beliefs_map.getOrDefault(t, new LinkedHashSet<Literal>())){
				InternalActionLiteral update_ia = new InternalActionLiteral("active_perception.update");
				update_ia.addTerm(b);

				PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.internalAction, update_ia);
				new_plan.getBody().add(bl);
			}

			Literal literal_aux = (Literal)t.getLiteral().clone();
			Atom rp = createAtom("rp");
			PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.achieve, literal_aux.addAnnots(rp));
			new_plan.getBody().add(bl);

			try{
				outerContent.getPL().add(new_plan);
			}catch(JasonException je){
				System.out.println("Error adding new plan");
			}
		}
		return null;
	}

}
