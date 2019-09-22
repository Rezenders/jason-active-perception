package active_perception;

import jason.asSemantics.Agent;
import jason.asSyntax.*;
import jason.asSyntax.directives.*;
import jason.asSyntax.BodyLiteral.BodyType;
import static jason.asSyntax.ASSyntax.*;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import jason.asSyntax.Trigger.TEType;
import jason.asSyntax.Trigger.TEOperator;
import jason.JasonException;
import jason.architecture.AgArch;

public class ApplyAP extends DefaultDirective implements Directive {

	Set<Literal> ap_beliefs_set = new HashSet<Literal>();

	public Agent process(Pred directive, Agent outerContent, Agent innerContent){
		if (outerContent == null)
            return null;

		Set<Trigger> triggers_ap = new HashSet<Trigger>();

		//Get plans that has any belief marked with ap in context
		for(Plan p : outerContent.getPL()){
			Literal context = (Literal)p.getContext();
			if(context != null){
				List<Literal>  ap_beliefs = getApBeliefs(context);
				if(!ap_beliefs.isEmpty()){
					triggers_ap.add(p.getTrigger().clone());
					ap_beliefs_set.addAll(ap_beliefs);
				}
			}
		}
		//Annotating plans with ap
		for(Plan p: outerContent.getPL()){
			if(triggers_ap.contains(p.getTrigger())){
				Atom ap = createAtom("ap");
				p.getTrigger().getLiteral().addAnnots(ap);
			}
		}

		for(Trigger t : triggers_ap){
			String new_label_str = "l__" + String.valueOf(outerContent.getPL().size() + 1);
			Pred new_label = new Pred(createLiteral(new_label_str));

			Plan new_plan = new Plan​(new_label, t, null, new PlanBodyImpl());

			for(Literal b : ap_beliefs_set){
				InternalActionLiteral update_ia = new InternalActionLiteral("active_perception.update");
				update_ia.addTerm(b);

				PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.internalAction, update_ia);
				new_plan.getBody().add(bl);
			}

			Literal literal_aux = (Literal)t.getLiteral().clone();
			Atom ap = createAtom("ap");
			PlanBodyImpl bl = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.achieve, literal_aux.addAnnots(ap));
			new_plan.getBody().add(bl);

			try{
				outerContent.getPL().add(new_plan);
			}catch(JasonException je){
				System.out.println("Error adding new plan");
			}
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
