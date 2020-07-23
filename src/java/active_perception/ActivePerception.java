package active_perception;

import jason.asSemantics.Agent;
import jason.asSyntax.*;
import jason.JasonException;
import static jason.asSyntax.ASSyntax.*;

import jason.asSyntax.Trigger.TEType;
import jason.asSyntax.Trigger.TEOperator;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;

public class ActivePerception{


	///Get beliefs marked with ap
	private static LinkedHashSet<Literal> findApBeliefs(Literal context){
		LinkedHashSet<Literal> beliefs = new LinkedHashSet<Literal>();
		Integer arity = context.getArity();
		if(context.isLiteral() && !context.getAnnots("ap").isEmpty()){
			beliefs.add(context);
		} else if(arity > 0 && context.isLiteral()){
			for (int a=0; a<arity; a++) {
				if(context.getTermsArray()[a].isLiteral()){
					beliefs.addAll(findApBeliefs((Literal)context.getTermsArray()[a]));
				}
			}
		}
		return beliefs;
	}

	//Get plans that has any belief marked with ap in context
	public static PlansApBel getPlansApBel(Agent outerContent){
		PlansApBel plans_ap_bel = new PlansApBel();
		for(Plan p : outerContent.getPL()){
			Literal context = (Literal)p.getContext();
			if(context != null){
				LinkedHashSet<Literal>  ap_beliefs = findApBeliefs(context);
				if(!ap_beliefs.isEmpty()){
					Trigger trigger = p.getTrigger().clone();
					Term label = p.getLabel().clone();

					ApBels ap_bel = new ApBels(trigger, label, ap_beliefs);

					Trigger trigger_del = trigger.clone();
					trigger_del.setTrigOp(Trigger.TEOperator.del);
					ApBels del_ap_bel = new ApBels(trigger_del, label, ap_beliefs);

					plans_ap_bel.addApBels(ap_bel);
					plans_ap_bel.addApBels(del_ap_bel);
				}
			}

		}
		return plans_ap_bel;
	}

	//Annotating inital goals with annot
	public static void annotInitGoals(Agent outerContent, Set<Trigger> trigger_set, Atom... annot){
		for(Literal l: outerContent.getInitialGoals()){
			for(Trigger t: trigger_set){
				if(t.getLiteral().getPredicateIndicator().equals(l.getPredicateIndicator())){
					l.addAnnots(annot);
				}
			}
		}
	}

	public static void annotInitGoalsLabel(Agent outerContent, PlansApBel plans_ap_bel, Atom... annot){
		for(Literal l: outerContent.getInitialGoals()){
			for(Trigger t: plans_ap_bel.getTriggerSet()){
				if(t.getLiteral().getPredicateIndicator().equals(l.getPredicateIndicator())){
					l.addAnnots(annot);
					l.addAnnots(plans_ap_bel.getLabel(t));
				}
			}
		}
	}

	//Annotating plans with annot
	public static void annotTriggers(Agent outerContent, Set<Trigger> trigger_set, Atom annot){
		for(Plan p: outerContent.getPL()){
			if(trigger_set.contains(p.getTrigger())){
				p.getTrigger().getLiteral().addAnnots(annot);
			}
		}
	}

	public static void annotTriggersLabel(Agent outerContent, Set<Trigger> trigger_set, Atom annot){
		for(Plan p: outerContent.getPL()){
			if(trigger_set.contains(p.getTrigger())){
				Term label = p.getLabel().clone();
				p.getTrigger().getLiteral().addAnnots(annot, label);
			}
		}
	}

	//add [annot] to !g inside plans body
	public static void annotPlanBodyAchieve(Agent outerContent, Set<Trigger> trigger_set, Atom... annot){
		for(Plan p: outerContent.getPL()){
			PlanBody pb = p.getBody();
			for(int i=0; i<p.getBody().getPlanSize(); i++){
				Literal pb_ap = (Literal)pb.getBodyTerm();
				for(Trigger t: trigger_set){
					if(t.getLiteral().getPredicateIndicator().equals(pb_ap.getPredicateIndicator()) && pb.getBodyType() == jason.asSyntax.PlanBody.BodyType.achieve ){
						pb_ap.addAnnots(annot);
						break;
					}
				}
				pb = pb.getBodyNext();
			}
		}
	}

	public static void annotPlanBodyAchieveLabel(Agent outerContent, PlansApBel plans_ap_bel, Atom... annot){
		for(Plan p: outerContent.getPL()){
			PlanBody pb = p.getBody();
			for(int i=0; i<p.getBody().getPlanSize(); i++){
				Literal pb_ap = (Literal)pb.getBodyTerm();
				for(Trigger t: plans_ap_bel.getTriggerSet()){
					if(t.getLiteral().getPredicateIndicator().equals(pb_ap.getPredicateIndicator()) && pb.getBodyType() == jason.asSyntax.PlanBody.BodyType.achieve ){
						pb_ap.addAnnots(annot);
						pb_ap.addAnnots(plans_ap_bel.getLabel(t));
						break;
					}
				}
				pb = pb.getBodyNext();
			}
		}
	}

	public static void addNewPlan(Agent outerContent, Trigger trigger, LogicalFormula context, PlanBodyImpl... planBody){
		String label_str = "l__" + String.valueOf(outerContent.getPL().size() + 1);
		Pred label = new Pred(createLiteral(label_str));

		Plan new_plan = new Plan​(label, trigger, context, new PlanBodyImpl());

		for(PlanBodyImpl pb: planBody){
			new_plan.getBody().add(pb);
		}

		try{
			outerContent.getPL().add(new_plan);
		}catch(JasonException je){
			System.out.println("Error adding new plan");
		}
	}

	//Adding new plans +!update(X[ap(T)]): not active_perception.isUpdated(X[ap(T)]) <- ?X[ap].
	//and +!update(X).
	public static void addUpdatePlan(Agent outerContent){

		// create ap
		Atom ap_atom = createAtom("ap");
		//create ap(T)
		Literal ap_atom_t = createLiteral("ap");
		VarTerm t_term = new VarTerm("T");
		ap_atom_t.addTerm(t_term);

		// create X
		VarTerm x_term_ap = new VarTerm("X");
		x_term_ap.addAnnot(ap_atom);
		// create X[ap(T)]
		VarTerm x_term_t = new VarTerm("X");
		x_term_t.addAnnot(ap_atom_t);

		//create update(X[ap(T)])
		Literal update = createLiteral("update");
		update.addTerm(x_term_t);

		Trigger update_trigger = new Trigger(TEOperator.add, TEType.achieve, update.addAnnots(ap_atom));

		InternalActionLiteral isUpdated = new InternalActionLiteral("active_perception.isUpdated");
		isUpdated.addTerm(x_term_t);
		LogExpr not_isUpdated = new LogExpr(LogExpr.LogicalOp.not, isUpdated);

		PlanBodyImpl update_body = new PlanBodyImpl(jason.asSyntax.PlanBody.BodyType.test, x_term_ap);

		ActivePerception.addNewPlan(outerContent, update_trigger, not_isUpdated, update_body);
		ActivePerception.addNewPlan(outerContent, update_trigger, null);
	}

}

class ApBels{
	public Trigger plan_trigger;
	public Term plan_label;
	public LinkedHashSet<Literal> ap_set = new LinkedHashSet();

	public ApBels(){}

	public ApBels(Trigger t, Term l, LinkedHashSet<Literal> s){
		plan_trigger = t;
		plan_label = l;
		ap_set = s;
	}
}

class PlansApBel{
	LinkedList <ApBels> ap_bels= new LinkedList<ApBels>();
	Map<Trigger, Term> triggers_label = new HashMap<Trigger, Term>();

	public LinkedList<ApBels> getApBels(Trigger t){
		LinkedList<ApBels> ap_list = new LinkedList<ApBels>();
		for(ApBels apb: ap_bels){
			if(apb.plan_trigger.equals(t)){
				ap_list.add(apb);
			}
		}
		return ap_list;
	}

	public ApBels getApBelsLabel(Term label){
		LinkedList<ApBels> ap_list = new LinkedList<ApBels>();
		ApBels ap_bel = new ApBels();
		for(ApBels apb: ap_bels){
			if(apb.plan_label.equals(label)){
				ap_bel = apb;
			}
		}
		return ap_bel;
	}

	public LinkedList<ApBels> getAllApBels(){
		return ap_bels;
	}

	public Set <Trigger> getTriggerSet(){
		return triggers_label.keySet();
	}

	public Term getLabel(Trigger t){
		return triggers_label.get(t);
	}

	public void addApBels(ApBels aps){
		ap_bels.add(aps);
		triggers_label.computeIfAbsent(aps.plan_trigger, k -> aps.plan_label);
	}
}
