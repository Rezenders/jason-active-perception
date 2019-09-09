package active_perception;

import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;
import static jason.asSyntax.ASSyntax.*;

import java.util.Iterator;

public class reveal_brute extends DefaultInternalAction {

	@Override
	public Object execute( TransitionSystem ts,	Unifier un,	Term[] args ) throws Exception {
        Atom belief_aux = (Atom)args[0];
        Literal belief = createLiteral(belief_aux.getFunctor());
        belief.addAnnot(ts.getAg().getBB().TSelf); //Change to active_perception?

		Literal plan;
		try {
		    plan = (Literal)args[1];
		} catch ( IndexOutOfBoundsException e ) {
		    plan = createLiteral("active_perception_plan");
		}

		Circumstance c = ts.getC();
		Event goal = c.addAchvGoal(plan, Intention.EmptyInt);
		boolean ap_plan_finished = false;

		while(!ap_plan_finished){
			Iterator<Intention> it = c.getAllIntentions();
			System.out.println("getALl");
			while(it!=null && it.hasNext()){
				IntendedMeans im = it.next().peek();
				System.out.println("has next");
				if(im.isFinished()==true && goal.getTrigger().equals(im.getTrigger())){
					System.out.println("ajsdjdsajd");
					ap_plan_finished = true;
				}
			}
		}

		System.out.println("teste!!!!");

		return true;
	}
}
