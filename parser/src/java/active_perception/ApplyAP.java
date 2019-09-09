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

public class ApplyAP extends DefaultDirective implements Directive {

	public Agent process(Pred directive, Agent outerContent, Agent innerContent) {
		// System.out.println(outerContent.getPL());
		if (outerContent == null)
            return null;
		Agent newAg = new Agent();
		Set<Trigger> triggers_ap = new HashSet<Trigger>();
		for(Plan p : outerContent.getPL()){
			Literal context = (Literal)p.getContext();
			if(context != null){
				System.out.println(context);
				System.out.println(p.getTrigger());
				if(isAP(context)){
					triggers_ap.add(p.getTrigger());
				}
			}
		}

		List<Plan> plans = new ArrayList<Plan>();
		// //trocar +!g.... por +!g <- !update_ap_g; !g_ap.
		for(Trigger t : triggers_ap){
			//Plan​(Pred label, Trigger te, LogicalFormula ct, PlanBody bd)
			Plan p = new Plan();
			// new Plan( new Pred((Literal)(lt.get(0))),
            //     (Trigger)lt.get(1),
            //     (LogicalFormula)c,
            //     (PlanBody)lt.get(3));
			System.out.println(p.getLabel());
		// 	p.
		}
		//adicionar +!g[ap(ALGUMA COISA)]


		return null;
	}

	//Retornar os beliefs que precisam ser atualizados ao invés de true/false
	boolean isAP(Literal context){
		Integer arity = context.getArity();
		if(arity==0 && !context.getAnnots("ap").isEmpty()){
			return true;
		}else if(arity > 0){
			if(isAP((Literal)context.getTermsArray()[0])){
				return true;
			}
			if(isAP((Literal)context.getTermsArray()[1])){
				return true;
			}
		}
		return false;
	}

}
