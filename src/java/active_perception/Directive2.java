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

		ActivePerception activePerception = new ActivePerception(outerContent);
		Map<Trigger, LinkedHashSet<Literal>> ap_beliefs_map = activePerception.getPlansApBel();

		for(Plan p: outerContent.getPL()){
			//Annotating plans with rp
			if(ap_beliefs_map.keySet().contains(p.getTrigger())){
				Atom rp = createAtom("rp");
				p.getTrigger().getLiteral().addAnnots(rp);
			}

			//add [ap] to !g inside plans body
			PlanBody pb = p.getBody();
			for(int i=0; i<p.getBody().getPlanSize(); i++){
				for(Trigger t: ap_beliefs_map.keySet()){
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
			for(Trigger t: ap_beliefs_map.keySet()){
				if(t.getLiteral().equals(l)){
					Atom rp = createAtom("ap");
					l.addAnnot(rp);
				}
			}
		}

		//Adding new plans +!g[ap] -> .update(...); !g[rp].
		for(Trigger t : ap_beliefs_map.keySet()){
			if(t.getOperator()!= Trigger.TEOperator.del){
				String new_label_str = "l__" + String.valueOf(outerContent.getPL().size() + 1);
				Pred new_label = new Pred(createLiteral(new_label_str));

				Trigger t_ap = t.clone();
				Atom ap = createAtom("ap");
				t_ap.getLiteral().addAnnots(ap);
				Plan new_plan = new Plan​(new_label, t_ap, null, new PlanBodyImpl());

				for(Literal b : ap_beliefs_map.getOrDefault(t, new LinkedHashSet<Literal>())){
					InternalActionLiteral update_ia = new InternalActionLiteral("active_perception.update");
					update_ia.addTerm(b);

					PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.internalAction, update_ia);
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

		return null;
	}

}