package active_perception;

import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;
import static jason.asSyntax.ASSyntax.*;

import java.util.logging.Level;
import java.util.Iterator;

public class reveal extends DefaultInternalAction {
	private boolean goal_finished = false;

	@Override
	public boolean suspendIntention(){
		return true;
	}

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

		Event goal = ts.getC().addAchvGoal(plan, Intention.EmptyInt);

		new WaitGoal(goal.getTrigger(), ts);
		// while(!goal_finished){
		// }
		System.out.println("teste!!!!");

		return true;
	}

	class WaitGoal implements CircumstanceListener {
		private Trigger te;
		private TransitionSystem ts;
		private Circumstance c;
		private Intention si;
		private boolean dropped = false;
		private String sEvt;

		WaitGoal(Trigger te, TransitionSystem ts){
			this.te = te;
			this.ts = ts;
			c = ts.getC();
			si = c.getSelectedIntention();
            // System.out.println(si);

            Iterator<Intention> it = c.getAllIntentions();
            while(it.hasNext()){
                System.out.println(it.next());
            }

            System.out.println(c.getPendingActions());
			c.addEventListener(this);
			// sEvt = String.valueOf(si.getId());
            // c.addPendingIntention(sEvt, si);
		}

		void resume(final boolean stopByTimeout) {
			c.removeEventListener(this);

			ts.runAtBeginOfNextCycle(new Runnable() {
				public void run() {
                    try {
						if (c.removePendingIntention(sEvt) == si && (si.isAtomic() || !c.hasRunningIntention(si)) && !dropped) {
							if (! si.isFinished()) {
								si.peek().removeCurrentStep();

								if (si.isSuspended()) { // if the intention was suspended by .suspend
								   c.addPendingIntention(jason.stdlib.suspend.SUSPENDED_INT+si.getId(), si);
								} else {
								   c.resumeIntention(si);
								}
							}
						}
					}catch (Exception e) {
                        ts.getLogger().log(Level.SEVERE, "Error at active_perception.reveal thread", e);
                    }
				}
			});
			ts.getUserAgArch().wakeUpDeliberate();
		}

		public void eventAdded(Event e) {
            if (dropped)
                return;
        }

		public void intentionDropped(Intention i) {
            if (i.equals(si)) {
                dropped = true;
                resume(false);
            }
        }

		public void intentionAdded(Intention i) {
			IntendedMeans im = i.peek();
			if(im.isFinished()==true && te.equals(im.getTrigger())){
				goal_finished = true;
				System.out.println("ajsdjdsajd");
				resume(true);
			}
		}
        public void intentionResumed(Intention i) { }
        public void intentionSuspended(Intention i, String reason) { }
	}
}
